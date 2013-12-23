package com.neteast.cloudtv2.listener;

import java.util.List;

import com.neteast.cloudtv2.bean.Channelbean;

public interface PageListener {
	int getPages();
	List<Channelbean> getPageData(int page);
}
