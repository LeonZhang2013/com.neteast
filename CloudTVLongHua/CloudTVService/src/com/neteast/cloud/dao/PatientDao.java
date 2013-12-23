package com.neteast.cloud.dao;

import com.neteast.cloud.domain.PatientBean;

public interface PatientDao {
	
	void add(PatientBean p) throws Exception;
	
	PatientBean find(String username,String password) throws Exception;
	
}
