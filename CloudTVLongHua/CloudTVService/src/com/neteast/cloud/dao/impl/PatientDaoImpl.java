package com.neteast.cloud.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.neteast.cloud.dao.PatientDao;
import com.neteast.cloud.domain.PatientBean;
import com.neteast.cloud.utils.JdbcUtil;

public class PatientDaoImpl implements PatientDao {

	public void add(PatientBean p) throws Exception {
	}

	public PatientBean find(String username, String password) throws Exception {
		PatientBean bean = null;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "select pat_no from pat_account where username=? and password=?";
			con = JdbcUtil.getConnection();
			ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, password);
			rs = ps.executeQuery();
			System.out.println(username+password);
			if(rs.next()){
				Object pat_no = rs.getString(1);
				System.out.println(pat_no);
				sql = "select * from pat_info where pat_no=?";
				QueryRunner q = new QueryRunner(JdbcUtil.getDataSource());
				bean = (PatientBean) q.query(sql,pat_no,new BeanHandler(PatientBean.class));
			}
		}finally{
			JdbcUtil.release(con, ps, rs);
		}
		return bean;
	}
}
