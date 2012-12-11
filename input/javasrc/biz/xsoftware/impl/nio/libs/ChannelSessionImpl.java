package biz.xsoftware.impl.nio.libs;

import java.util.HashMap;
import java.util.Map;

import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.libs.ChannelSession;

public class ChannelSessionImpl implements ChannelSession {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RegisterableChannel channel;
	private Map<Object, Object> map = new HashMap<Object, Object>();
	
	public ChannelSessionImpl(RegisterableChannel c) {
		this.channel = c;
	}
	
	public RegisterableChannel getChannel() {
		return channel;
	}

	public String toString() {
		return ""+channel;
	}
	
	public Object get(Object key) {
		return map.get(key);
	}
	
	public void put(Object key, Object value) {
		map.put(key, value);
	}
}
