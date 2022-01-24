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
            <li><a href="${pageContext.servletContext.contextPath}/mvc/dodavanjePodrucja">3.3 Upravljanje područjem</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/upravljanjeAerodromima">3.4 Upravljanje aerodromima</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/slanjeKomande">3.5 Slanje slobodno upisane komande na ahip20_aplikacija_1</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/odjava">Odjava</a></li>
        </ul>
            <p>${porukaIzbornik}</p>
    </body>
</html>
