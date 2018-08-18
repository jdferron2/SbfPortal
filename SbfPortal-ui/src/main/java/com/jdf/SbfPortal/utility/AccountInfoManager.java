package com.jdf.SbfPortal.utility;

import com.jdf.SbfPortal.authentication.UserSessionVars;
import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.data.SbfTeam;
import com.jdf.SbfPortal.backend.data.SbfUser;
import com.jdf.SbfPortal.backend.data.SbfUserTeam;

public class AccountInfoManager {

	public AccountInfoManager(){
		initialize();
	}
	
	private void initialize(){
		
	}
	
	public boolean createUserAccount(String userName, String password, String email){
		UserSessionVars.getLeagueService().insertSbfUser(new SbfUser(0, userName, password, email, "user"));
		return true;
	}
	
	
}
