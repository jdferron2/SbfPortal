package com.jdf.SbfPortal.backend.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@XmlRootElement(name="draftRecords")
public class SbfDraftRecord {
	protected int leagueId;
	protected int teamId;
	protected int playerId;
	protected int slotDrafted;
	protected Date timeDrafted;
	protected int auctionCost;

	public SbfDraftRecord(int leagueId, int teamId, int playerId, 
			int slotDrafted, Timestamp timeDrafted, int auctionCost){
		this.leagueId = leagueId;
		this.teamId = teamId;
		this.playerId = playerId;
		this.slotDrafted = slotDrafted;
		this.timeDrafted = timeDrafted;
		this.auctionCost = auctionCost;
	}
	
	public SbfDraftRecord(){
	}
	
	@XmlAttribute (name="leagueId")
	public int getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	
	@XmlElement (name="teamId")
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	
	@XmlElement (name="playerId")
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	@XmlElement (name="slotDrafted")
	public int getSlotDrafted() {
		return slotDrafted;
	}
	public void setSlotDrafted(int slotDrafted) {
		this.slotDrafted = slotDrafted;
	}
	
	public Date getTimeDrafted() {
		return timeDrafted;
	}
	public void setTimeDrafted(Timestamp timeDrafted) {
		this.timeDrafted = timeDrafted;
	}
	
	public int getAuctionCost() {
		return auctionCost;
	}

	public void setAuctionCost(int auctionCost) {
		this.auctionCost = auctionCost;
	}
	
	@XmlElement (name="timeDrafted")
	public String getDraftedTsString(){
		//return timeDrafted.toString();
		return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(timeDrafted);
	}

}
