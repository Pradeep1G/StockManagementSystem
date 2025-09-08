package com.ofss;

public class CustomerAssetDTO {
    private int customerId;
    private String firstName;
    private String lastName;
    private long phoneNumber;
    private String emailId;
    private double profit;
    private double assetsWorth; // <-- computed column
	public CustomerAssetDTO(int customerId, String firstName, String lastName, long phoneNumber, String emailId,
			double profit, double assetsWorth) {
		super();
		this.customerId = customerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.emailId = emailId;
		this.profit = profit;
		this.assetsWorth = assetsWorth;
	}
	public CustomerAssetDTO() {
		// TODO Auto-generated constructor stub
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public long getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public double getProfit() {
		return profit;
	}
	public void setProfit(double profit) {
		this.profit = profit;
	}
	public double getAssetsWorth() {
		return assetsWorth;
	}
	public void setAssetsWorth(double assetsWorth) {
		this.assetsWorth = assetsWorth;
	}

    // getters/setters
}
