package com.jdf.SbfPortal.backend.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Players")
public class Players {
	private List<Player> players;
	
	public Players(){}
	
	public Players(List<Player> players){
		super();
		this.players = players;
	}
	
	@XmlElement(name="Player")
    public List<Player> getPlayers() {
        if (players == null) {
        	players = new ArrayList<Player>();
        }
        return this.players;
    }
	
	public void setPlayers( List<Player> players){
		this.players = players;
	}

}
