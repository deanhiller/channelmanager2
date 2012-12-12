package biz.xsoftware.api.nio.channels;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.OperationCallback;
import biz.xsoftware.api.nio.libs.ChannelSession;

/**
 * @author Dean Hiller
 */
public interface Channel extends RegisterableChannel {
    /**
     * Use these two lines of code instead
     * 
     * FutureListener future = channel.write(b);
     * future.wait();
     * 
     * @param b
     */
	@Deprecated
    public int write(ByteBuffer b) throws IOException;
    
    /**
     * Use these two lines of code instead
     * FutureListener future = channel.write(b);
     * future.setSingleCallback(h); //callback called immediate if write happened between this line and the last line!!
     * or the callback is called later after the write occurs.
     * 
     * @param b
     * @param h
     * @throws IOException 
     * @throws InterruptedException 
     */
    public void write(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException;
    
    /**
     * This is synchronous/blocking for TCP and therefore not too scalable.  Use at
     * your own risk.  We advise using the TCPChannel.connect method instead.
     * For UDP, it is not blocking.
     * 
     * @param addr
     */
    @Deprecated
    public void connect(SocketAddress addr) throws IOException; 

    /**
     * Asynchronous close where the WriteCloseHandler will be notified once
     * the close is completed.
     * 
     * @param cb The callback that is notified of the completion or failure of the write.
     */
    @Deprecated
    public void close(OperationCallback cb);
    
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
     * Each Channel has a ChannelSession where you can store state.  IF you have one client per Socket, then you can
     * easily store client state in the Channel itself so instead of passing around a Session in your code, you can pass
     * around a Channel that has a ChannelSession. 
     * 
     * @return client's Session object
     */
    public ChannelSession getSession();
}
