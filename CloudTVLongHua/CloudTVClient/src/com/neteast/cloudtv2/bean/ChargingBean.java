package com.neteast.cloudtv2.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的费用
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-3-15
 */
public class ChargingBean extends ArrayList{

	private String name;
	private String price;
	private String unit;
	private String affordRate;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getAffordRate() {
		return affordRate;
	}
	public void setAffordRate(String affordRate) {
		this.affordRate = affordRate;
	}
	
	public ChargingBean(String name, String price, String unit, String affordRate) {
		super();
		this.name = name;
		this.price = price;
		this.unit = unit;
		this.affordRate = affordRate;
		add(name);
		add(price);
		add(unit);
		add(affordRate);
	}
	public ChargingBean() {
		super();
	}
	
	
	public List<String> getData2(int index){
		List<String> l = new ArrayList<String>();
		switch (index) {
		case 0:
			l.add("姓名：田二妞");
			l.add("类型：门诊");
			l.add("床号：123");
			l.add("性别：女");
			l.add("诊断：艾滋");
			l.add("备注：");
			l.add("接受事件：2011年3月13日");
			break;

		case 1:
			l.add("姓名：堕入阿迷");
			l.add("类型：住院诊1");
			l.add("床号：321");
			l.add("性别：男1");
			break;
			
		default:
			l.add("姓名：郭小小");
			l.add("类型：住院诊");
			l.add("床号：321");
			l.add("性别：男");
			l.add("诊断：感冒");
			l.add("备注：H9N12");
			l.add("接受事件：2021年13月13日");
			l.add("检验师：胡志明");
			break;
		}
		return l;
	}
	
	
	public List<List<String>> getData(int index){
		List<List<String>> listData = new ArrayList<List<String>>();
	
		switch (index) {
		case 0:
			listData.add(new ChargingBean("内部职工免挂号费", "3", "", ""));
			listData.add(new ChargingBean("门诊副高级职称", "8", "", ""));
			listData.add(new ChargingBean("门诊搞基职称", "5", "", ""));
			listData.add(new ChargingBean("普通号", "2", "", ""));
			listData.add(new ChargingBean("社区号", "13", "", ""));
			listData.add(new ChargingBean("妇科", "1", "", ""));
			listData.add(new ChargingBean("男科", "2", "", ""));
			listData.add(new ChargingBean("急诊", "11", "", ""));
			break;

		case 1:
			listData.add(new ChargingBean("内部职工免挂号费", "3", "", ""));
			listData.add(new ChargingBean("门诊副高级职称", "8", "", ""));
			listData.add(new ChargingBean("门诊搞基职称", "5", "", ""));
			listData.add(new ChargingBean("普通号", "2", "", ""));
			listData.add(new ChargingBean("社区号", "13", "", ""));
			listData.add(new ChargingBean("妇科", "1", "", ""));
			listData.add(new ChargingBean("男科", "2", "", ""));
			listData.add(new ChargingBean("急诊", "11", "", ""));
			listData.add(new ChargingBean("内部职工免挂号费", "3", "", ""));
			listData.add(new ChargingBean("门诊副高级职称", "8", "", ""));
			listData.add(new ChargingBean("门诊搞基职称", "5", "", ""));
			listData.add(new ChargingBean("普通号", "2", "", ""));
			listData.add(new ChargingBean("社区号", "13", "", ""));
			listData.add(new ChargingBean("妇科", "1", "", ""));
			listData.add(new ChargingBean("男科", "2", "", ""));
			listData.add(new ChargingBean("急诊", "11", "", ""));
			break;
		case 2:
			listData.add(new ChargingBean("内部职工免挂号费", "3", "", ""));
			listData.add(new ChargingBean("门诊副高级职称", "8", "", ""));
			listData.add(new ChargingBean("门诊搞基职称", "5", "", ""));
			listData.add(new ChargingBean("普通号", "2", "", ""));
			break;
		}
		
		return listData;
	}
}
