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
public class MyCostItemBean {

	private String chg_seq;
	private String item_name;
	private String item_quantity;
	private String item_unit;
	private String item_cost;
	private String reservation;
	private String reservation1;
	private String reservation2;
	
	@Override
	public String toString() {
		return "MyCostItemBean [chg_seq=" + chg_seq + ", item_name=" + item_name + ", item_quantity=" + item_quantity
				+ ", item_unit=" + item_unit + ", item_cost=" + item_cost + ", reservation=" + reservation + ", reservation1="
				+ reservation1 + ", reservation2=" + reservation2 + "]";
	}
	
	public String getChg_seq() {
		if (chg_seq == null)
			chg_seq = "";
		return chg_seq;
	}

	public void setChg_seq(String chg_seq) {
		this.chg_seq = chg_seq;
	}

	public String getItem_name() {
		if (item_name == null)
			item_name = "";
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getItem_quantity() {
		if (item_quantity == null)
			item_quantity = "";
		return item_quantity;
	}

	public void setItem_quantity(String item_quantity) {
		this.item_quantity = item_quantity;
	}

	public String getItem_unit() {
		if (item_unit == null)
			item_unit = "";
		return item_unit;
	}

	public void setItem_unit(String item_unit) {
		this.item_unit = item_unit;
	}

	public String getItem_cost() {
		if (item_cost == null)
			item_cost = "";
		return item_cost;
	}

	public void setItem_cost(String item_cost) {
		this.item_cost = item_cost;
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


	public static Document buildXml(List<MyCostItemBean> lis) {
		try {
			Document document = XmlUtils.getDocument();
			Element entitys = document.addElement("entitys");
			for (int i = 0; i < lis.size(); i++) {
				MyCostItemBean bean = lis.get(i);
				Element bean_tag = entitys.addElement("entity");

				Element chg_seq = bean_tag.addElement("sequence");
				Element item_name = bean_tag.addElement("itemName");
				Element quantity = bean_tag.addElement("itemQuantity");
				Element item_unit = bean_tag.addElement("itemUnit");
				Element item_cost = bean_tag.addElement("itemCost");
				Element reservation = bean_tag.addElement("reservation");
				Element reservation1 = bean_tag.addElement("reservation1");
				Element reservation2 = bean_tag.addElement("reservation2");

				chg_seq.setText(bean.getChg_seq());
				item_name.setText(bean.getItem_name());
				quantity.setText(bean.getItem_quantity());
				item_unit.setText(bean.getItem_unit());
				item_cost.setText(bean.getItem_cost());
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
