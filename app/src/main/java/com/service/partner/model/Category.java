package com.service.partner.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Category{

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("Catlist")
	private List<CatlistItem> catlist;

	@SerializedName("ResponseMsg")
	private String responseMsg;

	@SerializedName("Result")
	private String result;

	public String getResponseCode(){
		return responseCode;
	}

	public List<CatlistItem> getCatlist(){
		return catlist;
	}

	public String getResponseMsg(){
		return responseMsg;
	}

	public String getResult(){
		return result;
	}
}