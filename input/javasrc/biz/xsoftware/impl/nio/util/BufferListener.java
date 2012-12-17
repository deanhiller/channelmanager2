package biz.xsoftware.impl.nio.util;

import java.nio.ByteBuffer;

public interface BufferListener {

	void releaseBuffer(ByteBuffer data);

}
