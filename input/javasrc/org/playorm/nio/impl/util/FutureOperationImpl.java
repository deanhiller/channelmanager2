package org.playorm.nio.impl.util;

import java.io.IOException;

import org.playorm.nio.api.channels.Channel;
import org.playorm.nio.api.channels.RegisterableChannel;
import org.playorm.nio.api.handlers.FutureOperation;
import org.playorm.nio.api.handlers.OperationCallback;
import org.playorm.nio.api.handlers.TimeoutException;


public class FutureOperationImpl implements FutureOperation, OperationCallback {

	private RegisterableChannel channel;
	private Throwable e;
	private OperationCallback operationCallback;

	@Override
	public synchronized void finished(Channel channel) throws IOException {
		this.channel = channel;
		this.notify();
		if(operationCallback != null)
			operationCallback.finished(channel);
	}

	@Override
	public synchronized void failed(RegisterableChannel channel, Throwable e) {
		this.channel = channel;
		this.e = e;
		this.notify();
		if(operationCallback != null)
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
		
		if(channel == null)
			throw new TimeoutException("Waited for operation for time="+timeoutInMillis+" but did not complete");
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
