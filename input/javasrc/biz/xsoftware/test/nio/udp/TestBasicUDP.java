package biz.xsoftware.test.nio.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import junit.framework.TestCase;
import biz.xsoftware.api.nio.BufferHelper;
import biz.xsoftware.api.nio.ChannelManager;
import biz.xsoftware.api.nio.ChannelService;
import biz.xsoftware.api.nio.ChannelServiceFactory;
import biz.xsoftware.api.nio.Settings;
import biz.xsoftware.api.nio.channels.UDPChannel;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.api.nio.libs.FactoryCreator;
import biz.xsoftware.api.nio.testutil.CloneByteBuffer;
import biz.xsoftware.api.nio.testutil.HandlerForTests;
import biz.xsoftware.api.nio.testutil.MockDataHandler;
import biz.xsoftware.api.nio.testutil.MockNIOServer;
import biz.xsoftware.mock.CalledMethod;
import biz.xsoftware.mock.MockObject;
import biz.xsoftware.mock.MockObjectFactory;

public class TestBasicUDP extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestBasicUDP.class.getName());
	
	private InetSocketAddress svrAddr;
	private ChannelService chanMgr;
	private MockNIOServer mockServer;
	private MockObject mockHandler;
	private UDPChannel client1;
	private BufferHelper helper = ChannelServiceFactory.bufferHelper(null);
	private BufferFactory bufFactory;

	private ChannelServiceFactory basic;

    private InetAddress loopBack;

    private InetSocketAddress remoteAddr;
	
	public TestBasicUDP(String arg0) {
		super(arg0);
		basic = ChannelServiceFactory.createFactory(null);
		if(bufFactory == null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FactoryCreator.KEY_IS_DIRECT, false);
			FactoryCreator creator = FactoryCreator.createFactory(null);
			bufFactory = creator.createBufferFactory(map);			
		}		
	}
	
	protected void setUp() throws Exception {
		HandlerForTests.setupLogging();
		//here I keep using the same channel manager on purpose, just
		//so we get testing between tests that the channel manager shutdown
		//and started back up cleanly.....		
		if(chanMgr == null) {
			chanMgr = ChannelServiceFactory.createDefaultChannelMgr("client");
		}
		if(mockServer == null) {
			ChannelService svcChanMgr = getServerChanMgr();
			mockServer = new MockNIOServer(svcChanMgr, getServerFactoryHolder());
		}
		chanMgr.start();	
		
		svrAddr = mockServer.start();
		log.info("server port ="+svrAddr);
	
        loopBack = InetAddress.getByName("127.0.0.1");
        remoteAddr = new InetSocketAddress(loopBack, svrAddr.getPort()+1);
        
        mockHandler = MockObjectFactory.createMock(DataListener.class);
		mockHandler.setDefaultBehavior("incomingData", new CloneByteBuffer());
		client1 = chanMgr.createUDPChannel("ClientChannel", getClientFactoryHolder());
	}
	
	protected void tearDown() throws Exception {
		chanMgr.stop();
		chanMgr = null;
		mockServer.stop();
	}
	
	public void testUDPWithConnect() throws Exception {
		//make sure we are testing the right one....
		Class c = Class.forName(getChannelImplName());
		assertEquals("should be instance of secure channel", c, client1.getClass());
			
		//no bind, just do connect to test port is not zero
		client1.connect(remoteAddr);
		assertTrue("should be bound", client1.isBound());
		
		boolean isConnected = client1.isConnected();
		assertTrue("Client should be connected", isConnected);
		InetSocketAddress localAddr = client1.getLocalAddress();
		assertTrue("Port should not be 0", localAddr.getPort() != 0);
		
		DatagramChannel svrChan = mockServer.getUDPServerChannel();

		client1.registerForReads((DataListener)mockHandler);

		verifyDataPassing(svrChan);
		verifyTearDown();
        
        HandlerForTests.checkForWarnings();
	}

    public void testUDPWithDisconnectConnect() throws Exception
    {
        client1.connect(remoteAddr);
        DatagramChannel svrChan = mockServer.getUDPServerChannel();
        client1.registerForReads((DataListener)mockHandler);
        verifyDataPassing(svrChan);
        
        //now disconnect, have server send some udp packets which should be rejected
        client1.disconnect();
        
        writeFromServer(svrChan);
        
        try {
            client1.write(createBuffer());        
            fail("Should have thrown a NotYetConnectedException");
        } catch (IllegalStateException e) {
            //should land here
        }
     
//        try {
//            HandlerForTests.checkForWarnings();
//            fail("log should have had warnings");
//        } catch (LogHasWarningException e) {
//        }
    }

    private ByteBuffer createBuffer() {
        ByteBuffer b = ByteBuffer.allocate(10);
        helper.putString(b, "de");
        helper.doneFillingBuffer(b);
        return b;
    }
    
    private void writeFromServer(DatagramChannel svrChan) throws Exception {
        ByteBuffer b = createBuffer();
        
        svrChan.send(b, client1.getLocalAddress());
        
        
    }
    
	private ByteBuffer verifyDataPassing(DatagramChannel svrChan) throws Exception {
		ByteBuffer b = createBuffer();
		int expectedWrote = b.remaining();
		log.fine("***********************************************");
		int actualWrite = client1.write(b);
		assertEquals(expectedWrote, actualWrite);
	
		CalledMethod m = mockServer.expect(MockNIOServer.INCOMING_DATA);

		ByteBuffer actualBuf = (ByteBuffer)m.getAllParams()[1];
		String result = helper.readString(actualBuf, actualBuf.remaining());
		assertEquals("de", result);
		
		b.rewind();
		svrChan.send(b, client1.getLocalAddress());
		
		m = mockHandler.expect(MockDataHandler.INCOMING_DATA);
		actualBuf = (ByteBuffer)m.getAllParams()[1];
		result = helper.readString(actualBuf, actualBuf.remaining());
		assertEquals("de", result);	
		return b;
	}
	
	private void verifyTearDown() throws IOException {
		client1.close();
		assertTrue("Status should be closed", client1.isClosed());
	}
	
	private Settings getClientFactoryHolder() {
		return null;
	}

	private Settings getServerFactoryHolder() {
		return null;
	}

	private ChannelService getServerChanMgr() {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put(ChannelManager.KEY_ID, "[server]");
		p.put(ChannelManager.KEY_BUFFER_FACTORY, bufFactory);		
		return basic.createChannelManager(p);
	}

//	private ChannelManagerService getClientChanMgr() {
//		Map<String, Object> p = new HashMap<String, Object>();
//		p.put(ChannelManager.KEY_ID, "[client]");
//		p.put(ChannelManager.KEY_BUFFER_FACTORY, bufFactory);		
//		return basic.createChannelManager(p);
//	}
	
	private String getChannelImplName() {
		return "biz.xsoftware.impl.nio.util.UtilUDPChannel";
	}
}
