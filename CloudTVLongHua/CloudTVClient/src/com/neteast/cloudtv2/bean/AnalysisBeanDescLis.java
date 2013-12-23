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
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.tools.NetUtils;
import com.neteast.cloudtv2.tools.XmlTools;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-7-1 */
public class AnalysisBeanDescLis {

	private String sampleNo;
	private String specimenType;
	private String lisItemCode;
	private String lisItemName;
	private String lisValue;
	private String referScope;
	private String unit;
	private String auditingName;

	@Override
	public String toString() {
		return "AnalysisBeanDescLis [sampleNo=" + sampleNo + ", specimenType=" + specimenType + ", lisItemCode=" + lisItemCode
				+ ", lisItemName=" + lisItemName + ", lisValue=" + lisValue + ", referScope=" + referScope + ", unit=" + unit
				+ ", auditingName=" + auditingName + "]";
	}

	/** 检验号 **/
	public String getSampleNo() {
		return sampleNo;
	}

	/** 样本类型 **/
	public String getSpecimenType() {
		return specimenType;
	}

	/** 项目代码 **/
	public String getLisItemCode() {
		return lisItemCode;
	}

	/** 项目名称 **/
	public String getLisItemName() {
		return lisItemName;
	}

	/** 检查值 **/
	public String getLisValue() {
		return lisValue;
	}

	/** 参考范围 **/
	public String getReferScope() {
		return referScope;
	}

	/** 单位 **/
	public String getUnit() {
		return unit;
	}

	public String getAuditingName() {
		return auditingName;
	}

	public void setAuditingName(String auditingName) {
		this.auditingName = auditingName;
	}

	public void setSampleNo(String sampleNo) {
		this.sampleNo = sampleNo;
	}

	public void setSpecimenType(String specimenType) {
		this.specimenType = specimenType;
	}

	public void setLisItemCode(String lisItemCode) {
		this.lisItemCode = lisItemCode;
	}

	public void setLisItemName(String lisItemName) {
		this.lisItemName = lisItemName;
	}

	public void setLisValue(String lisValue) {
		this.lisValue = lisValue;
	}

	public void setReferScope(String referScope) {
		this.referScope = referScope;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public static List<AnalysisBeanDescLis> getDatas(String examNo) throws Exception {
		Map<String,String> data = new HashMap<String, String>();
		data.put("method", "getLisReportItems");
		data.put("reportid", examNo);
		data.put("size", String.valueOf(30));
		InputStream in = NetUtils.requestData(Constant.SERVER_PATH, data);
		return XmlTools.parseList(in, AnalysisBeanDescLis.class);
	}
	
	
}
