package biz.xsoftware.impl.nio.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.FutureOperation;
import biz.xsoftware.api.nio.handlers.NullWriteCallback;
import biz.xsoftware.api.nio.handlers.OperationCallback;
import biz.xsoftware.api.nio.libs.ChannelSession;

public abstract class UtilChannel extends UtilRegisterable implements Channel {
    
	public UtilChannel(Channel realChannel) {
		super(realChannel);
	}

	protected Channel getRealChannel() {
		return (Channel)super.getRealChannel();
	}
	
	public FutureOperation write(ByteBuffer b) throws IOException, InterruptedException {
		return getRealChannel().write(b);
	}
	
	public int oldWrite(ByteBuffer b) throws IOException {
		return getRealChannel().oldWrite(b);
	}
	
	public void oldWrite(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException {
		if(h == null)
			getRealChannel().oldWrite(b, NullWriteCallback.singleton());
		else
			getRealChannel().oldWrite(b, new UtilPassThroughWriteHandler(this, h));
	}
	
	public void registerForReads(DataListener listener) throws IOException, InterruptedException {
		UtilReaderProxy proxy = new UtilReaderProxy(getRealChannel(), listener);
		getRealChannel().registerForReads(proxy);
	}
	
	public void unregisterForReads() throws IOException, InterruptedException {
		getRealChannel().unregisterForReads();
	}

	@Override
	public FutureOperation connect(SocketAddress addr) throws IOException, InterruptedException {
		return getRealChannel().connect(addr);
	}
	
	public void oldConnect(SocketAddress addr) throws IOException {    
		getRealChannel().oldConnect(addr);
	}

	public void oldClose(OperationCallback h) {
		getRealChannel().oldClose(new UtilPassThroughWriteHandler(this, h));
	}

	public FutureOperation close() {
		return getRealChannel().close();
	}
	
	public InetSocketAddress getRemoteAddress() {
		return getRealChannel().getRemoteAddress();
	}
	public boolean isConnected() {
		return getRealChannel().isConnected();
	}

	public ChannelSession getSession() {
		return getRealChannel().getSession();
	}
	  	
}
