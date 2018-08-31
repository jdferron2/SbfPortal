package com.jdf.SbfPortal;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.jdf.SbfPortal.authentication.AccessControl;
import com.jdf.SbfPortal.authentication.BasicAccessControl;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.utility.Broadcaster;
import com.jdf.SbfPortal.utility.MessageHandler;
import com.jdf.SbfPortal.utility.UncaughtExceptionHandler;
import com.jdf.SbfPortal.views.HomeView;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@Theme("sbftheme") 
@Push
public class SbfUI extends UI implements Broadcaster.BroadcastListener{
	private AccessControl accessControl = new BasicAccessControl();

	boolean initialized = false;
	private MessageHandler handler;
	private static Logger logger;

	@VaadinServletConfiguration(productionMode = false, ui = SbfUI.class)
	@Resource(name="jdbc/MyDB")
	public static class Servlet extends VaadinServlet {
	}


	@Override
	protected void init(VaadinRequest request) {
		configureLog4j();
		UI.getCurrent().setErrorHandler(new UncaughtExceptionHandler());
		Broadcaster.register(this);
		UserSessionVars.setAccessControl(accessControl);
		handler = new MessageHandler(this.getSession());
		Responsive.makeResponsive(this);
		getPage().setTitle("Awesome Draft");
		showMainView();
	}

	@Override
	public void receiveBroadcast(String message) {
		UI.getCurrent().access(new Runnable() {
			@Override
			public void run() {
				// Show it somehow
				System.out.println("Received a message!!!! " + message);
			}
		});

	}


	@Override
	public void receiveBroadcast(VaadinSession ses, String command, Object[] args) {
		access(new Runnable() {
			@Override
			public void run() {
				handler.processMessage(ses, command, args);
			}
		});
	}

	@Override
	public void detach() {
		Broadcaster.unregister(this);
		super.detach();
	}

	protected void showMainView() {
		addStyleName(ValoTheme.UI_WITH_MENU);
		setContent(new MainScreen(SbfUI.this));
		getNavigator().setErrorView(new HomeView());
		getNavigator().navigateTo(getNavigator().getState());
	}

	private void configureLog4j(){
		String url = "";
		url = VaadinServlet.getCurrent().getServletContext().getRealPath("/WEB-INF") + "/sbfportal-log4j.xml";

		DOMConfigurator.configure(url);
		logger = Logger.getLogger(SbfUI.class);
		logger.info("Configured Log4J for the application:");
	}

	@Override
	public Integer getLeagueId() {
		return UserSessionVars.getCurrentLeague().getLeagueId();
	}

}