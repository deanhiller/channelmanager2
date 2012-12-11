package biz.xsoftware.impl.nio.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.DataListener;

public class UtilReaderProxy implements DataListener {

    private static final Logger log = Logger.getLogger(UtilReaderProxy.class.getName());
	private Channel channel;
	private DataListener handler;

	public UtilReaderProxy(Channel c, DataListener h) {
		super();
		this.channel = c;
		this.handler = h;
	}

	public void incomingData(Channel realChannel, ByteBuffer b) throws IOException {
		handler.incomingData(channel, b);
        
        if(b.remaining() > 0) {
            log.warning("Discarding unread data("+b.remaining()+") from class="+handler.getClass().getName());
            b.position(b.limit());
        }
	}

	public void farEndClosed(Channel realChannel) {
		handler.farEndClosed(channel);
	}

	public void failure(Channel realChannel, ByteBuffer data, Exception e) {
		handler.failure(channel, data, e);		
	}

}
