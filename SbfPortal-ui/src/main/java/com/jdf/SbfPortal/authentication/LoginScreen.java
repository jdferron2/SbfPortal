package com.jdf.SbfPortal.authentication;

import java.io.Serializable;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * UI content when the user is not logged in yet.
 */
public class LoginScreen extends CssLayout implements View {

	public static final String NAME = "Login";
	private TextField username;
	private PasswordField password;
	private Button login;
	private Button forgotPassword;
	private AccessControl accessControl;

	public LoginScreen(AccessControl accessControl) {
		this.accessControl = accessControl;
		buildUI();
		username.focus();
	}


	private void buildUI() {
		addStyleName("login-screen");

		// login form, centered in the available part of the screen
		Component loginForm = buildLoginForm();

		// layout to center login form when there is sufficient screen space
		// - see the theme for how this is made responsive for various screen
		// sizes
		VerticalLayout centeringLayout = new VerticalLayout();
		centeringLayout.setMargin(false);
		centeringLayout.setSpacing(false);
		centeringLayout.setStyleName("centering-layout");
		centeringLayout.addComponent(loginForm);
		centeringLayout.setComponentAlignment(loginForm,
				Alignment.MIDDLE_CENTER);

		addComponent(centeringLayout);
	}

	private Component buildLoginForm() {
		FormLayout loginForm = new FormLayout();

		loginForm.addStyleName("login-form");
		loginForm.setSizeUndefined();
		loginForm.setMargin(false);

		loginForm.addComponent(username = new TextField("Username"));
		username.setWidth(15, Unit.EM);
		loginForm.addComponent(password = new PasswordField("Password"));
		password.setWidth(15, Unit.EM);
		password.setDescription("Write anything");
		CssLayout buttons = new CssLayout();
		buttons.setStyleName("buttons");
		loginForm.addComponent(buttons);
		//leagueComboBox.setItems(leagueService.getAllSbfLeagues());
		//leagueComboBox.setItemCaptionGenerator(SbfLeague::getLeagueName);
		//  buttons.addComponent(leagueComboBox);
		buttons.addComponent(login = new Button("Login"));
		login.setDisableOnClick(true);
		login.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				try {
					login();
				} finally {
					login.setEnabled(true);
				}
			}
		});
		login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		login.addStyleName(ValoTheme.BUTTON_FRIENDLY);

		buttons.addComponent(forgotPassword = new Button("Forgot password?"));
		forgotPassword.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				showNotification(new Notification("Hint: It's Penis"));
			}
		});
		forgotPassword.addStyleName(ValoTheme.BUTTON_LINK);
		return loginForm;
	}


	private void login() {
		if (accessControl.signIn(username.getValue(), password.getValue())) {
			UI.getCurrent().getNavigator().navigateTo("");
			Page.getCurrent().reload();
		} else {
			showNotification(new Notification("Login failed",
					"Please check your username and password and try again.",
					Notification.Type.HUMANIZED_MESSAGE));
			username.focus();
		}
	}

	private void showNotification(Notification notification) {
		// keep the notification visible a little while after moving the
		// mouse, or until clicked
		notification.setDelayMsec(2000);
		notification.show(Page.getCurrent());
	}

	public interface LoginListener extends Serializable {
		void loginSuccessful();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (UserSessionVars.getAccessControl().isUserSignedIn()){
			UI.getCurrent().getNavigator().navigateTo("");
		}
		
	}
}
