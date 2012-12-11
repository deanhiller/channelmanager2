package biz.xsoftware.api.nio.testutil.nioapi;

import java.nio.channels.ClosedChannelException;

public interface SelectorRunnable {

	void run() throws ClosedChannelException;

}
