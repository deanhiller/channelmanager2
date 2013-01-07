package org.playorm.nio.test.nottested;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.playorm.nio.api.channels.TCPChannel;
import org.playorm.nio.api.deprecated.ChannelManager;
import org.playorm.nio.api.deprecated.ChannelService;
import org.playorm.nio.api.deprecated.ChannelServiceFactory;
import org.playorm.nio.api.deprecated.ConnectionCallback;
import org.playorm.nio.api.deprecated.Settings;
import org.playorm.nio.api.handlers.DataListener;
import org.playorm.nio.api.testutil.MockDataHandler;
import org.playorm.nio.api.testutil.MockSSLEngineFactory;

import biz.xsoftware.mock.MockObject;
import biz.xsoftware.mock.MockObjectFactory;


/**
 * @author Dean Hiller
 */
public class TryRealConnection extends TestCase {

	private static final Logger log = Logger.getLogger(TryRealConnection.class.getName());
	private ChannelService chanMgr;
	private InetAddress loopBack;
	private InetSocketAddress loopBackAddr;
	
	/**
	 * @param arg0
	 */
	public TryRealConnection(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		if(chanMgr == null) {
			Map<String, Object> factoryName = new HashMap<String, Object>();
			factoryName.put(ChannelServiceFactory.KEY_IMPLEMENTATION_CLASS, ChannelServiceFactory.VAL_SECURE_CHANNEL_MGR);
			ChannelServiceFactory secureFactory = ChannelServiceFactory.createFactory(factoryName);
			//ChannelManagerFactory secureFactory = ChannelManagerFactory.createFactory(null);
			
			Map<String, Object> p = new HashMap<String, Object>();
			p.put(ChannelManager.KEY_ID, "[client]");		
			chanMgr = secureFactory.createChannelManager(p);
			chanMgr.start();
		}
		
		loopBack = InetAddress.getByName("192.168.1.100");	
		loopBackAddr = new InetSocketAddress(loopBack, 0);
	}
	
	protected void tearDown() throws Exception {
		//mockServer.stop();
	}
	
	public void testRealConnection() throws Exception {
		MockObject mockConnect = MockObjectFactory.createMock(ConnectionCallback.class);
		Settings h = new Settings(new MockSSLEngineFactory(), null);
		TCPChannel channel = chanMgr.createTCPChannel("testId", h);
		channel.bind(loopBackAddr);

		
		log.fine("aaaaa");
		MockObject handler = new MockDataHandler();
		channel.registerForReads((DataListener)handler);
		
				
//		InetAddress host = InetAddress.getByName("shell.sourceforge.net");
		InetAddress host = InetAddress.getByName("www.xsoftware.biz");
		InetSocketAddress addr = new InetSocketAddress(host, 22);
		channel.oldConnect(addr, (ConnectionCallback)mockConnect);

		mockConnect.expect("connected");
		
		handler.addIgnore("getBuffer");

		//CalledMethod method = 
		handler.expect("incomingData");
//		ByteBuffer b = (ByteBuffer)method.getAllParams()[1];

		
	}
	
	public void testRegisterForReadAfterConnect() {
		
	}
	
	public void testNio() throws Exception {
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		
		Socket s = channel.socket();
		s.bind(loopBackAddr);
		
		InetAddress host = InetAddress.getByName("shell.sourceforge.net");
		InetSocketAddress addr = new InetSocketAddress(host, 22);		
		channel.connect(addr);
		
		for(int i = 0; i < 10; i++) {
			boolean b = channel.finishConnect();
			log.fine("finishConnect["+i+"]="+b);
			Thread.sleep(5000);
		}
	}
	
}
