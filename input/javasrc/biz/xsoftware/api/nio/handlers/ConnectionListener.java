package biz.xsoftware.api.nio.handlers;


import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;

public interface ConnectionListener {
	
	public void connected(Channel channel) throws IOException;
	
	public void failed(RegisterableChannel channel, Throwable e);
}
