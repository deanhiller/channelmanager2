package biz.xsoftware.api.nio.testutil.nioapi;


/**
 */
public interface SelectKey
{

    /**
     */
    Object attachment();

    /**
     */
    int interestOps();

    /**
     * @param opsNow
     */
    SelectKey interestOps(int opsNow);

}
