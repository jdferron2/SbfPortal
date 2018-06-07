package com.jdf.SbfPortal.views;

import com.jdf.SbfPortal.authentication.LoginScreen;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
public class CreateAccountView extends CssLayout implements View  {
	public static final String NAME = "Create Account";
	private TextField username;
	private TextField email;
	private PasswordField password;
	private PasswordField reenterPassword;
	private Button createAccount;
	private Label usernameError = new Label();
	private Label emailError = new Label();
	private Label passwordError = new Label();
	private Label reenterPasswordError = new Label();

	public CreateAccountView() {
		buildUI();
		username.focus();
	}


	private void buildUI() {
		//addStyleName("login-screen");

		// login form, centered in the available part of the screen
		Component registrationForm = buildRegistrationForm();

		// layout to center login form when there is sufficient screen space
		// - see the theme for how this is made responsive for various screen
		// sizes
		VerticalLayout centeringLayout = new VerticalLayout();
		centeringLayout.setMargin(false);
		centeringLayout.setSpacing(false);
		centeringLayout.setStyleName("centering-layout");
		centeringLayout.addComponent(registrationForm);
		centeringLayout.setComponentAlignment(registrationForm,
				Alignment.MIDDLE_CENTER);

		addComponent(centeringLayout);
	}

	private Component buildRegistrationForm() {
		FormLayout registrationForm = new FormLayout();

		registrationForm.addStyleName("login-form");
		registrationForm.setSizeUndefined();
		registrationForm.setMargin(false);

		usernameError.setVisible(false);
		emailError.setVisible(false);
		passwordError.setVisible(false);
		reenterPasswordError.setVisible(false);

		registrationForm.addComponent(username = new TextField("Username"));
		username.setWidth(15, Unit.EM);
		registrationForm.addComponent(usernameError);

		registrationForm.addComponent(email = new TextField("Email"));
		email.setWidth(15, Unit.EM);
		registrationForm.addComponent(emailError);

		registrationForm.addComponent(password = new PasswordField("Password"));
		password.setWidth(15, Unit.EM);
		password.setDescription("Write anything");
		registrationForm.addComponent(passwordError);

		registrationForm.addComponent(reenterPassword = new PasswordField("Reenter Password"));
		reenterPassword.setWidth(15, Unit.EM);
		registrationForm.addComponent(reenterPasswordError);

		CssLayout buttons = new CssLayout();
		buttons.setStyleName("buttons");
		registrationForm.addComponent(buttons);
		buttons.addComponent(createAccount = new Button("Create your Awesome Draft account"));

		createAccount.setDisableOnClick(true);
		createAccount.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				try {
					createAccount();
				} finally {
					createAccount.setEnabled(true);
					UI.getCurrent().getNavigator().navigateTo(LoginScreen.NAME);
				}
			}
		});
		createAccount.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		createAccount.addStyleName(ValoTheme.BUTTON_FRIENDLY);

		return registrationForm;
	}


	private void createAccount() {
		boolean errorFound = false;
		if (username.isEmpty()) {
			usernameError.setValue("Please enter a valid username");
			usernameError.setVisible(true);
			errorFound=true;
		} else {usernameError.setVisible(false);}
		if(email.isEmpty() || !email.getValue().contains("@")){
			emailError.setValue("Please enter a valid email");
			emailError.setVisible(true);
			errorFound=true;
		}else {emailError.setVisible(false);}
		if(password.isEmpty()){
			passwordError.setValue("Please enter a password");
			passwordError.setVisible(true);
			errorFound=true;
		}else {passwordError.setVisible(false);}
		if(!reenterPassword.getValue().equals(password.getValue())){
			reenterPasswordError.setValue("Passwords must match");
			reenterPasswordError.setVisible(true);
			errorFound=true;
		}else {reenterPasswordError.setVisible(false);}

		if(!errorFound){
			//send email veriy
			//redirect to success page
			UserSessionVars.getAccountInfoManager().
				createUserAccount(username.getValue(), password.getValue(), email.getValue());
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
