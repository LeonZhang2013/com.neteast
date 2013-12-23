package com.neteast.cloud.dao;

import java.sql.SQLException;
import java.util.List;

import com.neteast.cloud.domain.AnalysisBean;
import com.neteast.cloud.domain.AnalysisItemBean;

public interface AnalysisDao {

	void add(AnalysisItemBean ab);

	List<AnalysisItemBean> getLisReportDataList(String reportId, int offset, int endDate) throws SQLException ;

	List<AnalysisBean> getLisReportData(String patno, String startDate,int ofset, int size) throws SQLException ;
}
