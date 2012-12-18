package biz.xsoftware.impl.nio.util;


import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;

public class UtilProxyAcceptCb implements ConnectionListener {

	private TCPServerChannel channel;
	private ConnectionListener cb;

	public UtilProxyAcceptCb(TCPServerChannel c, ConnectionListener cb) {
		this.channel = c;
		this.cb = cb;
	}

	public void connected(Channel realChannel) throws IOException {
		UtilProxyTCPChannel newOne = new UtilProxyTCPChannel(realChannel);
		cb.connected(newOne);
	}

	public void failed(RegisterableChannel realChannel, Throwable e) {
		cb.failed(channel, e);
	}

}
