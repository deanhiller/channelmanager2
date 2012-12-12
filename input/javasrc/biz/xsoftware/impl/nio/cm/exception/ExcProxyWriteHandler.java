package biz.xsoftware.impl.nio.cm.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.WriteCloseCallback;

public class ExcProxyWriteHandler implements WriteCloseCallback {

	private static final Logger log = Logger.getLogger(ExcProxyWriteHandler.class.getName());
	private WriteCloseCallback handler;
	private Channel channel;

	public ExcProxyWriteHandler(Channel c, WriteCloseCallback h) {
		if(h == null)
			throw new IllegalArgumentException("Cannot use null writehandler");
		handler = h;
		channel = c;
	}

	public void finished(Channel realChannel) {
		try {
			handler.finished(channel);
		} catch(Exception e) {
			log.log(Level.WARNING, channel+"Exception occurred", e);
		}
	}

	public void failed(Channel realChannel, Throwable e) {
		try {
			handler.failed(channel, e);
		} catch(Exception ee) {
			log.log(Level.WARNING, channel+"Exception occurred", ee);
		}
	}

}
