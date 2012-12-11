package biz.xsoftware.api.safethread;

/**
 */
public interface ExceptionListener
{
    public void fireFailure(Throwable e, Object id);
}
