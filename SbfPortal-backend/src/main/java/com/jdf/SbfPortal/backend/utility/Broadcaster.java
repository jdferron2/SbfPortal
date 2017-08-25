package com.jdf.SbfPortal.backend.utility;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.jdf.SbfPortal.backend.DAO.SbfDraftRecordDAOMysql;
import com.vaadin.server.VaadinSession;

public class Broadcaster implements Serializable {
	private static final long serialVersionUID = -936517971387479256L;
	private static Logger logger = Logger.getLogger(SbfDraftRecordDAOMysql.class);
	
	static ExecutorService executorService =
        Executors.newSingleThreadExecutor();

    public interface BroadcastListener {
        void receiveBroadcast(String message);
		void receiveBroadcast(VaadinSession ses, String command, Object[] args);
		Integer getLeagueId();
    }

    private static LinkedList<BroadcastListener> listeners =
        new LinkedList<BroadcastListener>();

    public static synchronized void register(
            BroadcastListener listener) {
        listeners.add(listener);
        logger.info("New UI has been registered, listener count: " + listeners.size());
    }

    public static synchronized void unregister(
            BroadcastListener listener) {
        listeners.remove(listener);
        logger.info("UI unregister, listener count: " + listeners.size());
    }

    public static synchronized void broadcast(
            final String message) {
        for (final BroadcastListener listener: listeners)
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    listener.receiveBroadcast(message);
                }
            });
    }
    
    public static synchronized void broadcast(Integer leagueId,
            final VaadinSession ses, final String command, final Object[] args) {
    	logger.info("Broadcast command "+ command + " is being sent to " + listeners.size() + " clients...");
        for (final BroadcastListener listener: listeners){
        	if( listener.getLeagueId() == leagueId)
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    listener.receiveBroadcast(ses, command, args);
                }
            });
        }
    }
}