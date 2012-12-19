/**
 * 
 */
package org.playorm.nio.impl.util;

import java.io.IOException;

import org.playorm.nio.api.channels.Channel;
import org.playorm.nio.api.channels.RegisterableChannel;
import org.playorm.nio.api.deprecated.ConnectionCallback;


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