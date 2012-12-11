package biz.xsoftware.impl.nio.cm.threaded;

import java.io.IOException;
import java.util.concurrent.Executor;
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
	private Executor svc;
	private BufferFactory bufFactory;
	
	public ThdProxyAcceptCb(TCPServerChannel svrChannel, ConnectionListener cb, Executor svc, BufferFactory bufFactory) {
		this.svrChannel = svrChannel;
		this.cb = cb;
		this.svc = svc;
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
		svc.execute(r);			
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
		svc.execute(r);			
	}
}
