package biz.xsoftware.impl.nio.cm.basic;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import biz.xsoftware.api.nio.handlers.DataChunk;
import biz.xsoftware.impl.nio.util.DataChunkImpl;
import biz.xsoftware.impl.nio.util.ProcessedListener;

public class BufferPool {

	public Set<DataChunkImpl> freePackets = new HashSet<DataChunkImpl>();
	
	public synchronized DataChunkImpl nextBuffer(Object id, ProcessedListener l) {
		Iterator<DataChunkImpl> iter = freePackets.iterator();
		if(iter.hasNext()) {
			DataChunkImpl first = iter.next();
			first.setListener(l);
			first.setId(id);
			iter.remove();
			return first;
		}
		
		//the default size of our read buffers is 1000
		//and the packet layer OR encryption layer may increase the
		//size of the buffer
		ByteBuffer b = ByteBuffer.allocate(1000);
		DataChunkImpl impl = new DataChunkImpl(b);
		impl.setListener(l);
		impl.setId(id);
		return impl;
	}

	public void releaseChunk(DataChunk chunk) {
		if(freePackets.size() > 300)
			return; //we discard more than 300 buffers as we don't want to take up too much memory

		DataChunkImpl c = (DataChunkImpl) chunk;
		freePackets.add(c);
	}
}