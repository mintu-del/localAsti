package com.organicasti.localasti;

import androidx.annotation.Keep;

/**
 *Sai Gopal
 *Used to Hold Product Details
 */

@Keep
public class ProductDetails  {

    private String Name;
    private String ProductQuantity;
    private String ProductPrice;
    private int MinPackingQuantity;
    private String productID;
    private String vendorID;
    private String description;
    private String Quantity;
    private String ProductAndQuantity;


    public ProductDetails(String productAndQuantity) {
        ProductAndQuantity = productAndQuantity;
    }

    public String getProductAndQuantity() {
        return ProductAndQuantity;
    }

    public void setProductAndQuantity(String productAndQuantity) {
        ProductAndQuantity = productAndQuantity;
    }


    // private String QuantityType;

    public String getDescription() {
        return description;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getVendorID() {
        return vendorID;
    }

    public void setVendorID(String vendorID) {
        this.vendorID = vendorID;
    }

    public ProductDetails(String name, String productQuantity, String productPrice, int minPackingQuantity, String vendorID, String productID,String description) {
        Name = name;
        ProductQuantity = productQuantity;
        ProductPrice = productPrice;
        MinPackingQuantity = minPackingQuantity;
        this.vendorID = vendorID;
        this.productID = productID;
        this.description=description;
        //  QuantityType = quantityType;
    }

    public String getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        ProductQuantity = productQuantity;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public ProductDetails(String name, String productQuantity, String productPrice,String Description) {
        Name = name;
        ProductQuantity = productQuantity;
        ProductPrice = productPrice;
        description = Description;
    }


    public int getMinPackingQuantity() {
        return MinPackingQuantity;
    }

    public void setMinPackingQuantity(int minPackingQuantity) {
        MinPackingQuantity = minPackingQuantity;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }



    public ProductDetails(String productName, String name, String quantity, String rate, int i, String vendorID, String productID){

    }

}