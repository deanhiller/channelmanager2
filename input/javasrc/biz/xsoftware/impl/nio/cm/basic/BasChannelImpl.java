package biz.xsoftware.impl.nio.cm.basic;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.FutureOperation;
import biz.xsoftware.api.nio.handlers.OperationCallback;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.api.nio.libs.ChannelSession;
import biz.xsoftware.api.nio.libs.FactoryCreator;
import biz.xsoftware.impl.nio.util.UtilWaitForCompletion;


/**
 * @author Dean Hiller
 */
public abstract class BasChannelImpl
	extends RegisterableChannelImpl
	implements Channel {

	private static final Logger apiLog = Logger.getLogger(Channel.class.getName());
    private static final Logger log = Logger.getLogger(BasChannelImpl.class.getName());
    private static final FactoryCreator CREATOR = FactoryCreator.createFactory(null);
    
    private ChannelSession session;
	private LinkedBlockingQueue<DelayedWritesCloses> waitingWriters = new LinkedBlockingQueue<DelayedWritesCloses>(100);
	private ByteBuffer b;
	private boolean isConnecting = false;
	private boolean isClosed = false;
    private boolean registered;	
    
	public BasChannelImpl(IdObject id, BufferFactory factory, SelectorManager2 selMgr) {
		super(id, selMgr);
		session = CREATOR.createSession(this);
		//the default size of our read buffers is 1000
		//and the packet layer OR encryption layer may increase the
		//size of the buffer
		b = factory.createBuffer(id, 1000);      
	}
	
	/* (non-Javadoc)
	 * @see biz.xsoftware.nio.RegisterableChannelImpl#getRealChannel()
	 */
	public abstract SelectableChannel getRealChannel();

	/* (non-Javadoc)
	 * @see api.biz.xsoftware.nio.RegisterableChannel#isBlocking()
	 */
	public abstract boolean isBlocking();

	public abstract int readImpl(ByteBuffer b) throws IOException;
	protected abstract int writeImpl(ByteBuffer b) throws IOException;
   
    /**
     * This is the method where writes are added to the queue to be written later when the selector
     * fires and tells me we have room to write again.
     * 
     * @param id
     * @return true if the whole ByteBuffer was written, false if only part of it or none of it was written.
     * @throws IOException
     * @throws InterruptedException
     */
    private synchronized boolean tryWriteOrClose(DelayedWritesCloses action) throws IOException, InterruptedException {       
        
        //must see if this channel already has writers registered.  If not, we runDelayedAction to write
        //Otherwise, we continue on and register for writes down below.   
        if(!registered) {
            //if we are finished, just return immediately, otherwise continue on and queue the packet
            if(action.runDelayedAction(false))
                return true;
        }
        
        //TODO: make 30 seconds configurable in milliseoncds maybe
        boolean accepted = waitingWriters.offer(action, 30, TimeUnit.SECONDS);
        if(!accepted) {
            log.warning(this+"Dropping data, write buffer is backing up.  Remote end will not receive data");
            return false;
        }
        
        //if not already registered, then register for writes.....
        //NOTE: we must do this after waitingWriters.offer so there is something on the queue to read
        //otherwise, that could be bad.
        if(!registered) {
            registered = true;
            if(log.isLoggable(Level.FINER))
                log.finer(this+"registering channel for write msg cb="+action+" size="+waitingWriters.size());
            getSelectorManager().registerSelectableChannel(this, SelectionKey.OP_WRITE, null, false);
        }                       

        return false;
    }
       
    /**
     * This method is reading from the queue and writing out to the socket buffers that
     * did not get written out when client called write.
     *
     */
    synchronized void writeAll() {
        Queue<DelayedWritesCloses> writers = waitingWriters;

        if(writers.isEmpty())
            return;

        while(!writers.isEmpty()) {
            DelayedWritesCloses writer = writers.peek();
            boolean finished = writer.runDelayedAction(true);
            if(!finished) {
                if(log.isLoggable(Level.FINER))
                    log.finer(this+"Did not write all of id="+writer);
                break;
            }
            //if it finished, remove the item from the queue.  It
            //does not need to be run again.
            writers.remove();
        }
        
        if(writers.isEmpty()) {
            if(log.isLoggable(Level.FINER))
                log.fine(this+"unregister writes");
            registered = false;
            Helper.unregisterSelectableChannel(this, SelectionKey.OP_WRITE); 
        }
    }

    public void bind(SocketAddress addr) throws IOException {
        if(!(addr instanceof InetSocketAddress))
            throw new IllegalArgumentException(this+"Can only bind to InetSocketAddress addressses");
        if(apiLog.isLoggable(Level.FINE))
        	apiLog.fine(this+"Basic.bind called addr="+addr);
        
        bindImpl(addr);
    }
    
    private void bindImpl(SocketAddress addr) throws IOException {
        try {
            bindImpl2(addr);
        } catch(Error e) {
            //NOTE:  jdk was throwing Error instead of BindException.  We fix
            //this and throw BindException which is the logical choice!!
            //We are crossing our fingers hoping there are not other SocketExceptions
            //from things other than address already in use!!!
            if(e.getCause() instanceof SocketException) {
                BindException exc = new BindException(e.getMessage());
                exc.initCause(e.getCause());
                throw exc;
            }
            throw e;
        }        
    }
    
    /**
     * 
     * @param addr
     * @throws IOException
     */
    protected abstract void bindImpl2(SocketAddress addr) throws IOException;
    
	public void registerForReads(DataListener listener) throws IOException, InterruptedException {
		if(listener == null)
			throw new IllegalArgumentException(this+"listener cannot be null");
		else if(!isConnecting && !isConnected()) {
			throw new IllegalStateException(this+"Must call one of the connect methods first");
		}

		if(apiLog.isLoggable(Level.FINE))
			apiLog.fine(this+"Basic.registerForReads called");
		
        getSelectorManager().registerChannelForRead(this, listener);
	}
	
	public void unregisterForReads() throws IOException, InterruptedException {
		if(apiLog.isLoggable(Level.FINE))
			apiLog.fine(this+"Basic.unregisterForReads called");		
		getSelectorManager().unregisterChannelForRead(this);
	}	
	
	ByteBuffer getIncomingDataBuf() {
		return b;
	}
	
	public int oldWrite(ByteBuffer b) throws IOException {
		if(!getSelectorManager().isRunning())
			throw new IllegalStateException(this+"ChannelManager must be running and is stopped");
		else if(isClosed) {
			AsynchronousCloseException exc = new AsynchronousCloseException();
			IOException ioe = new IOException(this+"Client cannot write after the client closed the socket");
			exc.initCause(ioe);
			throw exc;
		}
		Object t = getSelectorManager().getThread();
		if(Thread.currentThread().equals(t)) {
			//leave this in, users should not do this....
			throw new RuntimeException(this+"You should not perform a " +
					"blocking write on the channelmanager thread unless you like deadlock.  " +
					"Use the cm threading layer, or put the code calling this write on another thread");
		}

		try {
            int remain = b.remaining();

			UtilWaitForCompletion waitWrite = new UtilWaitForCompletion(this, t);
			oldWrite(b, waitWrite);
            //otherwise if not all was written, wait for completion as it was added to queue
            //which writes on selector thread....
			waitWrite.waitForComplete();
            
			if(b.hasRemaining())
				throw new RuntimeException(this+"Did not write all of the ByteBuffer out");
			return remain;
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
    
	@Override
	public FutureOperation write(ByteBuffer b) throws IOException, InterruptedException {
		if(!getSelectorManager().isRunning())
			throw new IllegalStateException(this+"ChannelManager must be running and is stopped");		
		else if(isClosed) {
			AsynchronousCloseException exc = new AsynchronousCloseException();
			IOException ioe = new IOException(this+"Client cannot write after the client closed the socket");
			exc.initCause(ioe);
			throw exc;
		}
		FutureOperationImpl impl = new FutureOperationImpl();
		
		if(apiLog.isLoggable(Level.FINER))
			apiLog.finer(this+"Basic.write");
		
        //copy the buffer here
        ByteBuffer newOne = ByteBuffer.allocate(b.remaining());
        newOne.put(b);
        newOne.flip();
        WriteRunnable holder = new WriteRunnable(this, newOne, impl);
        
		boolean wroteNow = tryWriteOrClose(holder);
        
		if(wroteNow)
			impl.finished(this);
		
        if(log.isLoggable(Level.FINER)) {
	        if(!wroteNow)
	            log.finer(this+"did not write immediately, queued up for delivery");
    	    else
        	    log.finest(this+"delivered");
       	}
        return impl;
	}
	
	public void oldWrite(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException {
		if(!getSelectorManager().isRunning())
			throw new IllegalStateException(this+"ChannelManager must be running and is stopped");		
		else if(isClosed) {
			AsynchronousCloseException exc = new AsynchronousCloseException();
			IOException ioe = new IOException(this+"Client cannot write after the client closed the socket");
			exc.initCause(ioe);
			throw exc;
		}
		if(apiLog.isLoggable(Level.FINER))
			apiLog.finer(this+"Basic.write callback="+h);
		
        //copy the buffer here
        ByteBuffer newOne = ByteBuffer.allocate(b.remaining());
        newOne.put(b);
        newOne.flip();
        WriteRunnable holder = new WriteRunnable(this, newOne, h);
        
		boolean wroteNow = tryWriteOrClose(holder);
        
        if(log.isLoggable(Level.FINER)) {
	        if(!wroteNow)
	            log.finer(this+"did not write immediately, queued up for delivery");
    	    else
        	    log.finest(this+"delivered");
       	}
	}
           
	protected void setConnecting(boolean b) {
		isConnecting = b;
	}
	protected boolean isConnecting() {
		return isConnecting;
	}
	protected void setClosed(boolean b) {
		isClosed = b;    
	}

    /* (non-Javadoc)
     * @see api.biz.xsoftware.nio.SocketChannel#close()
     */
    public void oldClose() {                
        
        Object t = getSelectorManager().getThread();
        if(t != null && Thread.currentThread().equals(t)) {
            //leave this in, users should not do this....
            throw new RuntimeException(this+"You should not perform a blocking close "+
        "on the channelmanager thread for performance reasons.  Use the cm threading layer, "+
        "or put the code calling this write on another thread");
        }
        try {
            UtilWaitForCompletion waitWrite = new UtilWaitForCompletion(this, null);
            oldClose(waitWrite);
            waitWrite.waitForComplete();
        } catch(Exception e) {
            log.log(Level.WARNING, this+"Exception closing channel", e);
        }
    }
    
    public void oldClose(OperationCallback h) {
        //To prevent the following exception, in the readImpl method, we
        //check if the socket is already closed, and if it is we don't read
        //and just return -1 to indicate socket closed.
        //
        //This is very complicated.  It must be done after all the writes that have already
        //been called before the close was called.  Basically, the close may need be 
        //queued if there are writes on the queue.
        //unless you like to see the following
        //exception..........
        //Feb 19, 2006 6:06:03 AM biz.xsoftware.test.nio.tcp.ZNioSuperclassTest verifyTearDown
        //INFO: CLIENT1 CLOSE
        //Feb 19, 2006 6:06:03 AM biz.xsoftware.impl.nio.cm.basic.Helper read
        //INFO: [[client]] Exception
        //java.nio.channels.ClosedChannelException
        //  at sun.nio.ch.SocketChannelImpl.ensureReadOpen(SocketChannelImpl.java:112)
        //  at sun.nio.ch.SocketChannelImpl.read(SocketChannelImpl.java:139)
        //  at biz.xsoftware.impl.nio.cm.basic.TCPChannelImpl.readImpl(TCPChannelImpl.java:162)
        //  at biz.xsoftware.impl.nio.cm.basic.Helper.read(Helper.java:143)
        //  at biz.xsoftware.impl.nio.cm.basic.Helper.processKey(Helper.java:92)
        //  at biz.xsoftware.impl.nio.cm.basic.Helper.processKeys(Helper.java:47)
        //  at biz.xsoftware.impl.nio.cm.basic.SelectorManager2.runLoop(SelectorManager2.java:305)
        //  at biz.xsoftware.impl.nio.cm.basic.SelectorManager2$PollingThread.run(SelectorManager2.java:267)
    	try {
    		if(apiLog.isLoggable(Level.FINE))
    			apiLog.fine(this+"Basic.close called");
    		
	        if(!getRealChannel().isOpen())
	            h.finished(this);
	        
	        setClosed(true);
	        CloseRunnable runnable = new CloseRunnable(this, h);
	        tryWriteOrClose(runnable);
        } catch(Exception e) {
            log.log(Level.WARNING, this+"Exception closing channel", e);
        }
    }
    
    protected abstract void closeImpl() throws IOException;

    public ChannelSession getSession() {
    	return session;
    }    
}
