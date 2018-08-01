package com.jdf.SbfPortal;

import com.jdf.SbfPortal.authentication.LoginScreen;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.views.AdminView;
import com.jdf.SbfPortal.views.CheatSheetView;
import com.jdf.SbfPortal.views.CreateLeagueView;
import com.jdf.SbfPortal.views.DraftDayView;
import com.jdf.SbfPortal.views.EditCheatsheetSettingsView;
import com.jdf.SbfPortal.views.EditLeaguesView;
import com.jdf.SbfPortal.views.EditTeamsView;
import com.jdf.SbfPortal.views.HomeView;
import com.jdf.SbfPortal.views.KeepersView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

/**
 * Content of the UI when the user is logged in.
 * 
 * 
 */
public class MainScreen extends HorizontalLayout {
	private Menu menu;

	public MainScreen(SbfUI ui) {
		setSpacing(false);
		CssLayout viewContainer = new CssLayout();
		viewContainer.addStyleName("valo-content");
		viewContainer.setSizeFull();

		final Navigator navigator = new Navigator(ui, viewContainer);
		// navigator.setErrorView(ErrorView.class);
		menu = new Menu(navigator);
		navigator.addView("", new HomeView());
		navigator.addView(LoginScreen.NAME, new LoginScreen(UserSessionVars.getAccessControl()));
		
		if(UserSessionVars.getAccessControl().isUserSignedIn()){
			menu.addView(new CheatSheetView(), CheatSheetView.NAME,
					CheatSheetView.NAME, VaadinIcons.CHART_GRID);
			menu.addView(new EditCheatsheetSettingsView(), EditCheatsheetSettingsView.NAME,
					EditCheatsheetSettingsView.NAME, null);
			if(UserSessionVars.getCurrentLeague() != null){
				menu.addView(new DraftDayView(), DraftDayView.NAME, DraftDayView.NAME,
						VaadinIcons.BOMB);

				if(UserSessionVars.getAccessControl().isUserLeagueManager()){
					menu.addView(new EditTeamsView(), EditTeamsView.NAME, EditTeamsView.NAME, null);
					menu.addView(new KeepersView(), KeepersView.NAME, KeepersView.NAME, null);
				}
			}
			if(UserSessionVars.getAccessControl().isUserInRole("admin")){
				menu.addView(new AdminView(), AdminView.NAME, AdminView.NAME, null);
				menu.addView(new CreateLeagueView(), CreateLeagueView.NAME, CreateLeagueView.NAME, null);
			}
			menu.addView(new EditLeaguesView(), EditLeaguesView.NAME, "Edit Leagues", null);
		}
		
		
		
		

		navigator.addViewChangeListener(viewChangeListener);

		addComponent(menu);
		addComponent(viewContainer);
		setExpandRatio(viewContainer, 1);
		setSizeFull();
	}

	// notify the view menu about view changes so that it can display which view
	// is currently active
	ViewChangeListener viewChangeListener = new ViewChangeListener() {

		@Override
		public boolean beforeViewChange(ViewChangeEvent event) {
			return true;
		}

		@Override
		public void afterViewChange(ViewChangeEvent event) {
			menu.setActiveView(event.getViewName());
			UserSessionVars.setCurrentView(event.getNewView());;
		}

	};

}
