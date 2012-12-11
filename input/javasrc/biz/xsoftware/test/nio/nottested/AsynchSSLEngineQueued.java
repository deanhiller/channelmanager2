package biz.xsoftware.test.nio.nottested;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLEngine;

import biz.xsoftware.api.nio.BufferHelper;
import biz.xsoftware.api.nio.ChannelServiceFactory;
import biz.xsoftware.api.nio.libs.AsynchSSLEngine;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.api.nio.libs.SSLListener;
import biz.xsoftware.impl.nio.libs.AsynchSSLEngineImpl;

public class AsynchSSLEngineQueued extends AsynchSSLEngineImpl {

	private static final Logger log = Logger.getLogger(AsynchSSLEngineQueued.class.getName());
	private static final BufferHelper HELPER = ChannelServiceFactory.bufferHelper(null);	
	private ExecutorService svc;
	private BufferFactory bufFactory;
	
	public AsynchSSLEngineQueued(Object id, SSLEngine engine, ExecutorService svc, BufferFactory bufFactory) {
		super(id, engine);
		if(svc == null || bufFactory == null)
			throw new IllegalArgumentException("No argument to this constructor can be null");
		this.svc = svc;
		this.bufFactory = bufFactory;
	}
	@Override
	public void setListener(SSLListener l) {
		super.setListener(l);
	}
	@Override
	public void beginHandshake() throws IOException {
		Runnable r = new EnginesRunnable() {
			public void run() {
				try {
					AsynchSSLEngineQueued.super.beginHandshake();
				} catch (Exception e) {
					log.log(Level.WARNING, AsynchSSLEngineQueued.this+"Exception", e);
				}				
			}
			public AsynchSSLEngine getEngine() {
				return AsynchSSLEngineQueued.this;
			}
		};
		svc.execute(r);
	}
	
	@Override
	protected void scheduleRunnable(Runnable sslRun, boolean isInitialHandshake) {
		svc.execute(sslRun);
	}
	@Override
	public void feedPlainPacket(ByteBuffer b, Object passThrough) throws IOException {
		//copy ByteBuffer here....
		int length = b.remaining();
		final ByteBuffer newBuffer = bufFactory.createBuffer(this, length);
		newBuffer.put(b);		
		Runnable r = new EnginesRunnable() {
			public void run() {
				try {
					HELPER.doneFillingBuffer(newBuffer);
					AsynchSSLEngineQueued.super.feedPlainPacket(newBuffer, null);					
				} catch (Exception e) {
					log.log(Level.WARNING, AsynchSSLEngineQueued.this+"Exception", e);
				}				
			}
			public AsynchSSLEngine getEngine() {
				return AsynchSSLEngineQueued.this;
			}
		};
		svc.execute(r);		

	}
	@Override
	public void feedEncryptedPacket(ByteBuffer b) throws IOException {
		//copy ByteBuffer here....
		int length = b.remaining();
		final ByteBuffer newBuffer = bufFactory.createBuffer(this, length);
		newBuffer.put(b);		
		Runnable r = new EnginesRunnable() {
			public void run() {
				try {
					HELPER.doneFillingBuffer(newBuffer);
					AsynchSSLEngineQueued.super.feedEncryptedPacket(newBuffer);
				} catch (Exception e) {
					log.log(Level.WARNING, AsynchSSLEngineQueued.this+"Exception", e);
				}				
			}
			public AsynchSSLEngine getEngine() {
				return AsynchSSLEngineQueued.this;
			}
		};
		svc.execute(r);
	}
	@Override
	public void initiateClose() {
		Runnable r = new EnginesRunnable() {
			public void run() {
				try {
					AsynchSSLEngineQueued.super.initiateClose();				
				} catch (Exception e) {
					log.log(Level.WARNING, AsynchSSLEngineQueued.this+"Exception", e);
				}				
			}
			public AsynchSSLEngine getEngine() {
				return AsynchSSLEngineQueued.this;
			}
		};
		svc.execute(r);			
	}
	@Override
	public void close() {
		Runnable r = new EnginesRunnable() {
			public void run() {
				try {
					AsynchSSLEngineQueued.super.close();				
				} catch (Exception e) {
					log.log(Level.WARNING, AsynchSSLEngineQueued.this+"Exception", e);
				}				
			}
			public AsynchSSLEngine getEngine() {
				return AsynchSSLEngineQueued.this;
			}
		};
		svc.execute(r);
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
