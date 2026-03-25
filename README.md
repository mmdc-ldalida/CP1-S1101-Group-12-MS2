CP1 - MS2 Source Code

Team Details - S1101 Group 12:
Leandro Dalida
-Main Programmer

Juan Miguel Mabatas
-PhilHealth Deductions
-Withholding Tax Deductions
-Bug Testing

Samantha Angel Ng
-SSS Deductions
-Github

May Ann Tomogsoc
-Pag-Ibig Deductions
-Github

Program Details:
Basic Payroll Program

This program reads employee data from CSV files, calculates the total hours worked per payroll cutoff, and displays a simple salary summary including government deductions.

import java.util.Scanner;
-Used for asking inputs from the user to navigate the program

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
-Used to read CSV files to gather data and use it within the program

import java.time.Duration;
import java.time.LocalTime;
-Used to calculate the gathered data from the CSV files, particularly the hours logged

import java.time.LocalDate;
-Used to calculate the gathered data from the CSV files, particularly the date when the data was logged. Also used to set cutoffs.

import java.time.format.DateTimeFormatter;
-Used to help the program recognize the gathered data from the CSV files, the dates and time logs, so that the program could use the data.

import java.util.Locale;
-Used to write the English name of the month for printing.

Project Plan Link:
https://docs.google.com/spreadsheets/d/1WZkjY_GN1RYMNYBOq0_d-EzI1JifEao8W4zGYOV2yDo/edit?usp=sharing

QA Testing Form Link:
https://docs.google.com/spreadsheets/d/1arVlm1RlHZQy5_PL9rpNKZzxg3Bd3EDKuejLH0IKL5s/edit?pli=1&gid=1060320302#gid=1060320302
