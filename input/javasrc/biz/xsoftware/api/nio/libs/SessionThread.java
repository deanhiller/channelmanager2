package biz.xsoftware.api.nio.libs;

public interface SessionThread {
	
	@Deprecated
	public SessionContext getSessionState();
	
	public ChannelSession getSession();
	
}
