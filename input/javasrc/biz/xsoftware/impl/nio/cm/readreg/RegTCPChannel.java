package biz.xsoftware.impl.nio.cm.readreg;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.handlers.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.OperationCallback;

class RegTCPChannel extends RegHelperChannel implements TCPChannel {

	private static final Logger apiLog = Logger.getLogger(TCPChannel.class.getName());
//	private static final Logger log = Logger.getLogger(TCPChannelImpl.class.getName());
//	private BufferHelper helper = ChannelManagerFactory.bufferHelper(null);
	
	private TCPChannel realChannel;

	public RegTCPChannel(TCPChannel channel) {
		super(channel);
		realChannel = channel;
	}
	
	public synchronized void connect(SocketAddress addr, ConnectionCallback c) throws IOException, InterruptedException {
		if(apiLog.isLoggable(Level.FINE))
			apiLog.fine(this+"RegRead.connect called-addr="+addr);
		
		//Can I register for reads after initiating connect or should I register
		//for reads in a ConnectionCallback proxy??  For now, I register
		//after initiating the connect
		realChannel.connect(addr, c);
		if(cachedListener != null) {
			getRealChannel().registerForReads(cachedListener);
		}
	}	
	
	@Override
	public int oldWrite(ByteBuffer b) throws IOException {
		return getRealChannel().oldWrite(b);
	}
	
	public void write(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException {
		getRealChannel().write(b, h);
	}

	public boolean getKeepAlive() throws SocketException {
		return realChannel.getKeepAlive();
	}

	public void setKeepAlive(boolean b) throws SocketException {
		realChannel.setKeepAlive(b);
	}
}
