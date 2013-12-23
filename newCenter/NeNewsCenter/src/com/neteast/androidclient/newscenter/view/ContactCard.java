package com.neteast.androidclient.newscenter.view;

import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.domain.ContactData;
import com.neteast.androidclient.newscenter.utils.Utils;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactCard extends LinearLayout implements View.OnClickListener {
    
    static final int TYPE_CLASS_TEXT=1;
    static final int TYPE_CLASS_PHONE=3;
    static final int TYPE_TEXT_VARIATION_EMAIL_ADDRESS=32;
    
    static final int MIME_TYPE_PHONE=1;
    static final int MIME_TYPE_EMAIL=2;
    static final int MIME_TYPE_IM=3;
    static final int MIME_TYPE_LOCATION=4;
    
	private LayoutInflater mInflater;
    
	private int mMimeType;
    private int mContentInputType;
    private String mContentHint;
    private CharSequence[] mLabels;
	private int mLabelIndex;
    private PopupWindow mSelectLabelDialog;
    private PopupWindow mCustomLabelDialog;
    private TextView mLabelView;
	
	public ContactCard(Context context,AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ContainerType,
                0, 0
        );
        try {
            mMimeType=a.getInteger(R.styleable.ContainerType_mimeType, MIME_TYPE_PHONE);
            mContentInputType=a.getInteger(R.styleable.ContainerType_contentInputType, TYPE_CLASS_TEXT);
            mLabels = a.getTextArray(R.styleable.ContainerType_labels);
            mContentHint = a.getString(R.styleable.ContainerType_contentHint);
        } finally {
            a.recycle();
        }
        mInflater = LayoutInflater.from(context);
	}
	
	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_label:
                mLabelView=(TextView) v;
                showSelectLabelDialog();
                break;
            case R.id.item_delitem:
                deleteRow(v);
                break;
            case R.id.btn_cancel:
                mCustomLabelDialog.dismiss();
                break;
            default:
                break;
        }
    }

    public void addEmptyRow() {
        addRow(null,true);
    }
	
	public void addRow(ContactData data) {
	    addRow(data, false);
    }
	
	private void addRow(ContactData data,boolean editable) {
	    View row=newRow(editable);
	    fillRow(row,data);
    }

    private View newRow(boolean editable) {
        View row = mInflater.inflate(R.layout.item_usercenter, this, false);
        
        TextView label = (TextView) row.findViewById(R.id.item_label);
        EditText content=(EditText) row.findViewById(R.id.item_content);
        View delete=row.findViewById(R.id.item_delitem);
        row.setTag(R.id.item_label, label);
        row.setTag(R.id.item_content,content);
        row.setTag(R.id.item_delitem,delete);
        
        setEachRowMode(row, editable);
        
        if (mLabelIndex==(mLabels.length-1)) {
            mLabelIndex=0;
        }
        label.setText(mLabels[mLabelIndex++]);
        label.setOnClickListener(this);
        //内容
        content.setInputType(mContentInputType);
        content.setHint(mContentHint);
        //删除按钮
        delete.setOnClickListener(this);
        
        LayoutParams params=new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin=13;
        
        addView(row, params);
        return row;
    }
    
    private void fillRow(View row, ContactData data) {
        if (data==null) {
            return;
        }
        TextView label=(TextView) row.getTag(R.id.item_label);
        label.setText(data.label);
        EditText content=(EditText) row.getTag(R.id.item_content);
        content.setText(data.content);
    }
    
    public void showEditMode() {
        for(int i=0,size=getChildCount();i<size;i++){
            View row = getChildAt(i);
            setEachRowMode(row, true);
        }
    }
    /**
     * 显示浏览模式</BR>
     * 若在编辑模式下增加了空白行，会自动去除
     */
    public void showDisplayMode() {
        boolean hasEmptyRow=false;
        ArrayList<ContactData> cache=new ArrayList<ContactData>();
        
        for(int i=0,size=getChildCount();i<size;i++){
            View row = getChildAt(i);
            setEachRowMode(row, false);
            ContactData data = getContactInfoFromRow(row);
            if (data==null) {
                hasEmptyRow=true;
            }else {
                cache.add(data);
            }
        }
        
        if (hasEmptyRow) {
            removeAllViews();
            for (ContactData data : cache) {
                addRow(data, false);
            }
        }else {
            cache.clear();
        }
    }
    
    public ArrayList<ContactData> getContactInfos() {
        ArrayList<ContactData> values=new ArrayList<ContactData>();
        for(int i=0,size=getChildCount();i<size;i++){
            View row = getChildAt(i);
            ContactData data = getContactInfoFromRow(row);
            if (data!=null) {
                values.add(data);
            }
        }
        return values;
    }
    
    private ContactData getContactInfoFromRow(View row){
        TextView label=(TextView) row.getTag(R.id.item_label);
        EditText content=(EditText) row.getTag(R.id.item_content);
        String contentValue = content.getText().toString().trim();
        if (TextUtils.isEmpty(contentValue)) {
            return null;
        }else {
            ContactData data=new ContactData();
            data.label=label.getText().toString();
            data.content=contentValue;
            data.mimeTypeId=mMimeType;
            return data;
        }
    }
    
    /**
     * 根据[编辑/展示]模式，决定每一行的显示模式
     * @param row
     * @param editable
     */
    private void setEachRowMode(View row,boolean editable){
        TextView label=(TextView) row.getTag(R.id.item_label);
        label.setEnabled(editable);
        if (editable) {
			label.setBackgroundResource(R.drawable.usercenter_label_edit);
			label.setPadding(Utils.dp2px(getContext(), 5), 0, 0, 0);
		}else {
			label.setBackgroundResource(R.drawable.transparent);
			label.setPadding(0, 0, 0, 0);
		}
        ((View)row.getTag(R.id.item_content)).setEnabled(editable);            
        ((View)row.getTag(R.id.item_delitem)).setVisibility(editable?View.VISIBLE:View.GONE);
    }
    
    /**
     * 显示选择标签的窗口
     */
    private void showSelectLabelDialog() {
        if (mSelectLabelDialog==null) {
            View contentView=mInflater.inflate(R.layout.dialog_list_view, null);
            
            ListView labelList=(ListView) contentView.findViewById(R.id.dialog_list);
            labelList.setAdapter(new ArrayAdapter<CharSequence>(getContext(), R.layout.item_pop_label, mLabels));
            labelList.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String value=(String) parent.getItemAtPosition(position);
                    if (value.contains("自定义")) {
                        showCustomLabelDialog();
                    }else {
                        mLabelView.setText(value);
                    }
                    mSelectLabelDialog.dismiss();
                }
            });
            int width = getResources().getDimensionPixelSize(R.dimen.dialog_label_width);
            int height = getResources().getDimensionPixelSize(R.dimen.dialog_label_height);
            mSelectLabelDialog=new PopupWindow(contentView, width, height);
            mSelectLabelDialog.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mSelectLabelDialog.setFocusable(true);
        }
        mSelectLabelDialog.showAsDropDown(mLabelView, 0, 0);
    }
    
    /**
     * 显示添加自定义标签窗口    
     */
    private void showCustomLabelDialog() {
        if (mCustomLabelDialog==null) {
            View contentView = mInflater.inflate(R.layout.dialog_custom_label, null);
            
            final EditText customLabel=(EditText) contentView.findViewById(R.id.custom_label);
            
            contentView.findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String label = customLabel.getText().toString();
                    if (TextUtils.isEmpty(label)) {
                        Utils.showToast(getContext(), "自定义标签不能为空");
                        return;
                    }
                    mLabelView.setText(label);
                    mCustomLabelDialog.dismiss();
                }
            });
            
            contentView.findViewById(R.id.btn_cancel).setOnClickListener(this);
            
            mCustomLabelDialog=new PopupWindow(contentView, 
                    ViewGroup.LayoutParams.WRAP_CONTENT, 
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mCustomLabelDialog.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mCustomLabelDialog.setFocusable(true);
        }
        mCustomLabelDialog.showAtLocation(getRootView(), Gravity.CENTER, -20, 0);
    }
    
    private void deleteRow(View clickingView) {
        View row = (View)clickingView.getParent().getParent();
        removeView(row);
    }
	
}
