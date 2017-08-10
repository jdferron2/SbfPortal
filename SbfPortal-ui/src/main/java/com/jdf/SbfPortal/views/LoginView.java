package com.jdf.SbfPortal.views;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class LoginView extends HorizontalLayout implements View  {
	public static final String NAME = "LOGIN"; 
	boolean viewBuilt = false;
	protected SbfLeagueService leagueService = 
			(SbfLeagueService) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_SERVICE);

	public LoginView (){
	}
	@Override
	public void enter(ViewChangeEvent event) {
		if (!viewBuilt){
			buildView();
			viewBuilt=true;
		}
	}
	
	protected void buildView(){
		Panel panel = new Panel("Login");
		panel.setSizeUndefined();
		addComponent(panel);
		
		FormLayout content = new FormLayout();
		TextField username = new TextField("Username");
		content.addComponent(username);
		PasswordField password = new PasswordField("Password");
		content.addComponent(password);
		
		ComboBox<SbfLeague> leagueComboBox = new ComboBox<SbfLeague>("Leagues");
		leagueComboBox.setItems(leagueService.getAllSbfLeagues());
		leagueComboBox.setItemCaptionGenerator(SbfLeague::getLeagueName);
		
		Button send = new Button("Enter");
		send.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				String message = "Nope";
				if(leagueComboBox.getValue() != null && !leagueComboBox.getValue().equals("")){
					getUI().getSession().setAttribute(SessionAttributes.LEAGUE_ID, leagueComboBox.getValue().getLeagueId());
				}else {
					message = "Pick a league, dummy";
				}
				SbfTeam teamLoggingIn = leagueService.getSbfTeamByName(username.getValue(), leagueComboBox.getValue().getLeagueId());
				if(teamLoggingIn != null
						&& message.equals("Nope")){
					getUI().getSession().setAttribute(SessionAttributes.USER_NAME, username.getValue());
					getUI().getSession().setAttribute(SessionAttributes.SBF_ID, teamLoggingIn.getSbfId());
					getUI().getNavigator().navigateTo("");
				}else{
					Notification.show(message, Notification.Type.WARNING_MESSAGE);
				}
			}
			
		});
		
		
		
		content.addComponents(send,leagueComboBox);
		content.setSizeUndefined();
		content.setMargin(true);
		panel.setContent(content);
		setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
	}


}
