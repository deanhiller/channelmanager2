package biz.xsoftware.impl.nio.cm.packet;

import java.io.IOException;

import biz.xsoftware.api.nio.ChannelService;
import biz.xsoftware.api.nio.Settings;
import biz.xsoftware.api.nio.channels.DatagramChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.channels.UDPChannel;
import biz.xsoftware.api.nio.libs.PacketProcessor;
import biz.xsoftware.impl.nio.util.UtilProxyTCPChannel;
import biz.xsoftware.impl.nio.util.UtilProxyTCPServerChannel;
import biz.xsoftware.impl.nio.util.UtilUDPChannel;


/**
 * @author Dean Hiller
 */
class PacChannelService implements ChannelService {


	private ChannelService mgr;

	public PacChannelService(Object id, ChannelService manager) {
		this.mgr = manager;
	}

    public TCPServerChannel createTCPServerChannel(String id, Settings h) throws IOException {
        TCPServerChannel channel = mgr.createTCPServerChannel(id, h);
        if(h == null || h.getPacketProcessorFactory() == null) {
            return new UtilProxyTCPServerChannel(channel);
        }
        return new PacTCPServerChannel(channel, h.getPacketProcessorFactory());
    }

    public TCPChannel createTCPChannel(String id, Settings h) throws IOException {
        TCPChannel realChannel = mgr.createTCPChannel(id, h);
        if(h == null || h.getPacketProcessorFactory() == null) {
            return new UtilProxyTCPChannel(realChannel);
        }

        PacketProcessor processor = h.getPacketProcessorFactory().createPacketProcessor(realChannel);
        return new PacTCPChannel(realChannel, processor);
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


}
