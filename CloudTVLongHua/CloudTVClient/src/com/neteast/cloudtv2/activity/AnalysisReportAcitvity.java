package com.neteast.cloudtv2.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.R;
import com.neteast.cloudtv2.adapter.TableListAdapter;
import com.neteast.cloudtv2.bean.AnalysisBean;
import com.neteast.cloudtv2.bean.AnalysisBeanDescLis;
import com.neteast.cloudtv2.bean.UserBean;
import com.neteast.cloudtv2.tools.FormHelper;
import com.neteast.cloudtv2.tools.MyLog;
import com.neteast.cloudtv2.tools.TableHelper;
import com.neteast.cloudtv2.tools.Tools;

/** 检验报告
 * 
 * @author LeonZhang
 * @Email zhanglinlang1@163.com */
public class AnalysisReportAcitvity extends Activity {

	private LinearLayout mTableTitle;
	private TextView mCalendarTextView;
	private TextView mInspectionItem;
	private UserBean mUserBean;
	private FormHelper mFormHelper;
	private RequestAllData mRequestAllData;
	private final int REQUEST_LIST_DESC_SCCUESS = 10;
	private final int REQUEST_LIST_DESC_FAILED = 11;
	private final int REQUEST_ALLDATA_SCCUESS = 20;
	private final int REQUEST_ALLDATA_FAILED = 21;

	private String[] titles = { "", "代号", "项目", "结果", "参考值", "单位", "", "代号", "项目", "结果", "参考值", "单位" };
	private int[] titleW = { 40, 70, 190, 90, 130, 100, 40, 70, 190, 90, 130, 100 };

