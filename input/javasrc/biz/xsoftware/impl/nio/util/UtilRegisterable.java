package biz.xsoftware.impl.nio.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import biz.xsoftware.api.nio.channels.RegisterableChannel;


/**
 * @author Dean Hiller
 */
public class UtilRegisterable implements RegisterableChannel {

	private RegisterableChannel realChannel;

	public UtilRegisterable(RegisterableChannel realChannel) {
		this.realChannel = realChannel;
	}
	
	protected RegisterableChannel getRealChannel() {
		return realChannel;
	}
	
	public String toString() {
		return realChannel.toString();
	}
	
	public void close() {
		realChannel.close();
	}
	
	public boolean isClosed() {
		return realChannel.isClosed();
	}
	
	public boolean isBlocking() {
		return realChannel.isBlocking();
	}
	
	public void setReuseAddress(boolean b) throws SocketException {
		realChannel.setReuseAddress(b);
	}	

	public void bind(SocketAddress addr) throws IOException {
		realChannel.bind(addr);
	}

	public boolean isBound() {
		return realChannel.isBound();
	}

	public InetSocketAddress getLocalAddress() {
		return realChannel.getLocalAddress();
	}

    /**
     * @see biz.xsoftware.api.nio.channels.RegisterableChannel#setName(java.lang.String)
     */
    public void setName(String name)
    {
        realChannel.setName(name);
    }

    /**
     * @see biz.xsoftware.api.nio.channels.RegisterableChannel#getName()
     */
    public String getName()
    {
        return realChannel.getName();
    }
}


