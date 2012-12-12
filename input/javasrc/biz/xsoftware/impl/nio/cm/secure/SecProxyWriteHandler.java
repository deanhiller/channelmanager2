package biz.xsoftware.impl.nio.cm.secure;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.OperationCallback;

public class SecProxyWriteHandler implements OperationCallback {

	private OperationCallback handler;
	private Channel channel;

	public SecProxyWriteHandler(Channel c, OperationCallback h) {
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
