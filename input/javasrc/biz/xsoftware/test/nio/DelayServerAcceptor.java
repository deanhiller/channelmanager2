package biz.xsoftware.test.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.ChannelService;
import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;

public class DelayServerAcceptor implements ConnectionListener {

	private static final Logger log = Logger.getLogger(DelayServerAcceptor.class.getName());
	private InetSocketAddress realSvr;
	private ChannelService clientSideChanMgr;
	
	private List<TCPChannel> sockets = new LinkedList<TCPChannel>();
	
	private TCPChannel currentChannel;
	private InetAddress delaySvrAddr;

	
	public DelayServerAcceptor(ChannelService clientSideChanMgr, InetAddress delaySvrAddr, InetSocketAddress realSvr) {
		this.clientSideChanMgr = clientSideChanMgr;
		this.realSvr = realSvr;
		this.delaySvrAddr = delaySvrAddr;
	}
		
	public void connected(TCPChannel channel) throws IOException {
		if(log.isLoggable(Level.FINE))
			log.fine(channel+"about to accept");
		currentChannel = clientSideChanMgr.createTCPChannel("xxx", null);
        currentChannel.setName("<not known yet>");
		InetSocketAddress addr = new InetSocketAddress(delaySvrAddr, 0);
		currentChannel.bind(addr);
		currentChannel.oldConnect(realSvr);
		if(log.isLoggable(Level.FINE))
			log.fine(channel+"connected to real server");
		
		if(log.isLoggable(Level.FINE))
			log.fine(channel+":"+currentChannel+"connected all links");
		sockets.add(channel);
		sockets.add(currentChannel);

		try {
			currentChannel.registerForReads(new Delayer(channel));
			channel.registerForReads(new Delayer(currentChannel));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}			
	}

	public void connectFailed(RegisterableChannel channel, Throwable e) {
		log.log(Level.WARNING, "exception", e);
	}

	public void closeAllSockets() throws IOException {
		for(int i = 0; i < sockets.size(); i++) {
			Channel channel = sockets.get(i);
			channel.oldClose();
		}		
	}
	
//	private class IdHolder {
//		private Object id = "(Id not assigned yet)";
//		public void setId(Object id) {
//			this.id = id;
//		}
//		public String toString() {
//			return id+"";
//		}
//	}
}
