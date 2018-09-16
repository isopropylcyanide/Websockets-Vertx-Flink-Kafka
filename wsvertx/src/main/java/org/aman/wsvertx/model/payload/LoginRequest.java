package org.aman.wsvertx.model.payload;

public class LoginRequest {

	private String username;

	private String password;

	private String senderId;

	private String requestUrl;

	public LoginRequest() {
	}

	public LoginRequest(String username, String password, String senderId, String requestUrl) {
		this.username = username;
		this.password = password;
		this.senderId = senderId;
		this.requestUrl = requestUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	@Override
	public String toString() {
		return "LoginRequest{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				", senderId='" + senderId + '\'' +
				", requestUrl='" + requestUrl + '\'' +
				'}';
	}
}
