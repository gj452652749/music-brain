package com.gj.app.context;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppContext {
	//1表示train，2表示test
	volatile int netMode=1;

	public int getNetMode() {
		return netMode;
	}

	public void setNetMode(int netMode) {
		this.netMode = netMode;
	}
	

}
