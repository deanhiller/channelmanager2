package biz.xsoftware.impl.nio.cm.exception;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.handlers.ConnectionCallback;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.handlers.WriteCloseCallback;
import biz.xsoftware.impl.nio.util.UtilTCPChannel;

class ExcTCPChannel extends UtilTCPChannel implements TCPChannel {

	private static final Logger log = Logger.getLogger(ExcTCPChannel.class.getName());
//	private BufferHelper helper = ChannelManagerFactory.bufferHelper(null);
	private static final WriteCloseCallback NULL_WRITE_HANDLER = new NullWriteHandler();
	
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
	public int write(ByteBuffer b) throws IOException {
		return realChannel.write(b);
	}
	
	public void write(ByteBuffer b, WriteCloseCallback h, int id) throws IOException, InterruptedException {
		WriteCloseCallback callback;
		if(h == null) {
			callback = NULL_WRITE_HANDLER;
		}else
			callback = h;
		realChannel.write(b, new ExcProxyWriteHandler(this, callback), id);
	}
	
	public void connect(SocketAddress addr, ConnectionCallback c) throws IOException, InterruptedException {
		if(c == null)
			throw new IllegalArgumentException("ConnectCallback cannot be null");
		
		ExcProxyConnectCb proxy = new ExcProxyConnectCb(this, c);
		realChannel.connect(addr, proxy);
	}

	public void close(WriteCloseCallback h, int id) {
		WriteCloseCallback callback;
		if(h == null) {
			callback = NULL_WRITE_HANDLER;
		}else
			callback = h;
		realChannel.close(new ExcProxyWriteHandler(this, callback), id);
	}	
	
	private static class NullWriteHandler implements WriteCloseCallback {

		public void finished(Channel c, int id) {
		}

		public void failed(Channel c, int id, Throwable e) {
			log.log(Level.WARNING, "Exception trying to write", e);
		}
		
	}

    
}
