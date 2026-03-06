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
    
    public static double sssDeduct(double gross){
        double deduct = 0;
        if(gross > 25000.00){
            gross = 25000.00;
        }
        String data; //Declared string to store the line from CSV
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/SSS Contribution Brackets.csv"))) {
            while ((data = reader.readLine()) != null) {
                String[] details = data.split(",");
                double lower = Double.parseDouble(details[0]);
                double higher = Double.parseDouble(details[1]);
                if((lower<=gross)&&(higher>=gross)){
                    deduct = Double.parseDouble(details[2]);
                }
            }
        }
        catch (IOException e) { //Catch error handler
            System.err.println("Error reading file: " + e.getMessage()); //Prints error if it occurs
        }
        return deduct;
    }
    
    public static double phDeduct(double gross){
        double deduct = 0;
        if(gross<=10000.00){
            deduct = 10000 * 0.015;
        }
        else if(gross>=60000.00){
            deduct = 60000 * 0.015;
        }
        else{
            deduct = gross * 0.015;
        }
        return deduct;
    }
    
    public static double pagibigDeduct(double gross){
        double deduct = 0;
        if(gross>=1000&&gross<=1500){
            deduct = gross * 0.01;
        }
        else if(gross>1500){
            deduct = gross * 0.02;
        }
        return Math.min(deduct, 100.00);
    }
    
    public static double taxDeduct(double gross){
        double deduct = 0;
        String data; //Declared string to store the line from CSV
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/Withholding Tax Brackets.csv"))) {
            while ((data = reader.readLine()) != null) {
                String[] details = data.split(",");
                double lower = Double.parseDouble(details[0]);
                double higher = 0+Double.parseDouble(details[1]);
                if(gross>=666667){
                    higher = gross;
                }
                if((lower<=gross)&&(higher>=gross)){
                    deduct = ((gross-lower)*Double.parseDouble(details[2]))+Double.parseDouble(details[3]);
                }
            }
        }
        catch (IOException e) { //Catch error handler
            System.err.println("Error reading file: " + e.getMessage()); //Prints error if it occurs
        }
        return deduct; 
    }
    
    public static double calcHours(LocalTime in, LocalTime out){
        LocalTime grace = LocalTime.of(8,10);
        LocalTime cutOff = LocalTime.of(17,0);
        if(in.isBefore(grace)){
            in = LocalTime.of(8, 0);
        }
        if(out.isAfter(cutOff)){
            out = cutOff;
        }
        long minutes = Duration.between(in, out).toMinutes();
        if(minutes > 60){
            minutes -= 60;
        }
        double hours = minutes /60.0;
        return Math.min(hours, 8.0);
    }
    
    public static void processPayroll(String employeeNumber, double rate){
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");//Hour Format, only 1 H because listed data writes X:00 instead of 0X:00
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy"); //Date Format to read dates from file
        DateTimeFormatter cutoffFormat = DateTimeFormatter.ofPattern("MMMM dd "); //Date Format to display month name
        String data; //Declared string to store the line from CSV
        String startingDate = "2024-06-01";
        String endDate = "2024-12-31";
        LocalDate month = LocalDate.parse(startingDate);
        while(month.isBefore(LocalDate.parse(endDate))){
        double hoursWorked1 = 0;
        double hoursWorked2 = 0;
        LocalDate cutoff = month.plusDays(15);
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/Attendance Record.csv"))) {
            while ((data = reader.readLine()) != null) {
                if(data.contains(employeeNumber)){
                    String[] details = data.split(",");
                    LocalDate current = LocalDate.parse(details[3], dateFormat);
                    if((current.isEqual(month)||current.isAfter(month))&&current.isBefore(cutoff)){
                        hoursWorked1 += calcHours(LocalTime.parse(details[4], timeFormat),LocalTime.parse(details[5], timeFormat));
                        }
                    if((current.isEqual(cutoff)||current.isAfter(cutoff))&&current.isBefore(month.plusMonths(1))){
                        hoursWorked2 += calcHours(LocalTime.parse(details[4], timeFormat),LocalTime.parse(details[5], timeFormat));
                        }
                    }
                }
            System.out.println("\nCutoff Date: " +month.format(cutoffFormat.withLocale(Locale.US)) + "to 15");
            System.out.println("Hours Worked 1st cutoff: " +hoursWorked1);
            System.out.println("Gross Salary: " +hoursWorked1*rate);
            System.out.println("Net Salary: " +hoursWorked1*rate);
            System.out.println("\nCutoff Date: " +cutoff.format(cutoffFormat.withLocale(Locale.US)) + "to " + month.lengthOfMonth());
            System.out.println("Hours Worked 2nd cutoff: " +hoursWorked2);
            System.out.println("Gross Salary: " +hoursWorked2*rate);
            double sss = sssDeduct((hoursWorked1+hoursWorked2)*rate);
            System.out.println("SSS Deduction: " +sss);
            double ph = phDeduct((hoursWorked1+hoursWorked2)*rate);
            System.out.println("PhilHealth Deduction: " +ph);
            double pagibig = pagibigDeduct((hoursWorked1+hoursWorked2)*rate);
            System.out.println("Pag-IBIG Deduction: " +pagibig);
            double tax = taxDeduct((hoursWorked1+hoursWorked2)*rate);
            System.out.println("Tax Deduction: " +tax);
            System.out.println("Total Deductions: " +(sss+ph+pagibig+tax));
            System.out.println("Net Salary: " +((hoursWorked2*rate)-(sss+ph+pagibig+tax)));
            }
        catch (IOException e) { //Catch error handler
            System.err.println("Error reading file: " + e.getMessage()); //Prints error if it occurs
        }
        month = month.plusMonths(1);
        }
    }

    public static double employeeDetails(String employeeNumber){ //Reads the .csv file and searches it
        double hourlyRate = 0; //Declares double to store hourly rate found in Employee Details
        //Try Catch error handler used
        //Buffered Reader used
        //Used Absolute Address of File
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/Employee Details.csv"))) {
                String data; //Declares string to handle currently read line by Reader
                //While loop to read the whole file
                //Simultaneously stores the currently read line to String data
                //Will loop until it reaches the end of the line, where it would equal null
                while ((data = reader.readLine()) != null) {
                    String[] details = data.split(",");
                    if(details[0].contains(employeeNumber)){ //Checks each line if the thrown employee number matches the data on file
                        //String array declared to handle the splitting of the line
                        //"data" will be split into an array called "details[]"
                        //Split will check the line for "," and will split the line there
                        System.out.println("----------------------------------\n"); 
                        System.out.println("1. Employee #: " +details[0]); //Displays Employee Number
                        System.out.println("2. Employee Name: " +details[2]+ " " +details[1]); //Displays Employee Name, First name then Last name
                        System.out.println("3. Employee Birthday: " +details[3]); //Displays Birthday
                        System.out.println("\n----------------------------------");
                        hourlyRate = Double.parseDouble(details[details.length-1]); //Parses the found string in the last part of the array, which is the hourly rate
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
                                String Number = scanner.nextLine(); //Asks for the employee number
                                double hourRate = employeeDetails(Number); //Stores the hourly rate, sends the employee number
                                if(hourRate >0){ //Searches, Computes, and Displays payroll, throws employee number
                                    processPayroll(Number,hourRate); //Process the payroll, sends employee number and hourly rate
                                }
                            }
                            case "2" -> {
                                    for(int number = 10001;number < 10035;number++){ //counter starting at first employee number
                                        double hourRate =employeeDetails("" +number); //Stores the hourly rate, sends the employee number
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
                    String Number = scanner.nextLine();//Asks for employee number
                    employeeDetails(Number);//Searches and Displays employee details, throws employee number
                }
                case "2" -> System.out.println("Thank you for using the program. ");
                default -> System.out.println("Incorrect input.");//Wrong input
            }
    }
    
    public static int credentialCheck(String username, String password){//Checks Credentials
        String passkey = "12345";//Declare accepted password
        int user;//Declares usertype to return           
        if(passkey.equals(password)){//Checks password
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
        System.out.println("Username: ");       
        String Name = scanner.nextLine();//Asks for username      
        System.out.println("Password: ");       
        String Code = scanner.nextLine();//Asks for password
        int userType = credentialCheck(Name, Code);//Checks credentials, throws username and password to method
        switch(userType){//Checks user type
                case 1 -> employeeOptions();//Employee
                case 2 -> payrollOptions();//Payroll Staff
                default -> System.out.println("Incorrect username and/or password.");//Wrong Username/Password
            }
        scanner.close();//Close scanner
    }
}