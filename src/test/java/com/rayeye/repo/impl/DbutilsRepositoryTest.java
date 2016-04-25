package com.rayeye.repo.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.fri.timers.entity.Times;
import org.fri.timers.entity.TimesRepo;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rayeye.config.Config;
import com.rayeye.dbutils.QueryRunner;
import com.rayeye.utils.M;

public class DbutilsRepositoryTest {
	
	private static TimesRepo repo;
	
	@BeforeClass
	public static void setUp(){
//		DataSource ds = Config.dataSource();
//		QueryRunner  db = new QueryRunner(ds);
//		repo = new TimesRepo();
//		repo.setDb(db);
	}

//	@Test
	public void testInsert() {
		Times t = new Times();
//		t.setId(1L);
		t.setMonth(10);
		t.setYear(2010);
		
		repo.insert(t);
		
		repo.insertAttrs(t, "year");
		
		repo.insertExcludeAttrs(t, "year");
	}

//	@Test
	public void testUpdate() {
		Times t = new Times();
		t.setId(1L);
		t.setMonth(10);
		t.setYear(2014);
		repo.update(t);
		
		
		Times t2 = new Times();
		t2.setId(2L);
		t2.setMonth(12);
		t2.setYear(2015);
		repo.updateAttrs(t2, "year");
		
		
		Times t3 = new Times();
		t3.setId(3L);
		t3.setMonth(12);
		t3.setYear(2015);
		repo.updateExcludeAttrs(t3, "year");
	}

//	@Test
	public void testRemove() {
		
		
		Times t = new Times();
		t.setId(6L);
		t.setMonth(10);
		t.setYear(2014);
		repo.remove(t);
		
		repo.removeById(7);
		
		
		Times t3 = new Times();
		t3.setId(8L);
		t3.setMonth(12);
		t3.setYear(2015);
		repo.removeByAttrs(t3, "year");
	}

	@Test
	public void testFindById() {
		
//		Times t = repo.findById(1);
//		
//		assertEquals(t.getId(), 1L);
//		
//		Times t2 = repo.findByIdExcludeAttrs(1L, "year");
//		
//		System.out.println(t2.getId());
//		System.out.println(t2.getYear());
//		
//		List<Times> times = repo.findAll();
//		
//		System.out.println(times.size());
//	
//		List<Times> times2 = repo.find( M.<String,Object>ins().put2("year", 2010));
//		
//		System.out.println(times2.size());
		
	}

	@Test
	public void testFindAll() {
	}

	@Test
	public void testFindSql() {
	}

	@Test
	public void testFindSqlForMap() {
	}

	@Test
	public void testFindUniqueResultStringObjectArray() {
	}

	@Test
	public void testFindUniqueResultStringMapOfStringObject() {
	}

}
