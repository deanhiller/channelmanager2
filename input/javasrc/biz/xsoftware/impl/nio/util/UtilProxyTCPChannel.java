package biz.xsoftware.impl.nio.util;

import java.io.IOException;
import java.net.SocketAddress;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.deprecated.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.DataListener;

public class UtilProxyTCPChannel extends UtilTCPChannel implements TCPChannel {

	public UtilProxyTCPChannel(Channel realChannel) {
		super(realChannel);
	}

	protected TCPChannel getRealChannel() {
		return (TCPChannel)super.getRealChannel();
	}
	
	public void registerForReads(DataListener listener) throws IOException,
			InterruptedException {
		getRealChannel().registerForReads(new UtilReaderProxy(this, listener));
	}

	public void oldConnect(SocketAddress addr, ConnectionCallback c)
			throws IOException, InterruptedException {
		getRealChannel().oldConnect(addr, new UtilProxyConnectCb(this, c));
	}
}
