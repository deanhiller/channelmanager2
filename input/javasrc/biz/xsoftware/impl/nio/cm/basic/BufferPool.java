package biz.xsoftware.impl.nio.cm.basic;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import biz.xsoftware.api.nio.handlers.DataChunk;
import biz.xsoftware.api.nio.handlers.ProcessedListener;
import biz.xsoftware.impl.nio.util.DataChunkImpl;

public class BufferPool implements ProcessedListener {

	public Set<DataChunkImpl> freePackets = new HashSet<DataChunkImpl>();
	
	public synchronized DataChunkImpl nextBuffer() {
		Iterator<DataChunkImpl> iter = freePackets.iterator();
		if(iter.hasNext()) {
			DataChunkImpl first = iter.next();
			iter.remove();
			first.clearChunk();
			first.addProcessedListener(this);
			return first;
		}
		
		//the default size of our read buffers is 1000
		//and the packet layer OR encryption layer may increase the
		//size of the buffer
		ByteBuffer b = ByteBuffer.allocate(1000);
		DataChunkImpl impl = new DataChunkImpl(b);
		return impl;
	}

	@Override
	public synchronized void processed(DataChunk chunk) {
		if(freePackets.size() > 300)
			return; //we discard more than 300 buffers as we don't want to take up too much memory
		
		freePackets.add((DataChunkImpl) chunk);
	}

}
