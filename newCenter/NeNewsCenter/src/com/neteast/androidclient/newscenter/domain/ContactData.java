
package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.utils.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class ContactData implements Comparable<ContactData>, Cloneable, Parcelable {

    public int id;

    public String label;

    public String content;

    public int modified = Contact.CONTACT_NEW;

    public int mimeTypeId;

    @Override
    public String toString() {
        return "ContactData [id=" + id + ", label=" + label + ", content=" + content
                + ", modified=" + modified + ", mimeTypeId=" + mimeTypeId + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContactData other = (ContactData) obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        return true;
    }

    @Override
    public ContactData clone() {
        try {
            ContactData data = (ContactData) super.clone();
            return data;
        } catch (CloneNotSupportedException e) {
            Log.e("test", e.getMessage(), e);
            throw new RuntimeException("对象克隆失败");
        }
    }

    public static ContactData newInstance(JSONObject jobj) {
        ContactData contactData = new ContactData();
        contactData.id = JsonHelper.readInt(jobj, "cid");
        contactData.label = JsonHelper.readString(jobj, "label");
        contactData.content = JsonHelper.readString(jobj, "content");
        contactData.mimeTypeId = JsonHelper.readInt(jobj, "mimetypeId");
        return contactData;
    }

    public static ArrayList<ContactData> newIntances(JSONObject jobj, String keyword) {
        ArrayList<ContactData> datas = new ArrayList<ContactData>();
        if (!jobj.isNull(keyword)) {
            JSONArray ja;
            try {
                ja = jobj.getJSONArray(keyword);
                for (int i = 0, size = ja.length(); i < size; i++) {
                    datas.add(newInstance(ja.getJSONObject(i)));
                }
            } catch (JSONException e) {
            }
        }
        return datas;
    }

    public int compareTo(ContactData another) {
        return id - another.id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(label);
        dest.writeString(content);
        dest.writeInt(modified);
        dest.writeInt(mimeTypeId);
    }
    
    public static final Parcelable.Creator<ContactData> CREATOR=new Creator<ContactData>() {
        @Override
        public ContactData[] newArray(int size) {
            return new ContactData[size];
        }
        
        @Override
        public ContactData createFromParcel(Parcel source) {
            ContactData data=new ContactData();
            data.id=source.readInt();
            data.label=source.readString();
            data.content=source.readString();
            data.modified=source.readInt();
            data.mimeTypeId=source.readInt();
            return data;
        }
    };
}
