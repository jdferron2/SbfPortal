package com.jdf.SbfPortal.UiComponents;

import com.jdf.SbfPortal.SessionAttributes;
import com.jdf.SbfPortal.backend.PlayerService;
import com.jdf.SbfPortal.backend.SbfDraftService;
import com.jdf.SbfPortal.backend.SbfLeagueService;
import com.jdf.SbfPortal.backend.data.Player;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.utility.LeagueInfoManager;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Audio;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("sbftheme") 
@Push
public class DraftDisplayPopupUI extends UI {
	PlayerService playerService;
	SbfDraftService draftService;
	SbfLeagueService leagueService;
	LeagueInfoManager leagueMgr;
	Integer leagueId;
	boolean viewBuilt = false;
	boolean pickIsInDisplayed = false;
	private Audio FIREWORKSSOUND = new Audio(null, new ThemeResource("audio/fireworks2.mp3"));
	private Audio TADASOUND = new Audio(null, new ThemeResource("audio/tada.mp3"));
	private Audio CHEERINGSOUND = new Audio(null, new ThemeResource("audio/cheering.mp3"));

	VerticalLayout mainContent;
	VerticalLayout pickIsInContent;
	VerticalLayout winnerContent;

	Label latestPickLabel = new Label();
	Label selectedPlayerLabel = new Label();
	Label onTheClockLabel = new Label();


