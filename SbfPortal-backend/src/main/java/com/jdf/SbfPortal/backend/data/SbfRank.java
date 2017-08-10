package com.jdf.SbfPortal.backend.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@XmlRootElement(name="sbfRanks")
public class SbfRank implements Comparable{
	protected int sbfId;
	protected int playerId;
	protected int rank;
	protected boolean flagForUpdate;
	
	public SbfRank(){
	}
	public SbfRank(int sbfId, int playerId, int rank){
		this.sbfId = sbfId;
		this.playerId = playerId;
		this.rank=rank;
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
	
	@XmlElement (name="rank")
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
//	public boolean isFlagForUpdate() {
//		return flagForUpdate;
//	}
//	public void setFlagForUpdate(boolean flagForUpdate) {
//		this.flagForUpdate = flagForUpdate;
//	}
	@Override
	public int compareTo(Object o) {
		SbfRank compareRank = (SbfRank) o;
		if(this.getRank() == compareRank.getRank())
			return 0;
		else if (this.getRank() > compareRank.getRank()){
			return 1;
		} else {
			return -1;
		}
			
	}
}
