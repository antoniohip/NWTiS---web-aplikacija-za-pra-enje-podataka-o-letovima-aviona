<%-- 
    Document   : dodavanjeAerodroma
    Created on : May 8, 2021, 10:29:48 AM
    Author     : NWTiS_2
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dodavanje i oduzimanje ovlasti</title>
    </head>
    <body>
        <h2>Dodavanje i oduzimanje ovlasti</h2>
        <form method="GET" action="${pageContext.servletContext.contextPath}/mvc/dodavanjePodrucja/zahtjev">

            <p>Korisnici:</p>
            <select name="korisnik" size='7'>
                <c:forEach items="${korisnici}" var="k">
                    <c:set var="korisnik"value="${odabraniKorisnik}" />
                    <option value="${k.korisnik}">${k.korisnik} - ${k.ime} - ${k.prezime}</option>
                </c:forEach>
            </select>
            <p>Područja</p>
            <select name="podrucje" size="5">            
                <option selected value="administracija">administracija</option>
                <option value="korisnikTrazi">korisnikTrazi</option>
                <option value="pregledAerodroma">pregledAerodroma</option>
                <option value="administracijaAerodroma">administracijaAerodroma</option>
                 <option value="pregledKorisnik">pregledKorisnik</option>
                 <option value="pregledDnevnik">pregledDnevnik</option>
                 <option value="pregledJMS">pregledJMS</option>
                 <option value="pregledAktivnihKorisnika">pregledAktivnihKorisnika</option>
                
                
            </select>

            <button type="submit" name="akcija" value="dodaj">Dodaj</button>
            <button type="submit" name="akcija" value="izbrisi">Izbrisi</button>
            </br>
          
        </form>
        <p>${dohvacanjeKorisnikaGreska}</p>



        
        <ul>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/korisnik/izbornik">Početna</a></li>
        </ul>
    </body>

</html>
