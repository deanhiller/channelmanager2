package biz.xsoftware.impl.nio.util;

import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.handlers.DataChunk;

public class PacketChunk implements DataChunk {

	private ByteBuffer data;
	private DataChunk chunk;

	public PacketChunk(ByteBuffer b, DataChunk chunk) {
		this.data = b;
		this.chunk = chunk;
	}

	@Override
	public ByteBuffer getData() {
		return data;
	}

	@Override
	public void setProcessed() {
		chunk.setProcessed();
	}

	@Override
	public boolean releaseBuffer() {
		return true;
	}

}
