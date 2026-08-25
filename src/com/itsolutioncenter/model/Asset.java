
package com.itsolutioncenter.model;
import java.util.Date;
/**
 *
 * @author Ahmad Shafiq Amiri
 */

public class Asset {
    private int assetId;
    private String assetTag;
    private String assetName;
    private String category;
    private String serialNumber;
    private Date purchaseDate;
    private double purchaseCost;
    private double currentValue;
    private String status;
    private int assignedTo;
    private String location;
    private String notes;
    private Date createdAt;
   
    // Constructors
    public Asset() {}
   
    public Asset(String assetTag, String assetName, String category) {
        this.assetTag = assetTag;
        this.assetName = assetName;
        this.category = category;
        this.status = "available";
        this.createdAt = new Date();
    }
   
    // Getters and Setters
    public int getAssetId() { return assetId; }
    public void setAssetId(int assetId) { this.assetId = assetId; }
   
    public String getAssetTag() { return assetTag; }
    public void setAssetTag(String assetTag) { this.assetTag = assetTag; }
   
    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }
   
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
   
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
   
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
   
    public double getPurchaseCost() { return purchaseCost; }
    public void setPurchaseCost(double purchaseCost) { this.purchaseCost = purchaseCost; }
   
    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }
   
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
   
    public int getAssignedTo() { return assignedTo; }
    public void setAssignedTo(int assignedTo) { this.assignedTo = assignedTo; }
   
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
   
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
   
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
   
    @Override
    public String toString() {
        return assetTag + " - " + assetName + " (" + category + ")";
    }
}