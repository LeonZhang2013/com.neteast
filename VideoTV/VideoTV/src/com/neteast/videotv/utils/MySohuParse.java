package com.neteast.videotv.utils;

import java.util.regex.Pattern;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-11-5
 */
public class MySohuParse {
	
	public static final String generateUrl(String url) {
		String vid = getVid(url);
		String baseUrl = "http://my.tv.sohu.com/play/m3u8version.do?vid=";
		String a = "" +System.currentTimeMillis();
		String sig = shiftEN(a,new int[]{23, 12, 131, 1321});
		String key = shiftEN(vid,new int[]{23, 12, 131, 1321});
		return baseUrl + vid + "&sig="+sig+ "&key="+key;
	}
	
	private static String getVid(String url) {
		return url.substring(url.lastIndexOf("/")+1);
	}

	private static String shiftEN(String input,int[] signKey) {
    	int t = signKey.length;
    	int n = 0;
    	Pattern pattern = Pattern.compile("[0-9a-zA-Z]");
    	StringBuilder result = new  StringBuilder();
    	for(int index=0,size =input.length(); index<size ; index++){
    		String it = input.substring(index,index+1);
    		if (pattern.matcher(it).matches()) {
				int i = Character.codePointAt(it, 0);
				int s=65,o=26;
				if (i>=97){
	                s = 97;
	            }else if(i<65){
	                s =48;
	                o =10;
	            }
				int u = i-s;
				byte b = (byte) ((u+ signKey[n++ % t]) % o +s);
				result.append(new String(new byte[]{b}));
			}else {
				result.append(it);
			}
    	}
    	return result.toString();
	}
}
