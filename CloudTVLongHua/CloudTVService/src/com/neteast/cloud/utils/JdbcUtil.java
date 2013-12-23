package com.neteast.cloud.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JdbcUtil {
	
    private static DataSource ds = null;
    static{
           try{
                // 读取默认的可以不添，如果需要直指定就填写对应的配置名称
                ds = new ComboPooledDataSource();
          } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
          }
    }

    public static Connection getConnection() throws SQLException{
           return ds.getConnection();
    }
    
    public static DataSource getDataSource(){
    	return ds;
    }
	
	public static void release(Connection cn,Statement st,ResultSet rs){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rs = null;
		}
		
		if(st !=null){
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			st = null;
		}
		
		if(cn !=null){
			try {
				cn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			cn = null;
		}
	}
}

