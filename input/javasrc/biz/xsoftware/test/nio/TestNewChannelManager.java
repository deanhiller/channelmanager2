package biz.xsoftware.test.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import junit.framework.TestCase;
import biz.xsoftware.api.nio.ChannelManager;
import biz.xsoftware.api.nio.ChannelManagerFactory;
import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.deprecated.ChannelServiceFactory;
import biz.xsoftware.api.nio.handlers.ConnectionListener;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.FutureOperation;
import biz.xsoftware.api.nio.handlers.OperationCallback;
import biz.xsoftware.api.nio.libs.BufferHelper;
import biz.xsoftware.api.nio.testutil.HandlerForTests;
import biz.xsoftware.api.nio.testutil.MockDataHandler;
import biz.xsoftware.api.nio.testutil.MockNIOServer;
import biz.xsoftware.mock.CalledMethod;
import biz.xsoftware.mock.MockObject;
import biz.xsoftware.mock.MockObjectFactory;

public class TestNewChannelManager extends TestCase {

	private static final Logger log = Logger.getLogger(TestNewChannelManager.class.getName());
	
	private ChannelManager server;
	private ChannelManager client;
	private TCPChannel client1;
	private InetAddress loopBack;
	private InetSocketAddress loopBackAnyPort;
	private MockObject clientHandler;
	private MockObject clientConnect;
	private TCPServerChannel srvrChannel;
	private MockObject serverAccept;
	private BufferHelper helper = ChannelServiceFactory.bufferHelper(null);

	private Channel serverTcpChannel;

	private MockObject serverHandler;
	
	protected void setUp() throws Exception {
		HandlerForTests.setupLogging();
	
		server = ChannelManagerFactory.createChannelManager("server", null);
		client = ChannelManagerFactory.createChannelManager("client", null);
		
		loopBack = InetAddress.getByName("127.0.0.1");	
		loopBackAnyPort = new InetSocketAddress(loopBack, 0);
		
		serverAccept = MockObjectFactory.createMock(ConnectionListener.class);
		srvrChannel = server.createTCPServerChannel("jmxServer");
		srvrChannel.setReuseAddress(true);
		srvrChannel.bind(loopBackAnyPort);	
		srvrChannel.registerServerSocketChannel((ConnectionListener) serverAccept);
		
		clientHandler = MockObjectFactory.createMock(DataListener.class);
		clientConnect = MockObjectFactory.createMock(OperationCallback.class);
		client1 = client.createTCPChannel("ClientChannel");	
		
	}
	
	protected void tearDown() throws Exception {
		HandlerForTests.checkForWarnings();
	}

	public void testBasic() throws Exception {
		client1.bind(loopBackAnyPort);		
		InetSocketAddress remoteAddr = new InetSocketAddress(loopBack, srvrChannel.getLocalAddress().getPort());
		client1.registerForReads((DataListener)clientHandler);
		FutureOperation future = client1.connect(remoteAddr);
		future.setListener((OperationCallback) clientConnect);
		clientConnect.expect("finished");
		
		future.waitForOperation(); //should return immediately since listener fired
		
		serverHandler = MockObjectFactory.createMock(DataListener.class);
		CalledMethod m = serverAccept.expect("connected");
		serverTcpChannel = (Channel)m.getAllParams()[0];
		serverTcpChannel.registerForReads((DataListener) serverHandler);
		
		boolean isConnected = client1.isConnected();
		assertTrue("Client should be connected", isConnected);
		
		verifyDataPassing(client1);
		verifyTearDown();	
	}
	
	private ByteBuffer verifyDataPassing(TCPChannel svrChan) throws Exception {
		String myStr  = create();
		ByteBuffer b = null;
		for(int i = 0; i < 1000000; i++) {
			b = ByteBuffer.allocate(myStr.length());
			helper.putString(b, myStr);
			helper.doneFillingBuffer(b);
			log.fine("***********************************************");
			FutureOperation write = client1.write(b);
			write.waitForOperation(5000);
			log.info("wrote len="+myStr.length()+" count="+i);
		}
		
		CalledMethod m = serverHandler.expect("incomingData");
		TCPChannel actualChannel = (TCPChannel)m.getAllParams()[0];
		ByteBuffer actualBuf = (ByteBuffer)m.getAllParams()[1];
		String result = helper.readString(actualBuf, actualBuf.remaining());
		assertEquals("de", result);
		
		b.rewind();
		FutureOperation future = actualChannel.write(b);
		future.waitForOperation(5000); //synchronously wait for write to happen
		
		m = clientHandler.expect(MockDataHandler.INCOMING_DATA);
		actualBuf = (ByteBuffer)m.getAllParams()[1];
		result = helper.readString(actualBuf, actualBuf.remaining());
		assertEquals("de", result);	
		return b;
	}
	
	private String create() {
		String s = "";
		for(int i = 0; i < 10000; i++) {
			s += "dean";
		}
		return s;
	}

	private void verifyTearDown() throws IOException {
        log.info("local="+client1.getLocalAddress()+" remote="+client1.getRemoteAddress());
		log.info("CLIENT1 CLOSE");
		FutureOperation future = client1.close();
		serverHandler.expect(MockNIOServer.FAR_END_CLOSED);
		future.setListener((OperationCallback) clientConnect);
		clientConnect.expect("finished");
	}
}
