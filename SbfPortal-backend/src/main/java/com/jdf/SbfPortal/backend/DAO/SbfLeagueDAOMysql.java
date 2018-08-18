package com.jdf.SbfPortal.backend.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
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

	public void insertSbfLeague(SbfLeague r) {
		// TODO Auto-generated method stub
		
	}

	public void updateSbfLeague(SbfLeague r) {
		// TODO Auto-generated method stub
		
	}

	public void deleteSbfLeague(SbfLeague r) {
		// TODO Auto-generated method stub
		
	}	
}
