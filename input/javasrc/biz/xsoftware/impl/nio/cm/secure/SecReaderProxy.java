package biz.xsoftware.impl.nio.cm.secure;

import java.io.IOException;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.libs.AsynchSSLEngine;

class SecReaderProxy implements DataListener {
	
	private AsynchSSLEngine handler;
	private SecSSLListener sslListener;
	private ByteBuffer data = ByteBuffer.allocate(2000);
	private boolean isClosed;
	
	public SecReaderProxy(SecSSLListener sslListener) {
		this.sslListener = sslListener;
	}
	
	public ByteBuffer getBuffer(Channel c) {
		return data;
	}

	public void incomingData(Channel c, ByteBuffer b) throws IOException {
		if(!isClosed) {
			handler.feedEncryptedPacket(b);
		} else
			b.position(b.limit()); //if closed, read the data so we don't get warnings
	}
	
	public void farEndClosed(Channel c) {
		handler.close();
		sslListener.farEndClosed();
	}

	public void setHandler(AsynchSSLEngine handler) {
		this.handler = handler;
	}

	public AsynchSSLEngine getHandler() {
		return handler;
	}

	public void close() {
		isClosed = true;
		if(handler != null)
			handler.close();
	}

	public void failure(Channel c, ByteBuffer data, Exception e) {
		try {
			sslListener.feedProblemThrough(c, data, e);
		} catch (IOException e1) {
			RuntimeException exc = new RuntimeException(e1.getMessage(), e1);
			throw exc;
		}
	}

	
}
