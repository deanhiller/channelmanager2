package biz.xsoftware.impl.nio.cm.threaded;

import java.io.IOException;
import java.util.concurrent.Executor;

import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.impl.nio.util.UtilRegisterable;

class ThdTCPServerChannel extends UtilRegisterable implements TCPServerChannel {

	private TCPServerChannel realChannel;
	private Executor svc;
	private BufferFactory bufFactory;
	
	public ThdTCPServerChannel(TCPServerChannel c, Executor svc, BufferFactory bufFactory) {
		super(c);
		realChannel = c;
		this.svc = svc;
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
