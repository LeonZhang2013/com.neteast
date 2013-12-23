package com.neteast.cloudtv2.bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.tools.NetUtils;
import com.neteast.cloudtv2.tools.XmlTools;

/** 检验报告
 * 
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-5-31 */
public class AnalysisBean {

	private String examNo;
	private String itemName;
	private String lisTmNo;
	private String sampeNo;
	private String exPart;
	private String petitionDoc;
	private String deptDesc;
	private String clinicalDig;
	private String examDate;
	private String remark;

	@Override
	public String toString() {
		return "AnalysisBean [examNo=" + examNo + ", itemName=" + itemName + ", lisTmNo=" + lisTmNo + ", sampeNo=" + sampeNo
				+ ", exPart=" + exPart + ", petitionDoc=" + petitionDoc + ", deptDesc=" + deptDesc + ", clinicalDig="
				+ clinicalDig + ", examDate=" + examDate + ", remark=" + remark + "]";
	}

	/** 报告id **/
	public String getExamNo() {
		return examNo;
	}

	/** 诊断 **/
	public String getClinicalDig() {
		return clinicalDig;
	}

	/** 条形码 **/
	public String getLisTmNo() {
		return lisTmNo;
	}

	/** 样本号 **/
	public String getSampeNo() {
		if(sampeNo==null) return "";
		return sampeNo;
	}

	/** 标本种类 **/
	public String getExPart() {
		return exPart;
	}

	/** 申请医生 **/
	public String getPetitionDoc() {
		return petitionDoc;
	}

	public String getDeptDesc() {
		return deptDesc;
	}

	public String getRemark() {
		return remark;
	}

	/** 检验日期 **/
	public String getExamDate() {
		if (examDate.length() > 10) {
			examDate = examDate.substring(0, 10);
		}
		return examDate;
	}

	/** 检验项目 **/
	public String getItemName() {
		return itemName;
	}

	public void setExamNo(String examNo) {
		this.examNo = examNo;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setLisTmNo(String lisTmNo) {
		this.lisTmNo = lisTmNo;
	}

	public void setSampeNo(String sampeNo) {
		this.sampeNo = sampeNo;
	}

	public void setExPart(String exPart) {
		this.exPart = exPart;
	}

	public void setPetitionDoc(String petitionDoc) {
		this.petitionDoc = petitionDoc;
	}

	public void setDeptDesc(String deptDesc) {
		this.deptDesc = deptDesc;
	}

	public void setClinicalDig(String clinicalDig) {
		this.clinicalDig = clinicalDig;
	}

	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public static List<AnalysisBean> getDatas(String examdate) throws Exception {
		Map<String,String> data = new HashMap<String, String>();
		data.put("method", "getLisReport");
		data.put("patno", Constant.USER_BEAN.getPatNo());
		data.put("examdate", examdate);
		data.put("size", String.valueOf(30));
		InputStream in = NetUtils.requestData(Constant.SERVER_PATH, data);
		return XmlTools.parseList(in, AnalysisBean.class);
	}

}
