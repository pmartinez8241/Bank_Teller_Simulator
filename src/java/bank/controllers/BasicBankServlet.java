/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.controllers;

import bank.business.CheckingAccount;
import bank.data.AccountsDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author marti
 */
public class BasicBankServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //GET THE ACTION THAT IS GOING TO BE PERFORMED
        String action = req.getParameter("action");
        //GET THE ACCOUNT NUMBER OF THE ACCOUNT THAT THE ACTION IS GOING TO BE EXECUTED ON
        String accountNumber = req.getParameter("accountNumber");
        //GET DYNAMODB OBJECT FROM SESSION
        HttpSession session = req.getSession();
        //CHECKING ACCOUNT OBJECT THAT WILL BE SET UNLESS ACTION IS FILLDATA
        CheckingAccount selectedAccount = null;
        //IF THERE IS A PROBLEM
        String fail = "";
        //AN ACTION AND ATLEST 1 ACCOUNT NUMBER IS ALWAYS NEEDED
        //CREATE CHECKING ACCOUNT OBJECT TO SEND TO NEXT PAGE
        if (!action.equals("FILLDATA")) {
            if (accountNumber != null) {
                double balance = Double.parseDouble(req.getParameter("balance"));
                selectedAccount = new CheckingAccount();
                selectedAccount.setBalance(balance);
                selectedAccount.setName(req.getParameter("customerName"));
                selectedAccount.setOpenDate(req.getParameter("openDate"));
                selectedAccount.setNumber(accountNumber);
            } else {
                fail = "An account must be selected before you can " + action;
                req.setAttribute("errors", fail);
                getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
            }
        }

        switch (action.toUpperCase()) {

            //PRINTS JSON RESPONSE FOR AJAX REQUEST
            case "FILLDATA":

                PrintWriter pw = resp.getWriter();

                CheckingAccount currentlySelected = AccountsDB.getCheckingAccount(Integer.parseInt(accountNumber));

                pw.print(jsonString(currentlySelected));

                break;
            case "DEPOSIT":

                //SEND THE SELECTEDACCOUNT OBJECT TO DEPOSIT.JSP
                req.setAttribute("selected", selectedAccount);
                getServletContext().getRequestDispatcher("/deposit.jsp").forward(req, resp);

                break;
            case "WITHDRAW":
                //SEND THE SELECTEDACCOUNT OBJECT TO WITHDRAW.JSP
                req.setAttribute("selected", selectedAccount);
                getServletContext().getRequestDispatcher("/withdraw.jsp").forward(req, resp);
                break;
            case "TRANSFER":
                //SEND THE SELECTEDACCOUNT OBJECT TO TRANFER.JSP
                req.setAttribute("selected", selectedAccount);
                getServletContext().getRequestDispatcher("/transfer.jsp").forward(req, resp);
                break;
            default:
                fail = "An input value for ACTION was changed by user and is not valid</BR>";
                fail += "Please do not change input Values of any BUTTONS or read only fields";
                req.setAttribute("errors", fail);
                getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
                break;
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Get current session if it exists
        HttpSession session = req.getSession();

        //ARRAYLIST FULL OF CHECKING ACCOUNTS OR NULL
        int[] accounts = (int[]) session.getAttribute("accountNumbers");

        if (accounts == null) {
            accounts = AccountsDB.getAccountsData();
        }

        session.setAttribute("accountNumbers", accounts);

        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);

    }

    private JsonObject jsonString(CheckingAccount checking) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String formattedBalance = nf.format(checking.getBalance());
        JsonObject jsonFormat = Json.createObjectBuilder()
                .add("name", checking.getName())
                .add("formattedbalance", formattedBalance)
                .add("balance", checking.getBalance())
                .add("openDate", checking.getFormattedDate())
                .build();

        return jsonFormat;
    }

}
