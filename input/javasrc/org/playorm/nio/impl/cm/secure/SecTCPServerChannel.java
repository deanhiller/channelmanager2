package org.playorm.nio.impl.cm.secure;

import java.io.IOException;

import javax.net.ssl.SSLEngine;

import org.playorm.nio.api.channels.RegisterableChannel;
import org.playorm.nio.api.channels.TCPChannel;
import org.playorm.nio.api.channels.TCPServerChannel;
import org.playorm.nio.api.handlers.ConnectionListener;
import org.playorm.nio.api.handlers.FutureOperation;
import org.playorm.nio.api.libs.SSLEngineFactory;
import org.playorm.nio.impl.util.UtilRegisterable;


class SecTCPServerChannel extends UtilRegisterable implements TCPServerChannel {

	private TCPServerChannel realChannel;
	private SSLEngineFactory sslFactory;
	
	public SecTCPServerChannel(TCPServerChannel c, SSLEngineFactory sslFactory) {
		super(c);
		realChannel = c;
		this.sslFactory = sslFactory;
	}
		
	
	@Override
	protected TCPServerChannel getRealChannel() {
		return (TCPServerChannel) super.getRealChannel();
	}


	public void oldClose() {
		realChannel.oldClose();
	}
	
	public void registerServerSocketChannel(ConnectionListener listener) throws IOException, InterruptedException {
		SecProxyConnectCb proxyList = new SecProxyConnectCb(this, sslFactory, listener);
		realChannel.registerServerSocketChannel(proxyList);
	}
	
}