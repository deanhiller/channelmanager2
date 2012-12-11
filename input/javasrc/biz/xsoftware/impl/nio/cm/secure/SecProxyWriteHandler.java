package biz.xsoftware.impl.nio.cm.secure;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.WriteCloseCallback;

public class SecProxyWriteHandler implements WriteCloseCallback {

	private WriteCloseCallback handler;
	private int id;
	private Channel channel;

	public SecProxyWriteHandler(Channel c, WriteCloseCallback h, int id) {
		channel = c;
		handler = h;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void finished(Channel realChannel, int wrong) {
		handler.finished(channel, id);
	}

	public void failed(Channel realChannel, int wrong, Throwable e) {
		handler.failed(channel, id, e);
	}

}
