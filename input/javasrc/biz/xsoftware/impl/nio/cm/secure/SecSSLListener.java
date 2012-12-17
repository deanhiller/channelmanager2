package biz.xsoftware.impl.nio.cm.secure;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.handlers.DataChunk;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.NullWriteCallback;
import biz.xsoftware.api.nio.handlers.OperationCallback;
import biz.xsoftware.api.nio.libs.SSLListener;
import biz.xsoftware.impl.nio.util.PacketChunk;

class SecSSLListener implements SSLListener {

	private static final Logger log = Logger.getLogger(SecSSLListener.class.getName());
	
	private SecTCPChannel channel;
	private ConnectionListener cb;
	private DataListener client;
	private int tempId = 0;
	private boolean isConnected = false;
	
	public SecSSLListener(SecTCPChannel impl) {
		this.channel = impl;
	}
	
	public void encryptedLinkEstablished() throws IOException {
		try {
			channel.resetRegisterForReadState();
		} catch (InterruptedException e) {
			throw new RuntimeException(channel+"Exception occured", e);
		}
		cb.finished(channel);
		isConnected = true;
	}
	


	public void packetEncrypted(ByteBuffer toSocket, Object passThrough) throws IOException {
		OperationCallback h;
		if(passThrough == null){
			h = NullWriteCallback.singleton();
		} else {
			SecProxyWriteHandler handler = (SecProxyWriteHandler)passThrough;
			h = handler;
		}
		try {
			channel.getRealChannel().oldWrite(toSocket, h);
		} catch (InterruptedException e) {
			throw new RuntimeException(channel+e.getMessage(), e);
		}
	}
	
	public void packetUnencrypted(ByteBuffer out, Object passThrough) throws IOException {
		DataChunk c = (DataChunk) passThrough;
		DataChunk newChunk = new PacketChunk(out, c);
		client.incomingData(channel, newChunk);
	}
	
	public void setClientHandler(DataListener client) {
		this.client = client;
	}
	public boolean isClientRegistered() {
		return client != null;
	}

	public void setConnectCallback(ConnectionListener cb) {
		this.cb = cb;
	}

	public void farEndClosed() {
		if(client != null) //if the client did not register for reads, we can't fire to anyone(thought that would be mighty odd)
			client.farEndClosed(channel);
		else if(!isConnected) {
			log.info("The far end connected and did NOT establish security session and then closed his socket.  " +
					"This is normal behavior if a telnet socket connects to your secure socket and exits " +
					"because the socket was never officially 'connected' as we only fire " +
					"connected AFTER the SSL handshake is done.  You may want to check if" +
					" someone is trying to hack your server though");
		} else
			log.warning("When we called ConnectionListener.connected on YOUR ConnectionListener, " +
					"you forot to call registerForReads so we have not callback handler to call " +
					"to tell you this socket is closed from far end");
	}

	public void runTask(Runnable r) {
		r.run();
	}

	public void closed(boolean clientInitiated) {
//		if(fromEncryptedPacket && !closedAlready)
//			client.farEndClosed(channel);
		//can just drop this...we are using close, not initiateClose
		//which is effective immediately.
	}

	public void feedProblemThrough(Channel c, ByteBuffer b, Exception e) throws IOException {
		client.failure(c, b, e);
	}

}
