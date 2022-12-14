package com.service.partner.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Order{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("order_data")
	private List<OrderDataItem> orderData;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Result")
	private String result;

	@SerializedName("Is_approve")
	private String IsApprove;

	public String getResponseCode(){
		return responseCode;
	}

	public List<OrderDataItem> getOrderData(){
		return orderData;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getResult(){
		return result;
	}

	public String getIsApprove() {
		return IsApprove;
	}

	public void setIsApprove(String isApprove) {
		IsApprove = isApprove;
	}
}