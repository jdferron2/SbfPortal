package com.jdf.SbfPortal.backend.data;

import java.util.Date;

public class SbfRankSet {

	int userId;
	int rankSetId;
	String rankSetName;
	Date dateCreated;
	
	public SbfRankSet() {}
	public SbfRankSet(int userId, int rankSetId, String rankSetName, Date dateCreated){
		this.setUserId(userId);
		this.setRankSetId(rankSetId);
		this.setRankSetName(rankSetName);
		this.setDateCreated(dateCreated);
	}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getRankSetId() {
		return rankSetId;
	}
	public void setRankSetId(int rankSetId) {
		this.rankSetId = rankSetId;
	}
	public String getRankSetName() {
		return rankSetName;
	}
	public void setRankSetName(String rankSetName) {
		this.rankSetName = rankSetName;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
}