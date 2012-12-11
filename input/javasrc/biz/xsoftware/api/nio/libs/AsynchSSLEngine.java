package biz.xsoftware.api.nio.libs;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * AsynchSSLEngine is designed to be asynchronous because a client
 * may feed a plain packet in and SSLListener.packetEncrypted may be 
 * called twice and between each call the client must read all the data
 * from that buffer so it can be reused to be filled in with the next packet.
 * This can be due to handshaking and other things in the system....sometimes
 * when you feed a packet in nothing comes out on the listener side.  If
 * this was made to be synchronous there would have to be alot more code in the
 * client with if...else then logic on what to do next...send the packet up,
 * down, etc....can only tell with if else....also you would have to check
 * status and see if you keep needing to get the next buffer from the engine.
 * 
 * @author dean.hiller
 *
 */
public interface AsynchSSLEngine {
	
	void setListener(SSLListener connectProxy);
	
	/**
	 * Initiates handshaking (initial or renegotiation) on this AsynchSSLEngine
	 */
	void beginHandshake() throws IOException;
	
	/**
     * I believe calling this method will only result in SSLListener.packetEncrypted being called
     * unlike feedEncryptedPacket method.  Just like the SSLEngine.wrap method, 
     * this method is NOT thread safe!  It should not be called from two different threads.
     * It is also NOT thread safe with beginHandshake.  It should not be called in one
     * thread while beginHandshake is called in another(for rehanshaking purposes).
     * 
	 * @param b
	 * @param passThrough Object that is passed through to SSLListener.packetEncrypted 
	 * @throws IOException
	 */
	void feedPlainPacket(ByteBuffer b, Object passThrough) throws IOException;

	/**
	 * Feeding an encrypted packet results in one of three possible methods on SSLListener being called
	 * <ol>
	 *   <li>packetEncrypted - this is a handshake response message
	 *   <li>packetUnencrypted - this is a message that has been unencrypted and is ready for consumption
	 *   <li>encryptionLinkEstablished - the last handshake message was received and handshake is complete
	 * </ol>
	 * Like SSLEngine.unwrap, this method is not thread safe and should not be called from
	 * multiple threads.
	 * 
	 * @param b
	 */
	public void feedEncryptedPacket(ByteBuffer b) throws IOException;
	
	/**
	 * Calling close results in SSLListener.feedEncryptedPacket being called and then waiting
	 * for a message from far end that it has been closed.  If this does not work, 
	 * call close.
	 * 
	 */
	void initiateClose();

	/**
	 * This closes the engine right away.  It results in sending one last close
	 * handshake message out(packetEncrypted) which should be sent to peer.  Then it
	 * closes the entire SSLEngine and returns immediately.  No need to wait for
	 * peer to respond with closed handshake message as in initiateClose method.
	 * 
	 */
	void close();
	
	boolean isClosed();
	
	boolean isClosing();
	
	Object getId();
}
