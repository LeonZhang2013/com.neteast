package com.neteast.cloud.dao;


import java.util.List;

import com.neteast.cloud.domain.MyCostBean;
import com.neteast.cloud.domain.MyCostItemBean;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-5-28
 */
public interface MyCostDao {

	void add(MyCostBean m)  throws Exception;
	
	List<MyCostBean> getChargeData(String pat_no,
			String cost_date,String is_adm, int offset,int size)  throws Exception;
	
	public List<MyCostItemBean> getChargeDataItems(String chg_seq) throws Exception ;
}
