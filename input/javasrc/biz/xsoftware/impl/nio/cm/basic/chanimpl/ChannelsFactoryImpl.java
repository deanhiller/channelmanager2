package biz.xsoftware.impl.nio.cm.basic.chanimpl;

import java.io.IOException;

import biz.xsoftware.api.nio.testutil.chanapi.ChannelsFactory;
import biz.xsoftware.api.nio.testutil.chanapi.SocketChannel;

/**
 */
public class ChannelsFactoryImpl implements ChannelsFactory
{

    /**
     * @throws IOException 
     * @see biz.xsoftware.api.nio.testutil.chanapi.ChannelsFactory#open()
     */
    public SocketChannel open() throws IOException {
        java.nio.channels.SocketChannel channel = java.nio.channels.SocketChannel.open();
        return new SocketChannelImpl(channel);
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.chanapi.ChannelsFactory#open(java.nio.channels.SocketChannel)
     */
    public SocketChannel open(java.nio.channels.SocketChannel newChan) {
        return new SocketChannelImpl(newChan);
    }

}
