package biz.xsoftware.api.nio.libs;

import biz.xsoftware.api.nio.channels.RegisterableChannel;

public interface ChannelSession {

	public RegisterableChannel getChannel();

	public void put(Object key, Object value);
	
	public Object get(Object key);
}
