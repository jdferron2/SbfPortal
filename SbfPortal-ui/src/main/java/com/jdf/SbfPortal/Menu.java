package com.jdf.SbfPortal;

import java.util.HashMap;
import java.util.Map;

import com.jdf.SbfPortal.authentication.LoginScreen;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.authentication.LoginScreen.LoginListener;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.views.CreateAccountView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Responsive navigation menu presenting a list of available views to the user.
 */
public class Menu extends CssLayout {
	private SbfLeagueService leagueService;

	private static final String VALO_MENUITEMS = "valo-menuitems";
	private static final String VALO_MENU_TOGGLE = "valo-menu-toggle";
	private static final String VALO_MENU_VISIBLE = "valo-menu-visible";
	private Navigator navigator;
	private Map<String, Button> viewButtons = new HashMap<String, Button>();

	private CssLayout menuItemsLayout;
	private CssLayout menuPart;

	public Menu(Navigator navigator) {
		this.navigator = navigator;
		setPrimaryStyleName(ValoTheme.MENU_ROOT);
		menuPart = new CssLayout();
		menuPart.addStyleName(ValoTheme.MENU_PART);

		// header of the menu
		final HorizontalLayout top = new HorizontalLayout();
		top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		top.addStyleName(ValoTheme.MENU_TITLE);
		leagueService = UserSessionVars.getLeagueService();
		SbfLeague league = UserSessionVars.getCurrentLeague();
		Label title = new Label();
		title.setCaption("Awesome Draft");
		if (league != null){
			title.setCaption("Awesome Draft <br/>(" + league.getLeagueName() + ")");
			title.setCaptionAsHtml(true);		
		}
		
		title.addStyleName(ValoTheme.LABEL_H3);
		title.setSizeUndefined();
		top.addComponent(title);
		menuPart.addComponent(top);

		// logout menu item
		MenuBar logoutMenu = new MenuBar();
		MenuItem topLevel = logoutMenu.addItem("Account", null);
		MenuItem secondLevel;
		
		if(UserSessionVars.getAccessControl().isUserSignedIn()){
			secondLevel = topLevel.addItem("Switch Leagues", VaadinIcons.ADJUST, new Command() {

				@Override
				public void menuSelected(MenuItem selectedItem) {
					UI.getCurrent().addWindow(getSwitchLeaguesWindow());
				}
			});

			secondLevel.setStyleName(ValoTheme.MENU_ITEM);

			secondLevel = topLevel.addItem("Logout", VaadinIcons.SIGN_OUT, new Command() {

				@Override
				public void menuSelected(MenuItem selectedItem) {
					VaadinSession.getCurrent().getSession().invalidate();
					Page.getCurrent().reload();
				}
			});

			secondLevel.setStyleName(ValoTheme.MENU_ITEM);

			logoutMenu.addStyleName("user-menu");
		}
		else{//user not signed in, add sign in or create account option
			secondLevel = topLevel.addItem("Sign In", VaadinIcons.SIGN_IN, new Command() {

				@Override
				public void menuSelected(MenuItem selectedItem) {
					navigator.navigateTo(LoginScreen.NAME);
				}
			});

			secondLevel.setStyleName(ValoTheme.MENU_ITEM);
			navigator.addView(CreateAccountView.NAME, new CreateAccountView());
			secondLevel = topLevel.addItem("Create Account", null, new Command() {

				@Override
				public void menuSelected(MenuItem selectedItem) {
					navigator.navigateTo(CreateAccountView.NAME);
				}
			});

			secondLevel.setStyleName(ValoTheme.MENU_ITEM);
		}
		

		menuPart.addComponent(logoutMenu);

		// button for toggling the visibility of the menu when on a small screen
		final Button showMenu = new Button("Menu", new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				if (menuPart.getStyleName().contains(VALO_MENU_VISIBLE)) {
					menuPart.removeStyleName(VALO_MENU_VISIBLE);
				} else {
					menuPart.addStyleName(VALO_MENU_VISIBLE);
				}
			}
		});
		showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
		showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
		showMenu.addStyleName(VALO_MENU_TOGGLE);
		showMenu.setIcon(VaadinIcons.MENU);
		menuPart.addComponent(showMenu);

		// container for the navigation buttons, which are added by addView()
		menuItemsLayout = new CssLayout();
		menuItemsLayout.setPrimaryStyleName(VALO_MENUITEMS);
		menuPart.addComponent(menuItemsLayout);

		addComponent(menuPart);
	}

	/**
	 * Register a pre-created view instance in the navigation menu and in the
	 * {@link Navigator}.
	 *
	 * @see Navigator#addView(String, View)
	 *
	 * @param view
	 *            view instance to register
	 * @param name
	 *            view name
	 * @param caption
	 *            view caption in the menu
	 * @param icon
	 *            view icon in the menu
	 */
	public void addView(View view, final String name, String caption,
			Resource icon) {
		navigator.addView(name, view);
		createViewButton(name, caption, icon);
	}

	/**
	 * Register a view in the navigation menu and in the {@link Navigator} based
	 * on a view class.
	 *
	 * @see Navigator#addView(String, Class)
	 *
	 * @param viewClass
	 *            class of the views to create
	 * @param name
	 *            view name
	 * @param caption
	 *            view caption in the menu
	 * @param icon
	 *            view icon in the menu
	 */
	public void addView(Class<? extends View> viewClass, final String name,
			String caption, Resource icon) {
		navigator.addView(name, viewClass);
		createViewButton(name, caption, icon);
	}

	private void createViewButton(final String name, String caption,
			Resource icon) {
		Button button = new Button(caption, new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				navigator.navigateTo(name);

			}
		});
		button.setPrimaryStyleName(ValoTheme.MENU_ITEM);
		button.setIcon(icon);
		menuItemsLayout.addComponent(button);
		viewButtons.put(name, button);
	}

	/**
	 * Highlights a view navigation button as the currently active view in the
	 * menu. This method does not perform the actual navigation.
	 *
	 * @param viewName
	 *            the name of the view to show as active
	 */
	public void setActiveView(String viewName) {
		for (Button button : viewButtons.values()) {
			button.removeStyleName("selected");
		}
		Button selected = viewButtons.get(viewName);
		if (selected != null) {
			selected.addStyleName("selected");
		}
		menuPart.removeStyleName(VALO_MENU_VISIBLE);
	}


	private Window getSwitchLeaguesWindow(){
		Window subWindow = new Window("Select League");
		Integer leagueId = UserSessionVars.getCurrentLeague().getLeagueId();
		subWindow.setModal(true);
		HorizontalLayout subContent = new HorizontalLayout();
		subContent.setMargin(true);
		ComboBox<SbfLeague> selectLeague =
				new ComboBox<>("Select League");
		selectLeague.setItems(leagueService.getLeaguesForUser(UserSessionVars.getCurrentUser()));
		selectLeague.setItemCaptionGenerator(SbfLeague::getLeagueName);
		selectLeague.setValue(leagueService.getLeagueById(leagueId));
		selectLeague.setEmptySelectionAllowed(false);
		Button submitButton = new Button("Submit");
		submitButton.addClickListener(new Button.ClickListener()
		{ @Override public void buttonClick(Button.ClickEvent clickEvent)
		{
			if (selectLeague.getValue() != null && selectLeague.getValue().getLeagueId() != leagueId){
				UserSessionVars.setCurrentLeague(selectLeague.getValue());
				UserSessionVars.resetRankSetToDefault();
				subWindow.close();
				Page.getCurrent().reload();
			}else{
				subWindow.close();
			}
		} });

		subContent.addComponents(selectLeague, submitButton);
		subWindow.setContent(subContent);
		subWindow.center();
		//	subWindow.setClosable(false);
		return subWindow;

	}
}
