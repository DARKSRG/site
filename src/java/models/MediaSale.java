package models;

import java.sql.Date;
import java.sql.Timestamp;

public class MediaSale {
    private int id;
    private int userId;
    private int mediaReceiptId;
    private String receiptTitle;
    private String mediaTypeName;
    private Date saleDate;
    private int quantitySold;
    private double saleAmount;
    private Double unitPrice;
    private String customerName;
    private String customerPhone;
    private String notes;
    private Timestamp createdAt;

    public MediaSale() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMediaReceiptId() { return mediaReceiptId; }
    public void setMediaReceiptId(int mediaReceiptId) { this.mediaReceiptId = mediaReceiptId; }

    public String getReceiptTitle() { return receiptTitle; }
    public void setReceiptTitle(String receiptTitle) { this.receiptTitle = receiptTitle; }

    public String getMediaTypeName() { return mediaTypeName; }
    public void setMediaTypeName(String mediaTypeName) { this.mediaTypeName = mediaTypeName; }

    public Date getSaleDate() { return saleDate; }
    public void setSaleDate(Date saleDate) { this.saleDate = saleDate; }

    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }

    public double getSaleAmount() { return saleAmount; }
    public void setSaleAmount(double saleAmount) { this.saleAmount = saleAmount; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
