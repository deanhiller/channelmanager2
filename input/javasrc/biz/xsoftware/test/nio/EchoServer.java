package biz.xsoftware.test.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.deprecated.ChannelService;
import biz.xsoftware.api.nio.deprecated.Settings;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.handlers.DataChunk;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.NullWriteCallback;
import biz.xsoftware.api.nio.testutil.MockNIOServer;

public class EchoServer implements DataListener, ConnectionListener {


	public static final String CONNECTED = "connected";
	public static final String CONN_FAILED = "failed";
	
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

	public void connected(Channel channel) throws IOException {
		try {
			log.fine(channel+"mockserver accepted connection");
			sockets.add((TCPChannel) channel);
			channel.registerForReads(this);
		} catch (InterruptedException e) {
			log.log(Level.WARNING, "exception", e);
		}		
	}

	public void failed(RegisterableChannel channel, Throwable e) {
		log.log(Level.WARNING, "exception", e);
	}
	
	public String toString() {
		return chanMgr.toString();
	}

	private int id = 0;
	public void incomingData(Channel channel, DataChunk chunk) throws IOException {		
		try {
			ByteBuffer b = chunk.getData();
			channel.oldWrite(b, NullWriteCallback.singleton());
			chunk.setProcessed("EchoServer");
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
