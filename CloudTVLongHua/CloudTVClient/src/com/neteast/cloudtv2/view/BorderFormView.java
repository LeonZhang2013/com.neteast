package com.neteast.cloudtv2.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;

public class BorderFormView extends View {
	
	private  final float UNIT_COUNT=54;
	private  float mUnitWidth;
	private  final int mUnitHeight=40;
	private  int mPadding=7;
	private  float mTextSize=19f;
	private TextPaint mPaint;
	
	private int mFirstVerticalLineVertex;
	private int mSecondVerticalLineVertex;
	private int mThirdVerticalLineVertex;
	private int mFouthVerticalLineVertex;
	private int mFifthVerticalLineVertex;
	private int mSixthVerticalLineVertex;
	private int mSeventhVerticalLineVertex;
	
	private ArrayList<LineData> mDatas=new ArrayList<BorderFormView.LineData>();
	
	private int mFirstColumnWidth;
	private int mSecondColumnWidth;
	private int mThirdColumnWidth;
	private int mFouthColumnWidth;
	private int mFifthColumnWidth;
	private int mSixthColumnWidth;
	
	public BorderFormView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		
		mPaint = new TextPaint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(1f);
		mPaint.setTextSize(mTextSize);
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
			if (mDatas==null) {
				height=getPaddingBottom()+getPaddingTop();
			}else {
				height=(mDatas.size())*mUnitHeight+getPaddingBottom()+getPaddingTop();
			}
		}
		setMeasuredDimension(widthSize, height);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mUnitWidth=w/UNIT_COUNT;
		mFirstVerticalLineVertex = 0;
		mSecondVerticalLineVertex = (int) FloatMath.floor(mUnitWidth*4);
		mThirdVerticalLineVertex = (int) FloatMath.floor(mUnitWidth*10);
		mFouthVerticalLineVertex = (int) FloatMath.floor(mUnitWidth*26);
		mFifthVerticalLineVertex = (int) FloatMath.floor(mUnitWidth*34);
		mSixthVerticalLineVertex = (int) FloatMath.floor(mUnitWidth*45);
		mSeventhVerticalLineVertex = w;
		
		mFirstColumnWidth = mSecondVerticalLineVertex-mFirstVerticalLineVertex;
		mSecondColumnWidth = mThirdVerticalLineVertex-mSecondVerticalLineVertex;
		mThirdColumnWidth = mFouthVerticalLineVertex-mThirdVerticalLineVertex;
		mFouthColumnWidth = mFifthVerticalLineVertex-mFouthVerticalLineVertex;
		mFifthColumnWidth = mSixthVerticalLineVertex-mFifthVerticalLineVertex;
		mSixthColumnWidth = mSeventhVerticalLineVertex-mSixthVerticalLineVertex;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int totalWidth = getMeasuredWidth();
		int totalHeight = getMeasuredHeight();
		//画两条横线
		canvas.drawLine(0, 0, totalWidth, 0, mPaint);
		canvas.drawLine(0, mUnitHeight, totalWidth, mUnitHeight, mPaint);
		canvas.drawLine(0, totalHeight, totalWidth, totalHeight, mPaint);
		//画七条横线
		canvas.drawLine(mFirstVerticalLineVertex, 0, mFirstVerticalLineVertex, totalHeight, mPaint);
		canvas.drawLine(mSecondVerticalLineVertex, 0, mSecondVerticalLineVertex, totalHeight, mPaint);
		canvas.drawLine(mThirdVerticalLineVertex, 0, mThirdVerticalLineVertex, totalHeight, mPaint);
		canvas.drawLine(mFouthVerticalLineVertex, 0, mFouthVerticalLineVertex, totalHeight, mPaint);
		canvas.drawLine(mFifthVerticalLineVertex, 0, mFifthVerticalLineVertex, totalHeight, mPaint);
		canvas.drawLine(mSixthVerticalLineVertex, 0, mSixthVerticalLineVertex, totalHeight, mPaint);
		canvas.drawLine(mSeventhVerticalLineVertex, 0, mSeventhVerticalLineVertex, totalHeight, mPaint);
		//第一行写字
		float firstLineTextY=(mUnitHeight-mTextSize)/2+mTextSize;
		canvas.drawText("代号", mSecondVerticalLineVertex+mPadding, firstLineTextY, mPaint);
		canvas.drawText("项目", mThirdVerticalLineVertex+mPadding, firstLineTextY, mPaint);
		canvas.drawText("结果", mFouthVerticalLineVertex+mPadding, firstLineTextY, mPaint);
		canvas.drawText("参考值", mFifthVerticalLineVertex+mPadding, firstLineTextY, mPaint);
		canvas.drawText("单位", mSixthVerticalLineVertex+mPadding, firstLineTextY, mPaint);
		//添加数据
		final LineData[] lineDatas = mDatas.toArray(new LineData[]{});
		for(int i=0;i<lineDatas.length;i++){
			
			LineData lineData=lineDatas[i];
			
			final float lineTextY=(mUnitHeight-mTextSize)/2+mTextSize+(i+1)*mUnitHeight;
			
			CharSequence value=TextUtils.ellipsize(lineData.No, mPaint, mFirstColumnWidth-mPadding, TruncateAt.END);
			canvas.drawText(value.toString(), mFirstVerticalLineVertex+mPadding, lineTextY, mPaint);
			
			value=TextUtils.ellipsize(lineData.codeNamed, mPaint, mSecondColumnWidth-mPadding, TruncateAt.END);
			canvas.drawText(value.toString(), mSecondVerticalLineVertex+mPadding, lineTextY, mPaint);
			
			value=TextUtils.ellipsize(lineData.project, mPaint, mThirdColumnWidth-mPadding, TruncateAt.END);
			canvas.drawText(value.toString(), mThirdVerticalLineVertex+mPadding, lineTextY, mPaint);
			
			value=TextUtils.ellipsize(lineData.result, mPaint, mFouthColumnWidth-mPadding, TruncateAt.END);
			canvas.drawText(value.toString(), mFouthVerticalLineVertex+mPadding, lineTextY, mPaint);
			
			value=TextUtils.ellipsize(lineData.referenceValue, mPaint, mFifthColumnWidth-mPadding, TruncateAt.END);
			canvas.drawText(value.toString(), mFifthVerticalLineVertex+mPadding, lineTextY, mPaint);
			
			value=TextUtils.ellipsize(lineData.unit, mPaint, mSixthColumnWidth-mPadding, TruncateAt.END);
			canvas.drawText(value.toString(), mSixthVerticalLineVertex+mPadding, lineTextY, mPaint);
			
		}
	}
	
	public void addLine(String no, String codeNamed, String project,String result, String referenceValue, String unit) {
		mDatas.add(new LineData(no, codeNamed, project, result, referenceValue, unit));
		invalidate();
	}
	public void addLine(LineData lineData) {
		mDatas.add(lineData);
		invalidate();
	}
	
	public void addLines(List<LineData> datas) {
		mDatas.addAll(datas);
		invalidate();
	}
	
	public void clearFormData() {
		mDatas.clear();
		invalidate();
	}
	
	public class LineData extends ArrayList<String>{
		public String No;
		public String codeNamed;
		public String project;
		public String result;
		public String referenceValue;
		public String unit;
		
		public LineData() {}

		public LineData(String no, String codeNamed, String project, String result, String referenceValue, String unit) {
			No = no;
			this.codeNamed = codeNamed;
			this.project = project;
			this.result = result;
			this.referenceValue = referenceValue;
			this.unit = unit;
			
			add(no);
			add(codeNamed);
			add(project);
			add(result);
			add(referenceValue);
			add(unit);
		}
	}
	
}
