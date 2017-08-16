package com.jdf.SbfPortal.backend.data;

import java.sql.Date;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlAttribute;

public class SbfPickTrade {
	private int leagueId;
	private int fromTeamId;
	private int toTeamId;
	private int pick;
	private Timestamp processedTs;

	public SbfPickTrade() {}
	public SbfPickTrade(int leagueId, int fromTeamId, int toTeamId, int pick, Timestamp processedTs){
		this.leagueId = leagueId;
		this.fromTeamId = fromTeamId;
		this.toTeamId = toTeamId;
		this.pick = pick;
		this.setProcessedTs(processedTs);
	}
	
	@XmlAttribute (name="leagueId")
	public int getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	
	@XmlAttribute (name="fromTeamId")
	public int getFromTeamId() {
		return fromTeamId;
	}
	public void setFromTeamId(int teamId) {
		this.fromTeamId = teamId;
	}
	
	@XmlAttribute (name="pick")
	public int getPick() {
		return pick;
	}
	public void setPick(int pick) {
		this.pick = pick;
	}
	public int getToTeamId() {
		return toTeamId;
	}
	public void setToTeamId(int toTeamId) {
		this.toTeamId = toTeamId;
	}
	public Timestamp getProcessedTs() {
		return processedTs;
	}
	public void setProcessedTs(Timestamp processedTs) {
		this.processedTs = processedTs;
	}
}
