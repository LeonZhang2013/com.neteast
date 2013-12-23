package com.neteast.cloudtv2.activity;

import io.vov.utils.Log;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.R;
import com.neteast.cloudtv2.adapter.FirstLevelGridViewAdapter;
import com.neteast.cloudtv2.bean.Channelbean;
import com.neteast.cloudtv2.listener.PageListener;
import com.neteast.cloudtv2.play.PlayerActivity;
import com.neteast.cloudtv2.tools.MyLog;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-3-25
 */
public class ChannelGridViewActivity extends Fragment{

	private PageListener mPageListener;
	private int mPageIndex;
	
	public static ChannelGridViewActivity getInstance(int pageIndex){
		ChannelGridViewActivity v = new ChannelGridViewActivity();
		Bundle b = new Bundle();
		b.putInt("index", pageIndex);
		v.setArguments(b);
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			mPageListener = (PageListener) activity;
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageIndex = getArguments().getInt("index");
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.channel_grid_view_layout, null);
		final List<Channelbean> pageData = mPageListener.getPageData(mPageIndex);
		GridView mGridView = (GridView) view.findViewById(R.id.grid_view_layout);
		mGridView.setAdapter(new FirstLevelGridViewAdapter(getActivity(),pageData));
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Channelbean bean = pageData.get(position);
				if(checkLocalView(bean)) return;
				if(bean.getM3u8()!=null){ //当点击电视播放时
					String path;
					if(Constant.IS_CH){
						path = Constant.PLAY_PATH+"ch"+bean.getM3u8()+"/stream"+Constant.FLAG;
					}else{
						path = Constant.PLAY_PATH + bean.getM3u8() + Constant.FLAG;
					}
					MyLog.writeLog("bean = "+bean.getName()+"  path = "+path);
					Intent intent = new Intent(getActivity(), PlayerActivity.class);
					intent.putExtra("playPath", path);
					intent.putExtra("dianbo", true);
					startActivity(intent);
				}else if(bean.getContent().contains(".")){
					String path = Constant.DEMAND_PATH + bean.getContent();
					Intent intent = new Intent(getActivity(), VideoGridViewActivity.class);
					intent.putExtra("path", path);
					intent.putExtra("title", bean.getName());
					startActivity(intent);
				}
			}
		});
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	

	private boolean checkLocalView(Channelbean bean){
		String name = bean.getName();
		Intent intent = null;
		if(name.equals("医院介绍")){
			if("http://192.168.49.21:8080/".equals(Constant.HIS)){
				intent = new Intent(getActivity(), ImageViewActivity.class);
				intent.putExtra("service", R.drawable.desc_image_2);
				intent.putExtra("type", "desc");
			}else{
				intent = new Intent(getActivity(), ImageViewActivity.class);
				intent.putExtra("service", R.drawable.desc_image);
				intent.putExtra("type", "null");
			}
		}else if(name.equals("特色专科")){
			intent = new Intent(getActivity(), ImageViewActivity.class);
			intent.putExtra("service", R.drawable.special_image);
		}else if(name.equals("专家介绍")){
			intent = new Intent(getActivity(), ImageViewActivity.class);
			intent.putExtra("service", R.drawable.doctor_image);
		}else if(name.equals("收费标准")){
			intent = new Intent(getActivity(), ChargeStandardActivity.class);
		}else if(name.equals("我的费用")){
			intent = new Intent(getActivity(), MyCostActivity.class);
		}else if(name.equals("检查报告")){
			intent = new Intent(getActivity(), CheckupReportAcitvity.class);
		}else if(name.equals("检验报告")){
			intent = new Intent(getActivity(), AnalysisReportAcitvity.class);
		}
		if(intent!=null){
			startActivity(intent);
			return true;
		}else{
			return false;
		}
	}



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	
}
