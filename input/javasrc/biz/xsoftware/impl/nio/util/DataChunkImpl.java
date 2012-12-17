package biz.xsoftware.impl.nio.util;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.handlers.DataChunk;

public class DataChunkImpl implements DataChunk {

	private static final Logger log = Logger.getLogger(DataChunkImpl.class.getName());
	
	private ByteBuffer data;
	private ProcessedListener listener = null;

	private Object id;
	
	public DataChunkImpl(ByteBuffer newBuffer) {
		this.data = newBuffer;
	}

	@Override
	public ByteBuffer getData() {
		return data;
	}

	@Override
	public void setProcessed() {
		if(listener != null) {
			listener.processed(this);
			listener = null;
			if(data.hasRemaining()) {
				log.log(Level.WARNING, id+"Discarding unread data("+data.remaining()+") from class", new RuntimeException().fillInStackTrace());
			}
			data.clear();
		}
	}

	public void setListener(ProcessedListener l) {
		this.listener = l;
	}

	public void setId(Object id) {
		this.id = id;
	}

}
