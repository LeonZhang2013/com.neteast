package com.neteast.cloud.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.junit.Test;

import com.neteast.cloud.domain.AnalysisBean;
import com.neteast.cloud.domain.AnalysisItemBean;
import com.neteast.cloud.domain.CheckupReportBean;
import com.neteast.cloud.domain.MyCostBean;
import com.neteast.cloud.domain.MyCostItemBean;
import com.neteast.cloud.domain.PatientBean;
import com.neteast.cloud.service.BusinessService;
import com.neteast.cloud.service.impl.BusinessServiceImpl;

public class CloudData extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Document d = null;
		String method = request.getParameter("method");
		String result;
		try{
			if ("login".equals(method)) {
				d = login(request, response);
			} else if ("getMyCost".equals(method)) {
				d = getMyCost(request, response);
			} else if ("getMyCostItems".equals(method)) {
				d = getMyCostItems(request, response);
			} else if ("getLisReport".equals(method)) {
				d = getLisReport(request, response);
			} else if ("getLisReportItems".equals(method)) {
				d = getLisReportItems(request, response);
			} else if ("getRisReport".equals(method)) {
				d = getRisReport(request, response);
			}
			result = d.asXML();
		}catch (Exception e) {
			result = "数据不存在";
		}
		System.out.println(result);
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(result);
		out.flush();
		out.close();
	}

	private Document getMyCostItems(HttpServletRequest request,
			HttpServletResponse response) {
		String chg_seq = request.getParameter("chargeid");
		BusinessService bs = new BusinessServiceImpl();
		List<MyCostItemBean> list = null;
		try {
			list = bs.getChargeDataItems(chg_seq);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MyCostItemBean.buildXml(list);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private Document getRisReport(HttpServletRequest request,
			HttpServletResponse response) {
		String patno = request.getParameter("patno");
		String examdate = request.getParameter("examdate");
		int offset = getIntParams(request, "offset");
		int size = getIntParams(request, "size");
		BusinessService bs = new BusinessServiceImpl();
		List<CheckupReportBean> beans = null;
		try {
			beans = bs.getRisReportDataList(patno, examdate, offset, size);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CheckupReportBean.buildXml(beans);
	}

	private Document getLisReportItems(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String reportId = request.getParameter("reportid");
		int offset = getIntParams(request, "offset");
		int size = getIntParams(request, "size");
		BusinessService bs = new BusinessServiceImpl();
		List<AnalysisItemBean> bean = null;
		try {
			bean = bs.getLisReportDataList(reportId, offset, size);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return AnalysisItemBean.buildXml(bean);
	}

	private Document getLisReport(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String patno = request.getParameter("patno");
		String examdate = request.getParameter("examdate");
		int offset = getIntParams(request, "offset");
		int size = getIntParams(request, "size");
		BusinessService bs = new BusinessServiceImpl();
		List<AnalysisBean> beans = null;
		try {
			beans = bs.getLisReportData(patno, examdate, offset, size);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return AnalysisBean.buildXml(beans);
	}

	private Document getMyCost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String patno = request.getParameter("patno");
		String costdate = request.getParameter("costdate");
		String isadm = request.getParameter("isadm");
		int offset = getIntParams(request, "offset");
		int size = getIntParams(request, "size");
		BusinessService bs = new BusinessServiceImpl();
		List<MyCostBean> list = null;
		try {
			list = bs.getChargeData(patno, costdate,isadm,offset, size);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MyCostBean.buildXml(list);
	}
	
	private Document login(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		BusinessService service = new BusinessServiceImpl();
		PatientBean PatientBean = null;
		PatientBean = service.findPatient(username, password);
		return PatientBean.buildXml(PatientBean);
	}
	
	private int getIntParams(HttpServletRequest request,String paramsName){
		int result = 0;
		try {
			result = Integer.parseInt(request.getParameter(paramsName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
