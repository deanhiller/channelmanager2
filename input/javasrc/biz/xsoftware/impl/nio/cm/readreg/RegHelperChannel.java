package biz.xsoftware.impl.nio.cm.readreg;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import biz.xsoftware.api.nio.channels.Channel;
import biz.xsoftware.api.nio.handlers.DataListener;
import biz.xsoftware.impl.nio.util.UtilChannel;

public class RegHelperChannel extends UtilChannel implements Channel {

	private static final Logger apiLog = Logger.getLogger(Channel.class.getName());
	protected DataListener cachedListener;
	protected boolean isRegistered = false;
	public RegHelperChannel(Channel realChannel) {
		super(realChannel);
	}

	@Override
	public synchronized void registerForReads(DataListener listener) throws IOException, InterruptedException {
		if(apiLog.isLoggable(Level.FINE))
			apiLog.fine(this+"RegRead.registerForReads called");
		cachedListener = listener;
		if(!isConnected()) {
			return;
		}
	
		getRealChannel().registerForReads(listener);
		isRegistered = true;
	}
	
	@Override
	public synchronized void unregisterForReads() throws IOException, InterruptedException {
		if(apiLog.isLoggable(Level.FINE))
			apiLog.fine(this+"RegRead.unregisterForReads called");
		cachedListener = null;
		if(!isConnected()) {
			return;
		}
		getRealChannel().unregisterForReads();
		isRegistered = false;
	}

	@Override
	public synchronized void oldConnect(SocketAddress addr) throws IOException {
		if(apiLog.isLoggable(Level.FINE))
			apiLog.fine(this+"RegRead.connect called-addr="+addr);
		
		super.oldConnect(addr);
		if(cachedListener != null) {
			try {
				getRealChannel().registerForReads(cachedListener);
				isRegistered = true;
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
		
}
