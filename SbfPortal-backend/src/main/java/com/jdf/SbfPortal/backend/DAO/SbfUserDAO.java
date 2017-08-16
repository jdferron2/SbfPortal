package com.jdf.SbfPortal.backend.DAO;

import java.util.List;

import com.jdf.SbfPortal.backend.data.SbfUser;

public interface SbfUserDAO {
	List<SbfUser> getAllSbfUsers();
	void insertSbfUser(SbfUser u);
	void updateSbfUser(SbfUser u);
	void deleteSbfUser(SbfUser u);
}
