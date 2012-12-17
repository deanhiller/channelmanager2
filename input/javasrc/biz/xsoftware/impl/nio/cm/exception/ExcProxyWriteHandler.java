package biz.xsoftware.impl.nio.cm.exception;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.handlers.OperationCallback;

public class ExcProxyWriteHandler implements OperationCallback {

	private static final Logger log = Logger.getLogger(ExcProxyWriteHandler.class.getName());
	private OperationCallback handler;
	private Channel channel;

	public ExcProxyWriteHandler(Channel c, OperationCallback h) {
		if(h == null)
			throw new IllegalArgumentException("Cannot use null writehandler");
		handler = h;
		channel = c;
	}

	public void finished(Channel realChannel) throws IOException {
		try {
			handler.finished(channel);
		} catch(Exception e) {
			log.log(Level.WARNING, channel+"Exception occurred", e);
		}
	}

	public void failed(RegisterableChannel realChannel, Throwable e) {
		try {
			handler.failed(channel, e);
		} catch(Exception ee) {
			log.log(Level.WARNING, channel+"Exception occurred", ee);
		}
	}

}
