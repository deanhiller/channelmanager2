package biz.xsoftware.impl.nio.libs;

import java.nio.ByteBuffer;

import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.api.nio.mgmt.BufferFactoryMBean;

class DefaultByteBufferFactory implements BufferFactory, BufferFactoryMBean {

	private boolean isDirect;

	public ByteBuffer createBuffer(Object id, int size) {
		if(isDirect)
			return ByteBuffer.allocateDirect(size);

		return ByteBuffer.allocate(size);
	}

	public void setDirect(boolean b) {
		isDirect = b;
	}

	public boolean isDirect() {
		return isDirect;
	}

}
