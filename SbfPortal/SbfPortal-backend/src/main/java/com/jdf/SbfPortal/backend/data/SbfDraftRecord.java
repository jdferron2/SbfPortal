package com.jdf.SbfPortal.backend.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	protected int sbfId;
	protected int playerId;
	protected int slotDrafted;
	protected Date timeDrafted;
	
	public SbfDraftRecord(int leagueId, int sbfId, int playerId, int slotDrafted, Timestamp timeDrafted){
		this.leagueId = leagueId;
		this.sbfId = sbfId;
		this.playerId = playerId;
		this.slotDrafted = slotDrafted;
		this.timeDrafted = timeDrafted;
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
	
	@XmlElement (name="sbfId")
	public int getSbfId() {
		return sbfId;
	}
	public void setSbfId(int sbfId) {
		this.sbfId = sbfId;
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
	
	@XmlElement (name="timeDrafted")
	public String getDraftedTsString(){
		//return timeDrafted.toString();
		return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(timeDrafted);
	}

}
