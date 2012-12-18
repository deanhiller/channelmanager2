package biz.xsoftware.impl.nio.util;

import java.net.SocketException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.TCPChannel;

public abstract class UtilTCPChannel extends UtilChannel {
	
	public UtilTCPChannel(Channel realChannel) {
		super(realChannel);
	}

	@Override
	protected TCPChannel getRealChannel() {
		return (TCPChannel) super.getRealChannel();
	}

	public void oldClose() {
		TCPChannel realChannel = getRealChannel();
		realChannel.oldClose();
	}
	
	public boolean getKeepAlive() throws SocketException {
		TCPChannel realChannel = getRealChannel();
		return realChannel.getKeepAlive();
	}

	public void setKeepAlive(boolean b) throws SocketException {
		TCPChannel realChannel = getRealChannel();
		realChannel.setKeepAlive(b);
	}
	
}
