package com.neteast.clouddisk.receiver;

import java.io.File;
import java.util.List;

import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DownLoadApplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class InstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
    	String pack = intent.getDataString().substring(8);
    	AppDao dao = AppDao.getInstance(null);
		Integer id = dao.getAppIdByPackage(pack);
		if(id==null){
			return ;
		}
		dao.updateAppStatusAndPackage(1, pack, id);
		if (id != null) {
			List<DataInfo> list = dao.getAppById(id);
				if(list.size()>0){
					String url = ((DataInfo)list.get(0)).getUrl();
					int strIndex = url.lastIndexOf("/");
					String fileName = url.substring(strIndex + 1);
					File file = new File(Params.DOWNLOAD_FILE_PATH + fileName);
					if (file.exists()) {
						file.delete();
					}
				}
		}
		DownLoadApplication da = (DownLoadApplication)context.getApplicationContext();
		DataInfo di = new DataInfo();
		di.setId(id+"");
		di.setStatus(1);
		di.setPackages(pack);
		da.getAppList().put(id+"", di);
		da.updateBtn(id+"");
    }
}
