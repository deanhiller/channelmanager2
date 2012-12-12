package biz.xsoftware.impl.nio.util;


import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.WriteCloseCallback;

public class UtilPassThroughWriteHandler implements WriteCloseCallback {

	private Channel channel;
	private WriteCloseCallback handler;

	public UtilPassThroughWriteHandler(Channel c, WriteCloseCallback h) {
		if(c == null || h == null)
			throw new IllegalArgumentException(c+"Niether c nor h parameters can be null");
		channel = c;
		handler = h;
	}
	public void finished(Channel realChannel) {
		handler.finished(channel);
	}

	public void failed(Channel realChannel, Throwable e) {
		handler.failed(channel, e);
	}

}
