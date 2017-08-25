package com.jdf.SbfPortal;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.jdf.SbfPortal.authentication.AccessControl;
import com.jdf.SbfPortal.authentication.BasicAccessControl;
import com.jdf.SbfPortal.authentication.LoginScreen;
import com.jdf.SbfPortal.authentication.LoginScreen.LoginListener;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.utility.Broadcaster;
import com.jdf.SbfPortal.utility.MessageHandler;
import com.jdf.SbfPortal.utility.UncaughtExceptionHandler;
import com.jdf.SbfPortal.views.CheatSheetView;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@Theme("sbftheme") 
@Push
public class SbfUI extends UI implements Broadcaster.BroadcastListener{
	private AccessControl accessControl = new BasicAccessControl();
	private Navigator 			navigator;	
	boolean initialized = false;
	private MessageHandler handler;
	private static Logger logger;
	private Integer leagueId;

	@VaadinServletConfiguration(productionMode = false, ui = SbfUI.class)
	@Resource(name="jdbc/MyDB")
	public static class Servlet extends VaadinServlet {
	}
	private final Command menuCommand = new Command() {
		@Override
		public void menuSelected(final MenuItem selectedItem) {
			String navString = selectedItem.getText();
			if (navString.equals("Cheatsheet")) navString = "";
			navigator.navigateTo(navString);
		}
	};

	@Override
	protected void init(VaadinRequest request) {
		configureLog4j();
		UI.getCurrent().setErrorHandler(new UncaughtExceptionHandler());
		Broadcaster.register(this);
		UserSessionVars.setAccessControl(accessControl);
		handler = new MessageHandler(this.getSession());
		Responsive.makeResponsive(this);
		getPage().setTitle("SBF");
		if (!accessControl.isUserSignedIn()) {
			setContent(new LoginScreen(accessControl, new LoginListener() {
				@Override
				public void loginSuccessful() {
					showMainView();
				}
			}));
		} else {
			showMainView();
		}
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
		leagueId = UserSessionVars.getCurrentLeague().getLeagueId();
		addStyleName(ValoTheme.UI_WITH_MENU);
		setContent(new MainScreen(SbfUI.this));
		if (getNavigator().getState().equals("")){
			getNavigator().navigateTo(CheatSheetView.NAME);
		}else{
			getNavigator().navigateTo(getNavigator().getState());
		}
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
		return leagueId;
	}

}