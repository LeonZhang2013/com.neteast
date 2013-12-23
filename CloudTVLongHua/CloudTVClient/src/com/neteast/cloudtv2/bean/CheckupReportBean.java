package com.neteast.cloudtv2.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.tools.MyLog;
import com.neteast.cloudtv2.tools.NetUtils;
import com.neteast.cloudtv2.tools.XmlTools;

/** 检查报告
 * 
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-5-31 */
public class CheckupReportBean {

	private String examDate;
	private String deptDesc;
	private String reportDocName;
	private String exDigReport;
	private String imageDis;
	private String remark;
	public String getExamDate() {
		if(examDate!=null&&examDate.length()>10){
			examDate = examDate.substring(0,10);
		}
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public String getDeptDesc() {
		return deptDesc;
	}
	public void setDeptDesc(String deptDesc) {
		this.deptDesc = deptDesc;
	}
	public String getReportDocName() {
		return reportDocName;
	}
	public void setReportDocName(String reportDocName) {
		this.reportDocName = reportDocName;
	}
	public String getExDigReport() {
		return exDigReport;
	}
	public void setExDigReport(String exDigReport) {
		this.exDigReport = exDigReport;
	}
	public String getImageDis() {
		return imageDis;
	}
	public void setImageDis(String imageDis) {
		this.imageDis = imageDis;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "CheckupReportBean [examDate=" + examDate + ", deptDesc=" + deptDesc + ", reportDocName=" + reportDocName
				+ ", exDigReport=" + exDigReport + ", imageDis=" + imageDis + ", remark=" + remark + "]";
	}
	
	public static List<CheckupReportBean> getDatas(String examdate) throws Exception {
		Map<String,String> data = new HashMap<String, String>();
		data.put("method", "getRisReport");
		data.put("patno", Constant.USER_BEAN.getPatNo());
		data.put("examdate", examdate);
		data.put("size", String.valueOf(30));
		InputStream in = NetUtils.requestData(Constant.SERVER_PATH, data);
		return XmlTools.parseList(in, CheckupReportBean.class);
	}
	
}
