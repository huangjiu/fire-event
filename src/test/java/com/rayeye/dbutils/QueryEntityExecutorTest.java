package com.rayeye.dbutils;

import static org.junit.Assert.*;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.fri.timers.entity.Times;
import org.junit.Test;

import com.rayeye.config.Config;
import com.rayeye.dbutils.handlers.BeanHandler;

public class QueryEntityExecutorTest {

	@Test
	public void testQuery() throws SQLException {
		
		DataSource ds = Config.dataSource();
		QueryRunner  db = new QueryRunner(ds);
		
		Times en = db.query(Times.class).eq("id", 1).uniqueResult();
		
		Times en2 = db.query("select * from times where id = ? ").bind(1, 1).execute(new BeanHandler<>(Times.class));
		
		assertEquals(en.getId(), 1L);
		
		assertEquals(en2.getId(), 1L);
	}

}
