package com.organicasti.localasti.customer;
/*Aditya Kumar
    Used to Hold Customer Details Along with the products they have ordered
 */

import android.util.Log;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CustomerClass {
    String cust_name;
    String address;
    String phone_no;
    String CustomerID;
    String vendorID;
    List<CustProduct_Subclass> ProductList;

    public String getVendorID() {
        return vendorID;
    }

    public void setVendorID(String vendorID) {
        this.vendorID = vendorID;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public CustomerClass(String cust_name, String address, String phone_no) {
        Log.d(TAG, "CustomerClass Created");
        this.cust_name = cust_name;
        this.address = address;
        this.phone_no = phone_no;
    }

    public CustomerClass(String cust_name, String address, String phone_no, String customerID) {
        this.cust_name = cust_name;
        this.address = address;
        this.phone_no = phone_no;
        CustomerID = customerID;
    }

    public CustomerClass(String cust_name, String address, String phone_no, List<CustProduct_Subclass> ProductList) {
        Log.d(TAG, "CustomerClass Created");
        this.cust_name = cust_name;
        this.address = address;
        this.phone_no = phone_no;
        this.ProductList = ProductList;
    }
    public void setProductList(List<CustProduct_Subclass> productList) {
        ProductList = productList;
    }

    public String getCust_name() {
        return cust_name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getAddress() {
        return address;
    }
    public List<CustProduct_Subclass> getProductList() {
        return ProductList;
    }
}