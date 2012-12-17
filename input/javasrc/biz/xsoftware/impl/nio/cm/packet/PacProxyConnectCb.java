package biz.xsoftware.impl.nio.cm.packet;


import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.handlers.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.ConnectionListener;

class PacProxyConnectCb implements ConnectionCallback {

	private TCPChannel packetChannel;
	private ConnectionListener cb;

	public PacProxyConnectCb(TCPChannel packetChannel, ConnectionListener cb) {
		this.packetChannel = packetChannel;
		this.cb = cb;
	}
	
	public void finished(Channel channel) throws IOException {
		cb.finished(packetChannel);
	}

	public void failed(RegisterableChannel channel, Throwable e) {
		cb.failed(packetChannel, e);
	}
}
