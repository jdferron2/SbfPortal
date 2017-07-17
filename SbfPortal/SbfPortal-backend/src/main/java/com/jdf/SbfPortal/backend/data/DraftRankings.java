package com.jdf.SbfPortal.backend.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="DraftRankings")
public class DraftRankings {
	private List<DraftRank> draftRanks;
	
	public DraftRankings(){}
	
	public DraftRankings(List<DraftRank> draftRanks){
		super();
		this.draftRanks = draftRanks;
	}
	
	@XmlElement(name="Player")
    public List<DraftRank> getDraftRanks() {
        if (draftRanks == null) {
        	draftRanks = new ArrayList<DraftRank>();
        }
        return this.draftRanks;
    }
	
	public void setDraftRanks( List<DraftRank> draftRanks){
		this.draftRanks = draftRanks;
	}
	
}
