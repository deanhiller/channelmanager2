package biz.xsoftware.api.nio.libs;

import java.io.IOException;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.handlers.PacketListener;

public interface PacketProcessor {
	
	public ByteBuffer processOutgoing(ByteBuffer b);
	
	/**
	 * processIncoming reads from the ByteBuffer
	 * @param b
	 * @throws IOException 
	 */
	public void incomingData(ByteBuffer b) throws IOException;
	
	public void setPacketListener(PacketListener l);
	
}
