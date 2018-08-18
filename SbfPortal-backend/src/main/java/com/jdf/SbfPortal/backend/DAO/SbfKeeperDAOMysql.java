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

import com.jdf.SbfPortal.backend.data.SbfKeeper;

public class SbfKeeperDAOMysql implements SbfKeeperDAO {

	String jdbcUrl;
	private static Logger logger = Logger.getLogger(SbfKeeperDAOMysql.class);
	public SbfKeeperDAOMysql(){
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
	
	public synchronized List<SbfKeeper> getAllSbfKeepers(int leagueId) {
		List<SbfKeeper> SbfKeepers = new ArrayList<SbfKeeper>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);
			stmt = conn.createStatement();
			String sql = "select "
					+ "LEAGUE_ID, TEAM_ID, PLAYER_ID, ROUND "
					+ "from SBF_KEEPERS where league_id = " + leagueId;

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfKeeper keeper = new SbfKeeper(rs.getInt("LEAGUE_ID"),
						rs.getInt("TEAM_ID"),
						rs.getInt("PLAYER_ID"),
						rs.getInt("ROUND")
						);
				SbfKeepers.add(keeper);    				
			}
		} catch (Exception ex) {
			logger.error("Stack Trace: ",  ex);
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqlEx) { } // ignore

				conn = null;
			}

		}	


		return SbfKeepers;
	}

	public void insertSbfKeeper(SbfKeeper r) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "insert into SBF_KEEPERS "
					+ "(TEAM_ID, LEAGUE_ID, PLAYER_ID, ROUND) "
					+ "values (?,?,?,?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1, r.getTeamId());
			prepStmt.setInt(2, r.getLeagueId());
			prepStmt.setInt(3, r.getPlayerId());
			prepStmt.setInt(4, r.getRound());
			
			prepStmt.execute();
		} catch (Exception ex) {
			logger.error("Stack Trace: ",  ex);
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

	public void updateSbfKeeper(SbfKeeper r) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);
			
			String sql = "update SBF_KEEPERS "
					+ "set "
					+ "TEAM_ID=?, "
					+ "ROUND=? "
					+ "where league_id = ? and player_id = ?";
			prepStmt = conn.prepareStatement(sql);


			prepStmt.setInt(1, r.getTeamId());
			prepStmt.setInt(2, r.getRound());
			prepStmt.setInt(3, r.getLeagueId());
			prepStmt.setInt(4, r.getPlayerId());
			prepStmt.executeUpdate();
		} catch (Exception ex) {
			logger.error("Stack Trace: ",  ex);
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

	public void deleteSbfKeeper(SbfKeeper r) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "delete from SBF_KEEPERS where "
					+ "LEAGUE_ID = ? and "
					+ "PLAYER_ID = ? ";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1,r.getLeagueId());
			prepStmt.setInt(2,r.getPlayerId());
			prepStmt.execute();
		} catch (Exception ex) {
			logger.error("Stack Trace: ",  ex);
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
