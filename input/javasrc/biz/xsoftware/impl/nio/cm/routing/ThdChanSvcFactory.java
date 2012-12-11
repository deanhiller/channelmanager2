package biz.xsoftware.impl.nio.cm.routing;

import java.util.Map;

import biz.xsoftware.api.nio.ChannelManager;
import biz.xsoftware.api.nio.ChannelService;
import biz.xsoftware.api.nio.ChannelServiceFactory;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.api.nio.libs.StartableRouterExecutor;


/**
 * @author Dean Hiller
 */
public class ThdChanSvcFactory extends ChannelServiceFactory {

	private ChannelServiceFactory factory;
	
	public ThdChanSvcFactory() {
	}

	/* (non-Javadoc)
	 * @see api.biz.xsoftware.nio.ChannelManagerFactory#createChannelManager(java.util.Properties)
	 */
	public ChannelService createChannelManager(Map<String, Object> map) {
		if(map == null)
			throw new IllegalArgumentException("Properties cannot be null");
		Object id = map.get(ChannelManager.KEY_ID);
		Object o = map.get(ChannelManager.KEY_ROUTINGEXECUTORSVC_FACTORY);
		Object b = map.get(ChannelManager.KEY_BUFFER_FACTORY);
		if(id == null)
			throw new IllegalArgumentException("Properties must contain a value for property key=ChannelManagerFactory.KEY_ID");
		else if(o == null)
			throw new IllegalArgumentException("Key=ChannelManager.KEY_ROUTINGEXECUTORSVC_FACTORY must be specified");
		else if(!(o instanceof StartableRouterExecutor))
			throw new IllegalArgumentException("Key=ChannelManager.KEY_ROUTINGEXECUTORSVC_FACTORY " +
					"must contain an object of type="+StartableRouterExecutor.class.getName()+", obj="+o);
		else if(b == null)
			throw new IllegalArgumentException("Key=ChannelManager.KEY_BUFFER_FACTORY must be specified");
		else if(!(b instanceof BufferFactory))
			throw new IllegalArgumentException("Key=ChannelManager.KEY_BUFFER_FACTORY must " +
					"contain an object of type="+BufferFactory.class.getName()+", obj="+o);

		BufferFactory bufFactory = (BufferFactory)b;
		StartableRouterExecutor executor = (StartableRouterExecutor)o;
		//create a real ChannelManager for the SecureChannelManager to use
		ChannelService mgr = factory.createChannelManager(map);
        SpecialRoutingExecutor proxyExecutor = new SpecialRoutingExecutor(executor);
		return new ThdChannelService(id, mgr, proxyExecutor, bufFactory);
	}

	@Override
	public void configure(Map<String, Object> map) {
		if(map == null)
			throw new IllegalArgumentException("map cannot be null and must be set");
		Object o = map.get(ChannelServiceFactory.KEY_CHILD_CHANNELMGR_FACTORY);
		if(o == null || !(o instanceof ChannelServiceFactory))
			throw new IllegalArgumentException("Key=ChannelManagerFactory.KEY_CHILD_CHANNELMGR_FACTORY " +
					"must be set to an instance of ChannelmanagerFactory and wasn't.  your object="+o);
		this.factory = (ChannelServiceFactory)o;
	}
}
