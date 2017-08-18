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

import org.apache.log4j.Logger;

import com.jdf.SbfPortal.backend.data.SbfTeam;

public class SbfTeamDAOMysql implements SbfTeamDAO{
	InitialContext ctx;
	Context envContext;
	DataSource ds;
	private static Logger logger = Logger.getLogger(SbfTeamDAOMysql.class);
	public SbfTeamDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			logger.error("Stack Trace: ", e);
		}
	}
	
	public synchronized List<SbfTeam> getAllTeams() {

		List<SbfTeam> sbfTeams = new ArrayList<SbfTeam>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			stmt = conn.createStatement();
			String sql = "select "
					+ "TEAM_ID, OWNER_NAME, DRAFT_SLOT, TEAM_NAME, LEAGUE_ID, USER_ID "
					+ "from SBF_TEAMS ";

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfTeam team = new SbfTeam(rs.getInt("LEAGUE_ID"),
						rs.getString("OWNER_NAME"),
						rs.getInt("DRAFT_SLOT"),
						rs.getInt("TEAM_ID"),
						rs.getString("TEAM_NAME"),
						rs.getInt("USER_ID")
						);
				sbfTeams.add(team);    				
			}
		} catch (Exception ex) {
			logger.error("Stack Trace: ", ex);
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
					+ "(league_id, team_id, owner_name, draft_slot, user_id) "
					+ "values (?,?,?,?, ?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, r.getLeagueId());
			prepStmt.setInt(2, r.getTeamId());
			prepStmt.setString(3, r.getOwnerName());	
			prepStmt.setString(4, r.getTeamName());	
			prepStmt.setInt(5, r.getUserId());
			prepStmt.execute();
		} catch (Exception ex) {
			logger.error("Stack Trace: ", ex);
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
					+ "DRAFT_SLOT=?, "
					+ "USER_ID=? "
					+ "where league_id = ? and team_id = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setString(1, r.getOwnerName());
			prepStmt.setString(2, r.getTeamName());
			prepStmt.setInt(3, r.getDraftSlot());
			prepStmt.setInt(4, r.getUserId());
			prepStmt.setInt(5, r.getLeagueId());
			prepStmt.setInt(6, r.getTeamId());
			prepStmt.execute();
		} catch (Exception ex) {
			logger.error("Stack Trace: ", ex);
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
					+ "where team_id = ? ";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, r.getTeamId());
			prepStmt.execute();
		} catch (Exception ex) {
			logger.error("Stack Trace: ", ex);
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