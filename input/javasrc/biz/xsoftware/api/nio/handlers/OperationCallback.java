package biz.xsoftware.api.nio.handlers;


import java.io.IOException;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;

public interface OperationCallback {

	public void finished(Channel c) throws IOException;
	
	public void failed(RegisterableChannel c, Throwable e);
	
}
