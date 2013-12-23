
package com.neteast.androidclient.newscenter.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    
    private static final File LOG_DIR=new File(Environment.getExternalStorageDirectory(), "newscenter/log/");
    
    public final static String LOGTAG = "newscenter";

    public static boolean DEBUG = true;

    static{
        LOG_DIR.mkdirs();
    }
    
    public static void i(String log) {
        if (DEBUG) {
            Log.i(LOGTAG, log);
        }
    }

    public static void e(String log) {
        if (DEBUG) {
            Log.e(LOGTAG, log);
        }
    }

    public static void e(String log, Exception ex) {
        if (DEBUG) {
            Log.e(LOGTAG, log, ex);
        }
    }
    
    public static void e(Exception ex) {
        if (DEBUG) {
            Log.e(LOGTAG, "", ex);
        }
    }
    
    
    
    public static void printLog(String log) {
        i(log);
        print(getDividerLine()+log);
    }
    
    public static void printException(Exception e) {
        e(e);
        String dividerLine = getDividerLine();
        StringBuilder sb = new StringBuilder(dividerLine);
        sb.append(e.toString()).append("\n");
        StackTraceElement[] stackTrace = e.getStackTrace();
        final int N = stackTrace.length;
        for (int i = 0; i < N; i++) {
            sb.append(stackTrace[i].toString()).append("\n");
        }
        print(sb.toString());
    }

    private static String getDividerLine() {
        SimpleDateFormat dividerLineFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        Date date = new Date(System.currentTimeMillis());
        String dividerLine= "\n\n\n\n====================="+dividerLineFormat.format(date)+"=====================\n";
        return dividerLine;
    }
    
    private static void print(String message) {
        File logFile = getLogFile();
        FileWriter writer=null;
        try {
            writer = new FileWriter(logFile, true);
            writer.write(message);
        } catch (IOException e) {}finally{
            if(writer!=null)
                try {
                    writer.close();
                } catch (IOException e) { }
        }
    }
    
    private static File getLogFile() {
        SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时");
        Date date = new Date(System.currentTimeMillis());
        String fileName = fileNameFormat.format(date);
        File logFile = new File(LOG_DIR,fileName + ".txt");
        return logFile;
    }
}
