package com.organicasti.localasti.vendor;

import java.util.Date;

public class OrderData {
    String productName, quantity, rateperunit;
    String VendorName, VendorID;
    String isDelivered;
    Date dateofOrder;
    String productDescription;
    String OrderID;

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }


    public OrderData(String productName, String quantity, String rateperunit, Date dateofOrder, String vendorName, String vendorID, String isDelivered ) {
        this.productName = productName;
        this.quantity = quantity;
        this.rateperunit = rateperunit;
        VendorName = vendorName;
        VendorID = vendorID;
        this.isDelivered = isDelivered;
        this.dateofOrder = dateofOrder;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }



    public OrderData(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRateperunit() {
        return rateperunit;
    }

    public void setRateperunit(String rateperunit) {
        this.rateperunit = rateperunit;
    }

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        VendorName = vendorName;
    }

    public String getVendorID() {
        return VendorID;
    }

    public void setVendorID(String vendorID) {
        VendorID = vendorID;
    }

    public String getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(String isDelivered) {
        this.isDelivered = isDelivered;
    }

    public Date getDateofOrder() {
        return dateofOrder;
    }

    public void setDateofOrder(Date dateofOrder) {
        this.dateofOrder = dateofOrder;
    }
}