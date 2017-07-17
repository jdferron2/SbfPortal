package com.jdf.SbfPortal.backend.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.jdf.SbfPortal.backend.data.SbfTeam;

public class SbfTeamDAOMysql implements SbfTeamDAO{
	InitialContext ctx;
	Context envContext;
	DataSource ds;
	public SbfTeamDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized List<SbfTeam> getAllTeams(Integer leagueId) {

		List<SbfTeam> sbfTeams = new ArrayList<SbfTeam>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			stmt = conn.createStatement();
			String sql = "select "
					+ "SBF_ID, OWNER_NAME, DRAFT_SLOT "
					+ "from SBF_TEAMS "
					+ "where LEAGUE_ID = " + leagueId;

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfTeam team = new SbfTeam(leagueId, rs.getString("OWNER_NAME"),
						rs.getInt("DRAFT_SLOT"),
						rs.getInt("SBF_ID")
						);
				sbfTeams.add(team);    				
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			// handle the error
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqlEx) { } // ignore

				conn = null;
			}

		}	
		return sbfTeams;
	}

	@Override
	public void insertTeam(SbfTeam r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTeam(SbfTeam r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTeam(SbfTeam r) {
		// TODO Auto-generated method stub
		
	}
}