package com.neteast.clouddisk.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;

public class AppDataAdapter  extends BaseAdapter{
	private List<DataInfo> list;
	//private LibCloud libCloud;
	private Context context;
	private DataInfo dataInfo;
	private Map<String, DataInfo> appList;
	private Map<String,View> appListState;
	private List<String> installed = null;
	private int mPage=0;
	private int lastPosition = -1;
	//每页显示的Item个数
	public static final int SIZE = Params.RECOMMEND_APP_PER_PAGE_NUM;
	private ImageDownloader2 mImageDownloader2;
	public AppDataAdapter(Context context, List<DataInfo> result, int page,List<String> installApp) {
		this.context = context;
		mPage = page;
		installed = installApp;
		list = new ArrayList<DataInfo>();
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < result.size()) && (i < iEnd)) {
			list.add(result.get(i));
			i++;
		}
		//libCloud = LibCloud.getInstance(context);
		DownLoadApplication download = (DownLoadApplication)context.getApplicationContext();
		mImageDownloader2= download.getImageDownloader();
		appList = ((DownLoadApplication) context.getApplicationContext()) .getAppList();
		
		appListState = ((DownLoadApplication) context.getApplicationContext()) .getDownloadListState();
		//getInstalledApp();
		
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final DataInfo dataInfo = (DataInfo)list.get(position);
		if (convertView == null ){
			convertView = LayoutInflater.from(context).inflate(R.layout.recommendappelementlayout, null);
		}
		if(position == lastPosition) return convertView;
		lastPosition = position;
		TextView textName = (TextView) convertView.findViewById(R.id.textViewName);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
		ImageView appinstallbtn = (ImageView) convertView.findViewById(R.id.appinstallbtn);
		ImageView appopenbtn = (ImageView) convertView.findViewById(R.id.appopenbtn);
		if(mPage ==0){
			//libCloud.DisplayImage(dataInfo.getImage(), imageView);
			mImageDownloader2.download(dataInfo.getImage(), imageView);
		}
		textName.setText(dataInfo.getName());
		appinstallbtn.setTag(dataInfo);
		
		
		if(findPackageFromInstalled(dataInfo.getPackages())){
			appinstallbtn.setVisibility(View.GONE);
			appopenbtn.setVisibility(View.VISIBLE);
			appopenbtn.setTag(dataInfo.getPackages());
		}else if(findPackageDownloaded(dataInfo.getId())){
			appinstallbtn.setImageResource(R.drawable.installbtnstyle);
		}
		
		
		View vv = appListState.get(dataInfo.getId());
		if (vv!= null) {
			appinstallbtn.setVisibility(View.VISIBLE);
			appopenbtn.setVisibility(View.GONE);
			appinstallbtn.setImageResource(R.drawable.downloading);
			appinstallbtn.setClickable(false);
			appListState.put(dataInfo.getId(),appinstallbtn );
		} 

		return convertView;
	}

	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}

	
	/**
	 * 查找应用是否已经安装
	 * @param packageName
	 * @return
	 */
	public boolean findPackageFromInstalled(String packageName){
		boolean flag = false;
		//System.out.println(packageName+"=================");
		if(packageName==null||packageName.equals("")){
			return flag;
		}
		for (Iterator iterator = installed.iterator(); iterator.hasNext();) {
			String str = (String) iterator.next();
			//System.out.println(str+"-------------------------");
			if(packageName.equals(str)){
				flag = true;
				break;
			}
		}
		return flag;
	}
	public boolean findPackageDownloaded(String id){
		boolean flag = false;
		DataInfo di = appList.get(id);
		if(di!=null){
			String url = di.getUrl();
			String Name = url.substring(url.lastIndexOf("/"), url.length());
			File fName = new File(Params.DOWNLOAD_FILE_PATH + Name);
			if (fName.exists()) {
				flag = true;
				return flag;
			}
		}
		return flag;
	}
}
