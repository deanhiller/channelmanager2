package biz.xsoftware.impl.nio.libs;

import biz.xsoftware.api.nio.libs.ChannelSession;
import biz.xsoftware.api.nio.libs.SessionThread;
import biz.xsoftware.api.nio.libs.Sessions;

public final class SessionsImpl extends Sessions {

	private SessionsImpl() {
	}

	public static void init() {
		SessionsImpl impl = new SessionsImpl();
		Sessions.setCurrentInstance(impl);
	}
	
	@Override
	protected ChannelSession getSessionImpl() {
		SessionThread thread = (SessionThread)Thread.currentThread();
		return thread.getSession();
	}

}
