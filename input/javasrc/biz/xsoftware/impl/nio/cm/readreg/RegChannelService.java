package biz.xsoftware.impl.nio.cm.readreg;

import java.io.IOException;

import biz.xsoftware.api.nio.ChannelService;
import biz.xsoftware.api.nio.Settings;
import biz.xsoftware.api.nio.channels.DatagramChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.channels.UDPChannel;


/**
 * @author Dean Hiller
 */
class RegChannelService implements ChannelService {

	private ChannelService mgr;

	public RegChannelService(Object id, ChannelService manager) {
		this.mgr = manager;
	}

    public TCPServerChannel createTCPServerChannel(String id, Settings h) throws IOException {
        TCPServerChannel channel = mgr.createTCPServerChannel(id, h);
        return new RegTCPServerChannel(channel);
    }

    public TCPChannel createTCPChannel(String id, Settings h) throws IOException {
        TCPChannel realChannel = mgr.createTCPChannel(id, h);
        return new RegTCPChannel(realChannel);
    } 

    public UDPChannel createUDPChannel(String id, Settings h) throws IOException {
        UDPChannel realChannel = mgr.createUDPChannel(id, h);
        UDPChannel channel = new RegUDPChannel(realChannel);
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


}
