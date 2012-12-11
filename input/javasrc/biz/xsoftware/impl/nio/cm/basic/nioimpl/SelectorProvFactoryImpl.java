package biz.xsoftware.impl.nio.cm.basic.nioimpl;

import java.nio.channels.spi.SelectorProvider;

import biz.xsoftware.api.nio.testutil.nioapi.Select;
import biz.xsoftware.api.nio.testutil.nioapi.SelectorProviderFactory;

/**
 */
public class SelectorProvFactoryImpl implements SelectorProviderFactory
{

    /**
     */
    public Select provider(String id)
    {
        SelectorProvider provider = SelectorProvider.provider();
        return new SelectorImpl(id, provider);
    }

}
