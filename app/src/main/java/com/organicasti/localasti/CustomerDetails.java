package com.organicasti.localasti;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

/**
    *Sai Gopal
    *Used to Hold Customer Details
 */
@Keep
public class CustomerDetails  {


    private String CustomerName;
    private String CustomerAddress;
    private String CustomerPhoneNumber;
    private String Amount;
    private String SubscriptionID;
    private String Status;



    public CustomerDetails(String customerName, String customerAddress, String customerPhoneNumber, String amount, String subscriptionID, String status) {
        CustomerName = customerName;
        CustomerAddress = customerAddress;
        CustomerPhoneNumber = customerPhoneNumber;
        Amount = amount;
        SubscriptionID = subscriptionID;
        Status = status;
    }

    public CustomerDetails(String customerName, String amount, String customerPhoneNumber, String subscriptionID) {
        CustomerName = customerName;
        Amount = amount;
        CustomerPhoneNumber = customerPhoneNumber;
        SubscriptionID = subscriptionID;
    }

    public CustomerDetails(String customerName, String customerAddress, String customerPhoneNumber, String subscriptionID,String amount) {
        CustomerName = customerName;
        CustomerAddress = customerAddress;
        CustomerPhoneNumber = customerPhoneNumber;
        SubscriptionID = subscriptionID;
        Amount = amount;
    }

    @NonNull
    @Override
    public String toString() {
        return "CustomerDetails{" +
                "CustomerName='" + CustomerName + '\'' +
                ", Address='" + CustomerAddress + '\'' +
                ", PhoneNumber='" + CustomerPhoneNumber + '\'' +
                '}';
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getSubscriptionID() {
        return SubscriptionID;
    }

    public void setSubscriptionID(String subscriptionID) {
        SubscriptionID = subscriptionID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerAddress() {
        return CustomerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        CustomerAddress = customerAddress;
    }

    public String getCustomerPhoneNumber() {
        return CustomerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        CustomerPhoneNumber = customerPhoneNumber;
    }

    public CustomerDetails(){

    }
}
