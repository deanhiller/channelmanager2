package biz.xsoftware.impl.nio.cm.exception;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.RegisterableChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.deprecated.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.OperationCallback;
import biz.xsoftware.impl.nio.util.UtilTCPChannel;

class ExcTCPChannel extends UtilTCPChannel implements TCPChannel {

	private static final Logger log = Logger.getLogger(ExcTCPChannel.class.getName());
//	private BufferHelper helper = ChannelManagerFactory.bufferHelper(null);
	private static final OperationCallback NULL_WRITE_HANDLER = new NullWriteHandler();
	
	private TCPChannel realChannel;
	
	public ExcTCPChannel(TCPChannel channel) {
		super(channel);
		this.realChannel = channel;
	}
	
	public void registerForReads(DataListener listener) throws IOException, InterruptedException {
		ExcProxyDataHandler handler = new ExcProxyDataHandler(this, listener);
		realChannel.registerForReads(handler);
	}

	@Override
	public int oldWrite(ByteBuffer b) throws IOException {
		return realChannel.oldWrite(b);
	}
	
	public void oldWrite(ByteBuffer b, OperationCallback h) throws IOException, InterruptedException {
		OperationCallback callback;
		if(h == null) {
			callback = NULL_WRITE_HANDLER;
		}else
			callback = h;
		realChannel.oldWrite(b, new ExcProxyWriteHandler(this, callback));
	}
	
	public void oldConnect(SocketAddress addr, ConnectionCallback c) throws IOException, InterruptedException {
		if(c == null)
			throw new IllegalArgumentException("ConnectCallback cannot be null");
		
		ExcProxyConnectCb proxy = new ExcProxyConnectCb(this, c);
		realChannel.oldConnect(addr, proxy);
	}

	public void oldClose(OperationCallback h) {
		OperationCallback callback;
		if(h == null) {
			callback = NULL_WRITE_HANDLER;
		}else
			callback = h;
		realChannel.oldClose(new ExcProxyWriteHandler(this, callback));
	}	
	
	private static class NullWriteHandler implements OperationCallback {

		public void finished(Channel c) throws IOException {
		}

		public void failed(RegisterableChannel c, Throwable e) {
			log.log(Level.WARNING, "Exception trying to write", e);
		}
		
	}

    
}
