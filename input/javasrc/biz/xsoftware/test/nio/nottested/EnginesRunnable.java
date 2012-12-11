package biz.xsoftware.test.nio.nottested;

import biz.xsoftware.api.nio.libs.AsynchSSLEngine;

public interface EnginesRunnable extends Runnable {

	public AsynchSSLEngine getEngine();
	
}
