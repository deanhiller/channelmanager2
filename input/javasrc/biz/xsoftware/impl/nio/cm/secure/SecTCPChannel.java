package biz.xsoftware.impl.nio.cm.secure;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.deprecated.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.OperationCallback;
import biz.xsoftware.api.nio.libs.ChannelSession;
import biz.xsoftware.api.nio.libs.SSLEngineFactory;
import biz.xsoftware.impl.nio.util.UtilTCPChannel;
import biz.xsoftware.impl.nio.util.UtilWaitForConnect;

class SecTCPChannel extends UtilTCPChannel implements TCPChannel {

	private static final Logger log = Logger.getLogger(SecTCPChannel.class.getName());
	
	private TCPChannel realChannel;
	
	private boolean isConnecting = false;
	private SSLEngineFactory sslFactory;
	private SecReaderProxy reader;
	private SecSSLListener connectProxy;

	//Server Socket created by TCPServerSocket
	public SecTCPChannel(TCPChannel channel) {
		super(channel);
		this.realChannel = channel;

		connectProxy = new SecSSLListener(this);
		reader = new SecReaderProxy(connectProxy);
	}

	//Client Socket created by channel manager
	public SecTCPChannel(TCPChannel channel, SSLEngineFactory sslFactory) {
		this(channel);
		this.sslFactory = sslFactory;
	}

	public int oldWrite(ByteBuffer b) throws IOException {
		if(reader.getHandler() == null)
			throw new NotYetConnectedException();
		int remain = b.remaining();
		reader.getHandler().feedPlainPacket(b, null);
		if(b.hasRemaining())
			throw new RuntimeException(realChannel+"Bug, not all data written, buf="+b);
		return remain;
	}
	
	public void oldWrite(ByteBuffer b, OperationCallback h) throws IOException {
		if(reader.getHandler() == null)
			throw new NotYetConnectedException();
		
		SecProxyWriteHandler holder = new SecProxyWriteHandler(this, h);
		reader.getHandler().feedPlainPacket(b, holder);	
	}
	
	/**
	 * Not thread safe compatible with connect.  You should call this method
	 * on the same right after connect
	 */
	public synchronized void registerForReads(DataListener listener) throws IOException, InterruptedException {
		//TODO: this is a big problem, if they don't register for a read before the connect,
		//we will not receive the certificate info!!!!
		connectProxy.setClientHandler(listener);
		if(log.isLoggable(Level.FINEST))
			log.finest(realChannel+" about to register for reads");				
		if(!isConnecting) {
			if(log.isLoggable(Level.FINEST))
				log.finest(realChannel+" register for reads");			

			realChannel.registerForReads(reader);
		}
	}
	
	public synchronized void unregisterForReads() throws IOException, InterruptedException {

		//this first call ensure that if we are connecting, the
		//real unregisterForReads happens once connected...
		if(log.isLoggable(Level.FINEST))
			log.finest(realChannel+" about to unregister for reads");				
		if(!isConnecting) {
			if(log.isLoggable(Level.FINEST))
				log.finest(realChannel+" unregister for reads");	
			realChannel.unregisterForReads();
		}
		connectProxy.setClientHandler(null);
		
	}
	
	public synchronized void oldConnect(SocketAddress addr, ConnectionCallback c) throws IOException, InterruptedException {
		if(c == null)
			throw new IllegalArgumentException(realChannel+"ConnectCallback cannot be null");
		else if(sslFactory == null)
			throw new RuntimeException(realChannel+"This socket is already connected");
		isConnecting = true;
		
		SecProxyConnectCb connectCb = new SecProxyConnectCb(this, sslFactory, c);
		realChannel.oldConnect(addr, connectCb);
	}
	
	public synchronized void resetRegisterForReadState() throws IOException, InterruptedException {
		isConnecting = false;
		//if client has not registered for reads, unregister for reads
		//as we don't need any more data for handshake...
		if(log.isLoggable(Level.FINEST))
			log.finest(realChannel+" about to unregister for reads");				
		if(!connectProxy.isClientRegistered()) {
			if(log.isLoggable(Level.FINEST))
				log.finest(realChannel+" unregister for reads");	
			realChannel.unregisterForReads();
		}
	}

	public void oldConnect(SocketAddress addr) throws IOException {
		if(isBlocking()) {
			realChannel.oldConnect(addr);
		} else {
			try {
				UtilWaitForConnect connect = new UtilWaitForConnect();				
				oldConnect(addr, connect);
				connect.waitForConnect();
			} catch(InterruptedException e) {
				throw new RuntimeException(this+"Exception", e);
			}
		}
	}
	
	public InetSocketAddress getRemoteAddress() {
		return realChannel.getRemoteAddress();
	}

	public boolean isConnected() {
		return realChannel.isConnected();
	}
	
	public TCPChannel getRealChannel() {
		return realChannel;
	}

	@Override
	public void oldClose() {
		try {
			reader.close();
		} catch(Exception e) {
//TODO: this used to be AsynchronousCloseException which was okay.....fix this again...
//			//An SSL Channel does a write first(for SSL finish handshake)
//			//and if this is already closed(from far end or something else,
//			//it results in this exception, so ignore and move on)
//			return;
			log.log(Level.WARNING, this+"Exception on closing channel", e);
		}
		
		super.oldClose();
	}

	public void oldClose(OperationCallback h) {
		reader.close();
		realChannel.oldClose(new SecProxyWriteHandler(this, h));
	}
	
	public SecReaderProxy getReaderProxy() {
		return reader;
	}

	public SecSSLListener getConnectProxy() {
		return connectProxy;
	}
	
	public ChannelSession getSession() {
		return realChannel.getSession();
	}
}
