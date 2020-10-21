package com.handywedge.converter.tosvg.rest.requests;

public class FWErrorResponse {
	private int status;
	private String message;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public FWErrorResponse(int status, String message) {
		this.status = status;
		this.message = message;
	}
}
