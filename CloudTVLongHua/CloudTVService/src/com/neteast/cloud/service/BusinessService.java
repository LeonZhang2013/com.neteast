package com.neteast.cloud.service;

import java.util.List;

import com.neteast.cloud.domain.AnalysisBean;
import com.neteast.cloud.domain.AnalysisItemBean;
import com.neteast.cloud.domain.CheckupReportBean;
import com.neteast.cloud.domain.MyCostBean;
import com.neteast.cloud.domain.MyCostItemBean;
import com.neteast.cloud.domain.PatientBean;

public interface BusinessService {

	void add(PatientBean p)  throws Exception ;

	void add(AnalysisItemBean ab)  throws Exception ;

	void add(MyCostBean m)  throws Exception ;
	
	PatientBean findPatient(String username, String password) throws Exception;

	List<CheckupReportBean> getRisReportDataList(String patno, String examdate, int offset, int size)  throws Exception ;

	List<MyCostBean> getChargeData(String patno, String costDate, String isAdm, int offset, int size)  throws Exception ;

	List<AnalysisBean> getLisReportData(String patno, String examdate, int offset, int size)  throws Exception ;

	List<AnalysisItemBean> getLisReportDataList(String reportId, int offset, int size)  throws Exception ;
	
	public List<MyCostItemBean> getChargeDataItems(String chg_seq) throws Exception ;
}
