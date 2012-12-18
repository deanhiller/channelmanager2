package biz.xsoftware.api.nio;

import java.io.IOException;
import java.util.Map;

import biz.xsoftware.api.nio.channels.DatagramChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.channels.UDPChannel;
import biz.xsoftware.api.nio.deprecated.ChannelService;
import biz.xsoftware.api.nio.deprecated.ChannelServiceFactory;

public class ChannelManagerFactory {

	public static ChannelManager createChannelManager(String id, Map<String, Object> props) {
		ChannelService svc = ChannelServiceFactory.createRawChannelManager(id);
		try {
			svc.start();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return new ChannelMgrProxy(svc);
	}
	
	private static class ChannelMgrProxy implements ChannelManager {

		private ChannelService svc;

		public ChannelMgrProxy(ChannelService svc) {
			this.svc = svc;
		}

		@Override
		public TCPServerChannel createTCPServerChannel(String id)
				throws IOException {
			return svc.createTCPServerChannel(id, null);
		}

		@Override
		public TCPChannel createTCPChannel(String id) throws IOException {
			return svc.createTCPChannel(id, null);
		}

		@Override
		public UDPChannel createUDPChannel(String id) throws IOException {
			return svc.createUDPChannel(id, null);
		}

		@Override
		public DatagramChannel createDatagramChannel(String id, int bufferSize)
				throws IOException {
			return svc.createDatagramChannel(id, bufferSize);
		}
	}
}
