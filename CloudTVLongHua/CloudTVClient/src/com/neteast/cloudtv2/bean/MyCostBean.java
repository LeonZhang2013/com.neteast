package com.neteast.cloudtv2.bean;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Xml;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.tools.NetUtils;
import com.neteast.cloudtv2.tools.XmlTools;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-5-28
 */
public class MyCostBean {

	private String sequence;
	private String itemName;
	private String charge;
	private String personalCost;
	private String paymentDate;
	private String department;
	private String isAdm;

	/** 序号 **/
	public String getSequence() {
		return sequence;
	}
	/** 项目名称 **/
	public String getItemName() {
		return itemName;
	}
	
	/** 费用 **/
	public String getCharge() {
		String temp[] = null;
		try{
			temp = charge.split("\\.");
			return temp[0]+"."+temp[1].substring(0,2);
		}catch (Exception e) {
			return charge;
		}
	}
	/** 个人支付 **/
	public String getPersonalCost() {
		return personalCost;
	}
	/** 扣费时间 **/
	public String getPaymentDate() {
		if(paymentDate!=null&&paymentDate.length()>10){
			paymentDate = paymentDate.substring(0,10);
		}
		return paymentDate;
	}
	/** 执行科室 **/
	public String getDepartment() {
		return department;
	}
	
	public String getIsAdm() {
		return isAdm;
	}
	public void setIsAdm(String isAdm) {
		this.isAdm = isAdm;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public void setCharge(String charge) {
		this.charge = charge;
	}
	public void setPersonalCost(String personalCost) {
		this.personalCost = personalCost;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getChargeType() {
		if("true".equals(isAdm)){
			return "门诊";
		}else{
			return "住院";
		}
	}
	@Override
	public String toString() {
		return "MyCostBean [sequence=" + sequence + ", itemName=" + itemName + ", charge=" + charge + ", personalCost="
				+ personalCost + ", paymentDate=" + paymentDate + ", department=" + department + ", isAdm=" + isAdm + "]";
	}
	
	public static List<MyCostBean> getData(String patNo,String costDate,String isadm,int size) throws Exception{
		Map<String,String> data = new HashMap<String, String>(); 
		data.put("method", "getMyCost");
		data.put("patno", patNo);
		data.put("costdate", costDate);
		data.put("isadm", isadm);
		data.put("size", String.valueOf(size));
		InputStream in = NetUtils.requestData(Constant.SERVER_PATH, data);
		if(in==null) return null;
		return XmlTools.parseList(in, MyCostBean.class);
	}

}
