package com.jdf.SbfPortal.backend.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
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
	String jdbcUrl;
	private static Logger logger = Logger.getLogger(SbfTeamDAOMysql.class);
	public SbfTeamDAOMysql(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dbName = System.getProperty("RDS_DB_NAME");
			String userName = System.getProperty("RDS_USERNAME");
			String password = System.getProperty("RDS_PASSWORD");
			String hostname = System.getProperty("RDS_HOSTNAME");
			String port = System.getProperty("RDS_PORT");
			jdbcUrl = "jdbc:mysql://" + hostname + ":" +
					port + "/" + dbName + "?user=" + userName + "&password=" + password;
			logger.trace("Building connection string from environment variables.");
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFound in..." , e);
		} 
	}
	
	public synchronized List<SbfTeam> getAllTeams() {

		List<SbfTeam> sbfTeams = new ArrayList<SbfTeam>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			stmt = conn.createStatement();
			String sql = "select "
					+ "TEAM_ID, OWNER_NAME, DRAFT_SLOT, TEAM_NAME, LEAGUE_ID, USER_ID, THEME_SONG_URL "
					+ "from SBF_TEAMS ";

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfTeam team = new SbfTeam(rs.getInt("LEAGUE_ID"),
						rs.getString("OWNER_NAME"),
						rs.getInt("DRAFT_SLOT"),
						rs.getInt("TEAM_ID"),
						rs.getString("TEAM_NAME"),
						rs.getInt("USER_ID"),
						rs.getString("THEME_SONG_URL")
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
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "insert into SBF_TEAMS "
					+ "(league_id, owner_name, draft_slot, team_name, user_id, theme_song_url) "
					+ "values (?,?,?,?,?,?)";
			prepStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			prepStmt.setInt(1, r.getLeagueId());
			prepStmt.setString(2, r.getOwnerName());	
			prepStmt.setInt(3, r.getDraftSlot());	
			prepStmt.setString(4, r.getTeamName());	
			prepStmt.setInt(5, r.getUserId());
			prepStmt.setString(6, r.getThemeSongUrl());
			prepStmt.execute();
			ResultSet rs = prepStmt.getGeneratedKeys();
			rs.next();
			r.setTeamId(rs.getInt(1));
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
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "update SBF_TEAMS "
					+ "set "
					+ "OWNER_NAME=?, "
					+ "TEAM_NAME=?, "
					+ "DRAFT_SLOT=?, "
					+ "USER_ID=?, "
					+ "THEME_SONG_URL=? "
					+ "where league_id = ? and team_id = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setString(1, r.getOwnerName());
			prepStmt.setString(2, r.getTeamName());
			prepStmt.setInt(3, r.getDraftSlot());
			prepStmt.setInt(4, r.getUserId());
			prepStmt.setString(5, r.getThemeSongUrl());
			prepStmt.setInt(6, r.getLeagueId());
			prepStmt.setInt(7, r.getTeamId());
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
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "delete from SBF_TEAMS "
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