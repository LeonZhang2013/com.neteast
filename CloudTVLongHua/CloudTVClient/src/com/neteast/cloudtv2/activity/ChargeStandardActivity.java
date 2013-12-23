package com.neteast.cloudtv2.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.neteast.cloudtv2.R;
import com.neteast.cloudtv2.adapter.TableListAdapter;
import com.neteast.cloudtv2.bean.ChargingBean;
import com.neteast.cloudtv2.tools.TableHelper;
import com.neteast.cloudtv2.tools.Tools;

public class ChargeStandardActivity extends Activity {
	
	private LinearLayout mTableTitle;
	private String[] titles = {"项目名称","标准价格","单位","自付比例"};
	private int[]  titleW = {450,250,230,300};
	
	private TextView mChargTextView;
	private ListView mListView;
	private TableListAdapter mTableListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_activity_charging);
		((TextView)findViewById(R.id.activity_title)).setText("收费标准");
		initLayout();
		initDropDownListLayout();
		initData();
	}
	
	private void initData() {
		mTableListAdapter = new TableListAdapter(this,null,titleW);
		mListView.setAdapter(mTableListAdapter);
		new RequestData().execute(0);
	}

	private void initLayout(){
		mTableTitle = (LinearLayout) findViewById(R.id.list_view_charging_title);
		mChargTextView = (TextView) findViewById(R.id.charg_item_text);
		mListView = (ListView) findViewById(R.id.list_view_charging);
		/** 设置表格title **/
		TableHelper tableHelper = new TableHelper(this,mTableTitle);
		tableHelper.addUnits(titleW,titles);
	}
	
	
	private List<String> listTypeData;	
	private void initDropDownListLayout(){
		listTypeData = new ArrayList<String>();
		listTypeData.add("特需门诊");
		listTypeData.add("专家门诊");
		listTypeData.add("普通门诊");
		mChargTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Tools.showDropList(ChargeStandardActivity.this, mChargTextView, listTypeData, new Tools.ItemSelectListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						new RequestData().execute(position);
					}
				});
			}
		});
	}
	
	public void onClickBack(View view) {
		this.finish();
	}
	
	class RequestData extends AsyncTask<Integer, Integer, List<List<String>>>{

		@Override
		protected List<List<String>> doInBackground(Integer... params) {
			List<List<String>> beans = new ChargingBean().getData(params[0]);
			return beans;
		}

		@Override
		protected void onPostExecute(List<List<String>> result) {
			super.onPostExecute(result);
			mTableListAdapter.setData(result);
		}
	}
	
}
