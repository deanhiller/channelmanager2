package biz.xsoftware.impl.nio.cm.routing;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.api.nio.libs.ChannelsRunnable;

class ThdProxyAcceptCb implements ConnectionListener {

	private static final Logger log = Logger.getLogger(ThdProxyAcceptCb.class.getName());
	
	private TCPServerChannel svrChannel;
	private ConnectionListener cb;
	private SpecialRoutingExecutor svc;
	private BufferFactory bufFactory;
	
	public ThdProxyAcceptCb(TCPServerChannel svrChannel, ConnectionListener cb, SpecialRoutingExecutor svc2, BufferFactory bufFactory) {
		this.svrChannel = svrChannel;
		this.cb = cb;
		this.svc = svc2;
		this.bufFactory = bufFactory;
	}
	
	public void connected(final TCPChannel channel) throws IOException {
		ChannelsRunnable r = new ChannelsRunnable() {
			public void run() {
				try {
					TCPChannel newChannel = new ThdTCPChannel(channel, svc, bufFactory);
					cb.connected(newChannel);
				} catch (Exception e) {
					log.log(Level.WARNING, channel+"Exception", e);
				}				
			}
			public RegisterableChannel getChannel() {
				return svrChannel;
			}			
		};
		svc.execute(channel, r);			
	}

	public void connectFailed(RegisterableChannel channel, final Throwable e) {
        ChannelsRunnable r = new ChannelsRunnable() {
			public void run() {
				try {
					cb.connectFailed(svrChannel, e);
				} catch (Exception e) {
					log.log(Level.WARNING, svrChannel+"Exception", e);
				}				
			}
			public RegisterableChannel getChannel() {
				return svrChannel;
			}			
		};
		svc.execute(null, r);			
	}
}
