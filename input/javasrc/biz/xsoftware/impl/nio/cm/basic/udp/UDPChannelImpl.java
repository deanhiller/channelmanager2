package biz.xsoftware.impl.nio.cm.basic.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.UDPChannel;
import biz.xsoftware.api.nio.handlers.OperationCallback;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.impl.nio.cm.basic.BasChannelImpl;
import biz.xsoftware.impl.nio.cm.basic.IdObject;
import biz.xsoftware.impl.nio.cm.basic.SelectorManager2;

public class UDPChannelImpl extends BasChannelImpl implements UDPChannel {

	private static final Logger log = Logger.getLogger(UDPChannel.class.getName());
	private static final Logger apiLog = Logger.getLogger(UDPChannel.class.getName());
	private DatagramChannel channel;
	private boolean isConnected = false;
    private Calendar expires;
    
	public UDPChannelImpl(IdObject id, BufferFactory factory, SelectorManager2 selMgr) throws IOException {
		super(id, factory, selMgr);
		channel = DatagramChannel.open();
		channel.configureBlocking(false);
        channel.socket().setReuseAddress(true);
	}

	public void bindImpl2(SocketAddress addr) throws IOException {
        channel.socket().bind(addr);
	}

	public synchronized void oldConnect(SocketAddress addr) throws IOException {
		if(apiLog.isLoggable(Level.FINE))
			apiLog.fine(this+"Basic.connect called-addr="+addr);
		
		channel.connect(addr);
        isConnected = true;
	}
    
    public synchronized void oldDisconnect() throws IOException {
		if(apiLog.isLoggable(Level.FINE))
			apiLog.fine(this+"Basic.disconnect called");
		
        isConnected = false;        
        channel.disconnect();
    }

    public void setReuseAddress(boolean b) throws SocketException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public boolean isBlocking() {
		return channel.isBlocking();
	}

	public void closeImpl() throws IOException {
		channel.close();
	}

	public boolean isClosed() {
		return channel.socket().isClosed();
	}

	public boolean isBound() {
		return channel.socket().isBound();
	}

	public InetSocketAddress getLocalAddress() {
		InetAddress addr = channel.socket().getLocalAddress();
		int port = channel.socket().getLocalPort();      
		return new InetSocketAddress(addr, port);
	}

	public InetSocketAddress getRemoteAddress() {
		return (InetSocketAddress)channel.socket().getRemoteSocketAddress();
	}
    
	public boolean isConnected() {
		return channel.isConnected();
	}

	@Override
	public SelectableChannel getRealChannel() {
		return channel;
	}
	
	@Override
	public int readImpl(ByteBuffer b) throws IOException {
		if(b == null)
			throw new IllegalArgumentException("Cannot use a null ByteBuffer");
		else if(!isConnected)
			throw new IllegalStateException("Currently not connected");
		try {
			return channel.read(b);
		} catch(PortUnreachableException e) {
			if(expires != null) {
				//ignore the event if we are not at expires yet
				if(Calendar.getInstance().before(expires)) {
					return 0;
				}
			}

			expires = Calendar.getInstance();
			expires.add(Calendar.SECOND, 10);
			log.warning("PortUnreachable.  NOTICE NOTICE:  We will ignore this exc again on this channel for 10 seconds");
			throw e;
		}
	}

	@Override
	protected int writeImpl(ByteBuffer b) throws IOException {
		return channel.write(b);
	}

	/**
     * @see biz.xsoftware.impl.nio.cm.basic.BasChannelImpl#oldWrite(java.nio.ByteBuffer, biz.xsoftware.api.nio.handlers.OperationCallback)
     */
    @Override
    public void oldWrite(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException {
        if(!isConnected)
            throw new IllegalStateException(this+"Channel is not currently connected");        
        super.oldWrite(b, h);
    }

    /**
     * @see biz.xsoftware.impl.nio.cm.basic.BasChannelImpl#oldWrite(java.nio.ByteBuffer)
     */
    @Override
    public int oldWrite(ByteBuffer b) throws IOException {
        if(!isConnected)
            throw new IllegalStateException(this+"Channel is not currently connected");        
        return super.oldWrite(b);
    }

}
