package com.rayeye.config;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

public class Config {
	
	public static DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/event?autoReconnect=true&characterEncoding=utf-8");
		return dataSource;
	}

}
