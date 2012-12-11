package biz.xsoftware.api.nio.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface PacketListener {

	/**
	 * Should pass back a ready to read buffer
	 * 
	 * @param b
	 * @throws IOException 
	 */
	public void incomingPacket(ByteBuffer b) throws IOException;
}
