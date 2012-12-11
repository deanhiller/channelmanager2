package biz.xsoftware.impl.nio.cm.exception;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.impl.nio.util.UtilRegisterable;

class ExcTCPServerChannel extends UtilRegisterable implements TCPServerChannel {

	private TCPServerChannel realChannel;
	
	public ExcTCPServerChannel(TCPServerChannel c) {
		super(c);
		realChannel = c;
	}

	public TCPServerChannel getRealChannel() {
		return realChannel;
	}
	
	public void registerServerSocketChannel(ConnectionListener cb) throws IOException, InterruptedException {
		ExcProxyAcceptCb proxy = new ExcProxyAcceptCb(this, cb);
		realChannel.registerServerSocketChannel(proxy);
	}
}
