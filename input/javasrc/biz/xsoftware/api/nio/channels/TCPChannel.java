package biz.xsoftware.api.nio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketException;

import biz.xsoftware.api.nio.handlers.ConnectionCallback;




/**
 * @author Dean Hiller
 */
public interface TCPChannel extends Channel {

    /**
     * This is an asychronous connect so your thread can go do other stuff while the connection is being
     * established.  The whole SSL handshake will even happen in an asychronous fashion so your threads
     * can do work while waiting for network operations.
     * 
     * @param remoteAddr The remote address to connect to.
     * @param cb The callback implementation that will be told when the Channel is finally connected
     * @throws IOException
     * @throws InterruptedException
     */
	public void connect(SocketAddress remoteAddr, ConnectionCallback cb) throws IOException, InterruptedException;
	
	public boolean getKeepAlive() throws SocketException;
	public void setKeepAlive(boolean b) throws SocketException;
}
