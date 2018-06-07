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

import com.jdf.SbfPortal.backend.data.SbfUser;

public class SbfUserDAOMysql implements SbfUserDAO{
	InitialContext ctx;
	Context envContext;
	DataSource ds;
	private static Logger logger = Logger.getLogger(SbfUserDAOMysql.class);
	public SbfUserDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			logger.error("Stack Trace: ", e);
		}
	}

	@Override
	public synchronized List<SbfUser> getAllSbfUsers() {

		List<SbfUser> sbfUsers = new ArrayList<SbfUser>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			stmt = conn.createStatement();
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
			conn = ds.getConnection();

			String sql = "insert into sbf_users "
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
			conn = ds.getConnection();

			String sql = "update sbf_users "
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
			conn = ds.getConnection();

			String sql = "delete from sbf_users "
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