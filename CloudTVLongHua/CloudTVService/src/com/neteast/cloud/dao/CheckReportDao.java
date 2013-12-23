package com.neteast.cloud.dao;

import java.util.List;

import com.neteast.cloud.domain.CheckupReportBean;

public interface CheckReportDao {

	List<CheckupReportBean> getRisReportDataList(String pat_no,
			String examdate, int offset, int size) throws Exception;
}
