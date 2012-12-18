package biz.xsoftware.impl.nio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.management.Notification;
import javax.management.NotificationListener;

import biz.xsoftware.api.nio.channels.DatagramChannel;
import biz.xsoftware.api.nio.channels.TCPChannel;
import biz.xsoftware.api.nio.channels.TCPServerChannel;
import biz.xsoftware.api.nio.channels.UDPChannel;
import biz.xsoftware.api.nio.deprecated.ChannelManager;
import biz.xsoftware.api.nio.deprecated.ChannelService;
import biz.xsoftware.api.nio.deprecated.ChannelServiceFactory;
import biz.xsoftware.api.nio.deprecated.Settings;
import biz.xsoftware.api.nio.libs.BufferFactory;
import biz.xsoftware.api.nio.libs.FactoryCreator;
import biz.xsoftware.api.nio.libs.StartableExecutorService;
import biz.xsoftware.api.nio.mgmt.BufferFactoryMBean;
import biz.xsoftware.api.nio.mgmt.ChannelMgrSvcMBean;
import biz.xsoftware.api.nio.mgmt.ExecutorServiceMBean;

/**
 * TODO: Try to move this class to impl packages without breaking the design and without
 * breaking osgi.  This should be doable!!!!!!!
 * 
 */
public class ChannelManagerServiceImpl implements ChannelService, ChannelMgrSvcMBean {

	private static final Logger log = Logger.getLogger(ChannelManagerServiceImpl.class.getName());
	private static final NotificationListener NULL_LIST = new NullNotifyListener();
	private ChannelService mgr;
	
	private BufferFactoryMBean bufBean;
	private ExecutorServiceMBean execBean;
	private NotificationListener notifyList = NULL_LIST;
	
	public ChannelManagerServiceImpl() {
		log.info("constructed object");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FactoryCreator.KEY_IS_DIRECT, false);
		map.put(FactoryCreator.KEY_NUM_THREADS, 10);		

		FactoryCreator creator = FactoryCreator.createFactory(null);		
		BufferFactory bufferFactory = creator.createBufferFactory(map);
		StartableExecutorService execSvcFactory = creator.createExecSvcFactory(map);
		bufBean = (BufferFactoryMBean)bufferFactory;
		execBean = (ExecutorServiceMBean)execSvcFactory;
		mgr = createChannelManager(bufferFactory, execSvcFactory);		
	}

    public TCPServerChannel createTCPServerChannel(String id, Settings h) throws IOException {
        TCPServerChannel chan = mgr.createTCPServerChannel(id, h);
        notifyList.handleNotification(new Notification("register", this, 0), chan);
        return chan;
    }

    public TCPChannel createTCPChannel(String id, Settings h) throws IOException {
        TCPChannel chan = mgr.createTCPChannel(id, h);
        notifyList.handleNotification(new Notification("register", this, 0), chan);
        return chan;        
    } 
    
    public UDPChannel createUDPChannel(String id, Settings h) throws IOException {
        UDPChannel chan = mgr.createUDPChannel(id, h);
        notifyList.handleNotification(new Notification("register", this, 0), chan);
        return chan;
    }
    
    public DatagramChannel createDatagramChannel(String id, int bufferSize) throws IOException {
        DatagramChannel chan = mgr.createDatagramChannel(id, bufferSize);
        notifyList.handleNotification(new Notification("register", this, 0), chan);
        return chan;
    }

	public void start() throws IOException {
		log.info("Starting ChannelManager component");
		mgr.start();
		log.info("Started ChannelManager component");
	}

	public void stop() throws IOException, InterruptedException {
		log.info("Stopping ChannelManager component");
		mgr.stop();
		log.info("Stopped ChannelManager component");
	}

	public void setNotificationListener(NotificationListener l) {
		if(l == null)
			notifyList = NULL_LIST;
		else
			notifyList = l;
	}
	
	private static class NullNotifyListener implements NotificationListener {
		public void handleNotification(Notification notification, Object handback) {
		}
	}
	
	protected ChannelService createChannelManager(BufferFactory bufferFactory, StartableExecutorService execSvcFactory) {
		
		ChannelServiceFactory factory = ChannelServiceFactory.createDefaultStack();
		
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ChannelManager.KEY_ID, "ChanMgr");
		props.put(ChannelManager.KEY_EXECUTORSVC_FACTORY, execSvcFactory);
		props.put(ChannelManager.KEY_BUFFER_FACTORY, bufferFactory);
		ChannelService mgr = factory.createChannelManager(props);
		return mgr;
	}
	public void setDirect(boolean b) {
		bufBean.setDirect(b);
	}
	public boolean isDirect() {
		return bufBean.isDirect();
	}
	public String getName() {
		return execBean.getName();
	}
	public int getMaximumPoolSize() {
		return execBean.getMaximumPoolSize();
	}
	public void setMaximumPoolSize(int numThreads) {
		execBean.setMaximumPoolSize(numThreads);
	}
	public boolean isDaemonThreads() {
		return execBean.isDaemonThreads();
	}
	public boolean isRunning() {
		return execBean.isRunning();
	}
	public int getCorePoolSize() {
		return execBean.getCorePoolSize();
	}
	public void setCorePoolSize(int numThreads) {
		execBean.setCorePoolSize(numThreads);
	}
	public int getPoolSize() {
		return execBean.getPoolSize();
	}
	public int getLargestPoolSize() {
		return execBean.getLargestPoolSize();
	}
	public int getActiveCount() {
		return execBean.getActiveCount();
	}
	public long getCompletedTaskCount() {
		return execBean.getCompletedTaskCount();
	}
	public long getKeepAliveTime() {
		return execBean.getKeepAliveTime();
	}
	public void setKeepAliveTime(long time) {
		execBean.setKeepAliveTime(time);
	}
	public long getTaskCount() {
		return execBean.getTaskCount();
	}
	public int getQueueSize() {
		return execBean.getQueueSize();
	}
	public void setQueueSize(int max) {
		execBean.setQueueSize(max);
	}
	public int getRemainingCapacity() {
		return execBean.getRemainingCapacity();
	}
	public int getCurrentSize() {
		return execBean.getCurrentSize();
	}	
}
