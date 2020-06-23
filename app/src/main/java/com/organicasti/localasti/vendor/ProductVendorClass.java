package com.organicasti.localasti.vendor;

public class ProductVendorClass {

    String ProductName;
    String productID;
    String Rate;
    String Quantity;
    String Description;


    public ProductVendorClass(String productName, String productID, String rate, String quantity,String description) {
        ProductName = productName;
        this.productID = productID;
        Rate = rate;
        Quantity = quantity;
        Description=description;
    }

    public String getDescription() {
        return Description;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getRate() {
        return Rate;
    }

    public void setRate(String rate) {
        Rate = rate;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }



}
