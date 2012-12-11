package biz.xsoftware.impl.nio.libs;

public interface PacketProcessorMBean {
	
	public void setSeparator(byte[] bytes);
	public byte[] getSeparator();
}
