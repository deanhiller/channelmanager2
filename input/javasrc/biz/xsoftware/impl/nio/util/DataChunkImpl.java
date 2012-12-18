package biz.xsoftware.impl.nio.util;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.handlers.DataChunk;

public class DataChunkImpl implements DataChunk {

	private static final Logger log = Logger.getLogger(DataChunkImpl.class.getName());
	private ByteBuffer data;
	private ProcessedListener listener = null;
	private BufferListener bufferListener;
	private Object id;

	public DataChunkImpl(Object id, ByteBuffer newBuffer, BufferListener l) {
		this.data = newBuffer;
		this.bufferListener = l;
		this.id = id;
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
		}
	}

	public void setListener(ProcessedListener l) {
		this.listener = l;
	}

	public boolean releaseBuffer() {
		boolean fullyRead = true;
		if(data != null) {
			if(data.hasRemaining()) {
				fullyRead = false;
				log.log(Level.WARNING, id+"Discarding unread data("+data.remaining()+")", new RuntimeException().fillInStackTrace());
			}
			data.clear();
			bufferListener.releaseBuffer(data);
			data = null;
		}
		return fullyRead;
	}
}
