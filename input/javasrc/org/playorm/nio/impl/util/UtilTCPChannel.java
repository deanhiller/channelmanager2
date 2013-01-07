package org.playorm.nio.impl.util;

import java.net.SocketException;

import javax.net.ssl.SSLEngine;

import org.playorm.nio.api.channels.Channel;
import org.playorm.nio.api.channels.TCPChannel;
import org.playorm.nio.api.handlers.FutureOperation;


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
	
	public FutureOperation openSSL(SSLEngine engine) {
		TCPChannel realChannel = getRealChannel();
		return realChannel.openSSL(engine);
	}

	public FutureOperation closeSSL() {
		TCPChannel realChannel = getRealChannel();
		return realChannel.closeSSL();
	}
}
