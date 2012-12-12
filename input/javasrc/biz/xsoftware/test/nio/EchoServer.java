package biz.xsoftware.test.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.ChannelService;
import biz.xsoftware.api.nio.Settings;
import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.NullWriteCallback;
import biz.xsoftware.api.nio.testutil.MockNIOServer;

public class EchoServer implements DataListener, ConnectionListener {


	public static final String CONNECTED = "connected";
	public static final String CONN_FAILED = "connectFailed";
	
	private static final Logger log = Logger.getLogger(MockNIOServer.class.getName());
	private ChannelService chanMgr;
	private TCPServerChannel srvrChannel;
	private List<TCPChannel> sockets = new LinkedList<TCPChannel>();
	private Settings factoryHolder;
	
	public EchoServer(ChannelService svr, Settings h) {
		this.chanMgr = svr;
		this.factoryHolder = h;
	}
	
	public InetSocketAddress start() throws IOException, InterruptedException {
		int port = 0;
	
		chanMgr.start();
		
		InetAddress loopBack = InetAddress.getByName("127.0.0.1");
		InetSocketAddress svrAddr = new InetSocketAddress(loopBack, port);		
		srvrChannel = chanMgr.createTCPServerChannel("TCPServerChannel", factoryHolder);
		srvrChannel.setReuseAddress(true);
		srvrChannel.bind(svrAddr);	
		srvrChannel.registerServerSocketChannel(this);
		
		return srvrChannel.getLocalAddress();
	}
	
	public void stop() throws IOException, InterruptedException {		
		srvrChannel.oldClose();
		for(int i = 0; i < sockets.size(); i++) {
			Channel channel = sockets.get(i);
			channel.oldClose();
		}
		chanMgr.stop();		
	}

	public void connected(TCPChannel channel) throws IOException {
		try {
			log.fine(channel+"mockserver accepted connection");
			sockets.add(channel);
			channel.registerForReads(this);
		} catch (InterruptedException e) {
			log.log(Level.WARNING, "exception", e);
		}		
	}

	public void connectFailed(RegisterableChannel channel, Throwable e) {
		log.log(Level.WARNING, "exception", e);
	}
	
	public String toString() {
		return chanMgr.toString();
	}

	private int id = 0;
	public void incomingData(Channel channel, ByteBuffer b) throws IOException {		
		try {
			channel.oldWrite(b, NullWriteCallback.singleton());
		} catch (InterruptedException e) {
			log.log(Level.WARNING, "Exception occurred", e);
		}
	}

	public void farEndClosed(Channel channel) {
        channel.oldClose(null);
	}

	public void failure(Channel channel, ByteBuffer data, Exception e) {
		log.warning(channel+"Data not received");
	}

}