	@Override
	protected void init(VaadinRequest request) {
		leagueId = (Integer) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_ID);
		playerService = (PlayerService) UI.getCurrent().getSession().getAttribute(SessionAttributes.PLAYER_SERVICE);
		draftService = (SbfDraftService) UI.getCurrent().getSession().getAttribute(SessionAttributes.DRAFT_SERVICE);
		leagueService = (SbfLeagueService) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_SERVICE);
		if(leagueMgr==null){
			leagueMgr =
					(LeagueInfoManager) UI.getCurrent().getSession().getAttribute(SessionAttributes.LEAGUE_MANAGER);
		}
		if (!viewBuilt){
			buildLayout();
			viewBuilt=true;
		}
	}

	protected void buildLayout(){
		buildPickIsIn();
		buildWinnerDisplay();
		addStyleName("bgMain");
		mainContent = new VerticalLayout();
		HorizontalLayout topBannerLayout = new HorizontalLayout();
		HorizontalLayout onTheClockLayout = new HorizontalLayout();
		VerticalLayout lastPickLayout = new VerticalLayout();
		VerticalLayout selectedPlayerLayout = new VerticalLayout();
		selectedPlayerLayout.setSizeFull();
		topBannerLayout.setSizeFull();
		lastPickLayout.setSizeFull();
		onTheClockLayout.setWidth("100%");
		lastPickLayout.setMargin(new MarginInfo(false,false,true,false));
		lastPickLayout.addStyleName("bgRed");
		onTheClockLayout.setSpacing(true);
		selectedPlayerLayout.addStyleName("bgNfl");
		mainContent.setSizeFull();
		setSizeFull();


		setLatestPickValue();
		setSelectedPlayerLabel();
		setOnTheClockLabel();

		latestPickLabel.setContentMode(ContentMode.HTML);
		selectedPlayerLabel.setContentMode(ContentMode.HTML);
		onTheClockLabel.setContentMode(ContentMode.HTML);
		selectedPlayerLabel.setWidth("100%");

		onTheClockLabel.setSizeFull();
		onTheClockLayout.addComponent(onTheClockLabel);

		lastPickLayout.addComponent(latestPickLabel);
		lastPickLayout.setComponentAlignment(latestPickLabel, Alignment.MIDDLE_CENTER);

		selectedPlayerLayout.addComponent(selectedPlayerLabel);
		selectedPlayerLayout.setComponentAlignment(selectedPlayerLabel, Alignment.MIDDLE_CENTER);

		topBannerLayout.addComponents(selectedPlayerLayout, getSbfLogo(), lastPickLayout);
		mainContent.addComponents(topBannerLayout, onTheClockLayout);
		mainContent.setExpandRatio(topBannerLayout, 1);

		setContent(mainContent);
	}

	public synchronized void setLatestPickValue(){
		int lastPick = leagueMgr.getCurrentPick()-1;
		if (lastPick == 0) return;
		int round = leagueMgr.getRound(lastPick);
		int pickInRound = leagueMgr.getPickInRound(lastPick);
		latestPickLabel.setValue("<h1 class=\"otc\">Round <br />" + round 
				+ "<br />Pick<br />" + pickInRound + "</h1>");		
	}	

	public synchronized void setSelectedPlayerLabel(){		
		SbfDraftRecord latestPick = draftService.getSbfDraftRecordByPickNum(leagueMgr.getCurrentPick()-1, leagueId);
		if (latestPick == null) return;
		SbfTeam teamOnClock = leagueService.getSbfTeamBySbfId(latestPick.getSbfId(), leagueId);
		String teamName = teamOnClock.getOwnerName();
		Player selectedPlayer = playerService.getPlayerById(latestPick.getPlayerId());		
		selectedPlayerLabel.setValue("<div class=\"selectedPlayerInfo\">" + teamName + " selects<br />"
				+ "<div class=\"selectedPlayer\">" + selectedPlayer.getDisplayName() + "<br /> </div>"
				+ selectedPlayer.getPosition() + " | " + selectedPlayer.getTeam() + "<br />"
				+ "Pro Rank " + selectedPlayer.getProRank() + "</div>");
	}

	public synchronized void setOnTheClockLabel(){
		SbfTeam teamOnClock = leagueMgr.getTeamOnTheClock();	
		onTheClockLabel.setValue("<div class=\"onTheClock\">On the clock: " + teamOnClock.getOwnerName() + "</div>");
	}
	Image getSbfLogo(){
		Resource res = new ThemeResource("img/2017_SBF_Draft.png");
		Image image = new Image(null, res);
		image.setSizeFull();
		return image;
	}

	public void pickIsIn(){
		setContent(pickIsInContent);
		pickIsInDisplayed = true;
	}

	//	void removePickIsIn(){
	//		if (pickIsInDisplayed){
	//			setContent(mainContent);
	//			pickIsInDisplayed = false;
	//		}
	//	}

	void buildPickIsIn(){
		pickIsInContent = new VerticalLayout();
		pickIsInContent.setSizeFull();
		Label pickIsInLabel = new Label();
		pickIsInLabel.setValue("Pick Is In!");
		pickIsInLabel.addStyleName("pickIsIn");
		pickIsInLabel.setWidth("95%");
		pickIsInContent.addComponent(pickIsInLabel);
		pickIsInContent.setComponentAlignment(pickIsInLabel, Alignment.MIDDLE_CENTER);
	}

	void buildWinnerDisplay(){
		FIREWORKSSOUND.setShowControls(false); FIREWORKSSOUND.setSizeUndefined();
		TADASOUND.setShowControls(false); TADASOUND.setSizeUndefined();
		CHEERINGSOUND.setShowControls(false); CHEERINGSOUND.setSizeUndefined();
		VerticalLayout soundLayout = new VerticalLayout();
		soundLayout.addComponents(FIREWORKSSOUND,TADASOUND,CHEERINGSOUND);
		
		winnerContent = new VerticalLayout();
		winnerContent.setSizeFull();
		HorizontalLayout picLayout = new HorizontalLayout();
		Resource res = new ThemeResource("img/fireworks.gif");
		Image fireWorksImg = new Image(null, res);
		fireWorksImg.setSizeFull();

		Image fireWorksImg2 = new Image(null, res);
		fireWorksImg2.setSizeFull();

		res = new ThemeResource("img/ice.jpg");
		Image iceImg = new Image(null, res);
		//iceImg.setSizeFull();
		picLayout.setSizeFull();

		picLayout.addComponents(fireWorksImg,iceImg, fireWorksImg2);
		picLayout.setComponentAlignment(iceImg, Alignment.MIDDLE_CENTER);

		Label winnerLabel = new Label();
		winnerLabel.setValue("YOU WIN!!");
		winnerLabel.addStyleName("pickIsIn");
		winnerLabel.setWidth("100%");

		Label congratsLabel = new Label();
		congratsLabel.setValue("Congratulations!");
		congratsLabel.addStyleName("pickIsIn");
		congratsLabel.setWidth("100%");

		winnerContent.addComponents(congratsLabel, picLayout, winnerLabel, soundLayout);
		winnerContent.setExpandRatio(picLayout, 1);
		winnerContent.setExpandRatio(soundLayout, 0);
	}

	public synchronized void processPick(boolean isAWinner){
		access(new Runnable() {
			@Override
			public void run() {      
				if (isAWinner){
					setContent(winnerContent);
					TADASOUND.play();
					//FIREWORKSSOUND.play();
					//CHEERINGSOUND.play();
				}else{
					setOnTheClockLabel();
					setLatestPickValue();
					setSelectedPlayerLabel();
					setContent(mainContent);
				}

			}
		});
	}
}

