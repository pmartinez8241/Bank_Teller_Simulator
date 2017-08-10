/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.data;

import bank.business.CheckingAccount;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.TableDescription;


/*
*THIS CLASS GETS THE DATA FROM DYNAMODB
*
*ALL DATA FROM DYNAMODB IS RETURNED IN JSON FORM UNLESS INSTRUCTED OTHERWISE
*
 */
public class AccountsDB {

//ALL REQUEST REQUIRE A TOKEN AQUIRED WHEN YOUR CREDENTIALS ARE VALIDATED
    private static final BasicAWSCredentials credentials = new BasicAWSCredentials("", "");
//DYNAMO DB OBJECT
    private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            //REGION MUST ALWAYS BE INCLUDED
            .withRegion(Regions.US_WEST_2)
            //THE CREDENTIALS ARE NOW SENT TO AWS FOR VALIDATION
            //IF CREDENTIALS ARE WRONG YOU WILL NOT BE ALLOWED TO ACCESS DATA IN DATABASE
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();

    private static final DynamoDB dynamo = new DynamoDB(client);

    /*
 *THIS METHOD WILL GET ALL THE DATA FROM THE 
 *
 *ACCOUNTS TABLE 
     */

    public static int[] getAccountsData() {

        //GETS A DESCRIPION OF TABLE
        TableDescription tableDescription = dynamo.getTable("Accounts").describe();

        //INSTEAD OF SCANNING THE TABLE(COSTLY) I GET A COUNT AND CREATE MY OWN KEYS
        Integer size = Integer.parseInt(tableDescription.getItemCount().toString());

        //I USE AN ARRAY SINCE I KNOW THE SIZE FROM THE ITEM COUNT FROM TABLE DESCRIPTION
        int[] accounts = new int[size];

        int key = 1;
        //HERE I CREATE MY KEYS
        for (int i = 0; i < size; i++) {

            accounts[i] = (1000 + key);
            key++;
        }

        return accounts;
    }

    public static boolean updateAccount(String accountNumber, double newBalance) {
        double accNumb = Double.parseDouble(accountNumber);
        Table table = dynamo.getTable("Accounts");

        UpdateItemSpec update = new UpdateItemSpec()
                .withPrimaryKey("accountNumber", accNumb)
                .withUpdateExpression("set balance = :dollars")
                .withValueMap(new ValueMap()
                        .withNumber(":dollars", newBalance)
                );
        try {
            UpdateItemOutcome outcome = table.updateItem(update);

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public static CheckingAccount getCheckingAccount(int accountNumber) {
        CheckingAccount checking = new CheckingAccount();
        //GET THE TABLE I WANT TO WORK WITH
        Table table = dynamo.getTable("Accounts");
        //GET THE SINGLE ACCOUNT WITH THE accountNumber VARIABLE
        Item item = table.getItem("accountNumber", accountNumber);
        //SET NAME
        checking.setName(item.getString("name"));
        //SET BALANCE
        checking.setBalance(item.getDouble("balance"));
        //SET ACCOUNT NUMBER
        checking.setNumber(String.valueOf(accountNumber));
        //SET OPEN DATE
        checking.setOpenDate(item.getString("openDate"));

        return checking;
    }

}
