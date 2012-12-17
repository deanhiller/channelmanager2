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

	public boolean getKeepAlive() throws SocketException {
		return getRealChannel().getKeepAlive();
	}

	public void setKeepAlive(boolean b) throws SocketException {
		getRealChannel().setKeepAlive(b);
	}  	
	
}
