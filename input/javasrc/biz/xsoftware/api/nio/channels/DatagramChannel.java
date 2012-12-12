package biz.xsoftware.api.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.handlers.DatagramListener;
import biz.xsoftware.api.nio.libs.ChannelSession;

/**
 */
public interface DatagramChannel extends RegisterableChannel
{
    public void registerForReads(DatagramListener listener) throws IOException, InterruptedException;
    
    public void unregisterForReads() throws IOException, InterruptedException;
    
    public ChannelSession getSession();    
    
    public void oldWrite(SocketAddress addr, ByteBuffer b) throws IOException;
}
