package biz.xsoftware.impl.nio.cm.basic.chanimpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;



/**
 */
public class SocketChannelImpl implements biz.xsoftware.api.nio.testutil.chanapi.SocketChannel{

    private SocketChannel channel;

    public SocketChannelImpl(SocketChannel c) {
        channel = c;
    }
    
    /**
     * @throws IOException 
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#configureBlocking(boolean)
     */
    public void configureBlocking(boolean b) throws IOException {
        channel.configureBlocking(b);
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#isBlocking()
     */
    public boolean isBlocking() {
        return channel.isBlocking();
    }

    /**
     * @throws IOException 
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#bind(java.net.SocketAddress)
     */
    public void bind(SocketAddress addr) throws IOException {
        channel.socket().bind(addr);
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#isBound()
     */
    public boolean isBound() {
        return channel.socket().isBound();
    }

    /**
     * @throws IOException 
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#write(java.nio.ByteBuffer)
     */
    public int write(ByteBuffer b) throws IOException {
        return channel.write(b);
    }

    /**
     * @throws IOException 
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#read(java.nio.ByteBuffer)
     */
    public int read(ByteBuffer b) throws IOException {
        return channel.read(b);
    }

    /**
     * @throws IOException 
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#close()
     */
    public void close() throws IOException {
        channel.socket().close();
        channel.close();        
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#isClosed()
     */
    public boolean isClosed()
    {
        return channel.socket().isClosed();
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#isConnected()
     */
    public boolean isConnected()
    {
        return channel.socket().isConnected();
    }

    /**
     * @throws IOException 
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#connect(java.net.SocketAddress)
     */
    public boolean connect(SocketAddress addr) throws IOException
    {
        return channel.connect(addr);
    }

    /**
     * @throws SocketException 
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#setReuseAddress(boolean)
     */
    public void setReuseAddress(boolean b) throws SocketException {
        channel.socket().setReuseAddress(b);
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#getInetAddress()
     */
    public InetAddress getInetAddress() {
        return channel.socket().getInetAddress();
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#getPort()
     */
    public int getPort() {
        return channel.socket().getPort();
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#getLocalAddress()
     */
    public InetAddress getLocalAddress() {
        return channel.socket().getLocalAddress();
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#getLocalPort()
     */
    public int getLocalPort() {
        return channel.socket().getLocalPort();
    }

    /**
     * @throws IOException 
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#finishConnect()
     */
    public void finishConnect() throws IOException {
        channel.finishConnect();
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.chanapi.SocketChannel#getSelectableChannel()
     */
    public java.nio.channels.SelectableChannel getSelectableChannel()
    {
        return channel;
    }

	public boolean getKeepAlive() throws SocketException {
		return channel.socket().getKeepAlive();
	}

	public void setKeepAlive(boolean b) throws SocketException {
		channel.socket().setKeepAlive(b);
	}

}
