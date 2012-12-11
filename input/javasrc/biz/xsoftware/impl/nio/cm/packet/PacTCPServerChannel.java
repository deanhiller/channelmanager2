package biz.xsoftware.impl.nio.cm.packet;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.libs.PacketProcessorFactory;
import biz.xsoftware.impl.nio.util.UtilRegisterable;

class PacTCPServerChannel extends UtilRegisterable implements TCPServerChannel {

	private TCPServerChannel realChannel;
	private PacketProcessorFactory factory;
	
	public PacTCPServerChannel(TCPServerChannel c, PacketProcessorFactory f) {
		super(c);
		realChannel = c;
		this.factory = f;
	}

	public TCPServerChannel getRealChannel() {
		return realChannel;
	}
	
	public void registerServerSocketChannel(ConnectionListener cb) throws IOException, InterruptedException {
		PacProxyAcceptCb proxy = new PacProxyAcceptCb(this, factory, cb);
		realChannel.registerServerSocketChannel(proxy);
	}
}
