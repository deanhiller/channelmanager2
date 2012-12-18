package biz.xsoftware.impl.nio.cm.routing;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.deprecated.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.OperationCallback;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.impl.nio.util.UtilTCPChannel;

class ThdTCPChannel extends UtilTCPChannel implements TCPChannel {

//	private static final Logger log = Logger.getLogger(TCPChannelImpl.class.getName());
//	private BufferHelper helper = ChannelManagerFactory.bufferHelper(null);
	
	private SpecialRoutingExecutor svc;
	private BufferFactory bufFactory;
	
	public ThdTCPChannel(TCPChannel channel, SpecialRoutingExecutor svc2, BufferFactory bufFactory) {
		super(channel);
		this.svc = svc2;
		this.bufFactory = bufFactory;
	}
	
	public void registerForReads(DataListener listener) throws IOException, InterruptedException {
		ThdProxyDataHandler handler = new ThdProxyDataHandler(this, listener, svc, bufFactory);
		TCPChannel realChannel = getRealChannel();
		realChannel.registerForReads(handler);
	}

	@Override
	public int oldWrite(ByteBuffer b) throws IOException {
		TCPChannel realChannel = getRealChannel();
		return realChannel.oldWrite(b);
	}
	
	public void oldWrite(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException {
		TCPChannel realChannel = getRealChannel();
		realChannel.oldWrite(b, new ThdProxyWriteHandler(this, h, svc));
	}
	
	public void oldConnect(SocketAddress addr, ConnectionCallback c) throws IOException, InterruptedException {
		if(c == null)
			throw new IllegalArgumentException("ConnectCallback cannot be null");
		
		ThdProxyConnectCb proxy = new ThdProxyConnectCb(this, c, svc);
		TCPChannel realChannel = getRealChannel();
		realChannel.oldConnect(addr, proxy);
	}

	public void oldClose(OperationCallback h) {
		TCPChannel realChannel = getRealChannel();
		realChannel.oldClose(new ThdProxyWriteHandler(this, h, svc));
	}    
}
