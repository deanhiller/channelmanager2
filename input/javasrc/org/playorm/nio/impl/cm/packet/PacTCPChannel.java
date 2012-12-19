package org.playorm.nio.impl.cm.packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.playorm.nio.api.channels.Channel;
import org.playorm.nio.api.channels.TCPChannel;
import org.playorm.nio.api.deprecated.ConnectionCallback;
import org.playorm.nio.api.handlers.DataListener;
import org.playorm.nio.api.handlers.FutureOperation;
import org.playorm.nio.api.handlers.OperationCallback;
import org.playorm.nio.api.libs.PacketProcessor;
import org.playorm.nio.impl.util.UtilPassThroughWriteHandler;
import org.playorm.nio.impl.util.UtilTCPChannel;


class PacTCPChannel extends UtilTCPChannel implements TCPChannel {

	private static final Logger log = Logger.getLogger(PacTCPChannel.class.getName());
//	private BufferHelper helper = ChannelManagerFactory.bufferHelper(null);
	
	private PacketProcessor packetProcessor;
	
	public PacTCPChannel(TCPChannel channel, PacketProcessor proc) {
		super(channel);
		this.packetProcessor = proc;
	}
	
	public void registerForReads(DataListener listener) throws IOException, InterruptedException {
		PacProxyDataHandler handler = new PacProxyDataHandler(this, packetProcessor, listener);
		packetProcessor.setPacketListener(handler);
		TCPChannel realChannel = getRealChannel();
		realChannel.registerForReads(handler);
	}

	public FutureOperation write(ByteBuffer b) throws IOException, InterruptedException {
		ByteBuffer out = packetProcessor.processOutgoing(b);
		TCPChannel realChannel = getRealChannel();
		return realChannel.write(out);
	}
	
	@Override
	public int oldWrite(ByteBuffer b) throws IOException {
		int retVal = b.remaining();
		ByteBuffer out = packetProcessor.processOutgoing(b);
		TCPChannel realChannel = getRealChannel();
		realChannel.oldWrite(out);
		return retVal;
	}
	
	@Override
	public void oldWrite(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException {
		ByteBuffer out = packetProcessor.processOutgoing(b);
		TCPChannel realChannel = getRealChannel();
		realChannel.oldWrite(out, new UtilPassThroughWriteHandler(this, h));
	}
	
	public void oldConnect(SocketAddress addr, ConnectionCallback c) throws IOException, InterruptedException {
		if(c == null)
			throw new IllegalArgumentException("ConnectCallback cannot be null");
		
		PacProxyConnectCb proxy = new PacProxyConnectCb(this, c);
		TCPChannel realChannel = getRealChannel();
		realChannel.oldConnect(addr, proxy);
	}	
}
