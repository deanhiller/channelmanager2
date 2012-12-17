package biz.xsoftware.impl.nio.cm.basic;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.FutureOperation;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.handlers.OperationCallback;

public class FutureConnectImpl implements FutureOperation, ConnectionListener, OperationCallback {

	private RegisterableChannel channel;
	private Throwable e;

	@Override
	public void finished(Channel channel) throws IOException {
		this.channel = channel;
	}

	@Override
	public void failed(RegisterableChannel channel, Throwable e) {
		this.channel = channel;
		this.e = e;
	}
	
	public void waitConnect(long timeoutMillis) {
		throw new UnsupportedOperationException("quick to implement, let me know");
	}
	
	public void waitConnect() {
		
	}

}
