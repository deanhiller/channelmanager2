package biz.xsoftware.test.nio.fullcontrol;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface Key {

	SelectableChannel channel();

	Selector selector();

	int interestOps();

	SelectionKey interestOps(int ops);

	int readyOps();

}
