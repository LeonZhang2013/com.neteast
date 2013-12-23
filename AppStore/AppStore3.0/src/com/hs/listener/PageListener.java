package com.hs.listener;

import java.util.List;
import java.util.Map;

import com.hs.domain.AppBean;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-11-1
 */
public interface PageListener {
	int getPages();
	int getGridViewLayout();
	List<AppBean> getData(int pageIndex);
	BaseAdapter getMyPageAdapter(List<AppBean> list);
	OnItemClickListener getItemListener();
}
