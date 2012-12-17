package biz.xsoftware.impl.nio.cm.secure;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.libs.SSLEngineFactory;
import biz.xsoftware.impl.nio.util.UtilRegisterable;

class SecTCPServerChannel extends UtilRegisterable implements TCPServerChannel {

	private TCPServerChannel realChannel;
	private SSLEngineFactory sslFactory;
	
	public SecTCPServerChannel(TCPServerChannel c, SSLEngineFactory sslFactory) {
		super(c);
		realChannel = c;
		this.sslFactory = sslFactory;
	}
		
	public void oldClose() {
		realChannel.oldClose();
	}
	
	public void registerServerSocketChannel(ConnectionListener listener) throws IOException, InterruptedException {
		SecProxyConnectCb proxyList = new SecProxyConnectCb(this, sslFactory, listener);
		realChannel.registerServerSocketChannel(proxyList);
	}
	

}
