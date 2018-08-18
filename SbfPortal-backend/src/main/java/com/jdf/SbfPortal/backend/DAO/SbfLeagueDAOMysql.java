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

import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.utility.PropertyReader;

public class SbfLeagueDAOMysql implements SbfLeagueDAO {

	String jdbcUrl;
	private static Logger logger = Logger.getLogger(SbfLeagueDAOMysql.class);
	public SbfLeagueDAOMysql(){
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
	
	public synchronized List<SbfLeague> getAllSbfLeagues() {
		List<SbfLeague> sbfLeagues = new ArrayList<SbfLeague>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);
			stmt = conn.createStatement();
			String sql = "select "
					+ "LEAGUE_ID, LEAGUE_NAME, NUM_TEAMS, LEAGUE_MANAGER "
					+ "from SBF_LEAGUE";

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfLeague league = new SbfLeague(rs.getInt("LEAGUE_ID"),
						rs.getString("LEAGUE_NAME"),
						rs.getInt("NUM_TEAMS"),
						rs.getInt("LEAGUE_MANAGER")
						);
				sbfLeagues.add(league);    				
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


		return sbfLeagues;
	}

	public void insertSbfLeague(SbfLeague l) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "insert into SBF_LEAGUE "
					+ "(LEAGUE_NAME, NUM_TEAMS, LEAGUE_MANAGER) "
					+ "values (?,?,?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setString(1, l.getLeagueName());
			prepStmt.setInt(2, l.getNumTeams());	
			prepStmt.setInt(3, l.getLeagueManager());	
			prepStmt.execute();
			ResultSet rs = prepStmt.getGeneratedKeys();
			rs.next();
			l.setLeagueId(rs.getInt(1));
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

	public void updateSbfLeague(SbfLeague l) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "update SBF_LEAGUE set"
					+ "LEAGUE_NAME=?, "
					+ "NUM_TEAMS=?, "
					+ "LEAGUE_MANAGER=? "
					+ "WHERE league_id = ?";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setString(1, l.getLeagueName());
			prepStmt.setInt(2, l.getNumTeams());	
			prepStmt.setInt(3, l.getLeagueManager());	
			prepStmt.setInt(4, l.getLeagueId());
			prepStmt.execute();
			ResultSet rs = prepStmt.getGeneratedKeys();
			rs.next();
			l.setLeagueId(rs.getInt(1));
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

	public void deleteSbfLeague(SbfLeague r) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "delete from SBF_LEAGUE where "
					+ "LEAGUE_ID = ? ";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setInt(1,r.getLeagueId());
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
