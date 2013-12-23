package com.neteast.cloud.domain;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.neteast.cloud.utils.XmlUtils;

/**
 * 检验报告
 * 
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-5-31
 */
public class AnalysisBean {

	private String EXAM_NO;
	private String ITEM_NAME;
	private String LIS_TM_NO;
	private String SAMPLE_NO;
	private String EX_PART;
	private String PETITIONER_DOCNAME;
	private String DEPT_DESC;
	private String CLINICAL_DIG;
	private String EXAM_DATE;
	private String REMARK;

	public String getEXAM_NO() {
		if (EXAM_NO == null)
			EXAM_NO = "";
		return EXAM_NO;
	}

	public void setEXAM_NO(String exam_no) {
		EXAM_NO = exam_no;
	}

	public String getLIS_TM_NO() {
		if (LIS_TM_NO == null)
			LIS_TM_NO = "";
		return LIS_TM_NO;
	}

	public void setLIS_TM_NO(String lis_tm_no) {
		LIS_TM_NO = lis_tm_no;
	}

	public String getITEM_NAME() {
		if (ITEM_NAME == null)
			ITEM_NAME = "";
		return ITEM_NAME;
	}

	public void setITEM_NAME(String item_name) {
		ITEM_NAME = item_name;
	}

	public String getCLINICAL_DIG() {
		if (CLINICAL_DIG == null)
			CLINICAL_DIG = "";
		return CLINICAL_DIG;
	}

	public void setCLINICAL_DIG(String clinical_dig) {
		CLINICAL_DIG = clinical_dig;
	}

	public String getSAMPLE_NO() {
		if (SAMPLE_NO == null)
			SAMPLE_NO = "";
		return SAMPLE_NO;
	}

	public void setSAMPLE_NO(String sample_no) {
		SAMPLE_NO = sample_no;
	}

	public String getEX_PART() {
		if (EX_PART == null)
			EX_PART = "";
		return EX_PART;
	}

	public void setEX_PART(String ex_part) {
		EX_PART = ex_part;
	}

	public String getPETITIONER_DOCNAME() {
		if (PETITIONER_DOCNAME == null)
			PETITIONER_DOCNAME = "";
		return PETITIONER_DOCNAME;
	}

	public void setPETITIONER_DOCNAME(String petitioner_docname) {
		PETITIONER_DOCNAME = petitioner_docname;
	}

	public String getDEPT_DESC() {
		if (DEPT_DESC == null)
			DEPT_DESC = "";
		return DEPT_DESC;
	}

	public void setDEPT_DESC(String dept_desc) {
		DEPT_DESC = dept_desc;
	}

	public String getREMARK() {
		if (REMARK == null)
			REMARK = "";
		return REMARK;
	}

	public void setREMARK(String remark) {
		REMARK = remark;
	}

	public String getEXAM_DATE() {
		if (EXAM_DATE == null)
			EXAM_DATE = "";
		return EXAM_DATE;
	}

	public void setEXAM_DATE(String examdate) {
		EXAM_DATE = examdate;
	}

	@Override
	public String toString() {
		return "AnalysisBean [EXAM_NO=" + EXAM_NO + ", ITEM_NAME=" + ITEM_NAME + ", LIS_TM_NO=" + LIS_TM_NO + ", SAMPLE_NO="
				+ SAMPLE_NO + ", EX_PART=" + EX_PART + ", PETITIONER_DOCNAME=" + PETITIONER_DOCNAME + ", DEPT_DESC=" + DEPT_DESC
				+ ", CLINICAL_DIG=" + CLINICAL_DIG + ", EXAM_DATE=" + EXAM_DATE + ", REMARK=" + REMARK + "]";
	}
	
	public static Document buildXml(List<AnalysisBean> beans) {
		try {
			Document document = XmlUtils.getDocument();
			Element entitys = document.addElement("entitys");
			for (int i = 0; i < beans.size(); i++) {
				AnalysisBean bean = beans.get(i);
				Element bean_tag = entitys.addElement("entity");

				Element examNo = bean_tag.addElement("examNo");
				Element lisTmNo = bean_tag.addElement("lisTmNo");
				Element sampeNo = bean_tag.addElement("sampeNo");
				Element exPart = bean_tag.addElement("exPart");
				Element petitionDoc = bean_tag.addElement("petitionDoc");
				Element examDate = bean_tag.addElement("examDate");
				Element deptDesc = bean_tag.addElement("deptDesc");
				Element clinicalDig = bean_tag.addElement("clinicalDig");
				Element itemName = bean_tag.addElement("itemName");
				Element remark = bean_tag.addElement("remark");

				examNo.setText(bean.getEXAM_NO());
				lisTmNo.setText(bean.getLIS_TM_NO());
				sampeNo.setText(bean.getSAMPLE_NO());
				exPart.setText(bean.getEX_PART());
				petitionDoc.setText(bean.getPETITIONER_DOCNAME());
				examDate.setText(bean.getEXAM_DATE());
				deptDesc.setText(bean.getDEPT_DESC());
				clinicalDig.setText(bean.getCLINICAL_DIG());
				itemName.setText(bean.getITEM_NAME());
				remark.setText(bean.getREMARK());
			}
			return document;
		} catch (Exception e) {
			throw new RuntimeException(e); // checked unchecked
		}
	}

}
