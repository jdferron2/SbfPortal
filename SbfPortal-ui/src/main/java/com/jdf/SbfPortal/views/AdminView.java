package com.jdf.SbfPortal.views;

import java.util.HashMap;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.UiComponents.ConfirmButton;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.data.DraftRank;
import com.jdf.SbfPortal.backend.data.DraftRankings;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.Players;
import com.jdf.SbfPortal.backend.data.SbfRank;
import com.jdf.SbfPortal.backend.utility.RestAPIUtils;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class AdminView extends VerticalLayout implements View  {
	public static final String NAME = "Admin Functions";


	private boolean viewBuilt = false;

	private Integer rankSetId;
	private PlayerService playerService;

	private boolean icingEnabled;
	private boolean themeSongsEnabled;
	public AdminView(){

	}
	@Override
	public void enter(ViewChangeEvent event) {
		rankSetId = UserSessionVars.getRankSet().getRankSetId();
		playerService = UserSessionVars.getPlayerService();
		if(UI.getCurrent().getSession().getAttribute(SessionAttributes.ICING_ENABLED) == null){
			UI.getCurrent().getSession().setAttribute(SessionAttributes.ICING_ENABLED, true);
			icingEnabled = true;
		}else{
			icingEnabled = (boolean) UI.getCurrent().getSession().getAttribute(SessionAttributes.ICING_ENABLED);
		}

		if(UI.getCurrent().getSession().getAttribute(SessionAttributes.THEME_SONGS_ENABLED) == null){
			UI.getCurrent().getSession().setAttribute(SessionAttributes.THEME_SONGS_ENABLED, true);
			themeSongsEnabled = true;
		}else{
			themeSongsEnabled = (boolean) UI.getCurrent().getSession().getAttribute(SessionAttributes.ICING_ENABLED);
		}
		if(!viewBuilt){
			buildView();
			viewBuilt=true;
		}	
	}

	private void buildView(){
		CheckBox enableIcing = new CheckBox("Icing Enabled");
		enableIcing.setValue(icingEnabled);

		enableIcing.addValueChangeListener(event ->
		UI.getCurrent().getSession().setAttribute(SessionAttributes.ICING_ENABLED, enableIcing.getValue())
				);

		CheckBox enableThemeSongs = new CheckBox("Theme Songs Enabled");
		enableThemeSongs.setValue(themeSongsEnabled);

		enableThemeSongs.addValueChangeListener(event ->
		UI.getCurrent().getSession().setAttribute(SessionAttributes.THEME_SONGS_ENABLED, enableThemeSongs.getValue())
				);

		setSpacing(true);
		setMargin(true);
		Button resetPlayerList = new Button("Reset Players Table");
		resetPlayerList.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				//delete current player table
				
				HashMap<Integer, Player> playerLookup = new HashMap<Integer, Player>();
				//re-load active players from api
				Players players = RestAPIUtils.getInstance().invokeQueryPlayers();
				

				for(Player p : players.getPlayers()) {
					p.setProRank(9999);
					playerLookup.put(p.getPlayerId(), p);
				}
				DraftRankings ranks = RestAPIUtils.getInstance().invokeQueryRanks();
				for(DraftRank rank: ranks.getDraftRanks()){
					Player player = playerLookup.get(rank.getPlayerId());
					if (player != null){
						player.setProRank(rank.getProRank());
					}
				}
				
				playerService.deleteAllPlayers();
				playerService.batchPlayerInsert(players.getPlayers());

				Notification.show("Player list update successfully!");	
			}

		});

		ConfirmButton resetMyRanks = new ConfirmButton("Reset My Ranks");
		resetMyRanks.setConfirmationText("This will reset custom ranks to the default pro ranks.");
		resetMyRanks.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				//delete current ranks
				playerService.deleteAllSbfRanks(rankSetId);

				//add default ranks based on pro ranks
				for(Player player : playerService.getAllPlayers()){
					if(player.getProRank() < 500 && player.getProRank() != 0){
						SbfRank rank = new SbfRank(rankSetId, player.getPlayerId(), player.getProRank());
						playerService.insertSbfRank(rank);
					}

				}

				Notification.show("Ranks updated successfully!");
			}

		});

		ConfirmButton resetDefaultRanks = new ConfirmButton("Reset Default Ranks");
		resetDefaultRanks.setConfirmationText("This will reset default ranks to the current pro ranks.");
		resetDefaultRanks.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			public void buttonClick(ClickEvent event) {
				//delete current ranks
				playerService.deleteAllSbfRanks(0);

				//add default ranks based on pro ranks
				for(Player player : playerService.getAllPlayers()){
					if(player.getProRank() < 500 && player.getProRank() != 0){
						SbfRank rank = new SbfRank(0, player.getPlayerId(), player.getProRank());
						playerService.insertSbfRank(rank);
					}

				}

				Notification.show("Ranks updated successfully!");
			}

		});


		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addComponents(resetPlayerList, resetMyRanks,resetDefaultRanks);
		addComponents(enableIcing, enableThemeSongs, buttonLayout);
	}



}
