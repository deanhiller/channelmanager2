package biz.xsoftware.api.nio.handlers;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;

public interface ConnectionListener {

	/*
	 * Only called when using 	TCPServerChannel.registerServerSocketChannel(final ConnectCallback cb)
	 * This is called before accepting the socket.
	 * 
	 * This method is not viable with a threadpool as the connected method may beat
	 * the aboutToAccept method call through the threadpool failing tests to expect
	 * them to be in a different order.  We could come up with a solution to this, but
	 * for now, aboutToAccept is not needed as there is not much info from a TCPServerchannel
	 * that one can gather, and it would be too late to do much about it anyways.
	 */
	//public void aboutToAccept(TCPServerChannel channel) throws IOException;
	
	public void connected(TCPChannel channel) throws IOException;
	
	public void connectFailed(RegisterableChannel channel, Throwable e);
}
