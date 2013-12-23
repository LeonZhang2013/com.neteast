package com.neteast.cloud.domain;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.neteast.cloud.utils.XmlUtils;

/**
 * 检查报告
 * 
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-5-31
 */
public class CheckupReportBean {

	private String PAT_NO;
	private String EXAMDATE;
	private String DEPT_DESC;
	private String REPORT_DOCNAME;
	private String EX_DIG_PROMPT;
	private String IMAGE_DIS;
	private String REMARK;

	public String getPAT_NO() {
		if (PAT_NO == null)
			PAT_NO = "";
		return PAT_NO;
	}

	public void setPAT_NO(String pat_no) {
		PAT_NO = pat_no;
	}

	public String getDEPT_DESC() {
		if (DEPT_DESC == null)
			DEPT_DESC = "";
		return DEPT_DESC;
	}

	public void setDEPT_DESC(String dept_desc) {
		DEPT_DESC = dept_desc;
	}

	public void setEXAMDATE(String examdate) {
		EXAMDATE = examdate;
	}

	public void setREPORT_DOCNAME(String report_docname) {
		REPORT_DOCNAME = report_docname;
	}

	public void setEX_DIG_PROMPT(String ex_dig_prompt) {
		EX_DIG_PROMPT = ex_dig_prompt;
	}

	public void setIMAGE_DIS(String image_dis) {
		IMAGE_DIS = image_dis;
	}

	public void setREMARK(String remark) {
		REMARK = remark;
	}

	/** 备注 * */
	public String getREMARK() {
		if (REMARK == null)
			REMARK = "";
		return REMARK;
	}

	/** 检验日期结束 * */
	public String getEXAMDATE() {
		if (EXAMDATE == null)
			EXAMDATE = "";
		return EXAMDATE;
	}

	/** 检验师 * */
	public String getREPORT_DOCNAME() {
		if (REPORT_DOCNAME == null)
			REPORT_DOCNAME = "";
		return REPORT_DOCNAME;
	}


	/** 影像学表现 * */
	public String getIMAGE_DIS() {
		if (IMAGE_DIS == null)
			IMAGE_DIS = "";
		return IMAGE_DIS;
	}

	/** 影像学诊断 * */
	public String getEX_DIG_PROMPT() {
		if (EX_DIG_PROMPT == null)
			EX_DIG_PROMPT = "";
		return EX_DIG_PROMPT;
	}

	@Override
	public String toString() {
		return "CheckupReportBean [PAT_NO=" + PAT_NO + ", EXAMDATE="
				+ EXAMDATE + ", DEPT_DESC=" + DEPT_DESC
				+ ", REPORT_DOCNAME=" + REPORT_DOCNAME
				+ ", EX_DIG_PROMPT=" + EX_DIG_PROMPT + ", IMAGE_DIS=" + IMAGE_DIS
				+ ", REMARK=" + REMARK + "]";
	}

	public static Document buildXml(List<CheckupReportBean> beans) {
		try {
			Document document = XmlUtils.getDocument();
			Element entitys = document.addElement("entitys");
			for (int i = 0; i < beans.size(); i++) {
				CheckupReportBean bean = beans.get(i);
				Element bean_tag = entitys.addElement("entity");

				Element PAT_NO = bean_tag.addElement("patNo");
				Element EXAMDATE = bean_tag.addElement("examDate");
				Element DEPT_DESC = bean_tag.addElement("deptDesc");
				Element REPORT_DOCNAME = bean_tag.addElement("reportDocName");
				Element REMARK = bean_tag.addElement("remark");
				Element IMAGE_DIS = bean_tag.addElement("imageDis");
				Element EX_DIG_PROMPT = bean_tag.addElement("exDigReport");

				PAT_NO.setText(bean.getPAT_NO());
				EXAMDATE.setText(bean.getEXAMDATE());
				DEPT_DESC.setText(bean.getDEPT_DESC());
				REPORT_DOCNAME.setText(bean.getREPORT_DOCNAME());
				REMARK.setText(bean.getREMARK());
				IMAGE_DIS.setText(bean.getIMAGE_DIS());
				EX_DIG_PROMPT.setText(bean.getEX_DIG_PROMPT());
			}
			return document;
		} catch (Exception e) {
			throw new RuntimeException(e); // checked unchecked
		}
	}

}
