package biz.xsoftware.impl.nio.cm.basic;

import java.util.Map;

import biz.xsoftware.api.nio.ChannelManager;
import biz.xsoftware.api.nio.ChannelService;
import biz.xsoftware.api.nio.ChannelServiceFactory;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.api.nio.testutil.chanapi.ChannelsFactory;
import biz.xsoftware.api.nio.testutil.nioapi.SelectorProviderFactory;
import biz.xsoftware.impl.nio.cm.basic.chanimpl.ChannelsFactoryImpl;
import biz.xsoftware.impl.nio.cm.basic.nioimpl.SelectorProvFactoryImpl;


/**
 * @author Dean Hiller
 */
public class BasChanSvcFactory extends ChannelServiceFactory {

	@Override
	public void configure(Map<String, Object> props) {

	}	
	
	/* (non-Javadoc)
	 * @see api.biz.xsoftware.nio.ChannelManagerFactory#createChannelManager(java.util.Properties)
	 */
	@Override
	public ChannelService createChannelManager(Map<String, Object> map) {
		if(map == null)
			throw new IllegalArgumentException("map cannot be null");
		String id = map.get(ChannelManager.KEY_ID)+"";
		if(id == null)
			throw new IllegalArgumentException("map must contain a value for property key=ChannelManager.KEY_ID");
		Object o = map.get(ChannelManager.KEY_BUFFER_FACTORY);
		if(o == null || !(o instanceof BufferFactory))
			throw new IllegalArgumentException("Key=ChannelManager.KEY_BUFFER_FACTORY must " +
					"not be null and must contain an instance of ByteBufferFactory");
        ChannelsFactory factory;
        if(map.get("mock.channelsFactory") == null) {
            factory = new ChannelsFactoryImpl();            
        } else {
            factory = (ChannelsFactory)map.get("mock.channelsFactory");
        }
        
        SelectorProviderFactory mgr;
        if(map.get("mock.selectorProvider") == null) {
            mgr = new SelectorProvFactoryImpl();
        } else {
            mgr = (SelectorProviderFactory)map.get("mock.selectorProvider");
        }

		return new BasChannelService(id, factory, mgr, (BufferFactory)o);
	}


}
