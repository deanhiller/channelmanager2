package biz.xsoftware.test.nio.nottested;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.deprecated.ChannelService;
import biz.xsoftware.api.nio.deprecated.ChannelServiceFactory;
import biz.xsoftware.api.nio.deprecated.Settings;
import biz.xsoftware.api.nio.handlers.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.DataChunk;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.libs.SSLEngineFactory;
import biz.xsoftware.api.nio.testutil.MockSSLEngineFactory;
import biz.xsoftware.impl.nio.util.BufferHelper;

public class EventClient implements ConnectionCallback, DataListener {

	private static final Logger log = Logger.getLogger(EventClient.class.getName());
	private static final BufferHelper HELPER = ChannelServiceFactory.bufferHelper(null);
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		EventClient client = new EventClient();
		client.start();
	}
	
	public void start() throws IOException, InterruptedException {
		ChannelService mgr = ChannelServiceFactory.createDefaultChannelMgr("EventServer");
		mgr.start();
		
		SSLEngineFactory sslFactory = new MockSSLEngineFactory();
		Settings h = new Settings(sslFactory, null);		
		TCPChannel channel = mgr.createTCPChannel("SvrChan", h);
		
		InetAddress addr = InetAddress.getByName("192.168.1.102");
		InetSocketAddress sockAddr = new InetSocketAddress(addr, 801);

		log.info("Connecting to server="+sockAddr);
		channel.oldConnect(sockAddr, this);
	}

	public void finished(Channel channel) throws IOException {
		try {
			log.info(channel+"Connected now="+channel.getRemoteAddress());
			channel.registerForReads(this);
			
			//now write out the request and wait for events coming back.....
			String hello = "helloThere";
			ByteBuffer b = ByteBuffer.allocate(100);
			HELPER.putString(b, hello);
			HELPER.doneFillingBuffer(b);
			channel.oldWrite(b);			
		} catch (InterruptedException e) {
			log.log(Level.WARNING, channel+"Exception", e);
		}
	}

	public void failed(RegisterableChannel channel, Throwable e) {
		log.log(Level.WARNING, channel+"Exception", e);
	}

	public void incomingData(Channel channel, DataChunk chunk) throws IOException {
		ByteBuffer b = chunk.getData();
		String s = HELPER.readString(b, b.remaining());
		chunk.setProcessed();
		log.info(channel+"Received event="+s);
	}

	public void farEndClosed(Channel channel) {
		log.warning(channel+"Should never have closed from far end");
	}

	public void failure(Channel channel, ByteBuffer data, Exception e) {
		log.warning(channel+"Data not received");
	}

}
