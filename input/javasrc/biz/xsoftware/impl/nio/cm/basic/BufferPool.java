package biz.xsoftware.impl.nio.cm.basic;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import biz.xsoftware.impl.nio.util.BufferListener;
import biz.xsoftware.impl.nio.util.DataChunkImpl;
import biz.xsoftware.impl.nio.util.ProcessedListener;

public class BufferPool implements BufferListener {

	public Set<ByteBuffer> freePackets = new HashSet<ByteBuffer>();
	
	public synchronized DataChunkImpl nextBuffer(Object id, ProcessedListener l) {
		ByteBuffer b = null;
		Iterator<ByteBuffer> iter = freePackets.iterator();
		if(iter.hasNext()) {
			b = iter.next();
			iter.remove();
		} else 
			b = ByteBuffer.allocate(1000);
		
		DataChunkImpl impl = new DataChunkImpl(id, b, this);
		impl.setListener(l);
		return impl;
	}

	public synchronized void releaseBuffer(ByteBuffer buffer) {
		if(freePackets.size() > 300)
			return; //we discard more than 300 buffers as we don't want to take up too much memory

		buffer.clear();
		freePackets.add(buffer);
	}

}
