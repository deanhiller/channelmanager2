package biz.xsoftware.api.nio.libs;

import java.io.IOException;
import java.nio.ByteBuffer;


public interface SSLListener {

	void encryptedLinkEstablished() throws IOException;

	//real channel operations...
	void packetEncrypted(ByteBuffer engineToSocketData, Object passThrough) throws IOException;

	void packetUnencrypted(ByteBuffer out) throws IOException;
	
	/**
	 * This is passed to the SSLListener for two reasons
	 * <ol>
	 *   <li>Cleaner design so client can decide Threadpooling mechanism
	 *   <li>Better deterministic testing of AsynchSSLEngine to eliminate threading problems
	 * </ol>
	 * isInitialHandshake is passed because if isInitialHandshake is false, the client
	 * cannot feed any more data to the SSLEngine until the Runnable is run.  This is 
	 * because the Runnable may be responsible for changing cipher suites and so the data just
	 * after the handshake packet causing this runnable can't be decrypted until it is
	 * done.
	 * @param r
	 * @param isInitialHandshake - 
	 */
	void runTask(Runnable r, boolean isInitialHandshake);

	/**
	 * Called when the engine is closed after initiating a close
	 *
	 * @param clientInitiated true if client called close or initateClose.  false
	 * if closed was caused by far end sending close handshake message.
	 */
	void closed(boolean clientInitiated);
}