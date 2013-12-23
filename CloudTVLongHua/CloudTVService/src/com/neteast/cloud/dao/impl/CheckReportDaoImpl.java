package com.neteast.cloud.dao.impl;

import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;

import com.neteast.cloud.dao.CheckReportDao;
import com.neteast.cloud.domain.CheckupReportBean;
import com.neteast.cloud.domain.MyCostBean;
import com.neteast.cloud.utils.JdbcUtil;

public class CheckReportDaoImpl implements CheckReportDao{

	public List<CheckupReportBean> getRisReportDataList(String pat_no,
			String examdate, int offset, int size) throws Exception{
		List<CheckupReportBean> list;
		try{
			String sql = "select TOP "+size+" * from ris_report where pat_no=? and examdate<=? ORDER BY examdate DESC";
			Object[] params = {pat_no,examdate};
			QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
			 list = (List<CheckupReportBean>) runner.query(sql, new BeanListHandler(CheckupReportBean.class),params);
		}catch (Exception e) {
			String sql = "select * from ris_report where pat_no=? and examdate<=? ORDER BY examdate DESC limit "+size;
			Object[] params = {pat_no,examdate};
			QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
			list = (List<CheckupReportBean>) runner.query(sql, new BeanListHandler(CheckupReportBean.class),params);
		}
		return list;
	}
	
	@Test
	public void test() throws Exception{
		List<CheckupReportBean> chargeData = getRisReportDataList("10004","2012-9-12",0,3);
		System.out.println(chargeData);
	}

}
