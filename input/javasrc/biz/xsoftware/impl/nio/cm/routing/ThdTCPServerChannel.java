package biz.xsoftware.impl.nio.cm.routing;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.impl.nio.util.UtilRegisterable;

class ThdTCPServerChannel extends UtilRegisterable implements TCPServerChannel {

	private TCPServerChannel realChannel;
	private SpecialRoutingExecutor svc;
	private BufferFactory bufFactory;
	
	public ThdTCPServerChannel(TCPServerChannel c, SpecialRoutingExecutor svc2, BufferFactory bufFactory) {
		super(c);
		realChannel = c;
		this.svc = svc2;
		this.bufFactory = bufFactory;
	}

	public TCPServerChannel getRealChannel() {
		return realChannel;
	}
	
	public void registerServerSocketChannel(ConnectionListener cb) throws IOException, InterruptedException {
		ThdProxyAcceptCb proxy = new ThdProxyAcceptCb(this, cb, svc, bufFactory);
		realChannel.registerServerSocketChannel(proxy);
	}
}
