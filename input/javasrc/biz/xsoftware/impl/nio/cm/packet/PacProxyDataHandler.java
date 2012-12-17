package biz.xsoftware.impl.nio.cm.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.DataChunk;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.PacketListener;
import biz.xsoftware.api.nio.libs.PacketProcessor;
import biz.xsoftware.impl.nio.util.PacketChunk;

class PacProxyDataHandler implements DataListener, PacketListener {

	//private static final Logger log = Logger.getLogger(ProxyDataHandler.class.getName());
	private PacTCPChannel channel;
	private DataListener handler;
	private PacketProcessor packetProcessor;

	public PacProxyDataHandler(PacTCPChannel channel, PacketProcessor p, DataListener handler) {
		this.channel = channel;
		this.handler = handler;
		this.packetProcessor = p;
		//this.realChannel = (TCPChannel)channel.getRealChannel();
	}
	
	public void incomingData(Channel realChannel, DataChunk chunk) throws IOException {
		try {
			ByteBuffer b = chunk.getData();
			boolean notified = packetProcessor.incomingData(b, chunk);
			if(!notified)
				chunk.setProcessed();
		} catch(Exception e) {
			handler.incomingData(channel, chunk);
		}
	}
	
	public void farEndClosed(Channel realChannel) {
		handler.farEndClosed(channel);
	}

	public void incomingPacket(ByteBuffer b, Object passthrough) throws IOException {
		DataChunk chunk = (DataChunk) passthrough;
		PacketChunk newChunk = new PacketChunk(b, chunk);
		handler.incomingData(channel, newChunk);
	}

	public void failure(Channel realChannel, ByteBuffer data, Exception e) {
		handler.failure(channel, data, e);
	}

}
