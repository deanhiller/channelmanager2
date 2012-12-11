package biz.xsoftware.impl.nio.cm.threaded;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.handlers.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.libs.ChannelsRunnable;

class ThdProxyConnectCb implements ConnectionCallback {

	private static final Logger log = Logger.getLogger(ThdProxyConnectCb.class.getName());
	
	private TCPChannel channel;
	private ConnectionListener cb;
	private Executor svc;

	public ThdProxyConnectCb(TCPChannel channel, ConnectionListener cb, Executor svc) {
		this.channel = channel;
		this.cb = cb;
		this.svc = svc;
	}
	
	public void connected(TCPChannel realChannel) throws IOException {
        ChannelsRunnable r = new ChannelsRunnable() {
			public void run() {
				try {
					cb.connected(channel);
				} catch (Exception e) {
					log.log(Level.WARNING, channel+"Exception", e);
				}				
			}
			public RegisterableChannel getChannel() {
				return channel;
			}
		};
		svc.execute(r);
	}

	public void connectFailed(RegisterableChannel realChannel, final Throwable e) {
        ChannelsRunnable r = new ChannelsRunnable() {
			public void run() {
				try {
					cb.connectFailed(channel, e);
				} catch (Exception e) {
					log.log(Level.WARNING, channel+"Exception", e);
				}				
			}
			public RegisterableChannel getChannel() {
				return channel;
			}
		};
		svc.execute(r);		
	}
}
