package biz.xsoftware.impl.nio.cm.secure;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLEngine;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.deprecated.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.libs.AsynchSSLEngine;
import biz.xsoftware.api.nio.libs.FactoryCreator;
import biz.xsoftware.api.nio.libs.SSLEngineFactory;

class SecProxyConnectCb implements ConnectionCallback {

	private static final Logger log = Logger.getLogger(SecProxyConnectCb.class.getName());
	private static final FactoryCreator CREATOR = FactoryCreator.createFactory(null);
	
	private SecTCPChannel channel;
	private ConnectionListener cb;
	private SSLEngineFactory sslFactory;
	private SecTCPServerChannel svrChannel;
	
	//called by TCPChannelImpl.connect method
	public SecProxyConnectCb(SecTCPChannel impl, SSLEngineFactory factory, ConnectionListener cb) {
		this.channel = impl;
		this.cb = cb;
		this.sslFactory = factory;
	}
	//called by TCPServerSocket
	public SecProxyConnectCb(SecTCPServerChannel impl, SSLEngineFactory factory, ConnectionListener cb) {
		svrChannel = impl;
		this.sslFactory = factory;
		this.cb = cb;
	}
	public void connected(Channel realChannel) throws IOException {
		if(log.isLoggable(Level.FINE))
			log.fine(realChannel+" Tcp connected, running handshake before fire connect");
		SecTCPChannel secureChannel = channel;
		SSLEngine sslEngine;
		try {
			if(svrChannel != null) {
				sslEngine = sslFactory.createEngineForServerSocket();			
				secureChannel = new SecTCPChannel((TCPChannel) realChannel);
			} else
				sslEngine = sslFactory.createEngineForSocket();
		} catch (GeneralSecurityException e) {
			IOException ioe = new IOException(realChannel+"Security error");
			ioe.initCause(e);
			throw ioe;
		}
		
		SecSSLListener connectProxy = secureChannel.getConnectProxy();

		AsynchSSLEngine handler = CREATOR.createSSLEngine(realChannel, sslEngine, null);
//		AsynchSSLEngine handler = new AsynchSSLEngineImpl(realChannel, sslEngine);
//		AsynchSSLEngine handler = new AsynchSSLEngineSynchronized(realChannel, sslEngine);
//		AsynchSSLEngine handler = new AsynchSSLEngineQueued()
		secureChannel.getReaderProxy().setHandler(handler);
		handler.setListener(secureChannel.getConnectProxy());
		
		connectProxy.setConnectCallback(cb);
		try {
			synchronized(secureChannel) {
				if(log.isLoggable(Level.FINEST))
					log.finest(realChannel+" about to register for reads");				
				if(!connectProxy.isClientRegistered()) {
					if(log.isLoggable(Level.FINEST))
						log.finest(realChannel+" register for reads");		
					realChannel.registerForReads(secureChannel.getReaderProxy());
				}
			}
		} catch (InterruptedException e) {
			log.log(Level.WARNING, realChannel+"Exception trying to accept connection", e);
			throw new RuntimeException(e);
		}

		handler.beginHandshake();
	}

	public void failed(RegisterableChannel channel, Throwable e) {
		if(channel != null)
			cb.failed(channel, e);
		else
			cb.failed(svrChannel, e);
	}

}
