package biz.xsoftware.impl.nio.cm.secure;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.WriteCloseCallback;

public class SecProxyWriteHandler implements WriteCloseCallback {

	private WriteCloseCallback handler;
	private Channel channel;

	public SecProxyWriteHandler(Channel c, WriteCloseCallback h) {
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
