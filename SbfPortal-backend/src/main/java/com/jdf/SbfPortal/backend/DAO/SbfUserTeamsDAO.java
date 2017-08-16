package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.SbfUserTeam;

public interface SbfUserTeamsDAO {
	List<SbfUserTeam> getAllSBfUserTeams();
	void insertSbfUserTeam(SbfUserTeam a);
	void updateSbfUserTeam(SbfUserTeam a);
	void deleteSbfUserTeam(SbfUserTeam a);
}
