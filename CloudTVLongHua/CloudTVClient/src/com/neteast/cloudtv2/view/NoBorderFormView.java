package com.neteast.cloudtv2.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;

import com.neteast.cloudtv2.tools.LogUtil;

public class NoBorderFormView extends View{
	
	private static final int COLUMN_COUNT=4;
	
	private int mPadding=10;
	
	private int mColumnWidth;
	
	private float mTitleSize=25f;
	private float mTextSize=19f;
	
	private int mNormalLineHeight=40;
	private int mTitleLineHeight=50;
	
	private FormData mFormData;
	private TextPaint mTitlePaint;
	private TextPaint mTextPaint;
	
	public NoBorderFormView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTitlePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
		mTitlePaint.setTextSize(mTitleSize);
		mTitlePaint.setStyle(Paint.Style.FILL);
		mTitlePaint.setColor(Color.BLACK);
		mTitlePaint.setFakeBoldText(true);
		
		mTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setColor(Color.BLACK);
	}
	
	public void setFormData(FormData formData) {
		mFormData=formData;
		invalidate();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		int height;
		if (heightMode==MeasureSpec.EXACTLY) {
			height=heightSize;
		}else {
			if (mFormData==null) {
				height=getPaddingBottom()+getPaddingTop();
			}else {
				height=(mFormData.getLineCount()-1)*mNormalLineHeight+mTitleLineHeight+getPaddingBottom()+getPaddingTop();
			}
		}
		setMeasuredDimension(widthSize, height);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mColumnWidth=w/COLUMN_COUNT;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mFormData==null) {
			return;
		}
		int lineCount = mFormData.getLineCount();
		for(int i=0;i<lineCount;i++){
			if (i==0) {
				drawFirstLine(canvas,mFormData.getLine(i));
			}else {
				drawLine(canvas,i);
			}
		}
	}
	
    private void drawLine(Canvas canvas, int lineNo) {
    	int paddingY=(int) ((mNormalLineHeight-mTextSize)/2+mTextSize);
    	int textY=paddingY+mTitleLineHeight+mNormalLineHeight*(lineNo-1);
    	String[] line=mFormData.getLine(lineNo);
    	for(int i=0;i<line.length;i++){
    		int textX=i*mColumnWidth;
    		CharSequence sequence = TextUtils.ellipsize(line[i], mTextPaint, mColumnWidth-mPadding, TruncateAt.END);
    		canvas.drawText(sequence.toString(), textX, textY, mTextPaint);
    	}
	}

	private void drawFirstLine(Canvas canvas, String[] line) {
    	int titleY= (int) ((mTitleLineHeight-mTitleSize)/2+mTitleSize);
    	float titleWidth=Layout.getDesiredWidth(line[1], mTitlePaint);
    	float paddingX=(2*mColumnWidth-titleWidth)/2;
    	
    	canvas.drawText(line[0], 0, titleY, mTextPaint);
    	canvas.drawText(line[2], 3*mColumnWidth, titleY, mTextPaint);
    	canvas.drawText(line[1], mColumnWidth+paddingX , titleY, mTitlePaint);
	}
    
	public static class FormData {
		
		private ArrayList<String[]> data=new ArrayList<String[]>();
		private String formTitle;
		
		public String getFormTitle() {
			return formTitle;
		}
		
		public void setFormTitle(String formTitle) {
			this.formTitle = formTitle;
		}
		
		public int getLineCount() {
			return data.size();
		}
		
		public void addLine(String[] line) {
			data.add(line);
		}
		
		public String[] getLine(int lineNo) {
			return data.get(lineNo);
		}
		
		public boolean isFromTitle(String cellValue) {
			if (cellValue==null) {
				return false;
			}
			return cellValue.equals(formTitle);
		}

		@Override
		public String toString() {
			StringBuilder buf=new StringBuilder();
			for (String[] value : data) {
				buf.append(Arrays.toString(value));
			}
			return "FormData [data=" + buf.toString() + ", formTitle=" + formTitle + "]";
		}
	}
	
	public static class FormParser {
		public static FormData parse(InputStream ins) {
			XmlPullParser parser = Xml.newPullParser();
			FormData formData = new FormData();
			try {
				parser.setInput(ins, "UTF-8");
				int event = parser.next();
				String lastTagName = null;
				ArrayList<String> tempLine = new ArrayList<String>();
				while (event != XmlPullParser.END_DOCUMENT) {
					switch (event) {
					case XmlPullParser.START_TAG:
						lastTagName = parser.getName();
						if ("Line".equals(lastTagName)) {
							tempLine.clear();
						} else if ("Cell".equals(lastTagName)) {
							tempLine.add(parser.nextText());
						} else if ("Title".equals(lastTagName)) {
							String title = parser.nextText();
							tempLine.add(title);
							formData.setFormTitle(title);
						}
						break;
					case XmlPullParser.END_TAG:
						lastTagName=parser.getName();
						if ("Line".equals(lastTagName)) {
							String[] line = tempLine.toArray(new String[]{});
							formData.addLine(line);
						} 
						break;
					default:
						break;
					}
					event = parser.next();
				}
			} catch (XmlPullParserException e) {
				LogUtil.e(e);
			} catch (IOException e) {
				LogUtil.e(e);
			}
			return formData;
		}
	}
}
