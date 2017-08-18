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

import com.jdf.SbfPortal.backend.data.SbfRankSet;

public class SbfRankSetDAOMysql implements SbfRankSetsDAO{
	InitialContext ctx;
	Context envContext;
	DataSource ds;
	private static Logger logger = Logger.getLogger(SbfRankSetDAOMysql.class);
	public SbfRankSetDAOMysql(){
		try {
			ctx = new InitialContext();
			envContext  = (Context)ctx.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/MyDB");
		} catch (NamingException e) {
			logger.error("Stack Trace: " + e);
		}
	}

	@Override
	public synchronized List<SbfRankSet> getAllSbfRankSets(Integer userId) {

		List<SbfRankSet> SbfRankSets = new ArrayList<SbfRankSet>();
		Statement stmt=null;
		ResultSet rs=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			stmt = conn.createStatement();
			String sql = "select "
					+ "USER_ID, RANK_SET_ID, RANK_SET_NAME, DATE_CREATED "
					+ "from SBF_RANK_SETS where USER_ID = " + userId;

			rs = stmt.executeQuery(sql);
			while (rs.next()){
				SbfRankSet rankSet = new SbfRankSet(rs.getInt("USER_ID"),
						rs.getInt("RANK_SET_ID"),
						rs.getString("RANK_SET_NAME"),
						rs.getDate("DATE_CREATED")
						);
				SbfRankSets.add(rankSet);    				
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
		return SbfRankSets;
	}

	@Override
	public void insertSbfRankSet(SbfRankSet s) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "insert into sbf_rank_sets "
					+ "(USER_ID, RANK_SET_NAME) "
					+ "values (?,?)";
			prepStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			prepStmt.setInt(1, s.getUserId());
			prepStmt.setString(2, s.getRankSetName());
			prepStmt.executeUpdate();
			ResultSet rs = prepStmt.getGeneratedKeys();
			rs.next();
			s.setRankSetId(rs.getInt(1));
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

	@Override
	public void updateSbfRankSet(SbfRankSet s) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "update sbf_rank_sets "
					+ "set "
					+ "RANK_SET_NAME=? "
					+ "where RANK_SET_ID = ?";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setString(1, s.getRankSetName());
			prepStmt.setInt(2, s.getRankSetId());
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

	@Override
	public void deleteSbfRankSet(SbfRankSet s) {
		PreparedStatement prepStmt=null;
		Connection conn = null;
		try {
			conn = ds.getConnection();

			String sql = "delete from sbf_rank_sets "
					+ "where rank_set_id = ? ";
			prepStmt = conn.prepareStatement(sql);
			prepStmt.setInt(1, s.getRankSetId());
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