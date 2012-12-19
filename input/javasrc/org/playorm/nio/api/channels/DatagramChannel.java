package org.playorm.nio.api.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.playorm.nio.api.handlers.DatagramListener;
import org.playorm.nio.api.libs.ChannelSession;


/**
 */
public interface DatagramChannel extends RegisterableChannel
{
    public void registerForReads(DatagramListener listener) throws IOException, InterruptedException;
    
    public void unregisterForReads() throws IOException, InterruptedException;
    
    public ChannelSession getSession();    
    
    public void oldWrite(SocketAddress addr, ByteBuffer b) throws IOException;
    
	/**
	 * Closes and unregisters the channel if registered from the ChannelManager
	 */
	public void close();
}
