/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.business;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;


abstract public class Accounts implements Serializable {

    private String number;
    private String name;
    private GregorianCalendar openDate;
    private double balance;

    Accounts() {
        this.number = "";
        this.name = "";
        this.openDate = null;
        this.balance = 0;
    }

    Accounts(String number, String name, GregorianCalendar openDate, double balance) {
        this.number = number;
        this.name = name;
        this.openDate = openDate;
        this.balance = balance;
    }

    /**
     * ********************************************************
     * START OF GETTERS AND SETTERS
    ******************************************************
     */
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GregorianCalendar getOpenDate() {
        return openDate;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d");
        String formattedDate = sdf.format(openDate.getTime());
        return formattedDate;
    }

    public void setOpenDate(String dateToBeParsed) {
        String [] dateArray = dateToBeParsed.split("/");
        int year = Integer.parseInt(dateArray[0]);
        int month = Integer.parseInt(dateArray[1])-1;
        int day = Integer.parseInt(dateArray[2]);
        this.openDate = new GregorianCalendar(year,month,day);
    }

    public double getBalance() {

        return balance;
    }
    
    public String getFormattedBalance(){
    NumberFormat nf = NumberFormat.getCurrencyInstance();
    return nf.format(this.balance);
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * ********************************************************
     * START OF METHODS FOR DIFFERENT TRANSACTIONS
    ******************************************************
     */
    public abstract int transferTo(double b);

    public boolean deposit(double depositAmount) {
        boolean testDAmount = false;
        if (depositAmount > 0) {
            testDAmount = true;
        }

        return testDAmount;
    }

    public boolean withdraw(double withdrawAmount) {
        boolean testDAmount = false;
        if (withdrawAmount <= this.balance) {
            testDAmount = true;
        } 
    return testDAmount;

    }

    /**
     * ********************************************************
     * END OF METHODS FOR DIFFERENT TRANSACTIONS
    ******************************************************
     */
}
 

