package biz.xsoftware.impl.nio.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import biz.xsoftware.api.nio.handlers.DataChunk;
import biz.xsoftware.api.nio.handlers.ProcessedListener;

public class DataChunkImpl implements DataChunk {

	private ByteBuffer data;
	private List<ProcessedListener> listeners = new ArrayList<ProcessedListener>();
	
	public DataChunkImpl(ByteBuffer newBuffer) {
		this.data = newBuffer;
	}

	@Override
	public ByteBuffer getData() {
		return data;
	}

	@Override
	public void setProcessed() {
	}

	public void addProcessedListener(ProcessedListener l) {
		listeners.add(l);
	}
	public void removeProcessedListener(ProcessedListener l) {
		listeners.remove(l);
	}

	public void clearChunk() {
		listeners.clear();
		data.clear();
	}
}
