/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.compprog1;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/*
 * @author Lean
 */
public class CompProg1 {
    public static Scanner scanner = new Scanner(System.in); //Scanner Global Declaration
    
    public static double sssDeduct(double gross){ //Reads the CSV file to compare the sent gross to determine SSS deduction
        double deduct = 0; //Declares deduct which will store the calculated deduction
        if(gross > 25000.00){ //Checks if the gross salary exceeds the highest bracket
            gross = 25000.00; //Places a cap on the contribution once the gross salary exceeds the highest bracket
        }
        String fileLine; //Declared string to store the line from CSV
        //Try Catch error handler used
        //Buffered Reader used
        //Used Relative Address of File
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/SSS Contribution Brackets.csv"))) {
            //While loop to read the whole file
            //Simultaneously stores the currently read line to String fileLine
            //Will loop until it reaches the end of the line, where it would equal null
            while ((fileLine = reader.readLine()) != null) {
                String[] bracketDetails = fileLine.split(","); //Split will check the line for "," and will split the line there
                double lower = Double.parseDouble(bracketDetails[0]); //Assigns the lower value of the bracket to lower
                double higher = Double.parseDouble(bracketDetails[1]); //Assigns the higher value of the bracket to higher
                if((lower<=gross)&&(higher>=gross)){ //Checks which bracket the gross salary is in
                    deduct = Double.parseDouble(bracketDetails[2]); //Assigns the found deduction in the CSV file to deduct
                }
            }
        }
        catch (IOException e) { //Catch error handler
            System.err.println("Error reading file: " + e.getMessage()); //Prints error if it occurs
        }
        return deduct; //Returns the deduction
    }
    
    public static double phDeduct(double gross){ //Calculates the PhilHealth tax deduction
        double deduct;  //Declares deduct which will store the calculated deduction
        if(gross<=10000.00){ //Checks if the gross salary is in a lower bracket, which has a lower tax deduction
            deduct = 10000 * 0.015;
        }
        else if(gross>=60000.00){ //Checks if the gross salary exceeds the highest bracket, this places a cap on the tax deduction
            deduct = 60000 * 0.015;
        }
        else{ //If not in the lowest bracket or exceeds the highest bracket, computation proceeds as normal
            deduct = gross * 0.015;
        }
        return deduct; //Return deduction
    }
    
    public static double pagibigDeduct(double gross){ //Calculates the Pag-IBIG tax deduction
        double deduct = 0; //Declares deduct which will store the calculated deduction
        if(gross>=1000&&gross<=1500){ //Checks if the gross salary is in a lower bracket, which has a lower tax deduction
            deduct = gross * 0.01;
        }
        else if(gross>1500){ //Checks if the gross salary is in a higher bracket, which has a higher tax deduction
            deduct = gross * 0.02;
        }
        return Math.min(deduct, 100.00); //Sends either the computed deduction or 100, whichever is lower
    }
    
    public static double taxDeduct(double gross){ //Calculates the Withholding tax deduction by comparing taxable salary to the data in the CSV file
        double deduct = 0; //Declares deduct which will store the calculated deduction
        String fileLine; //Declared string to store the line from CSV
        //Try Catch error handler used
        //Buffered Reader used
        //Used Relative Address of File
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/Withholding Tax Brackets.csv"))) {
            //While loop to read the whole file
            //Simultaneously stores the currently read line to String fileLine
            //Will loop until it reaches the end of the line, where it would equal null
            while ((fileLine = reader.readLine()) != null) {
                String[] bracketDetails = fileLine.split(","); //Split will check the line for "," and will split the line there
                double lower = Double.parseDouble(bracketDetails[0]); //Assigns the first value of the CSV file to lower
                double higher = 0+Double.parseDouble(bracketDetails[1]); //Assigns the second value of the CSV file to higher
                if(gross>=666667){ //Checks if the taxable salary is in the highest bracket
                    higher = gross;
                }
                if((lower<=gross)&&(higher>=gross)){ //Checks which bracket the taxable salary is in
                    deduct = ((gross-lower)*Double.parseDouble(bracketDetails[2]))+Double.parseDouble(bracketDetails[3]); //Calculates the deduction based on where the bracket is and assigns it to deduct
                }
            }
        }
        catch (IOException e) { //Catch error handler
            System.err.println("Error reading file: " + e.getMessage()); //Prints error if it occurs
        }
        return deduct; //Returns the deduction
    }
    
    public static double calcHours(LocalTime in, LocalTime out){ //Calculates the hours worked for the day based on the time logs sent to this method
        LocalTime grace = LocalTime.of(8,10); //Declares the grace period
        LocalTime cutOff = LocalTime.of(17,0); //Declares the cutoff time for the day
        if(in.isBefore(grace)){ //If the person logged in earlier than the grace period, the log-in is considered to be at 8:00(the earliest accepted time)
            in = LocalTime.of(8, 0);
        }
        if(out.isAfter(cutOff)){ //If the person logged out later than the cutoff period, the log-out is considered to be at 17:00(the latest accepted time)
            out = cutOff;
        }
        long minutes = Duration.between(in, out).toMinutes(); //Minutes stores the calculated minutes between the log-in and log-out.
        if(minutes > 60){ //Deducts the 1-hour lunch period
            minutes -= 60;
        }
        double hours = minutes /60.0; //Converts the minutes back into hours
        return Math.min(hours, 8.0); //Sends either the computed hours or 8 hours(Max hours worked for the day), which ever is lower.
    }
    
    
    public static void displayResults(double hoursCutoff1, double hoursCutoff2, double rate, LocalDate month, LocalDate monthCutoff){ //Calls the methods to calculate the deductions, then displays all the results.
        DateTimeFormatter cutoffFormat = DateTimeFormatter.ofPattern("MMMM dd "); //Date Format to display month name
        double sss = sssDeduct((hoursCutoff1 + hoursCutoff2) * rate); //Declares sss that stores the SSS Deduction computed by method sssDeduct
        double ph = phDeduct((hoursCutoff1 + hoursCutoff2) * rate); //Declares ph that stores the PhilHealth Deduction computed by method phDeduct
        double pagibig = pagibigDeduct((hoursCutoff1 + hoursCutoff2) * rate); //Declares pagibig that stores the Pag-IBIG Deduction computed by method pagibigDeduct
        double tax = taxDeduct(((hoursCutoff1 + hoursCutoff2) * rate) - (sss + ph + pagibig)); //Declares tax that stores the Witholding Tax Deduction computed by method taxDeduct
        System.out.println("\nCutoff Date: " + month.format(cutoffFormat.withLocale(Locale.US)) + "to 15"); //Displays the month and the range of the cutoff
        System.out.println("Hours Worked 1st cutoff: " + hoursCutoff1); //Displays the amount of hours computed for the cutoff
        System.out.println("Net Salary(Gross Salary): " + hoursCutoff1 * rate); //Displays the calculated Net Salary(Gross Salary) for the first cutoff
        System.out.println("\nCutoff Date: " + monthCutoff.format(cutoffFormat.withLocale(Locale.US)) + "to " + month.lengthOfMonth()); //Displays the month and the range of the cutoff
        System.out.println("Hours Worked 2nd cutoff: " + hoursCutoff2); //Displays the amount of hours computed for the cutoff
        System.out.println("Gross Salary: " + hoursCutoff2 * rate); //Displays the calculated Gross Salary for the second cutoff
        System.out.println("SSS Deduction: " + sss); //Displays the calculated SSS Deduction
        System.out.println("PhilHealth Deduction: " + ph); //Displays the calculated PhilHealth Deduction
        System.out.println("Pag-IBIG Deduction: " + pagibig); //Displays the calculated Pag-IBIG Deduction
        System.out.println("Tax Deduction: " + tax); //Displays the calculated Withholding Tax Deduction
        System.out.println("Total Deductions: " + (sss + ph + pagibig + tax)); //Displays the calculated government Deductions
        System.out.println("Net Salary: " + ((hoursCutoff2 * rate) - (sss + ph + pagibig + tax))); //Displays the calculated Net Salary
    }
    
    public static void processPayroll(String employeeNumber, double rate){ //Takes the employee number and reads the attendance logs to get the data to process the payroll
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm"); //Hour Format, only 1 H because listed data writes X:00 instead of 0X:00
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy"); //Date Format to read dates from file
        String fileLine; //Declared string to store the line from CSV
        LocalDate month = LocalDate.parse("2024-06-01"); //Declares the start of the first month to check
        LocalDate cutoff = month.plusDays(15); //Declares the start of the first cutoff relative to the first month
        double hoursWorked1 = 0; //Declares where to store the calculated hours for the first cutoff
        double hoursWorked2 = 0; //Declares where to store the calculated hours for the second cutoff
        //Try Catch error handler used
        //Buffered Reader used
        //Used Relative Address of File
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/Attendance Record.csv"))) {
            //While loop to read the whole file
            //Simultaneously stores the currently read line to String fileLine
            //Will loop until it reaches the end of the line, where it would equal null
            while ((fileLine = reader.readLine()) != null) {
                String[] attendanceLogs = fileLine.split(","); //Split will check the line for "," and will split the line there
                if(attendanceLogs[0].contains("Employee")){ //Skips the first loop if the code detects that the line is reading the header
                    continue;
                }
                LocalDate current = LocalDate.parse(attendanceLogs[3], dateFormat); //Assigns the date of the log to LocalDate current
                if (current.isAfter(month.plusMonths(1)) || current.isEqual(month.plusMonths(1))){ //Checks if the current month being worked on is correct, if not it adjusts it
                    displayResults(hoursWorked1,hoursWorked2,rate,month,cutoff); //Displays the results of the previous month before moving on to the next month
                    month = month.plusMonths(1); //Adjusts the current month to the next one
                    cutoff = month.plusDays(15); //Adjusts the current cutoff
                    hoursWorked1 = 0; //Resets hoursWorked1 for the new month
                    hoursWorked2 = 0; //Resets hoursWorked2 for the new month
                }
                if (attendanceLogs[0].contains(employeeNumber)) { //Checks the CSV file for the matching employee number and calls the method "calcHours" to compute the total hours worked
                    if ((current.isEqual(month) || current.isAfter(month)) && current.isBefore(cutoff)) { //Checks if the hours calculated would be added to the first cutoff
                        hoursWorked1 += calcHours(LocalTime.parse(attendanceLogs[4], timeFormat), LocalTime.parse(attendanceLogs[5], timeFormat)); //hoursWorked1 stores all the hours calculated for the first cutoff
                    }
                    if ((current.isEqual(cutoff) || current.isAfter(cutoff)) && current.isBefore(month.plusMonths(1))) { //Checks if the hours calculated would be added to the second cutoff
                        hoursWorked2 += calcHours(LocalTime.parse(attendanceLogs[4], timeFormat), LocalTime.parse(attendanceLogs[5], timeFormat)); //hoursWorked1 stores all the hours calculated for the second cutoff
                    }
                }  
            }
            displayResults(hoursWorked1,hoursWorked2,rate,month,cutoff); //Displays the results for the last month since the code exits the loop before displaying the last month
        }
        catch (IOException e) { //Catch error handler
            System.err.println("Error reading file: " + e.getMessage()); //Prints error if it occurs
        }
    }

    public static double employeeDetails(String employeeNumber){ //Reads the .csv file and searches it
        double hourlyRate = 0; //Declares double to store hourly rate found in Employee Details
        //Try Catch error handler used
        //Buffered Reader used
        //Used Relative Address of File
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/Employee Details.csv"))) {
                String fileLine; //Declares string to handle currently read line by Reader
                //While loop to read the whole file
                //Simultaneously stores the currently read line to String fileLine
                //Will loop until it reaches the end of the line, where it would equal null
                while ((fileLine = reader.readLine()) != null) {
                    String[] employeeDetails = fileLine.split(","); //Split will check the line for "," and will split the line there
                    if(employeeDetails[0].contains(employeeNumber)){ //Checks each line if the thrown employee number matches the data on file
                        //String array declared to handle the splitting of the line
                        //"fileLine" will be split into an array called "employeeDetails[]"
                        System.out.println("----------------------------------\n"); 
                        System.out.println("1. Employee #: " +employeeDetails[0]); //The first item in the CSV file is the employee number, which is displayed here
                        System.out.println("2. Employee Name: " +employeeDetails[2]+ " " +employeeDetails[1]); //The second and third items in the CSV file is the employee name, which is displayed here
                        System.out.println("3. Employee Birthday: " +employeeDetails[3]); //The fourth item in the CSV file is the employee birthday, which is displayed here
                        System.out.println("\n----------------------------------");
                        hourlyRate = Double.parseDouble(employeeDetails[employeeDetails.length-1]); //The last item in the CSV file is the hourly rate, this code parses that into double and stores it into hourlyRate
                    }
                }
               if(hourlyRate == 0){ //Since the employee number was not found, hourly rate stays 0
                   System.out.println("Employee number does not exist");
               }
            }
            catch (IOException e) { //Catch error handler
            System.err.println("Error reading file: " + e.getMessage()); //Prints error if it occurs
        }
        return hourlyRate;
    }
    
    public static void payrollOptions(){
        System.out.println("1. Process Payroll\n2. Exit the program ");       
        String choice1 = scanner.nextLine(); //Asks for payroll_staff choice 1
            switch(choice1){ //Checks payroll_staff choice 1
                case "1" -> { //1. Process Payroll
                    System.out.println("1. One Employee\n2. All Employees\n3. Exit the program ");
                    String choice2 = scanner.nextLine(); //Asks for payroll_staff choice 2
                        switch(choice2){ //Checks payroll_staff choice 2
                            case "1" -> {
                                System.out.println("Enter the employee number: ");
                                String number = scanner.nextLine(); //Asks for the employee number
                                double hourRate = employeeDetails(number); //Stores the hourly rate, sends the employee number
                                if(hourRate >0){ //Searches, Computes, and Displays payroll, throws employee number
                                    processPayroll(number,hourRate); //Process the payroll, sends employee number and hourly rate
                                }
                            }
                            case "2" -> {
                                    for(int number = 10001;number < 10035;number++){ //counter starting at first employee number
                                        double hourRate = employeeDetails("" +number); //Stores the hourly rate, sends the employee number
                                        processPayroll("" +number,hourRate); //Process the payroll, sends employee number and hourly rate
                                    }
                            }
                            case "3" -> System.out.println("Thank you for using the program. ");
                            default -> System.out.println("Incorrect input."); //Wrong input
                        }
                }
                case "2" -> System.out.println("Thank you for using the program. ");
                default -> System.out.println("Incorrect input."); //Wrong input
            }
    }
    
    public static void employeeOptions(){
        System.out.println("1. Enter your employee number\n2. Exit the program ");       
        String choice = scanner.nextLine();//Asks for employee choice
            switch(choice){//Checks employee choice
                case "1" -> {//1. Employee Details
                    System.out.println("Enter your employee number: ");
                    String number = scanner.nextLine();//Asks for employee number
                    employeeDetails(number);//Searches and Displays employee details, throws employee number
                }
                case "2" -> System.out.println("Thank you for using the program. ");
                default -> System.out.println("Incorrect input.");//Wrong input
            }
    }
    
    public static int credentialCheck(){//Checks Credentials
        int user;//Declares usertype to return     
        System.out.println("Username: ");       
        String username = scanner.nextLine();//Asks for username      
        System.out.println("Password: ");       
        String password = scanner.nextLine();//Asks for password
        if(password.equals("12345")){//Checks password
            switch(username){//Checks username
                case "employee" -> user = 1;//Returns 1 for employee
                case "payroll_staff" -> user = 2;//Returns 2 for payroll staff
                default -> user = 3;//Returns 3 for wrong username
            }
        }
        else{//Wrong password
            user = 3;
        }
        return user;
    }
    
    public static void main(String[] args) {
        int userType = credentialCheck();//Checks credentials, throws username and password to method
        switch(userType){//Checks user type
                case 1 -> employeeOptions();//Employee
                case 2 -> payrollOptions();//Payroll Staff
                default -> System.out.println("Incorrect username and/or password.");//Wrong Username/Password
            }
        scanner.close();//Close scanner
    }
}