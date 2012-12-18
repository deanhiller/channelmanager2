package biz.xsoftware.impl.nio.cm.exception;

import java.util.Map;

import biz.xsoftware.api.nio.deprecated.ChannelManager;
import biz.xsoftware.api.nio.deprecated.ChannelService;
import biz.xsoftware.api.nio.deprecated.ChannelServiceFactory;


/**
 * @author Dean Hiller
 */
public class ExcChanSvcFactory extends ChannelServiceFactory {

	private ChannelServiceFactory factory;
	
	public ExcChanSvcFactory() {
	}
	
	/* (non-Javadoc)
	 * @see api.biz.xsoftware.nio.ChannelManagerFactory#createChannelManager(java.util.Properties)
	 */
	public ChannelService createChannelManager(Map<String, Object> p) {
		if(p == null)
			throw new IllegalArgumentException("Properties cannot be null");
		Object id = p.get(ChannelManager.KEY_ID);
		if(id == null)
			throw new IllegalArgumentException("Properties must contain a value for property key=ChannelManager.KEY_ID");
		
		//create a real ChannelManager for the SecureChannelManager to use
		ChannelService mgr = factory.createChannelManager(p);
		return new ExcChannelService(id, mgr);
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
