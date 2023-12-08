package org.example;

import java.util.Scanner;

public class PosDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Display the welcome message
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("| Welcome to Oppenheimer BitsAndBites Catering Sales Counter Application        |");
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        // Create PoS objects
        Sales sales1 = new Sales(10, 5, 8, 3, 2);
        PrePaidCard[] cards1 = new PrePaidCard[]{
                new PrePaidCard("Carnivore", "12345", 15, 12),
                new PrePaidCard("Vegetarian", "67890", 10, 9)
        };
        PoS pos1 = new PoS(sales1, cards1);

        Sales sales2 = new Sales(10, 5, 8, 3, 2);
        PrePaidCard[] cards2 = new PrePaidCard[]{
                new PrePaidCard("Carnivore", "54321", 15, 12),
                new PrePaidCard("Vegetarian", "09876", 10, 9)
        };
        PoS pos2 = new PoS(sales2, cards2);

        Sales sales3 = new Sales(8, 6, 3, 3, 5);
        PrePaidCard[] cards3 = new PrePaidCard[]{
                new PrePaidCard("Kosher", "11111", 5, 6),
                new PrePaidCard("Pescatarian", "22222", 7, 8),
                new PrePaidCard("Vigan", "33333", 3, 2)
        };
        PoS pos3 = new PoS(sales3, cards3);

        Sales sales4 = new Sales(5, 3, 7, 2, 1);
        PoS pos4 = new PoS(sales4, new PrePaidCard[0]);

        Sales sales5 = new Sales(5, 3, 7, 2, 1);
        PoS pos5 = new PoS(sales5, new PrePaidCard[0]);

        // Create an array of PoS objects
        PoS[] posArray = {pos1, pos2, pos3, pos4, pos5};

        while (true) {
            displayMenu();

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    displayAllPoSInformation(posArray);
                    break;
                case 2:
                    displaySinglePoSInformation(posArray, scanner);
                    break;
                case 3:
                    listPoSsWithSameTotalSales(posArray);
                    break;
                case 4:
                    listPoSsWithSameSalesCategories(posArray);
                    break;
                case 5:
                    listEqualPoSObjects(posArray);
                    break;
                case 6:
                    addPrepaidCardToPoS(posArray, scanner);
                    break;
                case 7:
                    removePrepaidCardFromPoS(posArray, scanner);
                    break;
                case 8:
                    updatePrepaidCardExpiry(posArray, scanner);
                    break;
                case 9:
                    addSalesToPoS(posArray, scanner);
                    break;
                case 0:
                    System.out.println("Thank you for using Oppenheimer BitsAndBites Catering Sales Counter Application!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Sorry that is not a valid choice. Try again.");
                    break;
            }
        }
    }

    // Display the main menu
    private static void displayMenu() {
        System.out.println("| What would you like to do?                                                    |");
        System.out.println("| 1 >> See the content of all PoSs                                              |");
        System.out.println("| 2 >> See the content of one PoS                                               |");
        System.out.println("| 3 >> List PoSs with same $ amount of sales                                    |");
        System.out.println("| 4 >> List PoSs with same number of Sales categories                           |");
        System.out.println("| 5 >> List PoSs with same $ amount of Sales and same number of prepaid cards   |");
        System.out.println("| 6 >> Add a PrePaiCard to an existing PoS                                      |");
        System.out.println("| 7 >> Remove an existing prepaid card from a PoS                               |");
        System.out.println("| 8 >> Update the expiry date of an existing Prepaid card                       |");
        System.out.println("| 9 >> Add Sales to a PoS                                                       |");
        System.out.println("| 0 >> To quit                                                                  |");
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println();
        System.out.println("Please enter your choice and press <Enter>: ");

    }

    // Display information for all PoS objects
    private static void displayAllPoSInformation(PoS[] posArray) {
        System.out.println("Content of each PoS:\n" +
                "---------------------");
        for (int i = 0; i < posArray.length; i++) {
            System.out.println("PoS #" + (i) + ":");
            System.out.println(posArray[i].toString());
        }
    }

    // Display information for a specific PoS object based on user input
    private static void displaySinglePoSInformation(PoS[] posArray, Scanner scanner) {
        System.out.print("Which PoS you want to see the content of? (Enter number 0 to " + (posArray.length - 1) + "): ");


        while (true) {
            int posNumber = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character
            if (posNumber >= 0 && posNumber <= posArray.length - 1) {
                System.out.println(posArray[posNumber].toString());
                break;
            } else {
                System.out.println("Sorry but there is no PoS number " + posNumber);
                System.out.print("--> Try again: (Enter number 0 to " + (posArray.length - 1) + "): ");
            }
        }
    }

    // List PoSs with the same total sales amount
    private static void listPoSsWithSameTotalSales(PoS[] posArray) {
        System.out.println("List of PoSs with same total $ Sales:\n");
        for (int i = 0; i < posArray.length; i++) {
            for (int j = i + 1; j < posArray.length; j++) {
                if (posArray[i].areSalesEqual(posArray[j])) {
                    System.out.print("\tPoSs " + (i) + " and PoS " + (j) + " both have ");
                    System.out.println(posArray[i].getTotalSales());
                }
            }
        }
        System.out.println();
    }

    // List PoSs with the same number of sales categories
    private static void listPoSsWithSameSalesCategories(PoS[] posArray) {
        System.out.println("List of PoSs with same Sales categories:\n");
        for (int i = 0; i < posArray.length; i++) {
            for (int j = i + 1; j < posArray.length; j++) {
                if (posArray[i].areSalesCategoriesEqual(posArray[j])) {
                    System.out.print("\tPoSs " + (i) + " and PoS " + (j) + " both have ");
                    System.out.println(posArray[i].getSalesBreakdown());
                }
            }
        }
        System.out.println();
    }

    // List PoSs with the same total sales amount and the same number of prepaid cards
    private static void listEqualPoSObjects(PoS[] posArray) {
        System.out.println("List of PoSs with same $ amount of sales and same number of PrePaiCards :\n");
        for (int i = 0; i < posArray.length; i++) {
            for (int j = i + 1; j < posArray.length; j++) {
                if (posArray[i].equals(posArray[j])) {
                    System.out.println("\tPoSs " + (i) + " and " + (j));
                }
            }
        }
        System.out.println();
    }

    // Add a prepaid card to an existing PoS based on user input
    private static void addPrepaidCardToPoS(PoS[] posArray, Scanner scanner) {
        System.out.print("Which PoS to you want to add an PrePaiCard to? (Enter number 0 to " + (posArray.length - 1) + "): ");
        int posNumber = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.println("Please enter the following information so that we may complete the PrePaiCard-");
        if (posNumber >= 0 && posNumber <= posArray.length - 1) {
            System.out.print("--> Type of PrePaiCard (Carnivore, Halal, Kosher, Pescatarian, Vegetarian, Vigan): ");
            String cardType = scanner.nextLine();

            System.out.print("--> Id of the prepaid card owner: ");
            String cardHolderID = scanner.nextLine();
            System.out.print("--> Expiry day number and month (seperate by a space): ");
            String[] expiryDateParts = scanner.nextLine().split(" ");
            int expiryDay = 0;
            int expiryMonth = 0;
            if (expiryDateParts.length != 2) {
                System.out.println("Invalid input. Please enter the expiry date in the format 'day month'.");
            } else {
                try {
                    expiryDay = Integer.parseInt(expiryDateParts[0]);
                    expiryMonth = Integer.parseInt(expiryDateParts[1]);

                    if (expiryDay >= 1 && expiryDay <= 31 && expiryMonth >= 1 && expiryMonth <= 12) {
                        // Valid input
                    } else {
                        System.out.println("Invalid input. Please enter valid day (1-31) and month (1-12).");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter numeric values for day and month.");
                }
            }

            PrePaidCard newCard = new PrePaidCard(cardType, cardHolderID, expiryDay, expiryMonth);
            posArray[posNumber].addPrepaidCard(newCard);
            System.out.println("You now have " + posArray[posNumber].getPrepaidCards().length + " PrePaiCard");
        } else {
            System.out.println("Invalid PoS number. Please try again.");
        }
    }

    // Remove a prepaid card from an existing PoS based on user input
    private static void removePrepaidCardFromPoS(PoS[] posArray, Scanner scanner) {
        System.out.print("Which PoS you want to remove an PrePaiCard from? (Enter number 0 to " + (posArray.length - 1) + "): ");
        int posNumber = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (posNumber >= 1 && posNumber <= posArray.length) {
            PoS pos = posArray[posNumber - 1];

            if (pos.getNumPrepaidCards() > 0) {
                System.out.print("(Enter number 0 to " + (pos.getNumPrepaidCards() - 1) + "): ");
                int cardIndex = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                if (pos.removePrepaidCard(cardIndex)) {
                    System.out.println("PrePaiCard was removed successfully\n");
                } else {
                    System.out.println("Invalid card index. Please try again.");
                }
            } else {
                System.out.println("Sorry that PoS has no PrePaiCards");
            }
        } else {
            System.out.println("Invalid PoS number. Please try again.");
        }
    }

    // Update the expiry date of a prepaid card in an existing PoS based on user input
    private static void updatePrepaidCardExpiry(PoS[] posArray, Scanner scanner) {
        System.out.print("Which PoS do you want to update an PrePaiCard from? (Enter number 0 to " + (posArray.length - 1) + "): ");
        int posNumber = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (posNumber >= 0 && posNumber <= posArray.length - 1) {
            PoS pos = posArray[posNumber];

            if (pos.getNumPrepaidCards() > 0) {
                System.out.print("Which PrePaiCard do you want to update? (Enter number 0 to " + (pos.getNumPrepaidCards() - 1) + "): ");
                int cardIndex = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                if (cardIndex >= 0 && cardIndex < pos.getNumPrepaidCards()) {
                    System.out.print("--> Enter new expiry date day number and month (seperate by a space): ");
                    String[] expiryDateParts = scanner.nextLine().split(" ");
                    int expiryDay = 0;
                    int expiryMonth = 0;
                    if (expiryDateParts.length != 2) {
                        System.out.println("Invalid input. Please enter the expiry date in the format 'day month'.");
                    } else {
                        try {
                            expiryDay = Integer.parseInt(expiryDateParts[0]);
                            expiryMonth = Integer.parseInt(expiryDateParts[1]);

                            if (expiryDay >= 1 && expiryDay <= 31 && expiryMonth >= 1 && expiryMonth <= 12) {
                                // Valid input
                            } else {
                                System.out.println("Invalid input. Please enter valid day (1-31) and month (1-12).");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter numeric values for day and month.");
                        }
                    }

                    pos.updatePrepaidCardExpiry(cardIndex, expiryDay, expiryMonth);
                    System.out.println("Expiry Date updated.");
                } else {
                    System.out.println("Invalid card index. Please try again.");
                }
            } else {
                System.out.println("PoS " + posNumber + " has no prepaid cards to update.");
            }
        } else {
            System.out.println("Invalid PoS number. Please try again.");
        }
    }

    // Add sales to an existing PoS based on user input
    private static void addSalesToPoS(PoS[] posArray, Scanner scanner) {
        System.out.print("Which PoS do you want to add Sales to? (Enter number 0 to " + (posArray.length - 1) + "): ");
        int posNumber = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (posNumber >= 0 && posNumber <= posArray.length - 1) {
            System.out.print("How many junior, teen, medium, big, and family meal menus do you want to add?\n(Enter 5 numbers separated by a space): ");
            String input = scanner.nextLine();
            String[] inputParts = input.split(" ");

            if (inputParts.length == 5) {
                try {
                    int junior = Integer.parseInt(inputParts[0]);
                    int teen = Integer.parseInt(inputParts[1]);
                    int medium = Integer.parseInt(inputParts[2]);
                    int big = Integer.parseInt(inputParts[3]);
                    int family = Integer.parseInt(inputParts[4]);

                    // Add sales to PoS
                    posArray[posNumber].addSalesToPoS(1,1,1,1,1);

                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter numeric values.");
                }
            } else {
                System.out.println("Invalid input. Please enter 5 numbers separated by a space.");
            }
            System.out.println("You now have $" + posArray[posNumber].getTotalSales());
            System.out.println();
        } else {
            System.out.println("Invalid PoS number. Please try again.");
        }
    }
}
