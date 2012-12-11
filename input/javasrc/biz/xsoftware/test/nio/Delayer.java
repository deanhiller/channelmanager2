package biz.xsoftware.test.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.BufferHelper;
import biz.xsoftware.api.nio.ChannelServiceFactory;
import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.api.nio.libs.FactoryCreator;

public class Delayer implements DataListener {

	private static final Logger log = Logger.getLogger(Delayer.class.getName());
	private static final BufferHelper HELPER = ChannelServiceFactory.bufferHelper(null);
	private BufferFactory bufFactory;
	private static Timer timer = new Timer();
	private TCPChannel to;


	public Delayer(TCPChannel to) {
		this.to = to;
		if(bufFactory == null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FactoryCreator.KEY_IS_DIRECT, false);
			FactoryCreator creator = FactoryCreator.createFactory(null);
			bufFactory = creator.createBufferFactory(map);			
		}		
	}
	public void incomingData(Channel channel, ByteBuffer b) throws IOException {
		final ByteBuffer newBuffer = bufFactory.createBuffer(channel, b.remaining());
		newBuffer.put(b);
		TimerTask t = new TimerTask() {
			@Override
			public void run() {
				try {
					HELPER.doneFillingBuffer(newBuffer);
					to.write(newBuffer);
				} catch (Exception e) {
					log.log(Level.WARNING, "exception", e);
				}
			}
			
		};
		timer.schedule(t, 1000);
	}

	public void farEndClosed(Channel channel) {
		TimerTask t = new TimerTask() {
			@Override
			public void run() {
				try {
					to.close();
				} catch (Exception e) {
					log.log(Level.WARNING, "exception", e);
				}
			}
			
		};
		timer.schedule(t, 1000);
	}
	public void failure(Channel channel, ByteBuffer data, Exception e) {
		log.warning(channel+"Data not received");
	}

	
}
