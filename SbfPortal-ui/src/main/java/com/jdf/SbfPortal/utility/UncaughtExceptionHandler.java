package com.jdf.SbfPortal.utility;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

@SuppressWarnings("serial")
public class UncaughtExceptionHandler implements ErrorHandler {
	private Logger logger = LogManager.getLogger(getClass());
	
	@Override
    public void error(com.vaadin.server.ErrorEvent event) {
        logger.error("Uncaught Exception", event.getThrowable());
         showNotification(new Notification("Something bad happened!"));
    }
    private void showNotification(Notification notification) {
        // keep the notification visible a little while after moving the
        // mouse, or until clicked
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }

}
