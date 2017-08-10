/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.controllers;

import bank.business.CheckingAccount;
import bank.data.AccountsDB;
import com.amazonaws.services.storagegateway.model.NFSFileShareDefaults;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author marti
 */
public class TransactionExecutor extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //WHAT ACTION NEEDS TO BE PERFORMED
        String performThis = req.getParameter("execute");
        //ACCOUNT NUMBER OF ACCOUNT TO EXECUTE THE ACTION ON
        String accountNumber = req.getParameter("accountNumber");
        //DOLLARS TO CHANGE THE ACCOUNT BY
        String amount = req.getParameter("amount");
        //ACCOUNT NUMBER OF THE ACCOUNT OF TRANSFEREE
        String transferAccountNumber = req.getParameter("transferNumber");
        //OBJECT THAT IS SET LATER TO COMPARE ACCOUNTS
        CheckingAccount checking = null;
        //RESULT OF INTERACTION WITH DATABASE TRUE = SUCCESSFUL INTERACTRION FALSE = FAILED INTERACTION
        boolean result = false;
        //FOR SUCCESS MESSAGE IF TRANSACTION IS COMPLETED
        String success = "";
        //FOR ERROR MESSAGE IF TRANSACTION FAILED
        String errors = "";
        //GET HTTP SESSION
        HttpSession session = req.getSession();
        //GET ACCOUNT NUMBERS
        int[] account = (int[]) session.getAttribute("accountNumbers");
        //FOR FORMATTIN NUMBER VALUES
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        /*
        *
        *IF PERFORM THIS EQUALS CANCEL BACK TO INDEX.JSP
        *
         */
        if (performThis.equalsIgnoreCase("CANCEL")) {
            performThis = null;
            accountNumber = null;
            amount = null;
            getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
        }
        if (!(accountNumber.isEmpty()) && !(amount.isEmpty())) {
            //SET CHECKING ACCOUNT VARIABLES
            checking = new CheckingAccount();
            checking.setBalance(Double.parseDouble(req.getParameter("balance")));
            checking.setName(req.getParameter("customerName"));
            checking.setNumber(accountNumber);
            //VALIDATE AMOUNT
            if (!tryParseDouble(amount)) {
                errors = "THE " + performThis + amount + " AMOUNT WAS NOT PROPERLY FORMATTED EX:(12.50 or $12.50)";
                session.setAttribute("errors", errors);
                req.setAttribute("selected", checking);
                switch (performThis) {
                    case "DEPOSIT":
                        getServletContext().getRequestDispatcher("/deposit.jsp").forward(req, resp);
                        break;
                    case "WITHDRAW":
                        getServletContext().getRequestDispatcher("/withdraw.jsp").forward(req, resp);
                        break;
                    case "TRANSFER":
                        getServletContext().getRequestDispatcher("/transfer.jsp").forward(req, resp);
                        break;
                }
            }
            switch (performThis) {

                case "DEPOSIT":
                    //GET DEPOSIT AMOUNTS
                    double deposit = Double.parseDouble(amount);
                    if (deposit <= 10000) {
                        if (checking.deposit(deposit)) {
                            String oldBalance = checking.getFormattedBalance();
                            double newBalance = checking.getBalance() + deposit;
                            result = AccountsDB.updateAccount(checking.getNumber(), newBalance);

                            if (result) {
                                success = "ACCOUNT " + accountNumber + " AFTER COMPLETION OF THIS TRANSACTION:";
                                success += "<BR>ORIGINAL ACCOUNT BALANCE: " + oldBalance;
                                success += "<BR>AMOUNT DEPOSITED: " + nf.format(deposit);
                                success += "<BR>NEW BALANCE: " + nf.format(newBalance);

                                session.setAttribute("success", success);

                                session.setAttribute("accountNumbers", account);

                                resp.sendRedirect("./index.jsp");

                            }
                        }

                    } else {
                        errors = "ONLY 10,000 MAY BE DEPOSITED IN A SINGLE TRANSACTION";
                        session.setAttribute("errors", errors);
                        req.setAttribute("selected", checking);
                        getServletContext().getRequestDispatcher("/deposit.jsp").forward(req, resp);
                    }
                    break;
                case "WITHDRAW":
                    double withdraw = Double.parseDouble(amount);
                    if (checking.withdraw(withdraw)) {
                        String oldBalance = checking.getFormattedBalance();
                        double newBalance = checking.getBalance() - withdraw;
                        result = AccountsDB.updateAccount(checking.getNumber(), newBalance);
                        if (result) {
                            success = "RESULTS FROM THE COMPLETION OF THIS TRANSACTION:";
                            success += "<BR>ORIGINAL ACCOUNT BALANCE: " + oldBalance;
                            success += "<BR>AMOUNT WITHDRAWN: " + nf.format(withdraw);
                            success += "<BR>NEW BALANCE: " + nf.format(newBalance);

                            session.setAttribute("success", success);

                            session.setAttribute("accountNumbers", account);

                            resp.sendRedirect("./index.jsp");
                        }
                    }
                    break;
                case "TRANSFER":
                    double transferAmount = Double.parseDouble(amount);
                    //GET ACCOUNT INFORMATION FOR THE TRANSFER ACCOUNT
                    CheckingAccount transferToAccount = AccountsDB.getCheckingAccount(Integer.parseInt(transferAccountNumber));
                    //GET STATUS INT INORDER TO SEE IF FEES APPLY OR IF THERE IS ENOUGH FUNDS FOR TRANSFER
                    int transferResult = checking.transferTo(transferAmount);
                    //FOR SUCCESS STRING
                    String oldTransfereeBalance = transferToAccount.getFormattedBalance();
                    //FOR SUCCESS STRING
                    String oldTransferorBalance = checking.getFormattedBalance();
                    //BOOLEAN TO CHECK IF TRANSFEREE WAS UPDATED IN DATABASE
                    boolean transfereeResult = false;
                    //FOR CALULATION OF NEW TRANSFEROR BALANCE
                    double newTransferorBalance = 0.0;
                    //FOR CALCULATION OF NEW TRANSFEREE
                    double newTransfereeBalance = 0.00;
                    switch (transferResult) {
                        case 0:
                            //SUBTRACT FROM THE TRANSFEROR BALANCE
                            newTransferorBalance = checking.getBalance() - transferAmount;
                            //UPDATE TRANSFEROR IN DATABASE
                            result = AccountsDB.updateAccount(checking.getNumber(), newTransferorBalance);
                            //ADD TO TRANSFEREE BALANCE
                            newTransfereeBalance = transferToAccount.getBalance() + transferAmount;
                            //UPDATE TRANSFEREE IN DATABASE
                            transfereeResult = AccountsDB.updateAccount(transferToAccount.getNumber(), newTransfereeBalance);

                            if (result && transfereeResult) {
                                success = "RESULTS FROM THE COMPLETION OF THIS TRANSACTION:";
                                success += "<BR>TRANSFEROR ORIGINAL BALANCE: " + oldTransferorBalance;
                                success += "<BR>TRANSFEREE ORIGINAL BALANCE" + oldTransfereeBalance;
                                success += "<BR><BR>AFTER " + nf.format(transferAmount) + " TRANSFER:" + transferResult + " <BR> <BR>";
                                success += "TRANSFEROR NEW BALANCE: " + nf.format(newTransferorBalance);
                                success += "<BR>TRANSFEREE NEW BALANCE" + nf.format(newTransfereeBalance);
                            }
                            session.setAttribute("success", success);
                            resp.sendRedirect("./index.jsp");
                            break;
                        case 1:
                            //SUBTRACT FROM THE TRANSFEROR BALANCE
                            newTransferorBalance = (checking.getBalance() - transferAmount) - 2;
                            //UPDATE TRANSFEROR IN DATABASE
                            result = AccountsDB.updateAccount(checking.getNumber(), newTransferorBalance);
                            //ADD TO TRANSFEREE BALANCE
                            newTransfereeBalance = transferToAccount.getBalance() + transferAmount;
                            //UPDATE TRANSFEREE IN DATABASE
                            transfereeResult = AccountsDB.updateAccount(transferToAccount.getNumber(), newTransfereeBalance);

                            if (result && transfereeResult) {
                                success = "RESULTS FROM THE COMPLETION OF THIS TRANSACTION:";
                                success += "<BR>TRANSFEROR ORIGINAL BALANCE: " + oldTransferorBalance;
                                success += "<BR>TRANSFEREE ORIGINAL BALANCE" + oldTransfereeBalance;
                                success += "<BR><BR>AFTER " + nf.format(transferAmount) + " TRANSFER AND $2.00 SERVICE FEE: <BR> <BR>";
                                success += "TRANSFEROR NEW BALANCE: " + nf.format(newTransferorBalance);
                                success += "<BR>TRANSFEREE NEW BALANCE" + nf.format(newTransfereeBalance);
                            }
                            session.setAttribute("success", success);
                            resp.sendRedirect("./index.jsp");
                            break;
                        case -1:
                            errors = "TRANSFEROR ACCOUNT: " + checking.getNumber() + " HAD A " + checking.getFormattedBalance() + " BALANCE";
                            errors += "<BR>" + checking.getFormattedBalance() + " IS AN INSUFFICIENT BALANCE FOR A " + nf.format(transferAmount) + " TRANSFER AND A $2.00 SERVICE CHARGE" + transferResult;
                            session.setAttribute("errors", errors);
                            resp.sendRedirect("./index.jsp");
                            break;
                        case -2:
                            errors = "TRANSFEROR ACCOUNT: " + checking.getNumber() + " HAD A " + checking.getFormattedBalance() + " BALANCE";
                            errors += "<BR>" + checking.getFormattedBalance() + " IS AN INSUFFICIENT BALANCE FOR A " + nf.format(transferAmount) + " TRANSFER";
                            session.setAttribute("errors", errors);
                            resp.sendRedirect("./index.jsp");
                            break;
                        case -3:
                            errors = "TRANSFEROR ACCOUNT: " + checking.getNumber() + " HAD A " + checking.getFormattedBalance() + " BALANCE";
                            errors += "<BR>WHILE THIS WAS ENOUGH FOR THE TRANSFER, IT WAS NOT ENOUGH FOR THE $2.00 SERVICE FEE";
                            errors += "<BR>TRANSACTION WAS ABORTED";
                            session.setAttribute("errors", errors);
                            resp.sendRedirect("./index.jsp");
                            break;
                    }
                    break;
            }

        } else {
            errors = "AN AMOUNT MUST BE ENTERED";
            session.setAttribute("errors", errors);
            switch (performThis) {

                case "DEPOSIT":
                    getServletContext().getRequestDispatcher("/deposit.jsp").forward(req, resp);
                    break;
                case "WITHDRAW":
                    getServletContext().getRequestDispatcher("/withdraw.jsp").forward(req, resp);
                    break;
                case "TRANSFER":
                    getServletContext().getRequestDispatcher("/transfer.jsp").forward(req, resp);
                    break;
            }
        }

    }

    private boolean tryParseDouble(String amount) {
        if (amount.contains("$")) {
            amount = amount.replace("$", "");
        }
        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
