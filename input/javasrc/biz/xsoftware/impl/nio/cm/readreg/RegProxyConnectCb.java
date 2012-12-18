package biz.xsoftware.impl.nio.cm.readreg;


import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.deprecated.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.ConnectionListener;

class RegProxyConnectCb implements ConnectionCallback {

	private TCPChannel regChannel;
	private ConnectionListener cb;

	public RegProxyConnectCb(TCPChannel regChan, ConnectionListener cb) {
		this.regChannel = regChan;
		this.cb = cb;
	}
	
	public void connected(Channel channel) throws IOException {
		cb.connected(regChannel);
	}

	public void failed(RegisterableChannel channel, Throwable e) {
		cb.failed(regChannel, e);
	}
}
