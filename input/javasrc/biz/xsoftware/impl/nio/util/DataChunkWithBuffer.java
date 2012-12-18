package biz.xsoftware.impl.nio.util;

import biz.xsoftware.api.nio.handlers.DataChunk;

public interface DataChunkWithBuffer extends DataChunk {

	public void releaseBuffer(String logInfo);
	
	public void setProcessedImpl();
	
}
