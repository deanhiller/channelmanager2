package biz.xsoftware.impl.nio.cm.routing;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.handlers.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.libs.ChannelsRunnable;

class ThdProxyConnectCb implements ConnectionCallback {

	private static final Logger log = Logger.getLogger(ThdProxyConnectCb.class.getName());
	
	private TCPChannel channel;
	private ConnectionListener cb;
	private SpecialRoutingExecutor svc;

	public ThdProxyConnectCb(TCPChannel channel, ConnectionListener cb, SpecialRoutingExecutor svc2) {
		this.channel = channel;
		this.cb = cb;
		this.svc = svc2;
	}
	
	public void finished(Channel realChannel) throws IOException {
        ChannelsRunnable r = new ChannelsRunnable() {
			public void run() {
				try {
					cb.finished(channel);
				} catch (Exception e) {
					log.log(Level.WARNING, channel+"Exception", e);
				}				
			}
			public RegisterableChannel getChannel() {
				return channel;
			}
		};
		svc.execute(realChannel, r);
	}

	public void failed(RegisterableChannel realChannel, final Throwable e) {
        ChannelsRunnable r = new ChannelsRunnable() {
			public void run() {
				try {
					cb.failed(channel, e);
				} catch (Exception e) {
					log.log(Level.WARNING, channel+"Exception", e);
				}				
			}
			public RegisterableChannel getChannel() {
				return channel;
			}
		};
		svc.execute(null, r);		
	}
}
