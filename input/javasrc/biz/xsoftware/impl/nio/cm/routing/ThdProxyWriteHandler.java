package biz.xsoftware.impl.nio.cm.routing;

import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.handlers.WriteCloseCallback;
import biz.xsoftware.api.nio.libs.ChannelsRunnable;

public class ThdProxyWriteHandler implements WriteCloseCallback {

	private static final Logger log = Logger.getLogger(ThdProxyWriteHandler.class.getName());
	
	private Channel channel;
	private WriteCloseCallback handler;
	private SpecialRoutingExecutor svc;

	public ThdProxyWriteHandler(Channel c, WriteCloseCallback h, SpecialRoutingExecutor svc2) {
		channel = c;
		handler = h;
		svc = svc2;
	}

	public void finished(Channel realChannel) {
        ChannelsRunnable r = new ChannelsRunnable() {
			public void run() {
				try {
					handler.finished(channel);
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

	public void failed(Channel c, final Throwable e) {
        ChannelsRunnable r = new ChannelsRunnable() {
			public void run() {
				try {
					handler.failed(channel, e);
				} catch (Exception e) {
					log.log(Level.WARNING, channel+"Exception", e);
				}				
			}
			public RegisterableChannel getChannel() {
				return channel;
			}
		};
		svc.execute(c, r);
	}

}
