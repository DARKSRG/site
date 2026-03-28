package models;

import java.sql.Date;
import java.sql.Timestamp;

public class DiskExchange {
    private int id;
    private int userId;
    private int mediaTypeId;
    private String mediaTypeName;
    private String title;
    private Integer releaseYear;
    private String diskCondition;
    private Date exchangeDate;
    private String clientInfo;
    private String exchangeNumber;
    private String diskForExchange;
    private String newDisk;
    private Timestamp createdAt;
    /** Для отчёта по всем пользователям (JOIN users) */
    private String ownerUsername;
    private String ownerFullName;

    public DiskExchange() {}

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

    public String getDiskCondition() { return diskCondition; }
    public void setDiskCondition(String diskCondition) { this.diskCondition = diskCondition; }

    public Date getExchangeDate() { return exchangeDate; }
    public void setExchangeDate(Date exchangeDate) { this.exchangeDate = exchangeDate; }

    public String getClientInfo() { return clientInfo; }
    public void setClientInfo(String clientInfo) { this.clientInfo = clientInfo; }

    public String getExchangeNumber() { return exchangeNumber; }
    public void setExchangeNumber(String exchangeNumber) { this.exchangeNumber = exchangeNumber; }

    public String getDiskForExchange() { return diskForExchange; }
    public void setDiskForExchange(String diskForExchange) { this.diskForExchange = diskForExchange; }

    public String getNewDisk() { return newDisk; }
    public void setNewDisk(String newDisk) { this.newDisk = newDisk; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getOwnerFullName() { return ownerFullName; }
    public void setOwnerFullName(String ownerFullName) { this.ownerFullName = ownerFullName; }

    /** Краткая подпись пользователя для таблицы */
    public String getOwnerLabel() {
        if (ownerUsername == null) return "—";
        if (ownerFullName != null && !ownerFullName.trim().isEmpty()) {
            return ownerUsername + " (" + ownerFullName + ")";
        }
        return ownerUsername;
    }
}
