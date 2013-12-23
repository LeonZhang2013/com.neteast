package com.neteast.cloud.service.impl;

import java.util.List;

import com.neteast.cloud.dao.AnalysisDao;
import com.neteast.cloud.dao.CheckReportDao;
import com.neteast.cloud.dao.MyCostDao;
import com.neteast.cloud.dao.PatientDao;
import com.neteast.cloud.domain.AnalysisBean;
import com.neteast.cloud.domain.AnalysisItemBean;
import com.neteast.cloud.domain.CheckupReportBean;
import com.neteast.cloud.domain.MyCostBean;
import com.neteast.cloud.domain.MyCostItemBean;
import com.neteast.cloud.domain.PatientBean;
import com.neteast.cloud.factory.DaoFactory;
import com.neteast.cloud.service.BusinessService;

public class BusinessServiceImpl implements BusinessService{

	private AnalysisDao Analysis = DaoFactory.getInstance().createDao(AnalysisDao.class); 
	private MyCostDao MyCost = DaoFactory.getInstance().createDao(MyCostDao.class); 
	private PatientDao Patient = DaoFactory.getInstance().createDao(PatientDao.class); 
	private CheckReportDao CheckReport = DaoFactory.getInstance().createDao(CheckReportDao.class); 
	
	public void add(PatientBean p) throws Exception {
		Patient.add(p);
	}

	public void add(AnalysisItemBean ab)  throws Exception {
		Analysis.add(ab);
	}

	public void add(MyCostBean m)  throws Exception {
		MyCost.add(m);
	}
	
	@Override
	public List<MyCostItemBean> getChargeDataItems(String chg_seq)
			throws Exception {
		return MyCost.getChargeDataItems(chg_seq);
	}

	@Override
	public PatientBean findPatient(String username, String password)
			throws Exception {
		return Patient.find(username, password);
	}

	@Override
	public List<MyCostBean> getChargeData(String patno, String costDate,
			String isAdm, int offset, int size) throws Exception {
		return MyCost.getChargeData(patno, costDate, isAdm, offset, size);
	}

	@Override
	public List<AnalysisBean> getLisReportData(String patno, String examdate,
			int offset, int size) throws Exception {
		return Analysis.getLisReportData(patno, examdate, offset, size);
	}

	@Override
	public List<AnalysisItemBean> getLisReportDataList(String reportId,
			int offset, int size) throws Exception {
		return Analysis.getLisReportDataList(reportId, offset, size);
	}

	@Override
	public List<CheckupReportBean> getRisReportDataList(String patno,
			String examdate, int offset, int size) throws Exception {
		return CheckReport.getRisReportDataList(patno, examdate, offset, size);
	}


}
