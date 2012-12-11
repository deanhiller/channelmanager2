package biz.xsoftware.impl.nio.util;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.handlers.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.ConnectionListener;

public class UtilProxyConnectCb implements ConnectionCallback {

	//private static final Logger log = Logger.getLogger(ProxyConnectCb.class.getName());
	private TCPChannel channel;
	private ConnectionListener cb;

	public UtilProxyConnectCb(TCPChannel c, ConnectionListener cb) {
		this.channel = c;
		this.cb = cb;
	}

	public void connected(TCPChannel realChannel) throws IOException {
		cb.connected(channel);
	}

	public void connectFailed(RegisterableChannel realChannel, Throwable e) {
		cb.connectFailed(channel, e);
	}

}
