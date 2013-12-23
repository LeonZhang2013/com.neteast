package com.neteast.cloudtv2.tools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-5-10
 */
public class FormHelper {

	private Context mContext;
	private LinearLayout mJoinPiont;
	private LayoutParams mParams;	
	private List<TextView> textViews;
	private LinearLayout mline;
	

	public FormHelper(Context mContext,LinearLayout joinPiont) {
		super();
		this.mContext = mContext;
		mJoinPiont = joinPiont;
		mJoinPiont.setOrientation(LinearLayout.VERTICAL);
		textViews = new ArrayList<TextView>();
	}
	
	/**
	 * 创建一行 
	 * @param height 行高
	 * @return
	 */
	public FormHelper createNewLine(int height){
		mline = new LinearLayout(mContext);
		mline.setGravity(Gravity.CENTER_VERTICAL);
		mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, height);
		mJoinPiont.addView(mline,mParams);
		return this;
	}
	
	/**
	 * 给一行添加数据， 
	 * @param textView 
	 * @return
	 */
	public FormHelper addItem(TextView textView){
		mline.addView(textView);
		return this;
	}
	
	/**
	 * 创建以个textView
	 * @param text 内容
	 * @param textW 宽度
	 * @return
	 */
	public TextView createItem(String text,int textW){
		TextView textView = new TextView(mContext);
		textView.setText(text);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(20);
		LayoutParams params = new LayoutParams(textW,LayoutParams.WRAP_CONTENT);
		textView.setLayoutParams(params);
		textViews.add(textView);
		return textView;
	}
	
	/**
	 * 按照添加顺序排序
	 * @return
	 */
	public List<TextView> getTextViews(){
		return textViews;
	}

	public void clearData() {
		for(int i=0; i<textViews.size(); i++){
			textViews.get(i).setText("");
		}
	}
	
	public void removeViews(){
		mJoinPiont.removeAllViews();
	}
	

}