	private ListView mListView;
	private TableListAdapter mTableListAdapter;
	private TextView mTextView;
	private String mYear,mMonth,mDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Debug.startMethodTracing();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkout_report);
		((TextView) findViewById(R.id.activity_title)).setText("检验报告");
		mUserBean = Constant.USER_BEAN;
		initLayout();
		initCalendar();
		startRequestData(mDateFormat.format(System.currentTimeMillis()));
	}

	private Handler mHandler;
	private void initLayout() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case REQUEST_LIST_DESC_SCCUESS:
					mProgressBar.setVisibility(View.GONE);
					List<AnalysisBeanDescLis> list = (List<AnalysisBeanDescLis>) msg.obj;
					if(list.size()==0) Toast.makeText(getApplicationContext(), "数据为空", Toast.LENGTH_LONG).show();
					mTableListAdapter.setData(translateList(list));
					break;
				case REQUEST_ALLDATA_SCCUESS:
					mAnalysisBeanList = (List<AnalysisBean>) msg.obj;
					if(mAnalysisBeanList.size()==0){
						mProgressBar.setVisibility(View.GONE);
						Toast.makeText(getApplicationContext(), "没有对应数据", Toast.LENGTH_LONG).show();
						break;
					}
					setPatientDesc(mAnalysisBeanList.get(0));
					mRequestListDesc = new RequestListDesc(mAnalysisBeanList.get(0).getExamNo());
					mRequestListDesc.start();
					initDatePickerDialog(mAnalysisBeanList);
					initDropDownListLayout(mAnalysisBeanList);
					break;
				case REQUEST_ALLDATA_FAILED:
					mFormHelper.clearData();
				case REQUEST_LIST_DESC_FAILED:
					mTableListAdapter.setData(null);
					mProgressBar.setVisibility(View.GONE);
					String error = (String) msg.obj;
					MyLog.writeLog(error);
					Toast.makeText(getApplicationContext(), "数据为空", Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mTableTitle = (LinearLayout) findViewById(R.id.list_view_charging_title);
		mListView = (ListView) findViewById(R.id.list_view_charging);
		LinearLayout patientJoin = (LinearLayout) findViewById(R.id.patient_desc);
		mFormHelper = new FormHelper(this, patientJoin);
		/** 设置表格title **/
		TableHelper tableHelper = new TableHelper(this, mTableTitle);
		tableHelper.addUnits(titleW, titles);
		mTableListAdapter = new TableListAdapter(this, null, titleW);
		mTableListAdapter.setDividerColor(Color.BLACK);
		mListView.setAdapter(mTableListAdapter);
		// 检查项目
		mInspectionItem = (TextView) findViewById(R.id.InspectionItem);
		mCalendarTextView = (TextView) findViewById(R.id.calendar_show_text);
	}
	
	private SimpleDateFormat mDateFormat;
	private void initCalendar(){
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		findViewById(R.id.SelectDate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String calendar = mCalendarTextView.getText().toString();
				int[] dates;
				if(calendar!=null&&calendar.length()==10){
					dates = Tools.parseDate(AnalysisReportAcitvity.this,calendar);
				}else{
					dates = Tools.parseDate(AnalysisReportAcitvity.this,mDateFormat.format(System.currentTimeMillis()));
				}
				new DatePickerDialog(AnalysisReportAcitvity.this, datePickListener, dates[0], dates[1]-1, dates[2]).show();
			}
		});
	}
	
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

	private void startRequestData(String examdateEnd){
		mProgressBar.setVisibility(View.VISIBLE);
		if(mRequestAllData!=null)mRequestAllData.stopThread();
		mRequestAllData = new RequestAllData(examdateEnd);
		mRequestAllData.start();
	}
	
	@Override
	protected void onDestroy() {
		if(mRequestAllData!=null){
			mRequestAllData.stopThread();
			mRequestAllData = null;
		}
		if(mRequestAllData!=null){
			mRequestListDesc.stopThread();
			mRequestListDesc = null;
		}
		super.onDestroy();
	}

	private List<AnalysisBean> mAnalysisBeanList;
	private ProgressBar mProgressBar;
	class RequestAllData extends Thread {
		private boolean isStop = false;
		private String examdateEnd;
		public RequestAllData(String examdateEnd) {
			this.examdateEnd = examdateEnd;
		}
		public void stopThread() {
			isStop = true;
		}
		@Override
		public void run() {
			Message msg = mHandler.obtainMessage();
			try {
				List<AnalysisBean> risReportDataList = AnalysisBean.getDatas(examdateEnd);
				msg.obj = risReportDataList;
				msg.what = REQUEST_ALLDATA_SCCUESS;
			} catch (Exception e) {
				msg.obj = e.toString();
				msg.what = REQUEST_ALLDATA_FAILED;
			}
			if (!isStop) mHandler.sendMessage(msg);
		}
	}

	private void setPatientDesc(AnalysisBean bean) {
		mInspectionItem.setText(bean.getItemName());
		List<String> result = new ArrayList<String>();
		result.add("姓名：" + mUserBean.getName());
		result.add("病人类型：" + "");
		result.add("床号：" + mUserBean.getBedNo());
		result.add("标本种类：" + bean.getExPart());
		result.add("性别：" + mUserBean.getSex());
		result.add("门诊/住院号：" + mUserBean.getPatNo());
		result.add("申请医生：" + bean.getPetitionDoc());
		result.add("申请时间：" + bean.getExamDate());
		result.add("年龄：" + mUserBean.getAge());
		result.add("科室：" + bean.getDeptDesc());
		result.add("诊断：" + bean.getDeptDesc());
		result.add("备注：" + bean.getRemark());

		((TextView) findViewById(R.id.list_id)).setText("条形码：" + bean.getLisTmNo());
		((TextView) findViewById(R.id.sample_no)).setText("样本号：" + bean.getSampeNo());
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
				if (i % 4 == 0)
					createNewLine = mFormHelper.createNewLine(40);
				createNewLine.addItem(mFormHelper.createItem(result.get(i), 300));
			}
		}
	}


	public void onClickBack(View view) {
		this.finish();
	}

	/** 显示选择日期对话框 */
	private void initDatePickerDialog(List<AnalysisBean> result) {
		final List<String> list = new ArrayList<String>();
		final List<AnalysisBean> ll = new ArrayList<AnalysisBean>();
		for (int i = 0; i < result.size(); i++) {
			boolean isExit = false;
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j).equals(result.get(i).getExamDate()))
					isExit = true;
			}
			if (!isExit) {
				ll.add(result.get(i));
				list.add(result.get(i).getExamDate());
			}
		}
		mCalendarTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Tools.showDropList(AnalysisReportAcitvity.this, mCalendarTextView, list, new Tools.ItemSelectListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						AnalysisBean analysisBean = ll.get(position);
						setPatientDesc(analysisBean);
						mProgressBar.setVisibility(View.VISIBLE);
						if (mRequestListDesc != null) mRequestListDesc.stopThread();
						mRequestListDesc = new RequestListDesc(analysisBean.getExamNo());
						mRequestListDesc.start();
					}
				});
			}
		});
	}
	
	/* ==========================检查项目加载=================== */
	private void initDropDownListLayout(List<AnalysisBean> result) {
		mInspectionItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ArrayList<String> mInspectionItemValue = new ArrayList<String>();
				String date = mCalendarTextView.getText().toString();
				final List<AnalysisBean> ll = new ArrayList<AnalysisBean>();
				for (int i = 0; i < mAnalysisBeanList.size(); i++) {
					if (date.equals(mAnalysisBeanList.get(i).getExamDate())) {
						mInspectionItemValue.add(mAnalysisBeanList.get(i).getItemName());
						ll.add(mAnalysisBeanList.get(i));
					}
				}
				Tools.showDropList(AnalysisReportAcitvity.this, mInspectionItem, mInspectionItemValue,
						new Tools.ItemSelectListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								AnalysisBean analysisBean = ll.get(position);
								mProgressBar.setVisibility(View.VISIBLE);
								setPatientDesc(analysisBean);
								if(mRequestListDesc!=null)mRequestListDesc.stopThread();
								mRequestListDesc = new RequestListDesc(analysisBean.getExamNo());
								mRequestListDesc.start();
							}
						});
			}
		});
	}
	
	
	private RequestListDesc mRequestListDesc;

	class RequestListDesc extends Thread {
		private boolean isStop = false;
		private String examNo;

		public RequestListDesc(String examNo) {
			this.examNo = examNo;
		}

		public void stopThread() {
			isStop = true;
		}

		@Override
		public void run() {
			Message msg = mHandler.obtainMessage();
			try {
				List<AnalysisBeanDescLis> analysisBeanDescLis = AnalysisBeanDescLis.getDatas(examNo);
				msg.obj = analysisBeanDescLis;
				msg.what = REQUEST_LIST_DESC_SCCUESS;
			} catch (Exception e) {
				msg.obj = e.toString();
				msg.what = REQUEST_LIST_DESC_FAILED;
			}
			if (!isStop)
				mHandler.sendMessage(msg);
		}
	}

	/** 填充表格排序
	 * 
	 * @param list 更具表格项目来排序显示顺序
	 * @return */
	public List<List<String>> translateList(List<AnalysisBeanDescLis> list) {
		if (list == null)
			return null;
		List<List<String>> dates = new ArrayList<List<String>>();
		int half = list.size()%2==0?  list.size()/2: list.size()/2+1;
		int j = 1;
		for (int i = 0; i < half; i ++) {
			List<String> item = new ArrayList<String>();
			AnalysisBeanDescLis itemBean = list.get(i);
			item.add(String.valueOf(j));
			item.add(itemBean.getLisItemCode());
			item.add(itemBean.getLisItemName());
			item.add(itemBean.getLisValue());
			item.add(itemBean.getReferScope());
			item.add(itemBean.getUnit());
			if (i + half >= list.size()) {
				item.add(" ");
				item.add(" ");
				item.add(" ");
				item.add(" ");
				item.add(" ");
				item.add(" ");
				dates.add(item);
				break;
			}
			AnalysisBeanDescLis itemBean1 = list.get(i + half);
			item.add(String.valueOf(j + half));
			item.add(itemBean1.getLisItemCode());
			item.add(itemBean1.getLisItemName());
			item.add(itemBean1.getLisValue());
			item.add(itemBean1.getReferScope());
			item.add(itemBean1.getUnit());
			dates.add(item);
			j++;
		}
		return dates;
	}

}
