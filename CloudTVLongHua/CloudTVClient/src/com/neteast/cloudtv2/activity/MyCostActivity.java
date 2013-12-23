package com.neteast.cloudtv2.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
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
import com.neteast.cloudtv2.adapter.TableListAdapter;
import com.neteast.cloudtv2.bean.MyCostBean;
import com.neteast.cloudtv2.bean.MyCostItemBean;
import com.neteast.cloudtv2.bean.UserBean;
import com.neteast.cloudtv2.tools.FormHelper;
import com.neteast.cloudtv2.tools.TableHelper;
import com.neteast.cloudtv2.tools.Tools;

public class MyCostActivity extends Activity {

	private LinearLayout mTableTitle;
	private String[] titles = { "序号", "项目名称", "数量", "费用", "个人支付" };
	private int[] titleW = { 50, 400, 250, 230, 300 };

	private TextView mCalendarTextView;
	private ListView mListView;
	private TableListAdapter mTableListAdapter;
	private UserBean user;
	private FormHelper mFormHelper;
	private Context mContext;
	private ProgressBar mprogressBar;
	private Handler mHanlder;
	private RequestDataLis mRequestDataLis;
	private PopupWindow mDropDownList;
	private List<String> listTypeData;
	private RequestData mRequestData;
	private final int REQUEST_DATA_SUCCESS = 10;
	private final int REQUEST_DATA_FAILED = 11;
	private final int REQUEST_DATA_LIS_SUCCESS = 20;
	private final int REQUEST_DATA_LIS_FAILED = 21;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.four_activity_charging);
		((TextView) findViewById(R.id.activity_title)).setText("我的费用");
		user = Constant.USER_BEAN;
		mContext = this;
		initLayout();
		startRequestData(mDateFormat.format(System.currentTimeMillis()));
	}

	private void startRequestData(String curEffDateEnd) {
		if (mRequestData != null)
			mRequestData.stopThread();
		mRequestData = new RequestData(curEffDateEnd);
		mRequestData.start();
	}

	private void startRequestDataLis(MyCostBean bean) {
		if (mRequestDataLis != null)
			mRequestDataLis.stopThread();
		mRequestDataLis = new RequestDataLis(bean);
		mRequestDataLis.start();
	}

	private void initLayout() {
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		mHanlder = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case REQUEST_DATA_LIS_SUCCESS:
					mprogressBar.setVisibility(View.GONE);
					List<List<String>> mDatas = (List<List<String>>) msg.obj;
					if (mDatas.size() == 0)
						Toast.makeText(getApplicationContext(), "没有查询到数据", Toast.LENGTH_LONG).show();
					mTableListAdapter.setData(mDatas);
					break;
				case REQUEST_DATA_SUCCESS:
					mListDates = (List<MyCostBean>) msg.obj;
					if (mListDates.size() == 0) {
						Toast.makeText(getApplicationContext(), "没有查询到数据", Toast.LENGTH_LONG).show();
						return;
					}
					setDateDisplay(mListDates.get(0));
					startRequestDataLis(mListDates.get(0));
					break;
				case REQUEST_DATA_FAILED:
				case REQUEST_DATA_LIS_FAILED:
					mprogressBar.setVisibility(View.GONE);
					mTableListAdapter.setData(null);
					String error = (String) msg.obj;
					Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};

		mprogressBar = (ProgressBar) findViewById(R.id.progressBar1);
		LinearLayout patientJoin = (LinearLayout) findViewById(R.id.patient_desc);
		mFormHelper = new FormHelper(this, patientJoin);

		List<String> list = new ArrayList<String>();
		list.add("姓名：" );
		list.add("性别：" );
		list.add("项目名称：");
		list.add("总费用：" );
		list.add("费用类别：" );
		list.add("收费部门：");
		setPatientDesc(list);

		mTableTitle = (LinearLayout) findViewById(R.id.list_view_charging_title);
		mCalendarTextView = (TextView) findViewById(R.id.calendar_item_text);
		mListView = (ListView) findViewById(R.id.list_view_charging);
		/** 设置表格title **/
		TableHelper tableHelper = new TableHelper(this, mTableTitle);
		tableHelper.addUnits(titleW, titles);
		mTableListAdapter = new TableListAdapter(this, null, titleW);
		mListView.setAdapter(mTableListAdapter);
		mCalendarTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSelectDropCalender(v);
			}
		});
	}

	/** 打印详细信息
	 * 
	 * @param result */
	private void setPatientDesc(List<String> result) {
		FormHelper createNewLine = null;
		List<TextView> textViews = mFormHelper.getTextViews();
		if (result.size() > 0 && result.size() == textViews.size()) {
			for (int i = 0; i < textViews.size(); i++) {
				textViews.get(i).setText(result.get(i));
			}
		} else {// 数据不一样时
			mFormHelper.clearData();
			for (int i = 0; i < result.size(); i++) {
				if (i % 2 == 0)
				createNewLine = mFormHelper.createNewLine(50);
				createNewLine.addItem(mFormHelper.createItem(result.get(i), 600));
			}
		}
	}

	private List<MyCostBean> mListDates;

	class RequestData extends Thread {

		private String costDate;
		private boolean isStop = false;

		public RequestData(String curEffDateEnd) {
			this.costDate = curEffDateEnd;
		}

		public void stopThread() {
			isStop = true;
		}

		@Override
		public void run() {
			Message msg = mHanlder.obtainMessage();
			try {
				List<MyCostBean> mListDates = MyCostBean.getData(user.getPatNo(), costDate, null, 30);
				msg.what = REQUEST_DATA_SUCCESS;
				msg.obj = mListDates;
			} catch (Exception e) {
				msg.what = REQUEST_DATA_FAILED;
				msg.obj = e.toString();
			}
			if (!isStop)
				mHanlder.sendMessage(msg);
		}
	}

	class RequestDataLis extends Thread {

		private MyCostBean bean;
		private boolean isStop = false;

		public RequestDataLis(MyCostBean bean) {
			this.bean = bean;
		}

		public void stopThread() {
			isStop = true;
		}

		@Override
		public void run() {
			Message msg = mHanlder.obtainMessage();
			try {
				List<MyCostItemBean> chargeData = MyCostItemBean.getDatas(bean.getSequence());
				List<List<String>> beans = translateList(chargeData);
				msg.what = REQUEST_DATA_LIS_SUCCESS;
				msg.obj = beans;
			} catch (Exception e) {
				msg.what = REQUEST_DATA_LIS_FAILED;
				msg.obj = e.toString();
			}
			if (!isStop)
				mHanlder.sendMessage(msg);
		}

	}

	/** 填充表格排序
	 * 
	 * @param list 更具表格项目来排序显示顺序
	 * @return */
	public static List<List<String>> translateList(List<MyCostItemBean> list) {
		List<List<String>> dates = new ArrayList<List<String>>();
		for (int i = 0; i < list.size(); i++) {
			List<String> item = new ArrayList<String>();
			MyCostItemBean itemBean = list.get(i);
			item.add(String.valueOf(i + 1));
			item.add(itemBean.getItemName());
			item.add(itemBean.getItemQuantity() + itemBean.getItemUnit());
			item.add(itemBean.getItemCost());
			item.add("---");
			dates.add(item);
		}
		return dates;
	}

	public void onSelectDropCalender(View view) {
		listTypeData = new ArrayList<String>();
		if (mListDates == null)
			return;
		for (int i = 0; i < mListDates.size(); i++) {
			String item = mListDates.get(i).getPaymentDate() + mListDates.get(i).getChargeType();
			listTypeData.add(item);
		}
		final View contentView = LayoutInflater.from(mContext).inflate(R.layout.drop_down_list_layout_230, null);
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
				mprogressBar.setVisibility(View.VISIBLE);
				startRequestDataLis(mListDates.get(position));
				updatePatientDesc(mListDates.get(position));
			}
		});
	}

	private void updatePatientDesc(MyCostBean bean) {
		List<String> list = new ArrayList<String>();
		list.add("姓名：" + user.getName());
		list.add("性别：" + user.getSex());
		list.add("项目名称：" + bean.getItemName());
		list.add("总费用：" + bean.getCharge());
		list.add("费用类别：" + bean.getChargeType());
		list.add("收费部门：" + bean.getDepartment());
		setPatientDesc(list);
	}

	private SimpleDateFormat mDateFormat;

	public void onSelectCalender(View view) {
		String calendar = mCalendarTextView.getText().toString();
		int[] dates;
		if (calendar != null && calendar.length() >= 10) {
			dates = Tools.parseDate(MyCostActivity.this, calendar);
		} else {
			dates = Tools.parseDate(MyCostActivity.this, mDateFormat.format(System.currentTimeMillis()));
		}
		new DatePickerDialog(MyCostActivity.this, datePickListener, dates[0], dates[1] - 1, dates[2]).show();
	}

	private String mYear, mMonth, mDay;
	private OnDateSetListener datePickListener = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			monthOfYear++;
			mprogressBar.setVisibility(View.VISIBLE);
			mYear = String.valueOf(year);
			mMonth = monthOfYear < 10 ? "0" + monthOfYear : String.valueOf(monthOfYear);
			mDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
			startRequestData(mYear + "-" + mMonth + "-" + mDay);
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mRequestData != null)
			mRequestData.stopThread();
		if (mRequestDataLis != null)
			mRequestDataLis.stopThread();
	}

	/** 第一次进入界面的时候初始化日期显示 */
	private void setDateDisplay(MyCostBean bean) {
		updatePatientDesc(bean);
		mCalendarTextView.setText(bean.getPaymentDate() + bean.getChargeType());
	}

	public void onClickBack(View view) {
		this.finish();
	}
}
