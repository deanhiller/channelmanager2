package org.playorm.api.safethread;

/**
 */
public interface ExceptionListener
{
    public void fireFailure(Throwable e, Object id);
}
