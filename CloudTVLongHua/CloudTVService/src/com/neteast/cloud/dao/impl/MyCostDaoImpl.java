package com.neteast.cloud.dao.impl;

import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;

import com.neteast.cloud.dao.MyCostDao;
import com.neteast.cloud.domain.MyCostBean;
import com.neteast.cloud.domain.MyCostItemBean;
import com.neteast.cloud.utils.JdbcUtil;

public class MyCostDaoImpl implements MyCostDao {

	@Override
	public void add(MyCostBean m) throws Exception {
		
	}

	@Override
	public List<MyCostBean> getChargeData(String pat_no, String cost_date,
			String is_adm, int offset, int size) throws Exception {
		String sql = "";
		List<MyCostBean> list;
		try{
			if("true".equals(is_adm)||"false".equals(is_adm)){
				sql = "select TOP "+size+" row_number() OVER (ORDER BY billing_date DESC), * from charge_record where pat_no=? and billing_date<=? and is_adm=?";
				Object[] params = {pat_no,cost_date,is_adm};
				QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
				list = (List<MyCostBean>) runner.query(sql, new BeanListHandler(MyCostBean.class),params);
			}else{
				sql = "select TOP "+size+" row_number() OVER (ORDER BY billing_date DESC), * from charge_record where pat_no=? and billing_date<=?";
				Object[] params = {pat_no,cost_date};
				QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
				list = (List<MyCostBean>) runner.query(sql, new BeanListHandler(MyCostBean.class),params);
			}
		}catch (Exception e) {
			if("true".equals(is_adm)||"false".equals(is_adm)){
				sql = "select * from charge_record where pat_no=? and billing_date<=? and is_adm=? ORDER BY billing_date DESC limit "+size;
				Object[] params = {pat_no,cost_date,is_adm};
				QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
				list = (List<MyCostBean>) runner.query(sql, new BeanListHandler(MyCostBean.class),params);
			}else{
				sql = "select * from charge_record where pat_no=? and billing_date<=? ORDER BY billing_date DESC limit "+size;
				Object[] params = {pat_no,cost_date};
				QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
				list = (List<MyCostBean>) runner.query(sql, new BeanListHandler(MyCostBean.class),params);
			}
		}
		return list;
	}
	
	@Override
	public List<MyCostItemBean> getChargeDataItems(String chg_seq) throws Exception {
		String sql = "select * from charge_record_items where chg_seq=?";
		Object[] params = {chg_seq};
		QueryRunner runner = new QueryRunner(JdbcUtil.getDataSource());
		List<MyCostItemBean> list = (List<MyCostItemBean>) runner.query(sql, new BeanListHandler(MyCostItemBean.class),params);
		return list;
	}
	
	@Test
	public void test() throws Exception{
		List<MyCostItemBean> chargeDataItems = getChargeDataItems("12345");
		for(int i=0; i<chargeDataItems.size(); i++){
			System.out.println(chargeDataItems.get(i));
		}
	}
	

}
