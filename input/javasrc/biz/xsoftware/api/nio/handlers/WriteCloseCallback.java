package biz.xsoftware.api.nio.handlers;


import biz.xsoftware.api.nio.channels.Channel;

public interface WriteCloseCallback {

	public void finished(Channel c, int id);
	
	public void failed(Channel c, int id, Throwable e);
	
}
