package com.jdf.SbfPortal.backend.data;

import javax.xml.bind.annotation.XmlAttribute;

public class SbfDraftPick {
	private int leagueId;
	private int sbfId;
	private int pick;

	public SbfDraftPick() {}
	public SbfDraftPick(int leagueId, int sbfId, int pick){
		this.leagueId = leagueId;
		this.sbfId = sbfId;
		this.pick = pick;
	}
	
	@XmlAttribute (name="leagueId")
	public int getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	
	@XmlAttribute (name="sbfId")
	public int getSbfId() {
		return sbfId;
	}
	public void setSbfId(int sbfId) {
		this.sbfId = sbfId;
	}
	
	@XmlAttribute (name="pick")
	public int getPick() {
		return pick;
	}
	public void setPick(int pick) {
		this.pick = pick;
	}
}
