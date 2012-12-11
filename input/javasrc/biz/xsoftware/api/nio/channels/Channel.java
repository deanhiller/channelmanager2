package biz.xsoftware.api.nio.channels;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.WriteCloseCallback;
import biz.xsoftware.api.nio.libs.ChannelSession;

/**
 * @author Dean Hiller
 */
public interface Channel extends RegisterableChannel {
    /**
     * A synchronous blocking write.  This blocks until the whole
     * ByteBuffer has been written out, or the write fails.
     * 
     * @param b
     */
    public int write(ByteBuffer b) throws IOException;
    
    /**
     * An asynchronous non-blocking write.  You will be notified from
     * a different thread when the write is finished or failed.
     * 
     * @param b
     * @param h
     * @throws IOException 
     * @throws InterruptedException 
     */
    public void write(ByteBuffer b, WriteCloseCallback h, int id) throws IOException, InterruptedException;
    
    /**
     * This is synchronous/blocking for TCP and therefore not too scalable.  Use at
     * your own risk.  We advise using the TCPChannel.connect method instead.
     * For UDP, it is not blocking.
     * 
     * @param addr
     */
    public void connect(SocketAddress addr) throws IOException; 
    
    /**
     * Asynchronous close where the WriteCloseHandler will be notified once
     * the close is completed.
     * 
     * @param cb The callback that is notified of the completion or failure of the write.
     */
    public void close(WriteCloseCallback cb, int id);

    /**
     * Gets the remote address the channel is communicating with.
     * 
     * @return the remote address the channel is communicating with.
     */
    public InetSocketAddress getRemoteAddress();
    
    /**
     * Returns whether or not the channel is connected.
     * @return whether or not the channel is connected.
     */
    public boolean isConnected();    
    
    /**
     * Registers a DataListener that will be notified of all incoming data.  If the threadpool layer setup,
     * requests from clients may come out of order unless you install your own executorService.
     * 
     */
    public void registerForReads(DataListener listener) throws IOException, InterruptedException;

    /**
     * Unregister the previously registered DataListener so incoming data is not fired to the client.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void unregisterForReads() throws IOException, InterruptedException;
    
    /**
     * Each Channel is associated with a client.  This method gets that client's Session object so
     * you can store client specific session stuff all in one place.
     * 
     * @return client's Session object
     */
    public ChannelSession getSession();
}
