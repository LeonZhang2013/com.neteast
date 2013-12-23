package com.neteast.cloudtv2.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.R;
import com.neteast.cloudtv2.bean.CheckupReportBean;
import com.neteast.cloudtv2.bean.UserBean;
import com.neteast.cloudtv2.tools.FormHelper;
import com.neteast.cloudtv2.tools.Tools;

/** 检查报告
 * 
 * @author LeonZhang
 * @Email zhanglinlang1@163.com */
public class CheckupReportAcitvity extends Activity {

	private TextView mCalendarTextView;
	private FormHelper mFormHelper;
	private List<CheckupReportBean> datas;
	private TextView content1;
	private TextView content2;
	private Context mContext;
	private ProgressBar mProgressBar;
	private RequestData mRequestData;
	private Handler mHanlder;
	private final int REQUEST_DATA_SCCUESS = 10;
	private final int REQUEST_DATA_FAILED = 11;
	private SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = CheckupReportAcitvity.this;
		setContentView(R.layout.activity_checkup_report);
		((TextView) findViewById(R.id.activity_title)).setText("检查报告");
		initLayout();
		startRequestData(ft.format(System.currentTimeMillis()));
	}
	
	private void startRequestData(String date){
		mProgressBar.setVisibility(View.VISIBLE);
		if(mRequestData!=null)mRequestData.stopThread();
		mRequestData = new RequestData(date);
		mRequestData.start();
	}

	private void initLayout() {
		mHanlder = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				mProgressBar.setVisibility(View.GONE);
				switch (msg.what) {
				case REQUEST_DATA_SCCUESS:
					datas = (List<CheckupReportBean>) msg.obj;
					if(datas.size()==0){
						clearDesc();
						Toast.makeText(getApplicationContext(), "没有查询到任何数据", Toast.LENGTH_LONG).show();
						break;
					}
					setPatientDesc(datas.get(0));
					mCalendarTextView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							onSelectDropCalender(v);
						}
					});
					break;
				case REQUEST_DATA_FAILED:
					String error = (String) msg.obj;
					Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
					break;
				}
			}
			
		};
		mProgressBar = (ProgressBar)findViewById(R.id.progressBar1);
		mCalendarTextView = (TextView) findViewById(R.id.calendar_item_text);
		content1 = (TextView) findViewById(R.id.content_desc1);
		content2 = (TextView) findViewById(R.id.content_desc2);
		LinearLayout patientJoin = (LinearLayout) findViewById(R.id.patient_desc);
		mFormHelper = new FormHelper(this, patientJoin);
	}

	class RequestData extends Thread {
		
		private boolean isStop = false;
		private String examdateEnd;
		public RequestData(String examdateEnd) {
			this.examdateEnd = examdateEnd;
		}

		public void stopThread(){
			isStop = true;
		}
		
		@Override
		public void run() {
			Message msg = mHanlder.obtainMessage();
			try{
				List<CheckupReportBean> risReportDataList = CheckupReportBean.getDatas(examdateEnd);
				msg.obj = risReportDataList;
				msg.what = REQUEST_DATA_SCCUESS;
			}catch (Exception e) {
				msg.what = REQUEST_DATA_FAILED;
				msg.obj = e.getMessage();
			}
			if(!isStop) mHanlder.sendMessage(msg);
		}
	}

	private void clearDesc(){
		content1.setText("");
		content2.setText("");
		mCalendarTextView.setText("");
		mFormHelper.clearData();
	}
	
	private void setPatientDesc(CheckupReportBean bean) {
		List<String> result = new ArrayList<String>();
		result.add("姓名：" + Constant.USER_BEAN.getName());
		result.add("性别：" +  Constant.USER_BEAN.getSex());
		result.add("病人卡号：" +  Constant.USER_BEAN.getPatNo());
		result.add("检验师：" + bean.getReportDocName());
		result.add("科室：" + bean.getDeptDesc());
		result.add("备注：" + bean.getRemark());
/*		result.add("诊断：" + bean.getClinicalDig());
		result.add("审核者：" + bean.getAuditingName());
		result.add("检查开始日期：" + bean.getPetitionerDocname());
		result.add("检查借宿日期：" + bean.getPetitionerDate()); */
		content1.setText(bean.getImageDis());
		content2.setText(bean.getExDigReport());
		mCalendarTextView.setText(bean.getExamDate());

		FormHelper createNewLine = null;
		List<TextView> textViews = mFormHelper.getTextViews();
		if (result.size() > 0 && result.size() == textViews.size()) {
			for (int i = 0; i < textViews.size(); i++) {
				textViews.get(i).setText(result.get(i));
			}
		} else {// 数据不一样时 
			mFormHelper.clearData();
			for (int i = 0; i < result.size(); i++) {
				if (i % 3 == 0)
					createNewLine = mFormHelper.createNewLine(40);
				createNewLine.addItem(mFormHelper.createItem(result.get(i), 350));
			}
		}
	}

	/** 日期选择 监听
	 * 
	 * @param view */
	private void onSelectDropCalender(View view) {
		listTypeData = new ArrayList<String>();
		if(datas==null||datas.size()==0) return;
		for (int i = 0; i < datas.size(); i++) {
			String item = datas.get(i).getExamDate();
			if(item==null) item = "";
			listTypeData.add(item);
		}
		final View contentView = LayoutInflater.from(mContext).inflate(R.layout.drop_down_list_layout_180, null);
		mDropDownList = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mDropDownList.setOutsideTouchable(false);
		mDropDownList.setFocusable(true);
		mDropDownList.setBackgroundDrawable(new BitmapDrawable());
		ListView itemlist = (ListView) contentView.findViewById(R.id.item_list);
		itemlist.setAdapter(new ArrayAdapter<String>(mContext, R.layout.drop_down_list_item, R.id.item_text, listTypeData));
		mDropDownList.showAsDropDown(mCalendarTextView, 0, 0);

		itemlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCalendarTextView.setText(listTypeData.get(position));
				mDropDownList.dismiss();
				setPatientDesc(datas.get(position));
			}
		});
	}

	private PopupWindow mDropDownList;
	private List<String> listTypeData;

	/** 返回监听
	 * 
	 * @param view */
	public void onClickBack(View view) {
		this.finish();
	}

	@Override
	protected void onDestroy() {
		mRequestData.stopThread();
		super.onDestroy();
	}
	
	public void onSelectCalender(View view) {
		String calendar = mCalendarTextView.getText().toString();
		int[] dates;
		if(calendar!=null&&calendar.length()>=10){
			dates = Tools.parseDate(CheckupReportAcitvity.this,calendar);
		}else{
			dates = Tools.parseDate(CheckupReportAcitvity.this,Constant.DATE_FORMAT.format(System.currentTimeMillis()));
		}
		new DatePickerDialog(CheckupReportAcitvity.this, datePickListener, dates[0], dates[1]-1, dates[2]).show();
	}
	
	private String mYear,mMonth,mDay;
	private OnDateSetListener datePickListener =new OnDateSetListener(){
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			monthOfYear++;
			mYear=String.valueOf(year);
			mMonth= monthOfYear<10? "0"+monthOfYear : String.valueOf(monthOfYear);
			mDay= dayOfMonth<10? "0"+dayOfMonth:String.valueOf(dayOfMonth);
			startRequestData(mYear+"-"+mMonth+"-"+mDay);
		}

	};
}
