package biz.xsoftware.impl.nio.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.NullWriteCallback;
import biz.xsoftware.api.nio.handlers.WriteCloseCallback;
import biz.xsoftware.api.nio.libs.ChannelSession;

public abstract class UtilChannel extends UtilRegisterable implements Channel {
    
	public UtilChannel(Channel realChannel) {
		super(realChannel);
	}

	protected Channel getRealChannel() {
		return (Channel)super.getRealChannel();
	}
	
	public int write(ByteBuffer b) throws IOException {
		return getRealChannel().write(b);
	}
	
	public void write(ByteBuffer b, WriteCloseCallback h, int id) throws IOException, InterruptedException {
		if(h == null)
			getRealChannel().write(b, NullWriteCallback.singleton(), id);
		else
			getRealChannel().write(b, new UtilPassThroughWriteHandler(this, h), id);
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

	public void close(WriteCloseCallback h, int id) {
		getRealChannel().close(new UtilPassThroughWriteHandler(this, h), id);
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
