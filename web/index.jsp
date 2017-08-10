<%-- 
    Document   : index
    Created on : Jul 4, 2017, 10:40:01 PM
    Author     : marti
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>JSP Page</title>
        <link type="text/css" rel="stylesheet" href="css/styles.css">
        <script
            src="https://code.jquery.com/jquery-3.2.1.js"
            integrity="sha256-DZAnKJ/6XZ9si04Hgrsxu/8s717jcIzLy3oi35EouyE="
        crossorigin="anonymous"></script>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <style>
            body{
                margin:auto;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="row">
                <div style="float: none;" class="col-md-10 col-md-offset-2  col-lg-10 col-lg-offset-2 center-block ">
                    <div class="page-header">
                        <h3 class="text-center">BANK TELLER SIMULATOR</h3>
                    </div>
                    <c:choose>
                        <c:when test="${not empty success}">
                            <div class="panel panel-success">
                                <div class="panel-heading text-center">TRANSACTION SUCCESSFULLY COMPLETED</div>
                                <div class="panel-body text-center">
                                    ${success}
                                    <c:set var="success" value="" scope="session"/>
                                </div>
                            </div>
                        </c:when>
                        <c:when test="${not empty errors}">
                            <div id="errorsPanel" class="panel panel-danger">
                                <div class="panel-heading text-center">ERROR</div>
                                <div class="panel-body text-center">
                                    ${errors}
                                </div>
                            </div>
                            <c:set var="errors" value="" scope="session"/>
                        </c:when>
                    </c:choose>
                    <div class="controls" style="">
                        <form action="./BasicBankServlet" method="POST" >
                            <table class="table table-bordered">
                                <TR>
                                    <TD><h2>Account Number: </h2></TD>
                                    <TD><select class="selected form-control " id="chosen" name="accountNumber" >
                                            <option disabled selected value> - SELECT AN ACCOUNT - </option>

                                            <c:forEach items="${accountNumbers}" var="acc" >

                                                <option value="<c:out value="${acc}"/>"><c:out value="${acc}"/></option>



                                            </c:forEach>
                                        </select></TD>
                                </TR> 
                                <TR>
                                    <TD><h2>Open Date: </h2></TD>
                                    <TD><input type="text" class="form-control" id="date" name="openDate"  readonly /></TD>
                                </TR>

                                <TR>
                                    <TD><h2>Customer Name: </h2></TD>
                                    <TD><input type="text" class="form-control" name="customerName" id="name"   readonly/></TD>

                                </TR>

                                <TR>
                                    <TD><h2>Balance: </h2></TD>
                                    <TD><input type="text"  class="form-control" id="bal" readonly/>
                                        <input type="hidden" id="balance" name="balance">
                                    </TD>
                                </TR> 
                            </table>
                            <table class="buttontable">
                                <TR>

                                    <TD><input type="submit" class="btn btn-default" value="DEPOSIT" name="action"></TD>
                                    <TD><input type="submit" class="btn btn-default" value="WITHDRAW" name="action"></TD>
                                    <TD><input type="submit" class="btn btn-default" value="TRANSFER" name="action"></TD>
                                </TR>
                            </table>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <script>
            $(document).ready(
                    function () {
                        $('#chosen').change(
                                function () {
                                    var accountElement = document.getElementById('chosen');
                                    var accountNumber = accountElement.options[accountElement.selectedIndex].value;

                                    console.log(accountNumber);
                                    $.ajax({
                                        url: './BasicBankServlet',
                                        type: 'POST',
                                        data: {'accountNumber': accountNumber, 'action': 'FILLDATA'},
                                        dataType: 'json',
                                        success: function (msg) {

                                            $('#name').val(msg.name);
                                            $('#bal').val(msg.formattedbalance);
                                            $('#balance').val(msg.balance);
                                            $('#date').val(msg.openDate);
                                            $('#errorsPanel').remove();
                                        },
                                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                                            alert(textStatus);

                                        }
                                    })

                                })



                    });

        </script>
    </body>
</html>
