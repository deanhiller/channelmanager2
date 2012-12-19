package org.playorm.nio.api.testutil;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import org.playorm.nio.api.libs.SSLEngineFactory;


public class MockSSLEngineFactory implements SSLEngineFactory {

	//private static final Logger log = Logger.getLogger(MockSSLEngineFactory.class.getName());
	
	private String clientKeystore = "input/testinput/client.keystore";
	private String serverKeystore = "input/testinput/server.keystore";
	private	String password = "root01";
	
	public SSLEngine createEngineForServerSocket()  throws GeneralSecurityException, IOException {
		// Create/initialize the SSLContext with key material

		char[] passphrase = password.toCharArray();
		// First initialize the key and trust material.
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(serverKeystore), passphrase);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		
		//****************Server side specific*********************
		// KeyManager's decide which key material to use.
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, passphrase);
		sslContext.init(kmf.getKeyManagers(), null, null);		
		//****************Server side specific*********************
		
		SSLEngine engine = sslContext.createSSLEngine();
		engine.setUseClientMode(false);
		
		return engine;
	}

	public SSLEngine createEngineForSocket() throws GeneralSecurityException, IOException {
		// Create/initialize the SSLContext with key material
		char[] passphrase = password.toCharArray();
		// First initialize the key and trust material.
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(clientKeystore), passphrase);
		SSLContext sslContext = SSLContext.getInstance("TLS");

		//****************Client side specific*********************
		// TrustManager's decide whether to allow connections.
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);
		sslContext.init(null, tmf.getTrustManagers(), null);		
		//****************Client side specific*********************
		
		SSLEngine engine = sslContext.createSSLEngine();
		engine.setUseClientMode(true);
		
		return engine;
	}

}
