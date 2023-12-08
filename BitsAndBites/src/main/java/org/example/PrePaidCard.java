package org.example;

public class PrePaidCard {
    // Attributes
    private String cardType;
    private String cardHolderID;
    private int expiryDay;
    private int expiryMonth;

    // Constructors
    public PrePaidCard() {
        // Default constructor
        cardType = "";
        cardHolderID = "";
        expiryDay = 0;
        expiryMonth = 0;
    }

    public PrePaidCard(String cardType, String cardHolderID, int expiryDay, int expiryMonth) {
        // Constructor with 4 parameters to set initial values
        this.cardType = cardType;
        this.cardHolderID = cardHolderID;
        setExpiryDay(expiryDay);
        setExpiryMonth(expiryMonth);
    }

    // Copy constructor
    public PrePaidCard(PrePaidCard otherPrePaidCard) {
        // Copy constructor with one parameter of type PrePaidCard
        this.cardType = otherPrePaidCard.cardType;
        this.cardHolderID = otherPrePaidCard.cardHolderID;
        this.expiryDay = otherPrePaidCard.expiryDay;
        this.expiryMonth = otherPrePaidCard.expiryMonth;
    }

    // Accessor methods
    public String getCardType() {
        return cardType;
    }

    public String getCardHolderID() {
        return cardHolderID;
    }

    public int getExpiryDay() {
        return expiryDay;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    // Mutator methods with validation
    public void setExpiryDay(int expiryDay) {
        if (expiryDay >= 1 && expiryDay <= 31) {
            this.expiryDay = expiryDay;
        } else {
            this.expiryDay = 0; // Set to 0 for invalid input
        }
    }

    public void setExpiryMonth(int expiryMonth) {
        if (expiryMonth >= 1 && expiryMonth <= 12) {
            this.expiryMonth = expiryMonth;
        } else {
            this.expiryMonth = 0; // Set to 0 for invalid input
        }
    }

    // toString method to display card information
    @Override
    public String toString() {
        String formattedExpiryDay = String.format("%02d", expiryDay);
        String formattedExpiryMonth = String.format("%02d", expiryMonth);
        return cardType +
                " - " + cardHolderID +
                " - " + formattedExpiryDay + "/" + formattedExpiryMonth;
    }

    // equals method to compare two PrePaidCard objects
    public boolean equals(PrePaidCard otherCard) {
        return this.cardType.equals(otherCard.cardType) &&
                this.cardHolderID.equals(otherCard.cardHolderID) &&
                this.expiryDay == otherCard.expiryDay &&
                this.expiryMonth == otherCard.expiryMonth;
    }
}
