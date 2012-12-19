package org.playorm.nio.api;

import java.io.IOException;
import java.util.Map;

import org.playorm.nio.api.channels.DatagramChannel;
import org.playorm.nio.api.channels.TCPChannel;
import org.playorm.nio.api.channels.TCPServerChannel;
import org.playorm.nio.api.channels.UDPChannel;
import org.playorm.nio.api.deprecated.ChannelService;
import org.playorm.nio.api.deprecated.ChannelServiceFactory;


public class ChannelManagerFactory {

	private ChannelManagerFactory() {}
	
	public static ChannelManager createChannelManager(String id, Map<String, Object> props) {
		ChannelService svc = ChannelServiceFactory.createNewChannelManager(id);
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
