package com.organicasti.localasti;

public class ReportHolder {

    private String VendorName;
    private String VendorPhoneNumber;
    private String CustomerName;
    private String CustomerPhoneNumber;
    private String ProductName;
    private String ProductPrice;
    private String ProductQuantity;
    private String Revenue;
    private String Description;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public ReportHolder(String vendorName, String vendorPhoneNumber, String revenue) {
        VendorName = vendorName;
        VendorPhoneNumber = vendorPhoneNumber;
        Revenue = revenue;
    }

    public ReportHolder(String vendorName, String vendorPhoneNumber, String customerName,
                        String customerPhoneNumber, String productName, String productPrice,
                        String productQuantity) {
        VendorName = vendorName;
        VendorPhoneNumber = vendorPhoneNumber;
        CustomerName = customerName;
        CustomerPhoneNumber = customerPhoneNumber;
        ProductName = productName;
        ProductPrice = productPrice;
        ProductQuantity = productQuantity;
    }

    public ReportHolder(String vendorName, String customerName, String productName, String productPrice, String productQuantity,String Description) {
        VendorName = vendorName;
        CustomerName = customerName;
        ProductName = productName;
        ProductPrice = productPrice;
        ProductQuantity = productQuantity;
        this.Description = Description;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return CustomerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        CustomerPhoneNumber = customerPhoneNumber;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        ProductQuantity = productQuantity;
    }

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        VendorName = vendorName;
    }

    public String getVendorPhoneNumber() {
        return VendorPhoneNumber;
    }

    public void setVendorPhoneNumber(String vendorPhoneNumber) {
        VendorPhoneNumber = vendorPhoneNumber;
    }

    public String getRevenue() {
        return Revenue;
    }

    public void setRevenue(String revenue) {
        Revenue = revenue;
    }
}
