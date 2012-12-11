package biz.xsoftware.impl.nio.cm.basic.nioimpl;

import java.nio.channels.SelectionKey;

import biz.xsoftware.api.nio.testutil.nioapi.SelectKey;

/**
 */
public class SelectKeyImpl implements SelectKey
{

    private SelectionKey key;

    /**
     * Creates an instance of SelectKeyImpl.
     * @param key
     */
    public SelectKeyImpl(SelectionKey key)
    {
        if(key == null)
            throw new IllegalArgumentException("key must not be null");
        this.key = key;
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.nioapi.SelectKey#attachment()
     */
    public Object attachment()
    {
        return key.attachment();
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.nioapi.SelectKey#interestOps()
     */
    public int interestOps()
    {
        return key.interestOps();
    }

    /**
     * @see biz.xsoftware.api.nio.testutil.nioapi.SelectKey#interestOps(int)
     */
    public SelectKey interestOps(int opsNow)
    {
        key.interestOps(opsNow);
        return this;
    }

}
