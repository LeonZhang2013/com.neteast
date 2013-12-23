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
 * @createtime 2013-6-3 */
public class MyCostItemBean {

	private String sequence;
	private String itemName;
	private String itemQuantity;
	private String itemUnit;
	private String itemCost;
	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemQuantity() {
		return itemQuantity;
	}
	public void setItemQuantity(String itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	public String getItemUnit() {
		return itemUnit;
	}
	public void setItemUnit(String itemUnit) {
		this.itemUnit = itemUnit;
	}
	public String getItemCost() {
		String temp[] = null;
		try{
			temp = itemCost.split("\\.");
			return temp[0]+"."+temp[1].substring(0,2);
		}catch (Exception e) {
			return itemCost;
		}
	}
	public void setItemCost(String itemCost) {
		this.itemCost = itemCost;
	}
	@Override
	public String toString() {
		return "MyCostItemBean [sequence=" + sequence + ", itemName=" + itemName + ", itemQuantity=" + itemQuantity
				+ ", itemUnit=" + itemUnit + ", itemCost=" + itemCost + "]";
	}
	
	public static List<MyCostItemBean> getDatas(String sequence) throws Exception {
		Map<String,String> data = new HashMap<String, String>(); 
		data.put("method", "getMyCostItems");
		data.put("chargeid", sequence);
		InputStream in = NetUtils.requestData(Constant.SERVER_PATH, data);
		if(in==null) return null;
		return XmlTools.parseList(in, MyCostItemBean.class);
	}
}
