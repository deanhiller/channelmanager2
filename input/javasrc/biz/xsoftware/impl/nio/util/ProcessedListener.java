package biz.xsoftware.impl.nio.util;

import java.nio.ByteBuffer;

public interface ProcessedListener {

	void processed(DataChunkImpl chunk);

	void releaseBuffer(ByteBuffer data);

}
