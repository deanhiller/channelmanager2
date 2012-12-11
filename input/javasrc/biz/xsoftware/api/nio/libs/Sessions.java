package biz.xsoftware.api.nio.libs;

public abstract class Sessions {

	private static Sessions currentInstance;

	public static ChannelSession getSession() {
		return currentInstance.getSessionImpl();
	}

	protected static void setCurrentInstance(Sessions sessions) {
		currentInstance = sessions;
	}
	
	protected abstract ChannelSession getSessionImpl();

	
}
