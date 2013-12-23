package com.neteast.clouddisk.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.utils.DataHelpter;

public class SearchActivity extends ActivityGroup {
	TextView searchResult = null;
	private int index;
	private int type ;
	private String childtype="";
	LibCloud libCloud;
	EditText keyword = null;
	private int totalPage = 1;
	String key = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchlayout);
		ImageButton button = (ImageButton) findViewById(R.id.searchbutton);
		searchResult = (TextView) findViewById(R.id.searchresulttextview);
		index = this.getIntent().getIntExtra("selectIndex", 1);
		type = this.getIntent().getIntExtra("searchType",1);
		childtype = this.getIntent().getStringExtra("childtype");
		if(childtype==null){
			childtype="";
		}
		libCloud = LibCloud.getInstance(this);
		keyword = (EditText) findViewById(R.id.searchkeyword);
		keyword.requestFocus();
		Timer timer = new Timer(); //设置定时器
		timer.schedule(new TimerTask() {
		@Override
			public void run() { //弹出软键盘的代码
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(keyword, InputMethodManager.RESULT_SHOWN);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		}, 300); //设置300毫秒的时长
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Editable text = keyword.getText();
				if (text != null) {
					key = text.toString().trim();
				}
				SearchAsync searcher = new SearchAsync();
				searcher.execute(1,18);
			}
		});
	}

	class SearchAsync extends AsyncTask<Integer, Integer, List<DataInfo>> {

		@Override
		protected List<DataInfo> doInBackground(Integer... params) {
		//	List<List<DataInfo>> list = new ArrayList<List<DataInfo>>();
			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();//下载
			//List<Map<String, Object>> dataList1 = new ArrayList<Map<String, Object>>();//上传
			try {
				System.out.println(" SearchActivity SearchAsync index = " + index + "key" + key + "type"+type + "childtype = " + childtype);
				//libCloud.Login("gaox@test.com", "123456");
				if(type ==2){
					dataList = (List<Map<String, Object>>) libCloud
							.Get_search_list(1, index, key,childtype).get("download");//下载
				}else if(type==1){
					dataList = (List<Map<String, Object>>) libCloud
							.Get_search_list(2, index, key,childtype).get("filelist");//上传
				}else if(type == 3){/**/
					Map<String, Object> map = libCloud.Get_search_list(3, index, key,childtype);
					if(map!=null && map.get("code").equals("1")){
						dataList = (List<Map<String, Object>>) map.get("datalist");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println(" SearchActivity SearchAsync result size  = " + dataList.size() );
			
			Map<String, Object> m;
			if(type == 3){
				if(index == 5){
					m = DataHelpter.fillData(dataList,true);
				}else{
					m = DataHelpter.fillData(dataList,null, params);
				}
			}else if(type ==2){
				m = DataHelpter.fillDownloadData(dataList, params);
			}
			else{
				 m= DataHelpter.fillData(dataList,false);
			}
			if(m!=null){
				//list.add((List<DataInfo>)m.get("result"));
				return (List<DataInfo>)m.get("result");
			}else{
				//list.add(new ArrayList<DataInfo>());
				return new ArrayList<DataInfo>();
			}
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			if (result.size() > 0) {
				Intent intent = new Intent();
				//intent.putExtra("download", (Serializable) result.get(0));
				//intent.putExtra("upload", (Serializable) result.get(1));
				intent.putExtra("result", (Serializable) result);
				intent.putExtra("key", key);
				intent.putExtra("searchType", type);
				intent.putExtra("index", index);
				 InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); 
	        	 View view = SearchActivity.this.getCurrentFocus(); 
	        	 if (view != null){
	        		  imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	        	 }
				SearchActivity.this.setResult(1, intent);
				SearchActivity.this.finish();
			} else {
				searchResult.setText("没有搜索到相关内容");
			}
		}
	}
}
