package biz.xsoftware.impl.nio.cm.secure;

import java.io.IOException;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.DataChunk;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.libs.AsynchSSLEngine;
import biz.xsoftware.api.nio.libs.PacketAction;

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

	public void incomingData(Channel c, DataChunk chunk) throws IOException {
		ByteBuffer b = chunk.getData();
		if(!isClosed) {
			PacketAction action = handler.feedEncryptedPacket(b, chunk);
			if(action == PacketAction.NOT_ENOUGH_BYTES_YET) {
				chunk.setProcessed(); //trigger another read from socket
			}
		} else {
			b.position(b.limit()); //if closed, read the data so we don't get warnings
			chunk.setProcessed();
		}
		
		chunk.releaseBuffer();
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
