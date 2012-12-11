package biz.xsoftware.test.nio.tcp;

import java.util.HashMap;
import java.util.Map;

import biz.xsoftware.api.nio.ChannelManager;
import biz.xsoftware.api.nio.ChannelService;
import biz.xsoftware.api.nio.ChannelServiceFactory;
import biz.xsoftware.api.nio.Settings;
import biz.xsoftware.api.nio.libs.FactoryCreator;
import biz.xsoftware.api.nio.libs.PacketProcessorFactory;
import biz.xsoftware.api.nio.libs.SSLEngineFactory;
import biz.xsoftware.api.nio.testutil.MockSSLEngineFactory;

public class PerfTestZSecure extends ZPerformanceSuper {
	
	private ChannelServiceFactory secureFactory;
	private SSLEngineFactory sslEngineFactory;
	private Settings clientFactoryHolder;
	private Settings serverFactoryHolder;
	
	public PerfTestZSecure(String name) {
		super(name);
		ChannelServiceFactory basic = ChannelServiceFactory.createFactory(null);
		
		Map<String, Object> factoryName = new HashMap<String, Object>();
		factoryName.put(ChannelServiceFactory.KEY_IMPLEMENTATION_CLASS, ChannelServiceFactory.VAL_SECURE_CHANNEL_MGR);
		factoryName.put(ChannelServiceFactory.KEY_CHILD_CHANNELMGR_FACTORY, basic);
		ChannelServiceFactory sslLayer = ChannelServiceFactory.createFactory(factoryName);
		
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ChannelServiceFactory.KEY_IMPLEMENTATION_CLASS, ChannelServiceFactory.VAL_PACKET_CHANNEL_MGR);
		props.put(ChannelServiceFactory.KEY_CHILD_CHANNELMGR_FACTORY, sslLayer);
		secureFactory = ChannelServiceFactory.createFactory(props);	
		
		sslEngineFactory = new MockSSLEngineFactory();
		
		FactoryCreator creator = FactoryCreator.createFactory(null);
		PacketProcessorFactory procFactory = creator.createPacketProcFactory(null);
		clientFactoryHolder = new Settings(sslEngineFactory, procFactory);
		serverFactoryHolder = new Settings(sslEngineFactory, procFactory);		
	}
	
	@Override
	protected ChannelService getClientChanMgr() {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put(ChannelManager.KEY_ID, "client");		
		p.put(ChannelManager.KEY_BUFFER_FACTORY, getBufFactory());
		ChannelService chanMgr = secureFactory.createChannelManager(p);		
		return chanMgr;
	}

	@Override
	protected ChannelService getServerChanMgr() {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put(ChannelManager.KEY_ID, "echoServer");
		p.put(ChannelManager.KEY_BUFFER_FACTORY, getBufFactory());
		ChannelService svcChanMgr = secureFactory.createChannelManager(p);
		
		return svcChanMgr;
	}

	@Override
	protected Settings getClientFactoryHolder() {
		return clientFactoryHolder;
	}
	@Override
	protected Settings getServerFactoryHolder() {
		return serverFactoryHolder;
	}
	
	@Override
	protected String getChannelImplName() {
		return "biz.xsoftware.impl.nio.cm.secure.SecTCPChannel";
	}

	@Override
	protected int getBasicConnectTimeLimit() {
		return 300;
	}	
	
	/**
	 * Realize this limit is bigger when useing AsynchSSLEngineSynchronized
	 */
	@Override
	protected int getSmallReadWriteTimeLimit() {
		return 80;
	}

	@Override
	protected int getLargerReadWriteTimeLimit() {
		return 300;
	}

	
}
