package com.neteast.cloud.domain;

import org.dom4j.Document;
import org.dom4j.Element;

import com.neteast.cloud.utils.XmlUtils;

public class PatientBean {
	private String PAT_ID;
	private String PAT_NO;
	private String PAT_DOB;
	private String PAT_NAME;
	private String PAT_MARITAL;
	private String PAT_NATION;
	private String PAT_GEO_LOCN;
	private String PAT_ID_TYPE;
	private String PAT_JOB;
	private String PAT_SEX;
	private String PAT_TEL_H;
	private String PAT_BED;
	private String RESERVATION;
	private String RESERVATION1;
	private String RESERVATION2;
	
	public String getPAT_ID() {
		if (PAT_ID == null)
			PAT_ID = "";
		return PAT_ID;
	}
	public String getPAT_NO() {
		if (PAT_NO == null)
			PAT_NO = "";
		return PAT_NO;
	}
	public String getPAT_NAME() {
		if (PAT_NAME == null)
			PAT_NAME = "";
		return PAT_NAME;
	}
	public String getPAT_MARITAL() {
		if (PAT_MARITAL == null)
			PAT_MARITAL = "";
		return PAT_MARITAL;
	}
	public String getPAT_NATION() {
		if (PAT_NATION == null)
			PAT_NATION = "";
		return PAT_NATION;
	}
	public String getPAT_GEO_LOCN() {
		if (PAT_GEO_LOCN == null)
			PAT_GEO_LOCN = "";
		return PAT_GEO_LOCN;
	}
	public String getPAT_ID_TYPE() {
		if (PAT_ID_TYPE == null)
			PAT_ID_TYPE = "";
		return PAT_ID_TYPE;
	}
	public String getPAT_SEX() {
		if (PAT_SEX == null)
			PAT_SEX = "";
		return PAT_SEX;
	}
	public String getPAT_JOB() {
		if (PAT_JOB == null)
			PAT_JOB = "";
		return PAT_JOB;
	}
	public String getPAT_TEL_H() {
		if (PAT_TEL_H == null)
			PAT_TEL_H = "";
		return PAT_TEL_H;
	}
	public String getPAT_BED() {
		if (PAT_BED == null)
			PAT_BED = "";
		return PAT_BED;
	}
	public String getRESERVATION() {
		if (RESERVATION == null)
			RESERVATION = "";
		return RESERVATION;
	}
	public String getRESERVATION1() {
		if (RESERVATION1 == null)
			RESERVATION1 = "";
		return RESERVATION1;
	}
	public String getRESERVATION2() {
		if (RESERVATION2 == null)
			RESERVATION2 = "";
		return RESERVATION2;
	}
	
