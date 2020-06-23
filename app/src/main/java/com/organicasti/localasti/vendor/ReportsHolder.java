package com.organicasti.localasti.vendor;

public class ReportsHolder
{
    private Double Amount;
    private String Revenue;
    private String From;
    private String To;

    private String LastDelivery;
    private String CustomerUID;
    private String Amount1;
    private String Quantity;


    public ReportsHolder(String lastDelivery, String customerUID, String amount1, String productID, String subscriptionID,String quantity) {
        LastDelivery = lastDelivery;
        CustomerUID = customerUID;
        Amount1 = amount1;
        ProductID = productID;
        SubscriptionID = subscriptionID;
        this.Quantity = quantity;
    }



    public String getLastDelivery() {
        return LastDelivery;
    }

    public void setLastDelivery(String lastDelivery) {
        LastDelivery = lastDelivery;
    }

    public String getCustomerUID() {
        return CustomerUID;
    }

    public void setCustomerUID(String customerUID) {
        CustomerUID = customerUID;
    }

    public String getAmount1() {
        return Amount1;
    }

    public void setAmount1(String amount1) {
        Amount1 = amount1;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getSubscriptionID() {
        return SubscriptionID;
    }

    public void setSubscriptionID(String subscriptionID) {
        SubscriptionID = subscriptionID;
    }

    private String ProductID;
    private String SubscriptionID;


    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }


    public String getSubscriptionUID() {
        return SubscriptionUID;
    }

    public void setSubscriptionUID(String subscriptionUID) {
        SubscriptionUID = subscriptionUID;
    }

    private String Name;
    private String SubscriptionUID;

    public ReportsHolder(String from, String to, String name, String subscriptionUID) {
        From = from;
        To = to;
        Name = name;
        SubscriptionUID = subscriptionUID;
    }

    public ReportsHolder(String from, String to, String name) {
        From = from;
        To = to;
        Name = name;
    }


    public ReportsHolder(String revenue, String name) {
        Revenue = revenue;
        Name = name;
    }


    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public String getRevenue() {
        return Revenue;
    }

    public void setRevenue(String revenue) {
        Revenue = revenue;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }


    public ReportsHolder()
    {

    }
}
