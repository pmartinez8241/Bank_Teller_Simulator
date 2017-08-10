<%-- 
    Document   : deposit
    Created on : Jul 6, 2017, 9:09:16 PM
    Author     : marti
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <script
            src="https://code.jquery.com/jquery-3.2.1.js"
            integrity="sha256-DZAnKJ/6XZ9si04Hgrsxu/8s717jcIzLy3oi35EouyE="
        crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <style>

            table tr{
                height: 100px;

            }

        </style>
    </head>
    <body >
        <div class="container">
            <div class="row">
                <div style="float: none" class="col-md-10 col-md-offset-2 col-lg-10 col-lg-offset-2 center-block">
                    <div class="page-header">
                        <h3 class="text-center">BANK TELLER SIMULATOR</h3>
                    </div>



                    <c:choose>
                        <c:when test="${not empty errors}">
                            <div id="errorsPanel" class="panel panel-danger">
                                <div class="panel-heading text-center">ERROR: AMOUNT WAS NOT FORMATTED PROPERLY</div>
                                <div class="panel-body text-center">
                                    ${errors}
                                </div>
                            </div>
                            <c:set var="errors" value="" scope="session"/>
                        </c:when>
                    </c:choose>
          
                        <form action="./TransactionExecutor" method="POST">
                            <table class="table table-bordered">
                                <TR>
                                    <TD><H4>DEPOSIT</H4></TD>
                                    <TD><H5>*ONLY UP TO $10,000 MAY BE DEPOSITED IN A SINGLE TRANSACTION</H5></TD>
                                </TR>
                                <TR>
                                    <TD><h2>Account Number: </h2></TD>
                                    <TD><input type="text"  name ="accountNumber" class="form-control" value="${selected.number}"  readonly/></TD>

                                </TR>

                                <TR>
                                    <TD><h2>Customer Name: </h2></TD>
                                    <TD><input type="text" id="name" class="form-control" name="customerName" value="${selected.name}" readonly/></TD>

                                </TR>

                                <TR>
                                    <TD><h2>Balance: </h2></TD>
                                    <TD><input type="text"  class="form-control" value="${selected.formattedBalance}" readonly/>
                                        <input type="hidden" name="balance" value="${selected.balance}">
                                    </TD>
                                </TR> 
                                <TR>
                                    <TD><h2>Deposit Amount: </h2></TD>
                                    <TD><input type="text" name="amount" class="form-control" id="bal" /></TD>
                                <input type="hidden" name="amountType" value="DEPOSIT AMOUNT">
                                </TR> 
                            </table>
                            <table>
                                <TR>
                                    <TD><input type="submit" class="btn btn-default" value ="DEPOSIT" name="execute"></TD>
                                    <TD><input type="submit" class="btn btn-danger" value ="CANCEL" name="execute"></TD>
                                </TR>
                            </table>
                        </form>
                 
                </div>
            </div>
        </div>
    </body>
</html>
