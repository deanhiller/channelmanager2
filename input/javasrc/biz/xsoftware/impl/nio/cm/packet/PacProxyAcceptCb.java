package biz.xsoftware.impl.nio.cm.packet;


import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.libs.PacketProcessor;
import biz.xsoftware.api.nio.libs.PacketProcessorFactory;

class PacProxyAcceptCb implements ConnectionListener {

	private TCPServerChannel svrChannel;
	private ConnectionListener cb;
	private PacketProcessorFactory factory;

	public PacProxyAcceptCb(TCPServerChannel svrChannel, PacketProcessorFactory proc, ConnectionListener cb) {
		this.svrChannel = svrChannel;
		this.cb = cb;
		this.factory = proc;
	}
	
	public void finished(Channel channel) throws IOException {
		PacketProcessor processor = factory.createPacketProcessor(channel);
		TCPChannel newChannel = new PacTCPChannel((TCPChannel) channel, processor);
		cb.finished(newChannel);		
	}

	public void failed(RegisterableChannel channel, Throwable e) {
		cb.failed(svrChannel, e);
	}
}
