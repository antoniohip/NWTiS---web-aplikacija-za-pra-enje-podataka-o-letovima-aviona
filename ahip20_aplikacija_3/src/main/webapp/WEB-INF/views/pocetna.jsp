<%-- 
    Document   : index.jsp
    Created on : Jun 10, 2021, 7:40:21 PM
    Author     : NWTiS_2
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ahip20_aplikacija_3</title>
    </head>
    <body>
        <h1>ahip20_aplikacija_3</h1>
        <ul>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/korisnik/registracijaKorisnika">Registracija</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/korisnik/prijavaKorisnika">Prijava</a></li>
        </ul>
    </body>
</html>
