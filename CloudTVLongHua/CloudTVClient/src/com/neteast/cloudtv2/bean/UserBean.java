package com.neteast.cloudtv2.bean;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.tools.MyLog;
import com.neteast.cloudtv2.tools.NetUtils;
import com.neteast.cloudtv2.tools.XmlTools;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-3-13 */
public class UserBean {

	private String name;
	private String password;
	private String username;
	private String patNo;
	private String birth;
	private String geoLocn;
	private String id;
	private String idType;
	private String marital;
	private String nationality;
	private String job;
	private String sex;
	private String tele;
	private String age;
	private String bedNo;
	
	public String getBedNo() {
		return bedNo;
	}

	public void setBedNo(String bedNo) {
		this.bedNo = bedNo;
	}

	public UserBean(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public UserBean() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPatNo() {
		return patNo;
	}

	public void setPatNo(String patNo) {
		this.patNo = patNo;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getGeoLocn() {
		return geoLocn;
	}

	public void setGeoLocn(String geoLocn) {
		this.geoLocn = geoLocn;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getMarital() {
		return marital;
	}

	public void setMarital(String marital) {
		this.marital = marital;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTele() {
		return tele;
	}

	public void setTele(String tele) {
		this.tele = tele;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public String getAge() {
		int age = 0;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = format.parse(birth);
			Date date2 = new Date();
			if (date1.getMonth() > date2.getMonth() || date1.getDay() > date2.getDay()) {
				age = date2.getYear() - date1.getYear() + 1;
			} else {
				age = date2.getYear() - date1.getYear();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return String.valueOf(age);
	}

	@Override
	public String toString() {
		return "UserBean [name=" + name + ", password=" + password + ", username=" + username + ", patNo=" + patNo + ", birth="
				+ birth + ", geoLocn=" + geoLocn + ", id=" + id + ", idType=" + idType + ", marital=" + marital
				+ ", nationality=" + nationality + ", job=" + job + ", sex=" + sex + ", tele=" + tele + ", age=" + age
				+ ", isLogin=" + isLogin + "bedNo"+bedNo+ "]";
	}

	/** 验证是否登录成功
	 * 
	 * @return true 成功 反正 false */
	public boolean isLogin() {
		return isLogin;
	}
	
	boolean isLogin = false;
	public void logout() {
		isLogin = false;
	}

	public boolean verifyPassword(UserBean bean) throws Exception {
		Map<String,String> data = new HashMap<String, String>();
		data.put("method", "login");
		data.put("username", bean.getUsername());
		data.put("password", bean.getPassword());
		InputStream in = NetUtils.requestData(Constant.SERVER_PATH, data);
		Constant.USER_BEAN = XmlTools.parseSingle(in, UserBean.class);
		if(Constant.USER_BEAN.getPatNo()!=null&&Constant.USER_BEAN.getPatNo().length()>0){
			Constant.USER_BEAN.setUsername(username);
			Constant.USER_BEAN.setPassword(password);
			Constant.USER_BEAN.setLogin(true);
		}
		return Constant.USER_BEAN.isLogin();
	}
}
