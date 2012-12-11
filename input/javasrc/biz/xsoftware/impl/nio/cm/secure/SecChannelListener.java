package biz.xsoftware.impl.nio.cm.secure;

import java.io.IOException;

interface SecChannelListener {

	String getId();

	void close() throws IOException;

}
