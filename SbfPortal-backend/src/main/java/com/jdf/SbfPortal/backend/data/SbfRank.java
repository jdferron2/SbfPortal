package com.jdf.SbfPortal.backend.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@XmlRootElement(name="sbfRanks")
public class SbfRank implements Comparable<SbfRank>{
	protected int rankSetId;
	protected int playerId;
	protected int rank;
	protected int tier;
	protected boolean flagForUpdate;
	protected int auctionValue;
	
	public SbfRank(){
	}
	public SbfRank(int rankSetId, int playerId, int rank, int tier, int auctionValue){
		this.rankSetId = rankSetId;
		this.playerId = playerId;
		this.rank=rank;
		this.auctionValue=auctionValue;
		setTier(tier);
	}
	
	@XmlElement (name="rankSetId")
	public int getRankSetId() {
		return rankSetId;
	}
	public void setRankSetId(int rankSetId) {
		this.rankSetId = rankSetId;
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
	public int getTier() {
		return tier;
	}
	public void setTier(int tier) {
		this.tier = tier;
	}
	public int getAuctionValue() {
		return auctionValue;
	}
	public void setAuctionValue(int auctionValue) {
		this.auctionValue = auctionValue;
	}
	
	@Override
	public int compareTo(SbfRank o) {
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
