package biz.xsoftware.impl.nio.libs;

import biz.xsoftware.api.nio.libs.PacketProcessor;
import biz.xsoftware.api.nio.libs.PacketProcessorFactory;

class DefaultPackProcessorFactory implements PacketProcessorFactory, PacketProcessorMBean {

	private byte[] separator;
	
	public PacketProcessor createPacketProcessor(Object id) {
		return new HeaderTrailerProcessor(id, separator);
	}

	public void setSeparator(byte[] bytes) {
		separator = bytes;
	}

	public byte[] getSeparator() {
		return separator;
	}

}
