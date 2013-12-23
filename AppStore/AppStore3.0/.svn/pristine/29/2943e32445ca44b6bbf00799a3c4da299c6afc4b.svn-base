package com.neteast.data_acquisition;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class MyLog {

	public static void writeLog(String message) {
		Log.i("show", message);
		Date date = new Date(System.currentTimeMillis());
		FileOutputStream os = null;
		try {
			File f = new File(Environment.getExternalStorageDirectory(),"应用仓库.txt");
			os = new FileOutputStream(f, true);
			os.write("\r\n\r\n\r\n========================================================".getBytes());
			os.write("\r\n".getBytes());
			os.write(date.toLocaleString().getBytes());
			os.write("\r\n".getBytes());
			if(message!=null){
				os.write(message.getBytes());
			}else{
				os.write("message is null".getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(os!=null)os.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void deleteLog(){
		File f = new File(Environment.getExternalStorageDirectory(),"VirtaulGPSLog.txt");
		if(f.exists()){
			f.delete();
		}
	}
}
