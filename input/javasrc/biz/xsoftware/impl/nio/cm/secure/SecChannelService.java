package biz.xsoftware.impl.nio.cm.secure;

import java.io.IOException;

import biz.xsoftware.api.nio.channels.DatagramChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.channels.UDPChannel;
import biz.xsoftware.api.nio.deprecated.ChannelService;
import biz.xsoftware.api.nio.deprecated.Settings;
import biz.xsoftware.impl.nio.util.UtilProxyTCPChannel;
import biz.xsoftware.impl.nio.util.UtilProxyTCPServerChannel;
import biz.xsoftware.impl.nio.util.UtilUDPChannel;


/**
 * @author Dean Hiller
 */
class SecChannelService implements ChannelService {


	private ChannelService mgr;

	public SecChannelService(String id, ChannelService manager) {
		this.mgr = manager;
	}

    public TCPServerChannel createTCPServerChannel(String id, Settings h) throws IOException {
        TCPServerChannel channel = mgr.createTCPServerChannel(id, h);
        if(h == null || h.getSSLEngineFactory() == null) {
            return new UtilProxyTCPServerChannel(channel);
        }
        return new SecTCPServerChannel(channel, h.getSSLEngineFactory());
    }

    public TCPChannel createTCPChannel(String id, Settings h) throws IOException {
        TCPChannel realChannel = mgr.createTCPChannel(id, h);
        if(h == null || h.getSSLEngineFactory() == null) {
            return new UtilProxyTCPChannel(realChannel);
        }
        return new SecTCPChannel(realChannel, h.getSSLEngineFactory());
    } 

    public UDPChannel createUDPChannel(String id, Settings h) throws IOException {
        UDPChannel realChannel = mgr.createUDPChannel(id, h);
        UDPChannel channel = new UtilUDPChannel(realChannel);
        return channel;
    }

    public DatagramChannel createDatagramChannel(String id, int bufferSize) throws IOException {
        return mgr.createDatagramChannel(id, bufferSize);
    }
    
	public void start() throws IOException {
		mgr.start();
	}

	public void stop() throws IOException, InterruptedException {
		mgr.stop();
	}
	
	public String toString() {
		return mgr.toString();
	}


}
