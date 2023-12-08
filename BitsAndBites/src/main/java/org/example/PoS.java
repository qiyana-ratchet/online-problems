package org.example;

public class PoS {
    // Attributes
    private Sales sales;
    private PrePaidCard[] prepaidCards;

    // Constructors
    public PoS() {
        // Default constructor
        sales = new Sales();
        prepaidCards = new PrePaidCard[0];
    }

    public PoS(Sales sales, PrePaidCard[] prepaidCards) {
        // Constructor with 2 parameters to set initial values
        this.sales = sales;
        this.prepaidCards = prepaidCards;
    }

    // Accessor methods
    public Sales getSales() {
        return sales;
    }

    public PrePaidCard[] getPrepaidCards() {
        return prepaidCards;
    }

    // Method to check if the total $ value of sales is equal to another PoS
    public boolean areSalesEqual(PoS otherPoS) {
        return this.sales.salesTotal() == otherPoS.sales.salesTotal();
    }

    // Method to check if the number of sales categories is equal to another PoS
    public boolean areSalesCategoriesEqual(PoS otherPoS) {
        return this.sales.equals(otherPoS.sales);
    }

    // Method to get the total $ sales of the PoS
    public int getTotalSales() {
        return sales.salesTotal();
    }

    // Method to get the number of prepaid cards in the PoS
    public int getNumPrepaidCards() {
        return prepaidCards.length;
    }

    // Method to add a new PrePaidCard to the PoS
    public int addPrepaidCard(PrePaidCard card) {
        PrePaidCard[] newCards = new PrePaidCard[prepaidCards.length + 1];
        for (int i = 0; i < prepaidCards.length; i++) {
            newCards[i] = prepaidCards[i];
        }
        newCards[prepaidCards.length] = card;
        prepaidCards = newCards;
        return prepaidCards.length;
    }

    // Method to remove a PrePaidCard from the PoS by index
    public boolean removePrepaidCard(int index) {
        if (index >= 0 && index < prepaidCards.length) {
            PrePaidCard[] newCards = new PrePaidCard[prepaidCards.length - 1];
            for (int i = 0, j = 0; i < prepaidCards.length; i++) {
                if (i != index) {
                    newCards[j++] = prepaidCards[i];
                }
            }
            prepaidCards = newCards;
            return true;
        } else {
            return false;
        }
    }

    // Method to update the expiry date of a PrePaidCard in the PoS
    public void updatePrepaidCardExpiry(int cardIndex, int newExpiryDay, int newExpiryMonth) {
        if (cardIndex >= 0 && cardIndex < prepaidCards.length) {
            prepaidCards[cardIndex].setExpiryDay(newExpiryDay);
            prepaidCards[cardIndex].setExpiryMonth(newExpiryMonth);
        }
    }

    // Method to add sales to the PoS
    public void addSalesToPoS(int junior, int teen, int medium, int big, int family) {
        sales.addSales(junior, teen, medium, big, family);
    }

    // Method to compare two PoS objects based on equality
    public boolean equals(PoS otherPoS) {
        if (!this.sales.equals(otherPoS.sales)) {
            return false; // Sales are not equal
        }

        if (this.prepaidCards.length != otherPoS.prepaidCards.length) {
            return false; // Different number of prepaid cards
        }

        // Compare prepaid cards one by one
        for (int i = 0; i < this.prepaidCards.length; i++) {
            if (!this.prepaidCards[i].equals(otherPoS.prepaidCards[i])) {
                return false; // Prepaid cards are not equal
            }
        }

        return true; // All checks passed, PoS objects are equal
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sales.toString()).append("\n");
        if (prepaidCards.length > 0) {
            for (PrePaidCard card : prepaidCards) {
                sb.append(card.toString()).append("\n");
            }
        } else {
            sb.append("No PrePaiCards\n");
        }
        return sb.toString();
    }

    public String getSalesBreakdown() {
        return sales.toString();
    }
}
