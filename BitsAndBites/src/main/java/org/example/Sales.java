package org.example;

public class Sales {
    // Attributes
    private int juniorSales;
    private int teenSales;
    private int mediumSales;
    private int bigSales;
    private int familySales;

    // Constants for meal prices
    private static final int JUNIOR_PRICE = 5;
    private static final int TEEN_PRICE = 10;
    private static final int MEDIUM_PRICE = 12;
    private static final int BIG_PRICE = 15;
    private static final int FAMILY_PRICE = 20;

    // Constructors
    public Sales() {
        // Default constructor
        juniorSales = 0;
        teenSales = 0;
        mediumSales = 0;
        bigSales = 0;
        familySales = 0;
    }

    public Sales(int junior, int teen, int medium, int big, int family) {
        // Constructor with 5 integer parameters to set sales counts
        this.juniorSales = junior;
        this.teenSales = teen;
        this.mediumSales = medium;
        this.bigSales = big;
        this.familySales = family;
    }

    public Sales(Sales otherSales) {
        this.juniorSales = otherSales.juniorSales;
        this.teenSales = otherSales.teenSales;
        this.mediumSales = otherSales.mediumSales;
        this.bigSales = otherSales.bigSales;
        this.familySales = otherSales.familySales;
    }

    // Accessor and mutator methods for all attributes
    public int getJuniorSales() {
        return juniorSales;
    }

    public void setJuniorSales(int juniorSales) {
        this.juniorSales = juniorSales;
    }

    // Getter and Setter for teenSales
    public int getTeenSales() {
        return teenSales;
    }

    public void setTeenSales(int teenSales) {
        this.teenSales = teenSales;
    }

    // Getter and Setter for mediumSales
    public int getMediumSales() {
        return mediumSales;
    }

    public void setMediumSales(int mediumSales) {
        this.mediumSales = mediumSales;
    }

    // Getter and Setter for bigSales
    public int getBigSales() {
        return bigSales;
    }

    public void setBigSales(int bigSales) {
        this.bigSales = bigSales;
    }

    // Getter and Setter for familySales
    public int getFamilySales() {
        return familySales;
    }

    public void setFamilySales(int familySales) {
        this.familySales = familySales;
    }

    // Method to add sales for each category
    public void addSales(int junior, int teen, int medium, int big, int family) {
        // Add the specified number of sales to each category
        this.juniorSales += junior;
        this.teenSales += teen;
        this.mediumSales += medium;
        this.bigSales += big;
        this.familySales += family;
    }

    // Method to calculate the total sales value
    public int salesTotal() {
        int total = (juniorSales * JUNIOR_PRICE) +
                (teenSales * TEEN_PRICE) +
                (mediumSales * MEDIUM_PRICE) +
                (bigSales * BIG_PRICE) +
                (familySales * FAMILY_PRICE);
        return total;
    }

    @Override
    public String toString() {
        return juniorSales + " x $5 + " +
                teenSales + " x $10 + " +
                mediumSales + " x $12 + " +
                bigSales + " x $15 + " +
                familySales + " x $20";
    }

    // equals method to compare two Sales objects
    public boolean equals(Sales otherSales) {
        return this.juniorSales == otherSales.juniorSales &&
                this.teenSales == otherSales.teenSales &&
                this.mediumSales == otherSales.mediumSales &&
                this.bigSales == otherSales.bigSales &&
                this.familySales == otherSales.familySales;
    }
}
