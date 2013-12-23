package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.utils.JsonHelper;
import com.neteast.androidclient.newscenter.utils.LogUtil;
import com.neteast.androidclient.newscenter.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Contact implements Cloneable,Parcelable{
    
    public static final int CONTACT_NOCHANGE=0;
    public static final int CONTACT_UPDATE=1;
    public static final int CONTACT_NEW=2;
    public static final int CONTACT_DELETE=3;
    
    
	public int contactId;
	public String displayName;
	public String fphone;
	public String femail;
	public String organization;
	public String job;
	public String photo;
	public String account;
	public String identityCard;
	public boolean hasPhoto=false;
	public boolean isReadOnly=false;
	
	public boolean isChecked;
	
	public ArrayList<ContactData> phones=new ArrayList<ContactData>();
	public ArrayList<ContactData> emails=new ArrayList<ContactData>();
	public ArrayList<ContactData> ims=new ArrayList<ContactData>();
	public ArrayList<ContactData> locations=new ArrayList<ContactData>();
	public ArrayList<String> groups=new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	@Override
	public Contact clone(){
		try {
			Contact contact=(Contact) super.clone();
			contact.phones=(ArrayList<ContactData>) phones.clone();
			contact.emails=(ArrayList<ContactData>) emails.clone();
			contact.ims=(ArrayList<ContactData>) ims.clone();
			contact.locations=(ArrayList<ContactData>) locations.clone();
			return contact;
		} catch (CloneNotSupportedException e) {
			Log.e("test", e.getMessage(),e);
			throw new RuntimeException("对象克隆失败");
		}
	}
	

	

    @Override
    public String toString() {
        return "Contact [contactId=" + contactId + ", displayName=" + displayName + ", fphone="
                + fphone + ", femail=" + femail + ", organization=" + organization + ", job=" + job
                + ", photo=" + photo + ", account=" + account + ", identityCard=" + identityCard
                + ", hasPhoto=" + hasPhoto + ", isReadOnly=" + isReadOnly + ", phones=" + phones
                + ", emails=" + emails + ", ims=" + ims + ", locations=" + locations + "]";
    }
    
	public static Contact newInstance(JSONObject jobj) {
	    Contact contact=new Contact();
	    contact.account=JsonHelper.readString(jobj, "username");
	    contact.identityCard=JsonHelper.readString(jobj, "identityCard");
	    contact.contactId=JsonHelper.readInt(jobj, "contactId");
	    contact.displayName=JsonHelper.readString(jobj, "displayName");
	    contact.photo=JsonHelper.readString(jobj, "photo");
	    contact.organization=JsonHelper.readString(jobj, "organization");
	    contact.job=JsonHelper.readString(jobj, "job");
	    contact.isReadOnly=JsonHelper.readBoolean(jobj, "isReadOnly");
	    contact.hasPhoto=JsonHelper.readBoolean(jobj, "hasPhoto");
	    contact.fphone=JsonHelper.readString(jobj, "fphone");
	    contact.femail=JsonHelper.readString(jobj, "femail");
	    contact.phones.addAll(ContactData.newIntances(jobj, "phones"));
	    contact.emails.addAll(ContactData.newIntances(jobj, "emails"));
	    contact.ims.addAll(ContactData.newIntances(jobj, "ims"));
	    contact.locations.addAll(ContactData.newIntances(jobj, "locations"));
	    
	    try {
            if (!jobj.isNull("groups")) {
                JSONArray ja = jobj.getJSONArray("groups");
                for(int i=0,size=ja.length();i<size;i++){
                    JSONObject obj = ja.getJSONObject(i);
                    contact.groups.add(JsonHelper.readString(obj, "groupName"));
                }
            }
        } catch (JSONException e) {
            LogUtil.printException(e);
        }
	    return contact;
    }
	
	public static ArrayList<Contact> newInstances(String json) {
	    ArrayList<Contact > contacts=new ArrayList<Contact>();
	    if (null==json || "null".equalsIgnoreCase(json)) {
            return contacts;
        }
	    try {
            JSONArray ja=new JSONArray(json);
            for(int i=0,size=ja.length();i<size;i++){
                contacts.add(newInstance(ja.getJSONObject(i)));
            }
        } catch (JSONException e) {
            LogUtil.e("Contact(130)");
            LogUtil.printException(e);
        }
	    return contacts;
    }
	
	public static String uploadPhoto(Context context,File file) throws IOException{
        String path=ConfigManager.URL_USERCENTER+"/Usercenter/photofile";
        FileBody fileBody=new FileBody(file,"image/*");
        MultipartEntity reqEntity=new MultipartEntity();
        reqEntity.addPart("file", fileBody);
        return Utils.doPost(path, reqEntity);
    }
	
	/**
     * 判断联系人是否发生改变 若发生改变，则将改变的部分组拼成json串
     * @param cold
     * @param cNew
     * @return
     */
    public static JSONObject isContactChanged(Contact cold,Contact cNew){
        try {
            boolean isChanged=false;
            JSONObject object=new JSONObject();
            object.put("contactId", cold.contactId);

            if (!cold.displayName.equals(cNew.displayName)) {
                isChanged = true;
                object.put("displayName", cNew.displayName);
            }
            if (!cold.fphone.equals(cNew.fphone)) {
                isChanged = true;
                object.put("fphone", cNew.fphone);
            }
            if (!cold.femail.equals(cNew.femail)) {
                isChanged = true;
                object.put("femail", cNew.femail);
            }
            if (!cold.photo.equals(cNew.photo)) {
                isChanged = true;
                object.put("photo", cNew.photo);
            }
            if (cold.hasPhoto!=cNew.hasPhoto) {
                isChanged = true;
                object.put("hasPhoto", cNew.hasPhoto?1:0);
            }
            if (!cold.organization.equals(cNew.organization)) {
                isChanged = true;
                object.put("organization", cNew.organization);
            }
            if (!cold.job.equals(cNew.job)) {
                isChanged = true;
                object.put("job", cNew.job);
            }
            
            JSONArray array = compareContactData(cold.phones, cNew.phones);
            if (array.length()>0) {
                isChanged=true;
                object.put("phones", array);
            }
            array = compareContactData(cold.emails, cNew.emails);
            if (array.length()>0) {
                isChanged=true;
                object.put("emails", array);
            }
            array = compareContactData(cold.ims, cNew.ims);
            if (array.length()>0) {
                isChanged=true;
                object.put("ims", array);
            }
            array = compareContactData(cold.locations, cNew.locations);
            if (array.length()>0) {
                isChanged=true;
                object.put("locations", array);
            }
            if (isChanged) {
                return object;
            }
        } catch (Exception e) {
            Log.e("test", e.getMessage(),e);
        }
        return null;
    }
    /**
     * 比较ContactData是否发生改变，若是，则将改变的数据组拼成json
     * @param oldPhones
     * @param newPhones
     * @return
     * @throws JSONException
     */
    private static JSONArray compareContactData(ArrayList<ContactData> oldPhones, 
            ArrayList<ContactData> newPhones)throws JSONException {
        JSONArray array=new JSONArray();
        JSONObject jsonObject;
        int countI=oldPhones.size();
        int countJ=newPhones.size();
        int i=0;
        int j=0;
        for (;i<countI&&j<countJ; i++,j++) {
            ContactData oldData = oldPhones.get(i);
            ContactData newData = newPhones.get(j);
            if (!oldData.equals(newData)) {
                jsonObject=new JSONObject();
                jsonObject.put("cid", oldData.id);
                jsonObject.put("label", newData.label);
                jsonObject.put("content", newData.content);
                jsonObject.put("modified", CONTACT_UPDATE);
                jsonObject.put("mimetypeId", newData.mimeTypeId);
                array.put(jsonObject);
            }
        }
        if (countI<countJ) {
            for (;j<countJ;j++){
                jsonObject=new JSONObject();
                ContactData data = newPhones.get(j);
                jsonObject.put("label", data.label);
                jsonObject.put("content", data.content);
                jsonObject.put("modified", CONTACT_NEW);
                jsonObject.put("mimetypeId", data.mimeTypeId);
                array.put(jsonObject);
            }
        }else if (countI>countJ) {
            for (;i<countI;i++){
                jsonObject=new JSONObject();
                ContactData data = oldPhones.get(i);
                jsonObject.put("cid", data.id);
                jsonObject.put("modified", CONTACT_DELETE);
                jsonObject.put("mimeTypeId", data.mimeTypeId);
                array.put(jsonObject);
            }
        }
        return array;
    }
    /**
     * 将联系人的改变保存到服务器
     * @param context
     * @param jobj
     * @return
     * @throws Exception
     */
    public static Contact saveContact(Context context,JSONObject jobj) throws Exception {
        int userId=CloudAccount.getInstance(context).getUserId();
        String token=CloudAccount.getInstance(context).getToken();
        String verify=Utils.md5(userId+""+jobj.toString()+ConfigManager.KEY);
        
        ArrayList<NameValuePair> valuePairs=new ArrayList<NameValuePair>();
        
        BasicNameValuePair valuePair=new BasicNameValuePair("appcode",ConfigManager.APPCODE);
        valuePairs.add(valuePair);
        valuePair=new BasicNameValuePair("verify",verify);
        valuePairs.add(valuePair);
        valuePair=new BasicNameValuePair("datatype","j");
        valuePairs.add(valuePair);
        valuePair=new BasicNameValuePair("token",token);
        valuePairs.add(valuePair);
        valuePair=new BasicNameValuePair("userid",userId+"");
        valuePairs.add(valuePair);
        valuePair=new BasicNameValuePair("json_string",jobj.toString());
        valuePairs.add(valuePair);
        
        UrlEncodedFormEntity entity=new UrlEncodedFormEntity(valuePairs, "UTF-8");
        String path=ConfigManager.URL_USERCENTER+"/Usercenter/useredit";
        String result = Utils.doPost(path, entity);
       return  newInstance(new JSONObject(result));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(contactId);
        dest.writeString(displayName);
        dest.writeString(fphone);
        dest.writeString(femail);
        dest.writeString(organization);
        dest.writeString(job);
        dest.writeString(photo);
        dest.writeString(account);
        dest.writeString(identityCard);
        dest.writeInt(hasPhoto?1:0);
        dest.writeInt(isReadOnly?1:0);
        dest.writeParcelableArray(phones.toArray(new ContactData[]{}), flags);
        dest.writeParcelableArray(emails.toArray(new ContactData[]{}), flags);
        dest.writeParcelableArray(ims.toArray(new ContactData[]{}), flags);
        dest.writeParcelableArray(locations.toArray(new ContactData[]{}), flags);
        dest.writeList(groups);
    }
    
    public static final Parcelable.Creator<Contact> CREATOR=new Creator<Contact>() {
        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
        
        @Override
        public Contact createFromParcel(Parcel source) {
            Contact contact=new Contact();
            contact.contactId=source.readInt();
            contact.displayName=source.readString();
            contact.fphone=source.readString();
            contact.femail=source.readString();
            contact.organization=source.readString();
            contact.job=source.readString();
            contact.photo=source.readString();
            contact.account=source.readString();
            contact.identityCard=source.readString();
            contact.hasPhoto=source.readInt()==1;
            contact.isReadOnly=source.readInt()==1;
            ContactData[] datas=(ContactData[]) source.readParcelableArray(ContactData.class.getClassLoader());
            contact.phones.addAll(Arrays.asList(datas));
            datas=(ContactData[]) source.readParcelableArray(ContactData.class.getClassLoader());
            contact.emails.addAll(Arrays.asList(datas));
            datas=(ContactData[]) source.readParcelableArray(ContactData.class.getClassLoader());
            contact.ims.addAll(Arrays.asList(datas));
            datas=(ContactData[]) source.readParcelableArray(ContactData.class.getClassLoader());
            contact.locations.addAll(Arrays.asList(datas));
            source.readList(contact.groups, String.class.getClassLoader());
            return contact;
        }
    };
}
