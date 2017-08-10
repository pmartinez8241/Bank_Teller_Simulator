/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.business;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 *
 * @author marti
 */
public class CheckingAccount extends Accounts implements AccountConstants,Serializable{
    
    public CheckingAccount() {
        super("", "", null, 0);
    }

    public CheckingAccount(String number, String name, GregorianCalendar opendate, double balance) {
        super(number, name, opendate, balance);
    }
    
    @Override
    public int transferTo(double transferAmount) {
       
       double transferTest = (this.getBalance()-transferAmount)-TRANSFER_FEE;
        int transferStatus=0;
        //BALANCE GREATER THAN $10,000 AND TRANSFER AMOUNT LESS THAN $10,000
        if(this.getBalance()>=CHECKING_BALANCE_THRESHOLD&&transferAmount<this.getBalance())
        {
            transferStatus = 0;
        }
        //BALANCE IS LESS THAN $10,000 AND BALANCE IS GREATER THAN TRANSFER AMOUNT AND IS MORE THAN SERVICE FEE
        else if (this.getBalance()<CHECKING_BALANCE_THRESHOLD&&this.getBalance()>transferAmount&& transferTest>=TRANSFER_FEE)
        {
        transferStatus = 1;
        }
        //TRANSFER AMOUNT GREATER THAN BALANCE AND BALANCE GREATER THAN TRANSFER FEES AND BALANCE IS LESS THAN $10,000
        else if (transferAmount>this.getBalance()&& transferTest<TRANSFER_FEE&&this.getBalance()<CHECKING_BALANCE_THRESHOLD)
        {
        transferStatus = -1;
        }
        else if(this.getBalance()>transferAmount&&transferTest<TRANSFER_FEE&&this.getBalance()<CHECKING_BALANCE_THRESHOLD)
        {
        transferStatus = -3;
        }
        //OVER THRESHOLD BUT BALANCE LESS THAN TRANSFER AMOUNT
        else if (this.getBalance()>=CHECKING_BALANCE_THRESHOLD&&transferAmount>this.getBalance())
        {
        transferStatus = -2;
        }
        System.out.println(transferStatus);
        return transferStatus;
    }

}