package com.jdf.SbfPortal.backend.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
					+ "SBF_ID, OWNER_NAME, DRAFT_SLOT, TEAM_NAME "
					+ "from SBF_TEAMS "
					+ "where LEAGUE_ID = " + leagueId;

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfTeam team = new SbfTeam(leagueId, rs.getString("OWNER_NAME"),
						rs.getInt("DRAFT_SLOT"),
						rs.getInt("SBF_ID"),
						rs.getString("TEAM_NAME")
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
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "insert into sbf_teams "
					+ "(league_id, sbf_id, owner_name, draft_slot) "
					+ "values (?,?,?,?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, r.getLeagueId());
			prepStmt.setInt(2, r.getSbfId());
			prepStmt.setString(3, r.getOwnerName());	
			prepStmt.setString(4, r.getTeamName());	
			prepStmt.execute();
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
		
	}

	@Override
	public void updateTeam(SbfTeam r) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "update sbf_teams "
					+ "set "
					+ "OWNER_NAME=?, "
					+ "TEAM_NAME=?, "
					+ "DRAFT_SLOT=? "
					+ "where league_id = ? and sbf_id = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setString(1, r.getOwnerName());
			prepStmt.setString(2, r.getTeamName());
			prepStmt.setInt(3, r.getDraftSlot());
			prepStmt.setInt(4, r.getLeagueId());
			prepStmt.setInt(5, r.getSbfId());
			prepStmt.execute();
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
	}

	@Override
	public void deleteTeam(SbfTeam r) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "delete from sbf_teams "
					+ "where sbf_id = ? ";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, r.getSbfId());
			prepStmt.execute();
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
		
		
	}
}