package models;

import java.sql.Date;

public class ClientCard {
    private int id;
    private int userId;
    private String lastName;
    private String firstName;
    private String middleName;
    private Date registrationDate;
    private String cardNumber;
    private Date createdAt;
    
    // Конструктор по умолчанию
    public ClientCard() {}
    
    // Конструктор с параметрами
    public ClientCard(int userId, String lastName, String firstName, 
                      String middleName, Date registrationDate, String cardNumber) {
        this.userId = userId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.registrationDate = registrationDate;
        this.cardNumber = cardNumber;
    }
    
    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    
    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    // Полное имя
    public String getFullName() {
        String full = lastName + " " + firstName;
        if (middleName != null && !middleName.isEmpty()) {
            full += " " + middleName;
        }
        return full;
    }
}