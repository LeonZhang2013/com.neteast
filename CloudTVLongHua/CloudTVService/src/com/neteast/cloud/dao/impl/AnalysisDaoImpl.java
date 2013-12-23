package com.neteast.cloud.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;

import com.neteast.cloud.dao.AnalysisDao;
import com.neteast.cloud.domain.AnalysisBean;
import com.neteast.cloud.domain.AnalysisItemBean;
import com.neteast.cloud.domain.CheckupReportBean;
import com.neteast.cloud.utils.JdbcUtil;

/**
 * 检验报告
 * 
 * @author zll
 * 
 */
public class AnalysisDaoImpl implements AnalysisDao {

	public void add(AnalysisItemBean ab) {

	}

	public List<AnalysisItemBean> getLisReportDataList(String exam_no,
			int offset, int size) throws SQLException {
		String sql = "select * from lis_report_items where exam_no=? ";
		Object[] params = {exam_no};
		QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
		List<AnalysisItemBean> list = (List<AnalysisItemBean>) runner.query(sql, new BeanListHandler(AnalysisItemBean.class),params);
		return list;
	}

	public List<AnalysisBean> getLisReportData(String patno, String startDate,
			int ofset, int size) throws SQLException{
		List<AnalysisBean> list;
		try{
			String sql = "select TOP "+size+" row_number() OVER (ORDER BY exam_date DESC),* from lis_report where pat_no=? and exam_date<=? ";
			Object[] params = {patno,startDate};
			QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
			list = (List<AnalysisBean>) runner.query(sql, new BeanListHandler(AnalysisBean.class),params);
		}catch (Exception e) {
			String sql = "select * from lis_report where pat_no=? and exam_date<=? order by exam_date desc limit "+size;
			Object[] params = {patno,startDate};
			QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
			list = (List<AnalysisBean>) runner.query(sql, new BeanListHandler(AnalysisBean.class),params);
		}
		return list;
	}
	
	@Test
	public void test() throws Exception{
		List<AnalysisBean> chargeData = getLisReportData("10004","2012-12-12",0,3);
		System.out.println(chargeData);
	}
	
	@Test
	public void test1() throws Exception{
		List<AnalysisItemBean> chargeData = getLisReportDataList("ax99",0,0);
		System.out.println(chargeData);
	}
}
