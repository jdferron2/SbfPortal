package com.jdf.SbfPortal.views;

import com.jdf.SbfPortal.authentication.LoginScreen;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * UI content when the user is not logged in yet.
 */
@SuppressWarnings("serial")
public class CreateLeagueView extends CssLayout implements View  {
	public static final String NAME = "Create League";
	private TextField leagueName;
	private ComboBox<Integer> numTeams;
	private Button submit;
	private Label leagueNameError = new Label();
	private Label numTeamsError = new Label();

	public CreateLeagueView() {
		buildUI();
		leagueName.focus();
	}


	private void buildUI() {
		//addStyleName("login-screen");

		// login form, centered in the available part of the screen
		Component createLeagueForm = buildCreateLeagueForm();

		// layout to center login form when there is sufficient screen space
		// - see the theme for how this is made responsive for various screen
		// sizes
		VerticalLayout centeringLayout = new VerticalLayout();
		centeringLayout.setMargin(false);
		centeringLayout.setSpacing(false);
		centeringLayout.setStyleName("centering-layout");
		centeringLayout.addComponent(createLeagueForm);
		centeringLayout.setComponentAlignment(createLeagueForm,
				Alignment.MIDDLE_CENTER);

		addComponent(centeringLayout);
	}

	private Component buildCreateLeagueForm() {
		FormLayout createLeagueForm = new FormLayout();

		//createLeagueForm.addStyleName("login-form");
		createLeagueForm.setSizeUndefined();
		createLeagueForm.setMargin(false);

		leagueNameError.setVisible(false);
		numTeamsError.setVisible(false);

		createLeagueForm.addComponent(leagueName = new TextField("League Name"));
		leagueName.setWidth(15, Unit.EM);
		createLeagueForm.addComponent(leagueNameError);

		createLeagueForm.addComponent(numTeams = new ComboBox("Number of Teams"));
		numTeams.setWidth(15, Unit.EM);
		numTeams.setItems(6,8,10,12,14,16);
		numTeams.setSelectedItem(12);
		createLeagueForm.addComponent(numTeamsError);

		CssLayout buttons = new CssLayout();
		buttons.setStyleName("buttons");
		createLeagueForm.addComponent(buttons);
		buttons.addComponent(submit = new Button("Create"));

		submit.setDisableOnClick(true);
		submit.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				try {
					createLeague();
				} finally {
					submit.setEnabled(true);
				}
			}
		});
		submit.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		submit.addStyleName(ValoTheme.BUTTON_FRIENDLY);

		return createLeagueForm;
	}


	private void createLeague() {
		boolean errorFound = false;
		if (leagueName.isEmpty()) {
			leagueNameError.setValue("Please enter a Leauge Name");
			leagueNameError.setVisible(true);
			errorFound=true;
		} 
		if(!errorFound){
			//send email veriy
			//redirect to success page
			UserSessionVars.getAccountInfoManager().createLeague(
					leagueName.getValue(), numTeams.getValue(), UserSessionVars.getCurrentUser().getUserId());
		}
	}

	private void showNotification(Notification notification) {
		// keep the notification visible a little while after moving the
		// mouse, or until clicked
		notification.setDelayMsec(2000);
		notification.show(Page.getCurrent());
	}


	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}
}
