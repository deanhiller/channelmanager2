package biz.xsoftware.test.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import biz.xsoftware.api.nio.ChannelManager;
import biz.xsoftware.api.nio.ChannelService;
import biz.xsoftware.api.nio.ChannelServiceFactory;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.api.nio.libs.FactoryCreator;

/**
 * A delay server to introduce simulated network delays for testing
 * performance of channelmanager.  It is a simple pass through but purposely
 * caches the data and sends it x milliseconds later.
 * 
 * @author dean.hiller
 */
public class DelayServer {
	
//	private static final Logger log = Logger.getLogger(MockNIOServer.class.getName());
	private ChannelService serverSideChanMgr;
	private ChannelService clientSideChanMgr;	
	private TCPServerChannel srvrChannel;
	private DelayServerAcceptor acceptor;
	
	public DelayServer() {
		ChannelServiceFactory factory = ChannelServiceFactory.createFactory(null);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FactoryCreator.KEY_IS_DIRECT, false);
		FactoryCreator creator = FactoryCreator.createFactory(null);
		BufferFactory bufFactory = creator.createBufferFactory(map);		
		
		Map<String, Object> p = new HashMap<String, Object>();
		p.put(ChannelManager.KEY_ID, "[serverSide]");
		p.put(ChannelManager.KEY_BUFFER_FACTORY, bufFactory);
		
		this.serverSideChanMgr = factory.createChannelManager(p);
		Map<String, Object> p2 = new HashMap<String, Object>();
		p2.put(ChannelManager.KEY_ID, "[clientSide]");
		p2.put(ChannelManager.KEY_BUFFER_FACTORY, bufFactory);		
		this.clientSideChanMgr = factory.createChannelManager(p2);
	}
	
	public InetSocketAddress start(InetSocketAddress realSvr) throws IOException, InterruptedException {
		int port = 0;
	
		clientSideChanMgr.start();
		serverSideChanMgr.start();
		InetAddress loopBack = InetAddress.getByName("127.0.0.1");
		
		acceptor = new DelayServerAcceptor(clientSideChanMgr, loopBack, realSvr);
		InetSocketAddress svrAddr = new InetSocketAddress(loopBack, port);		
		srvrChannel = serverSideChanMgr.createTCPServerChannel("ProxySvrChannel", null);
		srvrChannel.setReuseAddress(true);
		srvrChannel.bind(svrAddr);	
		srvrChannel.registerServerSocketChannel(acceptor);
		
		return srvrChannel.getLocalAddress();
	}
	
	public void stop() throws IOException, InterruptedException {		
		srvrChannel.close();
		acceptor.closeAllSockets();
		
		serverSideChanMgr.stop();		
	}

//	public static void main(String[] args) throws Exception {

//		if(args.length < 2) {
//		 return;
//		}
//		
//		String realServerIp = args[0];
//		int port = Integer.parseInt(args[1]);
//		InetAddress addr = InetAddress.getByName(realServerIp);
//		InetSocketAddress svrAddr = new InetSocketAddress(addr, port);
//		DelayServer svr = new DelayServer();
//		svr.start(svrAddr);		
//	}
}
