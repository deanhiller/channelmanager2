package biz.xsoftware.impl.nio.cm.routing;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
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
	
	public void finished(final Channel channel) throws IOException {
		ChannelsRunnable r = new ChannelsRunnable() {
			public void run() {
				try {
					TCPChannel newChannel = new ThdTCPChannel((TCPChannel) channel, svc, bufFactory);
					cb.finished(newChannel);
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

	public void failed(RegisterableChannel channel, final Throwable e) {
        ChannelsRunnable r = new ChannelsRunnable() {
			public void run() {
				try {
					cb.failed(svrChannel, e);
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
