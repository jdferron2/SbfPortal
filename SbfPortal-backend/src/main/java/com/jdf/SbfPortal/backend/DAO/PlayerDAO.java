package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.Player;

public interface PlayerDAO {
	List<Player> getAllPlayers();
	void insertPlayer(Player p);
	void updatePlayer(Player p);
	void deletePlayer(Player p);
	void insertPlayersBatch(List<Player> p);
	void deletePlayersBatch(List<Player> p);
}
