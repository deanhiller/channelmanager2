package org.playorm.nio.impl.cm.readreg;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLEngine;

import org.playorm.nio.api.channels.TCPChannel;
import org.playorm.nio.api.deprecated.ConnectionCallback;
import org.playorm.nio.api.handlers.FutureOperation;
import org.playorm.nio.api.handlers.OperationCallback;


class RegTCPChannel extends RegHelperChannel implements TCPChannel {

	private static final Logger apiLog = Logger.getLogger(TCPChannel.class.getName());
//	private static final Logger log = Logger.getLogger(TCPChannelImpl.class.getName());
//	private BufferHelper helper = ChannelManagerFactory.bufferHelper(null);
	
	public RegTCPChannel(TCPChannel channel) {
		super(channel);
	}
	
	protected TCPChannel getRealTcpChannel() {
		return (TCPChannel)super.getRealChannel();
	}
	
	public synchronized void oldConnect(SocketAddress addr, ConnectionCallback c) throws IOException, InterruptedException {
		if(apiLog.isLoggable(Level.FINE))
			apiLog.fine(this+"RegRead.connect called-addr="+addr);
		
		//Can I register for reads after initiating connect or should I register
		//for reads in a ConnectionCallback proxy??  For now, I register
		//after initiating the connect
		TCPChannel realChannel = getRealTcpChannel();
		realChannel.oldConnect(addr, c);
		if(cachedListener != null) {
			getRealChannel().registerForReads(cachedListener);
		}
	}	
	
	@Override
	public int oldWrite(ByteBuffer b) throws IOException {
		return getRealChannel().oldWrite(b);
	}
	
	public void oldWrite(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException {
		getRealChannel().oldWrite(b, h);
	}

	public boolean getKeepAlive() throws SocketException {
		TCPChannel realChannel = getRealTcpChannel();
		return realChannel.getKeepAlive();
	}

	public void setKeepAlive(boolean b) throws SocketException {
		TCPChannel realChannel = getRealTcpChannel();
		realChannel.setKeepAlive(b);
	}
	
	public FutureOperation openSSL(SSLEngine engine) {
		TCPChannel realChannel = getRealTcpChannel();
		return realChannel.openSSL(engine);
	}

	public FutureOperation closeSSL() {
		TCPChannel realChannel = getRealTcpChannel();
		return realChannel.closeSSL();
	}
}
