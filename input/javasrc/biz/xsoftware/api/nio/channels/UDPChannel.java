package biz.xsoftware.api.nio.channels;

import java.io.IOException;

/**
 * @author Dean Hiller
 */
public interface UDPChannel extends Channel {

    /**
     * @throws IOException 
     * 
     */
    public void oldDisconnect() throws IOException;

}
