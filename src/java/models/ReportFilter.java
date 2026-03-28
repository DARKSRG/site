package models;

import java.sql.Date;

public class ReportFilter {
    private String reportType; // "all", "by_date", "by_media_type", "by_batch"
    private Date startDate;
    private Date endDate;
    private Integer mediaTypeId;
    private String batchNumber;
    private String format; // "html", "excel", "pdf"
    
    public ReportFilter() {}
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public Integer getMediaTypeId() { return mediaTypeId; }
    public void setMediaTypeId(Integer mediaTypeId) { this.mediaTypeId = mediaTypeId; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}