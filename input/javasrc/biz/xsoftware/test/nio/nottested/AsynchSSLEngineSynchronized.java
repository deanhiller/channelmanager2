package biz.xsoftware.test.nio.nottested;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.net.ssl.SSLEngine;

import biz.xsoftware.api.nio.libs.SSLListener;
import biz.xsoftware.impl.nio.libs.AsynchSSLEngineImpl;

public class AsynchSSLEngineSynchronized extends AsynchSSLEngineImpl {
	
	public AsynchSSLEngineSynchronized(Object id, SSLEngine sslEngine) {
		super(id, sslEngine);
	}
	
	@Override
	public void setListener(SSLListener l) {
		super.setListener(l);
	}
	@Override
	public synchronized void beginHandshake() throws IOException {
		super.beginHandshake();
	}
	@Override
	protected synchronized void runRunnable(Runnable r) {
		super.runRunnable(r);
	}
	@Override
	public synchronized void feedPlainPacket(ByteBuffer b, Object passThrough) throws IOException {
		super.feedPlainPacket(b, passThrough);
	}
	@Override
	public synchronized void feedEncryptedPacket(ByteBuffer b) throws IOException {
		super.feedEncryptedPacket(b);
	}
	@Override
	public synchronized void initiateClose() {
		super.initiateClose();
	}
	@Override
	public synchronized void close() {
		super.close();
	}
	@Override
	public boolean isClosed() {
		return super.isClosed();
	}
	@Override
	public boolean isClosing() {
		return super.isClosing();
	}
	@Override
	public Object getId() {
		return super.getId();
	}
}
