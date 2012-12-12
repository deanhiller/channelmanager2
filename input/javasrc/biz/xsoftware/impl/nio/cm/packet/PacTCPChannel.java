package biz.xsoftware.impl.nio.cm.packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.handlers.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.OperationCallback;
import biz.xsoftware.api.nio.libs.PacketProcessor;
import biz.xsoftware.impl.nio.util.UtilPassThroughWriteHandler;
import biz.xsoftware.impl.nio.util.UtilTCPChannel;

class PacTCPChannel extends UtilTCPChannel implements TCPChannel {

//	private static final Logger log = Logger.getLogger(TCPChannelImpl.class.getName());
//	private BufferHelper helper = ChannelManagerFactory.bufferHelper(null);
	
	private TCPChannel realChannel;
	private PacketProcessor packetProcessor;
	
	public PacTCPChannel(TCPChannel channel, PacketProcessor proc) {
		super(channel);
		this.realChannel = channel;
		this.packetProcessor = proc;
	}
	
	public void registerForReads(DataListener listener) throws IOException, InterruptedException {
		PacProxyDataHandler handler = new PacProxyDataHandler(this, packetProcessor, listener);
		packetProcessor.setPacketListener(handler);
		realChannel.registerForReads(handler);
	}


	@Override
	public int oldWrite(ByteBuffer b) throws IOException {
		int retVal = b.remaining();
		ByteBuffer out = packetProcessor.processOutgoing(b);
		realChannel.oldWrite(out);
		return retVal;
	}
	
	@Override
	public void oldWrite(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException {
		ByteBuffer out = packetProcessor.processOutgoing(b);
		realChannel.oldWrite(out, new UtilPassThroughWriteHandler(this, h));
	}
	
	public void oldConnect(SocketAddress addr, ConnectionCallback c) throws IOException, InterruptedException {
		if(c == null)
			throw new IllegalArgumentException("ConnectCallback cannot be null");
		
		PacProxyConnectCb proxy = new PacProxyConnectCb(this, c);
		realChannel.oldConnect(addr, proxy);
	}	
}
