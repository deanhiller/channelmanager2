package org.playorm.nio.test.nottested;

import org.playorm.nio.api.libs.AsynchSSLEngine;

public interface EnginesRunnable extends Runnable {

	public AsynchSSLEngine getEngine();
	
}
