package com.neteast.clouddisk.utils;

import java.util.Comparator;

public class OrderComparator implements Comparator<String>{

	@Override
	public int compare(String lhs, String rhs) {
		System.out.println("&&&&&&&&&&&&");
		String [] strs1 = lhs.split("-");
		String [] strs2 = rhs.split("-");
		int a = Integer.parseInt(strs1[0]);
		int b = Integer.parseInt(strs2[0]);
		return a-b;
		
	}

}
