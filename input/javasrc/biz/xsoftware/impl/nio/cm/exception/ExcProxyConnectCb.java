package biz.xsoftware.impl.nio.cm.exception;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.handlers.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.ConnectionListener;

class ExcProxyConnectCb implements ConnectionCallback {

	private static final Logger log = Logger.getLogger(ExcProxyConnectCb.class.getName());
	
	private TCPChannel proxyChannel;
	private ConnectionListener cb;

	public ExcProxyConnectCb(TCPChannel channel, ConnectionListener cb) {
		this.proxyChannel = channel;
		this.cb = cb;
	}
	
	public void finished(Channel channel) throws IOException {
		try {
			cb.finished(proxyChannel);
		} catch(Exception e) {
			log.log(Level.WARNING, channel+"Exception", e);
		}
	}

	public void failed(RegisterableChannel channel, Throwable e) {
		try {
			cb.failed(proxyChannel, e);
		} catch(Exception ee) {
			log.log(Level.WARNING, channel+"Exception", ee);
		}
	}
}
