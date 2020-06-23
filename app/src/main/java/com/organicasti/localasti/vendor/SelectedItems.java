package com.organicasti.localasti.vendor;

public class SelectedItems {
    String customerID;
    String vendorID;
    String productID;
    String subscriptionID;

    public String getSubscriptionID() {
        return subscriptionID;
    }

    public void setSubscriptionID(String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getVendorID() {
        return vendorID;
    }

    public String getProductID() {
        return productID;
    }

    public SelectedItems(String customerID, String vendorID, String productID) {
        this.customerID = customerID;
        this.vendorID = vendorID;
        this.productID = productID;
    }
}