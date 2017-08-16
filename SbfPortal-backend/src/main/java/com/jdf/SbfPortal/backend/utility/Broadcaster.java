package com.jdf.SbfPortal.backend.utility;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vaadin.server.VaadinSession;

public class Broadcaster implements Serializable {
	private static final long serialVersionUID = -936517971387479256L;
	static ExecutorService executorService =
        Executors.newSingleThreadExecutor();

    public interface BroadcastListener {
        void receiveBroadcast(String message);
        void receiveBroadcast(VaadinSession ses, String command, Object obj);
    }

    private static LinkedList<BroadcastListener> listeners =
        new LinkedList<BroadcastListener>();

    public static synchronized void register(
            BroadcastListener listener) {
        listeners.add(listener);
    }

    public static synchronized void unregister(
            BroadcastListener listener) {
        listeners.remove(listener);
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
    
    public static synchronized void broadcast(
            final VaadinSession ses, final String command, final Object obj) {
        for (final BroadcastListener listener: listeners)
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    listener.receiveBroadcast(ses, command, obj);
                }
            });
    }
}