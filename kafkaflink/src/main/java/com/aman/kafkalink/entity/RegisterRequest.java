package com.aman.kafkalink.entity;

import java.io.Serializable;

public class RegisterRequest implements Serializable {

	private static final long serialVersionUID = -1533359700996484156L;

	private String username;

	private String email;

	private String password;

	private Boolean registerAsAdmin;

	private String senderId;

	public RegisterRequest(String username, String email,
			String password, Boolean registerAsAdmin, String senderId) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.registerAsAdmin = registerAsAdmin;
		this.senderId = senderId;
	}

	public RegisterRequest() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getRegisterAsAdmin() {
		return registerAsAdmin;
	}

	public void setRegisterAsAdmin(Boolean registerAsAdmin) {
		this.registerAsAdmin = registerAsAdmin;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	@Override
	public String toString() {
		return "RegisterRequest{" +
				"username='" + username + '\'' +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				", registerAsAdmin=" + registerAsAdmin +
				", senderId='" + senderId + '\'' +
				'}';
	}
}
