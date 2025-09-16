package net.juststock.trading.dto;

public class ApiMsg {
	private final String message;

	public ApiMsg(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
