package Project;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


    // 1. Abstract Base Class
    abstract class Policy {
        protected String policyId;
        protected String customerName;
        protected double coverageAmount;
        protected double premiumAmount;

        public Policy(String policyId, String customerName, double coverageAmount) {
            this.policyId = policyId;
            this.customerName = customerName;
            this.coverageAmount = coverageAmount;
        }

        public abstract double calculatePremium();

        public void displayPolicy() {
            System.out.println("Policy ID      : " + policyId);
            System.out.println("Customer Name  : " + customerName);
            System.out.println("Coverage Amount: $" + coverageAmount);
            System.out.println("Premium Amount : $" + premiumAmount);
        }

        public String toFileString() {
            return policyId + "," + customerName + "," + coverageAmount + "," + premiumAmount;
        }
    }

    // 2. Health Policy Subclass
    class HealthPolicy extends Policy {
        private int age;
        private boolean hasPreExistingCondition;

        public HealthPolicy(String policyId, String customerName, double coverageAmount, int age, boolean hasPreExistingCondition) {
            super(policyId, customerName, coverageAmount);
            this.age = age;
            this.hasPreExistingCondition = hasPreExistingCondition;
            this.premiumAmount = calculatePremium();
        }

        @Override
        public double calculatePremium() {
            double premium = coverageAmount * 0.05;
            if (age > 40) premium += 200;
            if (hasPreExistingCondition) premium += 300;
            return premium;
        }

        @Override
        public void displayPolicy() {
            super.displayPolicy();
            System.out.println("Type           : Health Policy");
            System.out.println("Age            : " + age);
            System.out.println("Pre-existing   : " + (hasPreExistingCondition ? "Yes" : "No"));
            System.out.println("----------------------------------------");
        }
    }

    // 3. Vehicle Policy Subclass
    class VehiclePolicy extends Policy {
        private String vehicleModel;
        private int vehicleAge;

        public VehiclePolicy(String policyId, String customerName, double coverageAmount, String vehicleModel, int vehicleAge) {
            super(policyId, customerName, coverageAmount);
            this.vehicleModel = vehicleModel;
            this.vehicleAge = vehicleAge;
            this.premiumAmount = calculatePremium();
        }

        @Override
        public double calculatePremium() {
            double premium = coverageAmount * 0.07;
            if (vehicleAge > 5) premium += 150;
            return premium;
        }

        @Override
        public void displayPolicy() {
            super.displayPolicy();
            System.out.println("Type           : Vehicle Policy");
            System.out.println("Vehicle Model  : " + vehicleModel);
            System.out.println("Vehicle Age    : " + vehicleAge + " years");
            System.out.println("----------------------------------------");
        }
    }

    // 4. Policy Manager Class (Handles list, file writing, search, sort, and updates)
    class PolicyManager {
        private ArrayList<Policy> policyList = new ArrayList<>();
        private final String FILE_NAME = "policies.txt";

        public void addPolicy(Policy policy) {
            policyList.add(policy);
            saveAllToFile();
        }

        public void displayAllPolicies() {
            if (policyList.isEmpty()) {
                System.out.println("No policies found in the system.");
                return;
            }
            for (Policy p : policyList) {
                p.displayPolicy();
            }
        }

        public void searchById(String id) {
            boolean found = false;
            for (Policy p : policyList) {
                if (p.policyId.equalsIgnoreCase(id)) {
                    System.out.println("\n--- Policy Found ---");
                    p.displayPolicy();
                    found = true;
                    break;
                }
            }
            if (!found) System.out.println("No policy found with ID: " + id);
        }

        public void searchByName(String name) {
            boolean found = false;
            System.out.println("\n--- Search Results for: " + name + " ---");
            for (Policy p : policyList) {
                if (p.customerName.equalsIgnoreCase(name)) {
                    p.displayPolicy();
                    found = true;
                }
            }
            if (!found) System.out.println("No policies found under customer name: " + name);
        }

        public void updateCoverage(String id, double newCov) {
            boolean updated = false;
            for (Policy p : policyList) {
                if (p.policyId.equalsIgnoreCase(id)) {
                    p.coverageAmount = newCov;
                    p.premiumAmount = p.calculatePremium(); // Recalculate
                    saveAllToFile();
                    System.out.println("Coverage updated successfully! New premium calculated.");
                    updated = true;
                    break;
                }
            }
            if (!updated) System.out.println("Policy ID not found for update.");
        }

        public void deletePolicy(String id) {
            boolean removed = false;
            for (int i = 0; i < policyList.size(); i++) {
                if (policyList.get(i).policyId.equalsIgnoreCase(id)) {
                    policyList.remove(i);
                    removed = true;
                    break;
                }
            }
            if (removed) {
                saveAllToFile();
                System.out.println("Policy deleted successfully.");
            } else {
                System.out.println("Policy ID not found.");
            }
        }

        public void countPolicies() {
            System.out.println("Total Active Policies: " + policyList.size());
        }

        public void totalRevenue() {
            double total = 0;
            for (Policy p : policyList) {
                total += p.premiumAmount;
            }
            System.out.println("Total Premium Revenue: $" + total);
        }

        public void findHighestPremium() {
            if (policyList.isEmpty()) {
                System.out.println("No policies available.");
                return;
            }
            Policy high = policyList.get(0);
            for (Policy p : policyList) {
                if (p.premiumAmount > high.premiumAmount) {
                    high = p;
                }
            }
            System.out.println("\n--- Policy with Highest Premium ---");
            high.displayPolicy();
        }

        public void findLowestPremium() {
            if (policyList.isEmpty()) {
                System.out.println("No policies available.");
                return;
            }
            Policy low = policyList.get(0);
            for (Policy p : policyList) {
                if (p.premiumAmount < low.premiumAmount) {
                    low = p;
                }
            }
            System.out.println("\n--- Policy with Lowest Premium ---");
            low.displayPolicy();
        }

        public void filterByMinCoverage(double min) {
            boolean found = false;
            System.out.println("\n--- Policies with Coverage >= $" + min + " ---");
            for (Policy p : policyList) {
                if (p.coverageAmount >= min) {
                    p.displayPolicy();
                    found = true;
                }
            }
            if (!found) System.out.println("No policies meet this criteria.");
        }

        public void sortPoliciesByPremium() {
            if (policyList.size() < 2) {
                System.out.println("Not enough policies to sort.");
                return;
            }
            // Simple rookie Bubble Sort
            for (int i = 0; i < policyList.size() - 1; i++) {
                for (int j = 0; j < policyList.size() - i - 1; j++) {
                    if (policyList.get(j).premiumAmount > policyList.get(j + 1).premiumAmount) {
                        Policy temp = policyList.get(j);
                        policyList.set(j, policyList.get(j + 1));
                        policyList.set(j + 1, temp);
                    }
                }
            }
            System.out.println("Policies sorted by premium amount (Low to High)!");
            displayAllPolicies();
        }

        public void clearAllPolicies() {
            policyList.clear();
            saveAllToFile();
            System.out.println("All system records cleared!");
        }

        private void saveAllToFile() {
            try (FileWriter writer = new FileWriter(FILE_NAME, false)) {
                for (Policy p : policyList) {
                    writer.write(p.toFileString() + "\n");
                }
            } catch (IOException e) {
                System.out.println("Error saving to file: " + e.getMessage());
            }
        }
    

    // Main Method with 15 options
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PolicyManager manager = new PolicyManager();
        int choice = 0;

        do {
            System.out.println("\n================ INSURANCE POLICY SYSTEM ================");
            System.out.println("1. Add Health Policy");
            System.out.println("2. Add Vehicle Policy");
            System.out.println("3. Display All Policies");
            System.out.println("4. Search Policy by ID");
            System.out.println("5. Search Policy by Customer Name");
            System.out.println("6. Update Policy Coverage");
            System.out.println("7. Delete Policy by ID");
            System.out.println("8. Count Total Policies");
            System.out.println("9. Calculate Total Premium Revenue");
            System.out.println("10. View Policy with Highest Premium");
            System.out.println("11. View Policy with Lowest Premium");
            System.out.println("12. Filter Policies by Minimum Coverage");
            System.out.println("13. Sort Policies by Premium (Low to High)");
            System.out.println("14. Clear All Records");
            System.out.println("15. Exit");
            System.out.print("Enter your choice (1-15): ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter Policy ID: ");
                        String hid = scanner.nextLine();
                        System.out.print("Enter Customer Name: ");
                        String hname = scanner.nextLine();
                        System.out.print("Enter Coverage Amount: ");
                        double hcov = scanner.nextDouble();
                        System.out.print("Enter Age: ");
                        int age = scanner.nextInt();
                        System.out.print("Has pre-existing condition? (true/false): ");
                        boolean cond = scanner.nextBoolean();
                        manager.addPolicy(new HealthPolicy(hid, hname, hcov, age, cond));
                        System.out.println("Health Policy added successfully!");
                        break;

                    case 2:
                        System.out.print("Enter Policy ID: ");
                        String vid = scanner.nextLine();
                        System.out.print("Enter Customer Name: ");
                        String vname = scanner.nextLine();
                        System.out.print("Enter Coverage Amount: ");
                        double vcov = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Enter Vehicle Model: ");
                        String model = scanner.nextLine();
                        System.out.print("Enter Vehicle Age (years): ");
                        int vage = scanner.nextInt();
                        manager.addPolicy(new VehiclePolicy(vid, vname, vcov, model, vage));
                        System.out.println("Vehicle Policy added successfully!");
                        break;

                    case 3:
                        System.out.println("\n--- All Stored Policies ---");
                        manager.displayAllPolicies();
                        break;

                    case 4:
                        System.out.print("Enter Policy ID to Search: ");
                        manager.searchById(scanner.nextLine());
                        break;

                    case 5:
                        System.out.print("Enter Customer Name to Search: ");
                        manager.searchByName(scanner.nextLine());
                        break;

                    case 6:
                        System.out.print("Enter Policy ID to Update: ");
                        String updateId = scanner.nextLine();
                        System.out.print("Enter New Coverage Amount: ");
                        double newCov = scanner.nextDouble();
                        manager.updateCoverage(updateId, newCov);
                        break;

                    case 7:
                        System.out.print("Enter Policy ID to Delete: ");
                        manager.deletePolicy(scanner.nextLine());
                        break;

                    case 8:
                        manager.countPolicies();
                        break;

                    case 9:
                        manager.totalRevenue();
                        break;

                    case 10:
                        manager.findHighestPremium();
                        break;

                    case 11:
                        manager.findLowestPremium();
                        break;

                    case 12:
                        System.out.print("Enter Minimum Coverage Amount: ");
                        double minCov = scanner.nextDouble();
                        manager.filterByMinCoverage(minCov);
                        break;

                    case 13:
                        manager.sortPoliciesByPremium();
                        break;

                    case 14:
                        System.out.print("Are you sure you want to clear all records? (yes/no): ");
                        if (scanner.nextLine().equalsIgnoreCase("yes")) {
                            manager.clearAllPolicies();
                        } else {
                            System.out.println("Action cancelled.");
                        }
                        break;

                    case 15:
                        System.out.println("Exiting program. Good luck with your review!");
                        break;

                    default:
                        System.out.println("Invalid choice! Choose between 1 and 15.");
                }
            } else {
                System.out.println("Please enter a valid number.");
                scanner.next();
            }
        } while (choice != 15);

        scanner.close();
    }
}


