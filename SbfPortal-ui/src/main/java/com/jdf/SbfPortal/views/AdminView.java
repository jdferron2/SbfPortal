package com.jdf.SbfPortal.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.UiComponents.ConfirmButton;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.data.DraftRank;
import com.jdf.SbfPortal.backend.data.DraftRankings;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfRank;
import com.jdf.SbfPortal.backend.data.jsonModel.PlayerJson;
import com.jdf.SbfPortal.backend.utility.RestAPIUtilsFFCalc;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class AdminView extends VerticalLayout implements View  {
	public static final String NAME = "Admin Functions";


	private boolean viewBuilt = false;

	private Integer rankSetId;
	private PlayerService playerService;

	private boolean icingEnabled;
	private Integer icePercent;
	private Integer shotPercent;
	private Integer shotgunPercent;

	private boolean themeSongsEnabled;

	public static final Integer DEFAULT_ICE_PERCENT = 12;
	public static final Integer DEFAULT_SHOT_PERCENT = 8;
	public static final Integer DEFAULT_SHOTGUN_PERCENT = 3;
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

		if(UI.getCurrent().getSession().getAttribute(SessionAttributes.ICE_PERCENT) == null){
			UI.getCurrent().getSession().setAttribute(SessionAttributes.ICE_PERCENT, DEFAULT_ICE_PERCENT);
			icePercent = DEFAULT_ICE_PERCENT;
		}else{
			icePercent = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.ICE_PERCENT);
		}

		if(UI.getCurrent().getSession().getAttribute(SessionAttributes.SHOT_PERCENT) == null){
			UI.getCurrent().getSession().setAttribute(SessionAttributes.SHOT_PERCENT, DEFAULT_SHOT_PERCENT);
			shotPercent = DEFAULT_SHOT_PERCENT;
		}else{
			shotPercent = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.SHOT_PERCENT);
		}

		if(UI.getCurrent().getSession().getAttribute(SessionAttributes.SHOTGUN_PERCENT) == null){
			UI.getCurrent().getSession().setAttribute(SessionAttributes.SHOTGUN_PERCENT, DEFAULT_SHOTGUN_PERCENT);
			shotgunPercent = DEFAULT_SHOTGUN_PERCENT;
		}else{
			shotgunPercent = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.SHOTGUN_PERCENT);
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

		TextField icePercentTF = new TextField("Ice Percent");
		icePercentTF.setValue(String.valueOf(icePercent));
		icePercentTF.addValueChangeListener(event->{
			if(Integer.valueOf(icePercentTF.getValue()) + shotPercent + shotgunPercent <= 100) {
				icePercent = Integer.valueOf(icePercentTF.getValue());
				UI.getCurrent().getSession().setAttribute(SessionAttributes.ICE_PERCENT, icePercent);		
			}else {
				icePercentTF.setValue(String.valueOf(icePercent));
			}
		}
				);
		
		TextField shotPercentTF = new TextField("Shot Percent");
		shotPercentTF.setValue(String.valueOf(shotPercent));
		shotPercentTF.addValueChangeListener(event->{
			if(Integer.valueOf(shotPercentTF.getValue()) + icePercent + shotgunPercent <= 100) {
				shotPercent = Integer.valueOf(shotPercentTF.getValue());
				UI.getCurrent().getSession().setAttribute(SessionAttributes.SHOT_PERCENT, shotPercent);		
			}else {
				shotPercentTF.setValue(String.valueOf(shotPercent));
			}
		}
				);
		
		TextField shotgunPercentTF = new TextField("Shotgun Percent");
		shotgunPercentTF.setValue(String.valueOf(shotgunPercent));
		shotgunPercentTF.addValueChangeListener(event->{
			if(Integer.valueOf(shotgunPercentTF.getValue()) + shotPercent + icePercent <= 100) {
				shotgunPercent = Integer.valueOf(shotgunPercentTF.getValue());
				UI.getCurrent().getSession().setAttribute(SessionAttributes.SHOTGUN_PERCENT, shotgunPercent);		
			}else {
				shotgunPercentTF.setValue(String.valueOf(shotgunPercent));
			}
		}
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

				ArrayList<Player> playerList = new ArrayList<Player>();
				//re-load active players from api
				PlayerJson[] players = RestAPIUtilsFFCalc.getInstance().invokeQueryPlayers();

				Arrays.sort(players, Comparator.comparing(PlayerJson::getAdp));
				int rank = 1;
				for(PlayerJson p : players) {
					Player dbPlayer = new Player();
					dbPlayer.setProRank(rank++);
					dbPlayer.setActive(1);
					dbPlayer.setDisplayName(p.getFullName());
					dbPlayer.setPlayerId(p.getPlayerId());
					dbPlayer.setPosition(p.getPosition());
					dbPlayer.setTeam(p.getTeam());

					playerList.add(dbPlayer);
				}


				playerService.deleteAllPlayers();
				playerService.batchPlayerInsert(playerList);

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
						SbfRank rank = new SbfRank(rankSetId, player.getPlayerId(), player.getProRank(),0);
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
						SbfRank rank = new SbfRank(0, player.getPlayerId(), player.getProRank(),0);
						playerService.insertSbfRank(rank);
					}

				}

				Notification.show("Ranks updated successfully!");
			}

		});


		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addComponents(resetPlayerList, resetMyRanks,resetDefaultRanks);
		addComponents(enableIcing, enableThemeSongs, icePercentTF, shotPercentTF, shotgunPercentTF, buttonLayout);
	}



}
