package com.rayeye.repo.impl;

import javax.sql.DataSource;

import org.fri.timers.entity.Times2;
import org.fri.timers.entity.Times2Repo;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rayeye.config.Config;
import com.rayeye.dbutils.QueryRunner;

public class DbutilsRepositoryTest2 {
	
	private static Times2Repo repo;
	
	@BeforeClass
	public static void setUp(){
//		DataSource ds = Config.dataSource();
//		QueryRunner  db = new QueryRunner(ds);
//		repo = new Times2Repo();
//		repo.setDb(db);
	}

	@Test
	public void testInsert() {
//		Times2 t = new Times2();
//		t.setId(1L);
//		t.setMonth(10);
//		t.setYear(2010);
//		t.setCreated(100L);
//		
//		repo.insert(t);
//		
//		
//		Times2 t2 = new Times2();
//		t2.setId(2L);
//		t2.setMonth(10);
//		t2.setYear(2010);
//		
//		repo.insertAttrs(t2, "year" , "id");
//		
//		Times2 t3 = new Times2();
//		t3.setId(3L);
//		t3.setMonth(10);
//		t3.setYear(2010);
//		
//		repo.insertExcludeAttrs(t3, "year");
	}


}
