package com.organicasti.localasti.customer;

/*Aditya Kumar
    To easily generate variable number of products for a customer
    Used in CustomerClass
 */
public class CustProduct_Subclass {

    String ProductName;
    String Quantity;
    String Amount;
    String Description;
    String Delivered;
    String productID;

    public boolean isYesterdayProduct() {
        return yesterdayProduct;
    }

    public void setYesterdayProduct(boolean yesterdayProduct) {
        this.yesterdayProduct = yesterdayProduct;
    }

    String customerID;
    boolean yesterdayProduct = false;

    public String getSubscriptionID() {
        return subscriptionID;
    }

    public void setSubscriptionID(String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    String subscriptionID;

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getVendorID() {
        return vendorID;
    }

    public void setVendorID(String vendorID) {
        this.vendorID = vendorID;
    }

    String vendorID;

    public CustProduct_Subclass(String productName, String quantity, String amount, String description, String delivered) {
        ProductName = productName;
        Quantity = quantity;
        Amount = amount;
        Description = description;
        Delivered = delivered;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDelivered() {
        return Delivered;
    }

    public void setDelivered(String delivered) {
        Delivered = delivered;
    }
}