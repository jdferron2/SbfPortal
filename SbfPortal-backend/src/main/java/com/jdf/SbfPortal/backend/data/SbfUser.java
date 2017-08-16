package com.jdf.SbfPortal.backend.data;

public class SbfUser {
	private Integer userId;
	private String  password;
	private String email;
	private String userName;
	private String role;
	
	public SbfUser(int userId, String userName,  String password, String email, String role) {
		this.setPassword(password);
		this.setUserId(userId);
		this.setUserName(userName);
		this.setEmail(email);
		this.setRole(role);
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
