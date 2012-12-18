package biz.xsoftware.impl.nio.cm.packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.DataChunk;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.libs.PacketListener;
import biz.xsoftware.api.nio.libs.PacketProcessor;
import biz.xsoftware.impl.nio.util.DataChunkWithBuffer;
import biz.xsoftware.impl.nio.util.PacketChunk;

class PacProxyDataHandler implements DataListener, PacketListener {

	private static final Logger log = Logger.getLogger(PacProxyDataHandler.class.getName());
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
		ByteBuffer b = chunk.getData();
		try {
			boolean notified = packetProcessor.incomingData(b, chunk);
			
			DataChunkWithBuffer c = (DataChunkWithBuffer) chunk;
			c.releaseBuffer(" handler that didn't consume="+handler);
			
			if(!notified)
				chunk.setProcessed("PacProxyDataHandler");
		} catch(Exception e) {
			log.log(Level.WARNING, "exception", e);
			handler.failure(channel, b, e);
		}
	}
	
	public void farEndClosed(Channel realChannel) {
		handler.farEndClosed(channel);
	}

	public void incomingPacket(ByteBuffer b, Object passthrough) throws IOException {
		//MUST create a new packet here as the same DataChunk is sometimes used
		//since one ByteBuffer can contain multiple packets!!!!
		DataChunkWithBuffer chunk = (DataChunkWithBuffer) passthrough;
		PacketChunk c = new PacketChunk(b, chunk);
		handler.incomingData(channel, c);
	}

	public void failure(Channel realChannel, ByteBuffer data, Exception e) {
		handler.failure(channel, data, e);
	}

}
