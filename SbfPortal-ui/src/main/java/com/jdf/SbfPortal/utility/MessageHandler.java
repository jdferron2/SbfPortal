package com.jdf.SbfPortal.utility;

import com.jdf.SbfPortal.SbfUI;
import com.jdf.SbfPortal.UiComponents.DraftBoardPopupUI;
import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.data.SbfDraftRecord;
import com.jdf.SbfPortal.backend.utility.BroadcastCommands;
import com.jdf.SbfPortal.views.DraftDayBoardView;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

public class MessageHandler  {
	private VaadinSession session;
	public MessageHandler(VaadinSession ses){
		session = ses;
	}
	public void processMessage(VaadinSession ses, String command, Object obj){
		if(!session.equals(ses)){
			if(command.equals(BroadcastCommands.DRAFT_PLAYER)){
				playerDraftedCmd((SbfDraftRecord)obj);
			}
		}
	}

	private void playerDraftedCmd(SbfDraftRecord r){
		View v = null;
		UserSessionVars.getDraftService().addSbfDraftRecordtoSession(r);
		for(UI ui : session.getUIs()){
			if (ui.getClass().equals(SbfUI.class)){
				v = ((SbfUI)ui).currentView;
			}
			if (ui.getClass().equals(DraftBoardPopupUI.class)){
				((DraftBoardPopupUI)ui).addDraftSelection(r);
			}
		}
		if(v != null){
			if (v.getClass().equals(DraftDayBoardView.class)){
				((DraftDayBoardView) v).addDraftSelection(r);
			}
		}
	}

}
