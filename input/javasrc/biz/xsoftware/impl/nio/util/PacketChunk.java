package biz.xsoftware.impl.nio.util;

import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.handlers.DataChunk;

public class PacketChunk implements DataChunk {

	private ByteBuffer data;
	private DataChunk c;

	public PacketChunk(ByteBuffer out, DataChunk c) {
		this.data = out;
		this.c = c;
	}

	@Override
	public ByteBuffer getData() {
		return data;
	}

	@Override
	public void setProcessed() {
		c.setProcessed();
	}
}