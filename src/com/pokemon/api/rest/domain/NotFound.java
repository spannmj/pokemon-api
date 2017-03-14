package com.pokemon.api.rest.domain;

public class NotFound {
	
	private String detail;

	public NotFound(String detail) {
		this.detail = detail;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
