package biz.xsoftware.impl.nio.util;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;

public class UtilProxyTCPServerChannel extends UtilRegisterable implements TCPServerChannel {

	public UtilProxyTCPServerChannel(RegisterableChannel realChannel) {
		super(realChannel);
	}

	protected TCPServerChannel getRealChannel() {
		return (TCPServerChannel)super.getRealChannel();
	}
	
	public void registerServerSocketChannel(ConnectionListener cb)
			throws IOException, InterruptedException {
		getRealChannel().registerServerSocketChannel(new UtilProxyAcceptCb(this, cb));
	}

}
