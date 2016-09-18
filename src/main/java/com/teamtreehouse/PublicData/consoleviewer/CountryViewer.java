package com.teamtreehouse.PublicData.consoleviewer;

import com.teamtreehouse.PublicData.controller.CountryController;
import com.teamtreehouse.PublicData.math.Matrix;
import com.teamtreehouse.PublicData.math.Statistics;
import com.teamtreehouse.PublicData.model.Country;
import com.teamtreehouse.PublicData.model.Country.CountryBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CountryViewer {
    private CountryController controller;
    private BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public CountryViewer(CountryController controller) {
        this.controller = controller;
    }

    public void run() {
        String choice = "";
        do {
            System.out.printf("%n%n");
            try {
                choice = promptForAction();

                switch (choice) {

                    case "data":
                        showData();
                        break;

                    case "stats":
                        showStats();
                        break;

                    case "add":
                        // Get the data for the new country
                        System.out.printf("Enter the name of the new country: ");
                        String countryAddName = promptForString();
                        if (countryAddName.isEmpty()) {
                            break;
                        }

                        System.out.printf("Enter the internet users per 100 (leave blank for NULL): ");
                        Double usersAdd = promptForDouble();

                        System.out.printf("Enter the adult literacy rate (leave blank for NULL): ");
                        Double rateAdd = promptForDouble();

                        // Create the new country
                        Country country = new CountryBuilder(countryAddName)
                            .withAdultLiteracyRate(rateAdd)
                            .withInternetUsers(usersAdd)
                            .build();

                        // Now, get a unique code
                        List<String> allCodes = controller.findAllCountryCodes();
                        String code = "";
                        do {
                            System.out.printf("Enter a unique three-letter country code: ");
                            code = promptForString().toUpperCase();
                        } while (allCodes.contains(code) && !code.isEmpty());
                        if (code.isEmpty()) {
                            break;
                        }
                        country.setCode(code);

                        // Add the country
                        controller.addCounty(country);
                        break;

                    case "edit":
                        showData();
                        System.out.printf("%nEnter the code of the country to edit: ");
                        String countryEditCode = promptForString().toUpperCase();

                        // Find this country by its name
                        Country countryEdit = controller.findCountryByCode(countryEditCode);
                        if (null == countryEdit) {
                            System.out.printf("No country found with the code '%s'", countryEditCode);
                            break;
                        }

                        // Show the data for this country and allow the user to edit
                        System.out.println("Enter new data (press enter for NULL)");

                        // Update the name
                        System.out.printf("Name (%s): ", countryEdit.getName());
                        String newName = promptForString();
                        if (!newName.isEmpty()) {
                            countryEdit.setName(newName);
                        } else {
                            System.out.println("Name cannot be null.  Keeping old name");
                        }

                        // Update the internet users
                        System.out.printf("Internet Users (%f): ", countryEdit.getInternetUsers());
                        Double newUsers = promptForDouble();
                        countryEdit.setInternetUsers(newUsers);

                        // Update the adult literacy rate
                        System.out.printf("Adult Literacy Rate (%f): ", countryEdit.getAdultLiteracyRate());
                        Double newRate = promptForDouble();
                        countryEdit.setAdultLiteracyRate(newRate);

                        // Update the DB
                        controller.updateCountry(countryEdit);
                        break;

                    case "delete":
                        showData();
                        System.out.printf("%nEnter the code of a country to delete: ");
                        String countryCode = promptForString().toUpperCase();
                        if (countryCode.isEmpty()) {
                            break;
                        }
                        controller.deleteCountryByCode(countryCode);
                        break;

                    case "quit":
                        System.out.println("Goodbye!");
                        break;

                    default:
                        System.out.printf("Unknown choice %s. ", choice);
                }
            } catch (IOException ioe) {
                System.out.println("Problem with input.");
                ioe.printStackTrace();
            }

        } while (!choice.equals("quit"));
    }

    public void showData() {
        // Get the list of country entries from the DB
        List<Country> countries = controller.findAllCountries();

        // Print the header to the screen
        System.out.printf("Code Country                             Internet Users          Literacy%n" +
            "-------------------------------------------------------------------------%n");

        // For each country...
        for (Country country : countries) {
            // Print the formatted country to the screen
            System.out.println(formatCountryString(country));
        }

        // Print a footer
        System.out.printf("-------------------------------------------------------------------------%n");

    }

    public void showStats() {
        // Get the list of country entries from the DB
        List<Country> countries = controller.findAllCountries();

        // Form the numeric vectors needed for the statistics
        List<Double> internetUsersList = new ArrayList<>();
        List<Double> adultLiteracyRateList = new ArrayList<>();
        for (Country country : countries) {
            if ( (null != country.getInternetUsers()) & (null != country.getAdultLiteracyRate()) ) {
                internetUsersList.add(country.getInternetUsers());
                adultLiteracyRateList.add(country.getAdultLiteracyRate());
            }
        }

        // The two non-null vectors should now be of the same length
        double[] iuVector = new double[internetUsersList.size()];
        double[] alrVector = new double[adultLiteracyRateList.size()];
        for (int ii=0; ii<internetUsersList.size(); ii++) {
            iuVector[ii] = (double) internetUsersList.get(ii);
            alrVector[ii] = (double) adultLiteracyRateList.get(ii);
        }

        // Calculate the statistics
        double iuMin = Statistics.getMin(iuVector);
        double alrMin = Statistics.getMin(alrVector);

        double iuMax = Statistics.getMax(iuVector);
        double alrMax = Statistics.getMax(alrVector);

        double iuMean = Statistics.getMean(iuVector);
        double alrMean = Statistics.getMean(alrVector);

        double[] linearRegressionCoefficients = Matrix.getNthDegLeastSquares(1,
            iuVector, alrVector);

        double[] quadraticRegressionCoefficients = Matrix.getNthDegLeastSquares(2,
            iuVector, alrVector);


        // Show the statistics
        System.out.printf("Statistics%n----------%n");
        System.out.printf("Minimum Internet Users:    %f %n", iuMin);
        System.out.printf("Maximum Internet Users:    %f %n", iuMax);
        System.out.printf("Mean Internet Users:       %f %n", iuMean);
        System.out.printf("Minimum Literacy Rate:     %f %n", alrMin);
        System.out.printf("Maximum Literacy Rate:     %f %n", alrMax);
        System.out.printf("Mean Literacy Rate:        %f %n", alrMean);
        System.out.printf("Correlation Coefficent:    %f %n", linearRegressionCoefficients[1]);
        System.out.printf("Linear Regression Line:    y = %f*x + %f %n", linearRegressionCoefficients[1],
            linearRegressionCoefficients[0]);
        System.out.printf("Quadratic Regression Line: y = %f*x^2 + %f*x + %f %n", quadraticRegressionCoefficients[2],
            quadraticRegressionCoefficients[1], quadraticRegressionCoefficients[0]);
        System.out.printf("%n%n");
    }

    public String promptForAction() throws IOException {
        System.out.printf("Enter a command.  You choices are:%n" +
            "  data   - Show the raw data in a table%n" +
            "  stats  - Show the stats in a table%n" +
            "  add    - Add a country%n" +
            "  edit   - Edit a country%n" +
            "  delete - Delete a country%n" +
            "  quit   - Exit the program%n" +
            "%n");
        return promptForString().toLowerCase();
    }

    public String promptForString() throws IOException {
        return bufferedReader.readLine().trim();
    }

    public Double promptForDouble() throws IOException {
        String doubleAsString = promptForString();
        if (doubleAsString.isEmpty()) {
            return null;
        }
        return Double.parseDouble(doubleAsString);
    }

    private String formatCountryString(Country country) {

        // Get the country name and stats
        String code = country.getCode();
        String name = country.getName();
        String iuString = formNiceNeatNumberString(country.getInternetUsers());
        String alrString = formNiceNeatNumberString(country.getAdultLiteracyRate());

        // Initialize the output string
        String str = code + "  " + name;

        /* Determine the indices where the data starts:
            - The Country field starts at position zero
            - The Internet Users field starts at position 55 - iuString.length()
            - The Literacy field starts at position 73 - alrString.length() */
        str = padWithSpaces(str,55-iuString.length(),name.length()+5);
        str += iuString;
        str = padWithSpaces(str,73-alrString.length(),55);
        str += alrString;

        return str;
    }

    private String formNiceNeatNumberString(Double number) {
        String str = "";
        if (null == number) {
            str = "--";
        } else {
            str = Double.toString(roundToHundreth(number));
            /* Now, we need to make sure there are two digits after the decimal point.
               We're dealing with doubles, so there should always be a decimal point, which will at
               least be followed by a zero if the number would otherwise be an integer. */
            if ( 2 == (str.length() - str.indexOf("."))) {
                str += "0";
            }
        }
        return str;
    }

    private String padWithSpaces(String str, int finalPosition, int startPosition) {
        // Add spaces between the start position and the final position
        for (int ii=0; ii<(finalPosition-startPosition); ii++) {
            str += " ";
        }
        return str;
    }

    private double roundToHundreth(double number) {
        return Math.round(number * 100.0) / 100.0;
    }
}
