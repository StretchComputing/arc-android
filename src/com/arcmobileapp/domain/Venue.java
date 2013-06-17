package com.arcmobileapp.domain;

import java.io.Serializable;

public class Venue implements Serializable {

	private static final long serialVersionUID = 4604170714255213162L;
	private int id;
    private String name;
    private String address;
    private String phone;
    private String website;
    
    public Venue(String name, String address, String phone, String website) {
    	this(-1, name, address, phone, website);
    }
    
    public Venue(int id, String name, String address, String phone, String website) {
    	setId(id);
    	setName(name);
    	setAddress(address);
    	setPhone(phone);
    	setWebsite(website);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
}
