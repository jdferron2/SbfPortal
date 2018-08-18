package com.jdf.SbfPortal.backend.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jdf.SbfPortal.backend.data.SbfUser;

public class SbfUserDAOMysql implements SbfUserDAO{
	String jdbcUrl;
	private static Logger logger = Logger.getLogger(SbfUserDAOMysql.class);
	public SbfUserDAOMysql(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dbName = System.getProperty("RDS_DB_NAME");
			String userName = 	System.getProperty("RDS_USERNAME");
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
	public synchronized List<SbfUser> getAllSbfUsers() {

		List<SbfUser> sbfUsers = new ArrayList<SbfUser>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			logger.trace("Getting connection for getAllSbfUsers()");
			conn = DriverManager.getConnection(jdbcUrl);
			stmt = conn.createStatement();
			logger.info("Remote connection successful.");
			String sql = "select "
					+ "USER_ID, USER_NAME, EMAIL, PASSWORD, ROLE "
					+ "from SBF_USERS ";

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfUser user = new SbfUser(rs.getInt("USER_ID"),
						rs.getString("USER_NAME"),
						rs.getString("PASSWORD"),
						rs.getString("EMAIL"),
						rs.getString("ROLE")
						);
				sbfUsers.add(user);    				
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
		return sbfUsers;
	}

	@Override
	public void insertSbfUser(SbfUser u) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "insert into SBF_USERS "
					+ "(user_name, password, email, role) "
					+ "values (?,?,?,?)";
			prepStmt = conn.prepareStatement(sql);

			prepStmt.setString(1, u.getUserName());
			prepStmt.setString(2, u.getPassword());	
			prepStmt.setString(3, u.getEmail());	
			prepStmt.setString(4, u.getRole());
			prepStmt.execute();
			ResultSet rs = prepStmt.getGeneratedKeys();
			rs.next();
			u.setUserId(rs.getInt(1));
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
	public void updateSbfUser(SbfUser u) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "update SBF_USERS "
					+ "set "
					+ "EMAIL=?, "
					+ "PASSWORD=?, "
					+ "ROLE=? "
					+ "where USER_ID = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setString(1, u.getEmail());
			prepStmt.setString(2, u.getPassword());
			prepStmt.setString(3, u.getRole());
			prepStmt.setInt(4, u.getUserId());
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
	public void deleteSbfUser(SbfUser u) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(jdbcUrl);

			String sql = "delete from SBF_USERS "
					+ "where USER_ID = ? ";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, u.getUserId());
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