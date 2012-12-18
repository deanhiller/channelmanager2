package biz.xsoftware.api.nio.handlers;

import java.nio.ByteBuffer;

public interface DataChunk {

	/**
	 * IMPORTANT: After reading ALL the data from ByteBuffer, and potentially after writing something
	 * upstream to another node, you MUST call setProcessed or we will not read any more data in 
	 * from the socket.  Why? Because this enables tcp flow control to take affect so the downstream server is slowed
	 * down and can't write to the socket anymore until you have processed your data.  It is a very clean way of
	 * slowly degrading and forcing clients to slowdown while you catchup.
	 * 
	 * @return
	 */
	public ByteBuffer getData();
	
	/**
	 * 
	 */
	public void setProcessed();

	/**
	 * Calling this allows lower levers to recycle the buffers.  If you don't call it, it will just be garbage
	 * collected.  If you do call it, it may or may not be added back to a buffer pool to be re-used.  It depends
	 * on how the layers are setup.  This also
	 * @param c
	 * @param handler
	 * @return 
	 */
	public boolean releaseBuffer();

}
