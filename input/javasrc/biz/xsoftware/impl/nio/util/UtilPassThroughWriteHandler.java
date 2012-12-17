package biz.xsoftware.impl.nio.util;


import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.handlers.OperationCallback;

public class UtilPassThroughWriteHandler implements OperationCallback {

	private Channel channel;
	private OperationCallback handler;

	public UtilPassThroughWriteHandler(Channel c, OperationCallback h) {
		if(c == null || h == null)
			throw new IllegalArgumentException(c+"Niether c nor h parameters can be null");
		channel = c;
		handler = h;
	}
	public void finished(Channel realChannel) throws IOException {
		handler.finished(channel);
	}

	public void failed(RegisterableChannel realChannel, Throwable e) {
		handler.failed(channel, e);
	}

}
