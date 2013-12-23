package com.neteast.cloud.domain;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.neteast.cloud.utils.XmlUtils;

/**
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-7-1
 */
public class AnalysisItemBean {

	private String EXAM_NO;
	private String LIS_ITEM_CODE;
	private String SPECIMEN_TYPE;
	private String LIS_ITEM_NAME;
	private String LIS_VALUE;
	private String REFER_SCOPE;
	private String UNIT;
	private String AUDITING_NAME;

	public String getEXAM_NO() {
		if (EXAM_NO == null)
			EXAM_NO = "";
		return EXAM_NO;
	}

	public void setEXAM_NO(String exam_no) {
		EXAM_NO = exam_no;
	}

	public String getSPECIMEN_TYPE() {
		if (SPECIMEN_TYPE == null)
			SPECIMEN_TYPE = "";
		return SPECIMEN_TYPE;
	}

	public void setSPECIMEN_TYPE(String specimen_type) {
		SPECIMEN_TYPE = specimen_type;
	}

	public String getLIS_ITEM_CODE() {
		if (LIS_ITEM_CODE == null)
			LIS_ITEM_CODE = "";
		return LIS_ITEM_CODE;
	}

	public void setLIS_ITEM_CODE(String lis_item_code) {
		LIS_ITEM_CODE = lis_item_code;
	}

	public String getLIS_ITEM_NAME() {
		if (LIS_ITEM_NAME == null)
			LIS_ITEM_NAME = "";
		return LIS_ITEM_NAME;
	}

	public void setLIS_ITEM_NAME(String lis_item_name) {
		LIS_ITEM_NAME = lis_item_name;
	}

	public String getLIS_VALUE() {
		if (LIS_VALUE == null)
			LIS_VALUE = "";
		return LIS_VALUE;
	}

	public void setLIS_VALUE(String lis_value) {
		LIS_VALUE = lis_value;
	}

	public String getREFER_SCOPE() {
		if (REFER_SCOPE == null)
			REFER_SCOPE = "";
		return REFER_SCOPE;
	}

	public void setREFER_SCOPE(String refer_scope) {
		REFER_SCOPE = refer_scope;
	}

	public String getUNIT() {
		if (UNIT == null)
			UNIT = "";
		return UNIT;
	}

	public void setUNIT(String unit) {
		UNIT = unit;
	}

	public String getAUDITING_NAME() {
		if (AUDITING_NAME == null)
			AUDITING_NAME = "";
		return AUDITING_NAME;
	}

	public void setAUDITING_NAME(String auditing_name) {
		AUDITING_NAME = auditing_name;
	}

	@Override
	public String toString() {
		return "AnalysisItemBean [EXAM_NO=" + EXAM_NO + ", LIS_ITEM_CODE=" + LIS_ITEM_CODE + ", SPECIMEN_TYPE=" + SPECIMEN_TYPE
				+ ", LIS_ITEM_NAME=" + LIS_ITEM_NAME + ", LIS_VALUE=" + LIS_VALUE + ", REFER_SCOPE=" + REFER_SCOPE + ", UNIT="
				+ UNIT + ", AUDITING_NAME=" + AUDITING_NAME + "]";
	}
	
	public static Document buildXml(List<AnalysisItemBean> beans) {
		try {
			Document document = XmlUtils.getDocument();
			Element entitys = document.addElement("entitys");
			for (int i = 0; i < beans.size(); i++) {
				AnalysisItemBean bean = beans.get(i);
				Element bean_tag = entitys.addElement("entity");

				Element EXAM_NO = bean_tag.addElement("sampleNo");
				Element SPECIMEN_TYPE = bean_tag.addElement("specimenType");
				Element LIS_ITEM_CODE = bean_tag.addElement("lisItemCode");
				Element LIS_ITEM_NAME = bean_tag.addElement("lisItemName");
				Element LIS_VALUE = bean_tag.addElement("lisValue");
				Element REFER_SCOPE = bean_tag.addElement("referScope");
				Element AUDITING_NAME = bean_tag.addElement("auditingName");
				Element UNIT = bean_tag.addElement("unit");
				
				EXAM_NO.setText(bean.getEXAM_NO());
				SPECIMEN_TYPE.setText(bean.getSPECIMEN_TYPE());
				LIS_ITEM_CODE.setText(bean.getLIS_ITEM_CODE());
				LIS_ITEM_NAME.setText(bean.getLIS_ITEM_NAME());
				LIS_VALUE.setText(bean.getLIS_VALUE());
				REFER_SCOPE.setText(bean.getREFER_SCOPE());
				AUDITING_NAME.setText(bean.getAUDITING_NAME());
				UNIT.setText(bean.getUNIT());
				
			}
			return document;
		} catch (Exception e) {
			throw new RuntimeException(e); // checked unchecked
		}
	}
}
