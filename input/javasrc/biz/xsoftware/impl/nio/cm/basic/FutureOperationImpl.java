package biz.xsoftware.impl.nio.cm.basic;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.handlers.FutureOperation;
import biz.xsoftware.api.nio.handlers.OperationCallback;

public class FutureOperationImpl implements FutureOperation, OperationCallback {

	private RegisterableChannel channel;
	private Throwable e;
	private OperationCallback operationCallback;

	@Override
	public synchronized void finished(Channel channel) throws IOException {
		this.channel = channel;
		this.notify();
		operationCallback.finished(channel);
	}

	@Override
	public synchronized void failed(RegisterableChannel channel, Throwable e) {
		this.channel = channel;
		this.e = e;
		this.notify();
		operationCallback.failed(channel, e);
	}

	@Override
	public synchronized void waitForOperation(long timeoutInMillis) throws InterruptedException {
		if(channel != null) {
			if(e != null)
				throw new RuntimeException(e);
			return;
		}
		
		if(timeoutInMillis > 0) {
			this.wait(timeoutInMillis);
		} else
			this.wait();
	}

	@Override
	public synchronized void waitForOperation() throws InterruptedException {
		waitForOperation(0);
	}

	@Override
	public synchronized void setListener(OperationCallback cb) {
		if(channel != null) {
			if(e != null) {
				cb.failed(channel, e);
			} else
				fireFinished(cb);
			return;
		}
		operationCallback = cb;
	}

	private void fireFinished(OperationCallback cb) {
		try {
			cb.finished((Channel) channel);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
