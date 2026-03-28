package models;

import java.sql.Date;

public class MediaReceipt {
    private int id;
    private int userId;
    private int mediaTypeId;
    private String mediaTypeName;
    private String title;
    private Integer releaseYear;
    private int quantity;
    private Date receiptDate;
    private String documentNumber;
    private Integer supplierId;
    private String supplierName;
    private String supplierInfo;
    private String batchNumber;
    private Double totalAmount;
    private String notes;
    private Date createdAt;
    /** Остаток на складе по приходу (для формы продажи), не из БД напрямую */
    private Integer availableStock;
    
    public MediaReceipt() {}
    
    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getMediaTypeId() { return mediaTypeId; }
    public void setMediaTypeId(int mediaTypeId) { this.mediaTypeId = mediaTypeId; }
    
    public String getMediaTypeName() { return mediaTypeName; }
    public void setMediaTypeName(String mediaTypeName) { this.mediaTypeName = mediaTypeName; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public Date getReceiptDate() { return receiptDate; }
    public void setReceiptDate(Date receiptDate) { this.receiptDate = receiptDate; }
    
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    
    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
    
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    
    public String getSupplierInfo() { return supplierInfo; }
    public void setSupplierInfo(String supplierInfo) { this.supplierInfo = supplierInfo; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
}