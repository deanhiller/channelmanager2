package biz.xsoftware.impl.nio.cm.readreg;


import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;

class RegProxyAcceptCb implements ConnectionListener {

	private TCPServerChannel svrChannel;
	private ConnectionListener cb;

	public RegProxyAcceptCb(TCPServerChannel svrChannel, ConnectionListener cb) {
		this.svrChannel = svrChannel;
		this.cb = cb;
	}
	
	public void connected(Channel channel) throws IOException {
		TCPChannel newChannel = new RegTCPChannel((TCPChannel) channel);
		cb.connected(newChannel);		
	}

	public void failed(RegisterableChannel channel, Throwable e) {
		cb.failed(svrChannel, e);
	}
}
