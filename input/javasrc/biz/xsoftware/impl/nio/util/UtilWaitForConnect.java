/**
 * 
 */
package biz.xsoftware.impl.nio.util;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.deprecated.ConnectionCallback;

public class UtilWaitForConnect implements ConnectionCallback {

	private Throwable e;
	private boolean isFinished = false;
	
	public synchronized void connected(Channel channels) throws IOException {
		isFinished = true;
		this.notifyAll();
	}

	public synchronized void failed(RegisterableChannel channel, Throwable e) {
		this.e = e;
		isFinished = true;
		this.notifyAll();
	}
	
	public synchronized void waitForConnect() throws IOException, InterruptedException {
		if(!isFinished)
			this.wait();

		if(e != null) {
			if(e instanceof IOException) {
				IOException exc = new IOException(e.getMessage(), e);
				throw (IOException)exc;
			} else
				throw new RuntimeException(e.getMessage(), e);
		}	
	}
}