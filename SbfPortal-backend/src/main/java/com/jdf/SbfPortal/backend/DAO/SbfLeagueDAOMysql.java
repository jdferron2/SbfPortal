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

import com.jdf.SbfPortal.backend.data.SbfLeague;
import com.jdf.SbfPortal.backend.utility.PropertyReader;

public class SbfLeagueDAOMysql implements SbfLeagueDAO {

	InitialContext ctx;
	Context envContext;
	DataSource ds;
	public SbfLeagueDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized List<SbfLeague> getAllSbfLeagues() {
		List<SbfLeague> sbfLeagues = new ArrayList<SbfLeague>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();
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
