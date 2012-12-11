package biz.xsoftware.impl.nio.cm.readreg;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.impl.nio.util.UtilRegisterable;

class RegTCPServerChannel extends UtilRegisterable implements TCPServerChannel {

	private TCPServerChannel realChannel;
	
	public RegTCPServerChannel(TCPServerChannel c) {
		super(c);
		realChannel = c;
	}

	public TCPServerChannel getRealChannel() {
		return realChannel;
	}
	
	public void registerServerSocketChannel(ConnectionListener cb) throws IOException, InterruptedException {
		RegProxyAcceptCb proxy = new RegProxyAcceptCb(this, cb);
		realChannel.registerServerSocketChannel(proxy);
	}
}
