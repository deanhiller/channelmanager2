package biz.xsoftware.impl.nio.cm.basic;

import java.net.PortUnreachableException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.handlers.WriteCloseCallback;

public class WriteRunnable implements DelayedWritesCloses {

	private static final Logger apiLog = Logger.getLogger(WriteCloseCallback.class.getName());
	private static final Logger log = Logger.getLogger(WriteRunnable.class.getName());
	private ByteBuffer buffer;
	private WriteCloseCallback handler;
	private int id;
	private BasChannelImpl channel;

	public WriteRunnable(BasChannelImpl c, ByteBuffer b, WriteCloseCallback h, int id) {
		channel = c;
		buffer = b;
		handler = h;
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public boolean runDelayedAction(boolean isSelectorThread) {
		try {

			channel.writeImpl(buffer);

            //log.info("count="+count+++"  remain="+buffer.remaining()+" wasRemain="+remain);
//			log.info(channel+"CCwriter thread id="+id);
        } catch(PortUnreachableException e) {
            //if a client sends a stream of udp, we fire a failure for each one, but only log it
            //at the finest level as these are not really devastating sometimes.  They are really just
            //telling someone that you are sending to a bad port or bad host or unreachable host
            log.log(Level.FINEST,  channel+"Client sent data to a host or port that is not listening " +
                    "to udp, or udp can't get through to that machine", e);
            handler.failed(channel, id, e);            
		} catch(Exception e) {
			log.log(Level.WARNING, channel+"Fire failure to client", e);
			handler.failed(channel, id, e);
			//we failed so return that the write was tried...no more data is going out
            //at least I don't think so...is it different when getting an icmp(PortUnreachableException)?
            return true; 
		}
		if(buffer.hasRemaining())
			return false;
              
		if(apiLog.isLoggable(Level.FINER))
			log.finer(channel+"WriteCloseCallback.finished called on client");
		
		handler.finished(channel, id);
		return true;
	}

}
