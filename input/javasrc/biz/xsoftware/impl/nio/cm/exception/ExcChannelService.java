package biz.xsoftware.impl.nio.cm.exception;

import java.io.IOException;

import biz.xsoftware.api.nio.ChannelService;
import biz.xsoftware.api.nio.Settings;
import biz.xsoftware.api.nio.channels.DatagramChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.channels.UDPChannel;
import biz.xsoftware.impl.nio.util.UtilUDPChannel;


/**
 * @author Dean Hiller
 */
class ExcChannelService implements ChannelService {


	private ChannelService mgr;

	public ExcChannelService(Object id, ChannelService manager) {
		this.mgr = manager;
	}

    public TCPServerChannel createTCPServerChannel(String id, Settings h) throws IOException {
        TCPServerChannel channel = mgr.createTCPServerChannel(id, h);
        return new ExcTCPServerChannel(channel);
    }

    public TCPChannel createTCPChannel(String id, Settings h) throws IOException {
        TCPChannel realChannel = mgr.createTCPChannel(id, h);
        ExcTCPChannel channel = new ExcTCPChannel(realChannel);
        return channel;
    } 

    public UDPChannel createUDPChannel(String id, Settings h) throws IOException {
        //TODO: implement this correctly.....
        UDPChannel realChannel = mgr.createUDPChannel(id, h);
        UDPChannel channel = new UtilUDPChannel(realChannel);
        return channel;
    }

    public DatagramChannel createDatagramChannel(String id, int bufferSize) throws IOException {
        //TODO: implement this correctly....
        return mgr.createDatagramChannel(id, bufferSize);
    }
    
	public void start() throws IOException {
		mgr.start();
	}

	public void stop() throws IOException, InterruptedException {
		mgr.stop();
	}


}
