package com.banking.system.domain;

import org.springframework.stereotype.Component;

@Component
public class Types {
	private String type;
	
	Types() {
		
	}
	
	public Types(String types) {
		this.type = types;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}