	public String getPAT_DOB() {
		if (PAT_DOB == null)
			PAT_DOB = "";
		return PAT_DOB;
	}
	public void setPAT_DOB(String pat_birth) {
		PAT_DOB = pat_birth;
	}
	public void setPAT_ID(String pat_id) {
		PAT_ID = pat_id;
	}
	public void setPAT_NO(String pat_no) {
		PAT_NO = pat_no;
	}
	public void setPAT_NAME(String pat_name) {
		PAT_NAME = pat_name;
	}
	public void setPAT_MARITAL(String pat_marital) {
		PAT_MARITAL = pat_marital;
	}
	public void setPAT_NATION(String pat_nation) {
		PAT_NATION = pat_nation;
	}
	public void setPAT_GEO_LOCN(String pat_geo_locn) {
		PAT_GEO_LOCN = pat_geo_locn;
	}
	public void setPAT_ID_TYPE(String pat_id_type) {
		PAT_ID_TYPE = pat_id_type;
	}
	public void setPAT_JOB(String pat_job) {
		PAT_JOB = pat_job;
	}
	public void setPAT_SEX(String pat_sex) {
		PAT_SEX = pat_sex;
	}
	public void setPAT_TEL_H(String pat_tel_h) {
		PAT_TEL_H = pat_tel_h;
	}
	public void setPAT_BED(String pat_bed) {
		PAT_BED = pat_bed;
	}
	public void setRESERVATION(String reservation) {
		RESERVATION = reservation;
	}
	public void setRESERVATION1(String reservation1) {
		RESERVATION1 = reservation1;
	}
	public void setRESERVATION2(String reservation2) {
		RESERVATION2 = reservation2;
	}
	@Override
	public String toString() {
		return "PatientBean [PAT_ID=" + PAT_ID + ", PAT_NO=" + PAT_NO + ", PAT_DOB=" + PAT_DOB + ", PAT_NAME=" + PAT_NAME
				+ ", PAT_MARITAL=" + PAT_MARITAL + ", PAT_NATION=" + PAT_NATION + ", PAT_GEO_LOCN=" + PAT_GEO_LOCN
				+ ", PAT_ID_TYPE=" + PAT_ID_TYPE + ", PAT_JOB=" + PAT_JOB + ", PAT_SEX=" + PAT_SEX + ", PAT_TEL_H=" + PAT_TEL_H
				+ ", PAT_BED=" + PAT_BED + ", RESERVATION=" + RESERVATION + ", RESERVATION1=" + RESERVATION1 + ", RESERVATION2="
				+ RESERVATION2 + "]";
	}
	
	public Document buildXml(PatientBean patient){
		try {
			Document document = XmlUtils.getDocument();
			
			Element patient_tag = document.addElement("entity");
			
			Element id_tag = patient_tag.addElement("id");
			Element name_tag = patient_tag.addElement("name");
			Element birth_tag = patient_tag.addElement("birth");
			Element geolocn_tag = patient_tag.addElement("geoLocn");
			Element idtype_tag = patient_tag.addElement("idType");
			Element marital_tag = patient_tag.addElement("marital");
			Element nation_tag = patient_tag.addElement("nationality");
			Element no_tag = patient_tag.addElement("patNo");
			Element job_tag = patient_tag.addElement("job");
			Element sex_tag = patient_tag.addElement("sex");
			Element tele_tag = patient_tag.addElement("tele");
			Element bedno = patient_tag.addElement("bedNo");
			Element reservation = patient_tag.addElement("reservation");
			Element reservation1 = patient_tag.addElement("reservation1");
			Element reservation2 = patient_tag.addElement("reservation2");
			
			
			id_tag.setText(initData(patient.getPAT_ID()));
			name_tag.setText(initData(patient.getPAT_NAME()));
			birth_tag.setText(initData(patient.getPAT_DOB()));
			geolocn_tag.setText(initData(patient.getPAT_GEO_LOCN()));
			idtype_tag.setText(initData(patient.getPAT_ID_TYPE()));
			marital_tag.setText(initData(patient.getPAT_MARITAL()));
			nation_tag.setText(initData(patient.getPAT_NATION()));
			no_tag.setText(initData(patient.getPAT_NO()));
			job_tag.setText(initData(patient.getPAT_JOB()));
			sex_tag.setText(initData(patient.getPAT_SEX()));
			tele_tag.setText(initData(patient.getPAT_TEL_H()));
			bedno.setText(initData(patient.getPAT_BED()));
			reservation.setText(initData(patient.getRESERVATION()));
			reservation1.setText(initData(patient.getRESERVATION1()));
			reservation2.setText(initData(patient.getRESERVATION2()));
			
			
/*			 Writer fileWriter=new FileWriter("src/exam.xml");
	         XMLWriter xmlWriter=new XMLWriter(fileWriter);    
	         xmlWriter.write(document);   //写入文件中 
	         xmlWriter.close(); */
			return document;
		} catch (Exception e) {
			throw new RuntimeException(e);  //checked  unchecked
		}
	}
	private static String initData(String str){
		if(str == null){
			str = "";
		}
		return str;
	}
}
