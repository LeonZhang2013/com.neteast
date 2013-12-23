package com.neteast.cloud.domain;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.neteast.cloud.utils.XmlUtils;

/**
 * 
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-5-28
 */
public class MyCostBean {

	private String chg_seq;
	private String pat_no;
	private String chg_desc;
	private String chg_cost;
	private String chg_prvt_amt;
	private String billing_date;
	private String dept_desc;
	private String is_adm;
	private String reservation;
	private String reservation1;
	private String reservation2;

	
	
	public String getChg_seq() {
		if (chg_seq == null)
			chg_seq = "";
		return chg_seq;
	}

	public void setChg_seq(String chg_seq) {
		this.chg_seq = chg_seq;
	}

	public String getBilling_date() {
		if (billing_date == null)
			billing_date = "";
		return billing_date;
	}

	public void setBilling_date(String billing_date) {
		this.billing_date = billing_date;
	}

	public String getPat_no() {
		if (pat_no == null)
			pat_no = "";
		return pat_no;
	}

	public void setPat_no(String pat_no) {
		this.pat_no = pat_no;
	}

	public String getChg_desc() {
		if (chg_desc == null)
			chg_desc = "";
		return chg_desc;
	}

	public void setChg_desc(String chg_desc) {
		this.chg_desc = chg_desc;
	}


	public String getChg_cost() {
		if (chg_cost == null)
			chg_cost = "";
		return chg_cost;
	}

	public void setChg_cost(String chg_cost) {
		this.chg_cost = chg_cost;
	}

	public String getChg_prvt_amt() {
		if (chg_prvt_amt == null)
			chg_prvt_amt = "";
		return chg_prvt_amt;
	}

	public void setChg_prvt_amt(String chg_prvt_amt) {
		this.chg_prvt_amt = chg_prvt_amt;
	}

	public String getDept_desc() {
		if (dept_desc == null)
			dept_desc = "";
		return dept_desc;
	}

	public void setDept_desc(String dept_desc) {
		this.dept_desc = dept_desc;
	}

	public String getIs_adm() {
		if (is_adm == null)
			is_adm = "";
		return is_adm;
	}

	public void setIs_adm(String is_adm) {
		this.is_adm = is_adm;
	}

	public String getReservation() {
		if (reservation == null)
			reservation = "";
		return reservation;
	}

	public void setReservation(String reservation) {
		this.reservation = reservation;
	}

	public String getReservation1() {
		if (reservation1 == null)
			reservation1 = "";
		return reservation1;
	}

	public void setReservation1(String reservation1) {
		this.reservation1 = reservation1;
	}

	public String getReservation2() {
		if (reservation2 == null)
			reservation2 = "";
		return reservation2;
	}

	public void setReservation2(String reservation2) {
		this.reservation2 = reservation2;
	}
	
	@Override
	public String toString() {
		return "MyCostBean [chg_seq=" + chg_seq + ", pat_no=" + pat_no + ", chg_desc=" + chg_desc +
					", chg_cost=" + chg_cost + ", chg_prvt_amt=" + chg_prvt_amt
				+ ", dept_desc=" + dept_desc + ", is_adm=" + is_adm + ", reservation=" + reservation + ", reservation1="
				+ reservation1 + ", reservation2=" + reservation2 + "]";
	}

	public static Document buildXml(List<MyCostBean> lis) {
		try {
			Document document = XmlUtils.getDocument();
			Element entitys = document.addElement("entitys");
			for (int i = 0; i < lis.size(); i++) {
				MyCostBean bean = lis.get(i);
				Element bean_tag = entitys.addElement("entity");

				Element chg_seq = bean_tag.addElement("sequence");
				Element chg_desc = bean_tag.addElement("itemName");
				Element chg_cost = bean_tag.addElement("charge");
				Element chg_prvt_amt = bean_tag.addElement("personalCost");
				Element dept_desc = bean_tag.addElement("department");
				Element is_adm = bean_tag.addElement("isAdm");
				Element cost_date = bean_tag.addElement("paymentDate");
				Element reservation = bean_tag.addElement("reservation");
				Element reservation1 = bean_tag.addElement("reservation1");
				Element reservation2 = bean_tag.addElement("reservation2");

				chg_seq.setText(bean.getChg_seq());
				chg_desc.setText(bean.getChg_desc());
				chg_cost.setText(bean.getChg_cost());
				chg_prvt_amt.setText(bean.getChg_prvt_amt());
				dept_desc.setText(bean.getDept_desc());
				is_adm.setText(bean.getIs_adm());
				cost_date.setText(bean.getBilling_date());
				reservation.setText(bean.getReservation());
				reservation1.setText(bean.getReservation1());
				reservation2.setText(bean.getReservation2());
			}
			return document;
		} catch (Exception e) {
			throw new RuntimeException(e); // checked unchecked
		}
	}
}
