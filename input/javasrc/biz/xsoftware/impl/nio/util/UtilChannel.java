package biz.xsoftware.impl.nio.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.DataListener;
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
	
	public int oldWrite(ByteBuffer b) throws IOException {
		return getRealChannel().oldWrite(b);
	}
	
	public void write(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException {
		if(h == null)
			getRealChannel().write(b, NullWriteCallback.singleton());
		else
			getRealChannel().write(b, new UtilPassThroughWriteHandler(this, h));
	}
	
	public void registerForReads(DataListener listener) throws IOException, InterruptedException {
		UtilReaderProxy proxy = new UtilReaderProxy(getRealChannel(), listener);
		getRealChannel().registerForReads(proxy);
	}
	
	public void unregisterForReads() throws IOException, InterruptedException {
		getRealChannel().unregisterForReads();
	}

	public void connect(SocketAddress addr) throws IOException {    
		getRealChannel().connect(addr);
	}

	public void close(OperationCallback h) {
		getRealChannel().close(new UtilPassThroughWriteHandler(this, h));
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
