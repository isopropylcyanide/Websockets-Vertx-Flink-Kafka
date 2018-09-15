package org.aman.wsvertx.model.payload;

public class ApiRequest {

	private String endPoint;

	private String payLoad;

	private String senderId;

	public ApiRequest() {
	}

	public ApiRequest(String endPoint, String payLoad, String senderId) {
		this.endPoint = endPoint;
		this.payLoad = payLoad;
		this.senderId = senderId;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(String payLoad) {
		this.payLoad = payLoad;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	@Override
	public String toString() {
		return "ApiRequest{" +
				" senderId='" + senderId + '\'' +
				",endPoint='" + endPoint + '\'' +
				",payLoad='" + payLoad + '\'' +
				'}';
	}
}
