/**
 * 
 */
package biz.xsoftware.impl.nio.util;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.WriteCloseCallback;

public class UtilWaitForCompletion implements WriteCloseCallback {

	private Throwable e;
	private boolean isFinished = false;
	private Channel channel;
	private Object thread;
	
	public UtilWaitForCompletion(Channel c, Object t) {
		channel = c;
		thread = t;
	}
	
	public synchronized void finished(Channel c, int id) {
		isFinished = true;
		this.notifyAll();
	}

	public synchronized void failed(Channel c, int id, Throwable e) {
		this.e = e;
		isFinished = true;
		this.notifyAll();
	}
	
	public synchronized void waitForComplete() throws IOException, InterruptedException {
		if(!isFinished)
			this.wait(30000);
		if(!isFinished)
			throw new RuntimeException(channel+"Failed to finish for 10 seconds.  current="+Thread.currentThread()+" selector="+thread);
		
		if(e != null) {
			if(e instanceof IOException)
				throw (IOException)e;
			else if(e instanceof RuntimeException)
				throw (RuntimeException)e;
			else
				throw new RuntimeException(e);
		}	
	}
}