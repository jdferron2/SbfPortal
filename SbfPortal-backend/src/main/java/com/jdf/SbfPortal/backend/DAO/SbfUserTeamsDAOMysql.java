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

import com.jdf.SbfPortal.backend.data.SbfUserTeam;

public class SbfUserTeamsDAOMysql implements SbfUserTeamsDAO{
	String jdbcUrl;
	private static Logger logger = Logger.getLogger(SbfUserTeamsDAOMysql.class);
	public SbfUserTeamsDAOMysql(){
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

	@Override
	public synchronized List<SbfUserTeam> getAllSBfUserTeams() {

		List<SbfUserTeam> sbfUserTeams = new ArrayList<SbfUserTeam>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			stmt = conn.createStatement();
			String sql = "select "
					+ "USER_ID, LEAGUE_ID, TEAM_ID, DEFAULT_RANK_SET_ID "
					+ "from SBF_USER_TEAMS ";

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfUserTeam team = new SbfUserTeam(rs.getInt("LEAGUE_ID"),
						rs.getInt("TEAM_ID"),
						rs.getInt("USER_ID"),
						rs.getInt("DEFAULT_RANK_SET_ID")
						
						);
				sbfUserTeams.add(team);    				
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
		return sbfUserTeams;
	}

	@Override
	public void insertSbfUserTeam(SbfUserTeam t) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "insert into SBF_USER_TEAMS "
					+ "(USER_ID, TEAM_ID, LEAGUE_ID) "
					+ "values (?,?,?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, t.getUserId());
			prepStmt.setInt(2, t.getTeamId());
			prepStmt.setInt(3, t.getLeagueId());
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
	public void updateSbfUserTeam(SbfUserTeam t) {
				PreparedStatement prepStmt=null;
				Connection conn = null;
				try {
					conn = DriverManager.getConnection(jdbcUrl);
		
					String sql = "update SBF_USER_TEAMS "
							+ "set "
							+ "TEAM_ID=?, "
							+ "DEFAULT_RANK_SET_ID=? "
							+ "where USER_ID = ? and league_id = ?";
					prepStmt = conn.prepareStatement(sql);
					prepStmt.setInt(1, t.getTeamId());
					prepStmt.setInt(2, t.getDefaultRankSetId());
					prepStmt.setInt(3, t.getUserId());
					prepStmt.setInt(4, t.getLeagueId());
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
	public void deleteSbfUserTeam(SbfUserTeam t) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "delete from SBF_USER_TEAMS "
					+ "where USER_ID = ? and team_id = ? and league_id = ? ";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, t.getUserId());
			prepStmt.setInt(2, t.getTeamId());
			prepStmt.setInt(3, t.getLeagueId());
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