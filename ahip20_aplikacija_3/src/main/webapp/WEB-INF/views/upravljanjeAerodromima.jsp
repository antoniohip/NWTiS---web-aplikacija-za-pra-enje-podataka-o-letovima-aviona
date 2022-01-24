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
        <title>Upravljanje aerodromima</title>
    </head>
    <body>
        <h2>Upravljanje aerodromima</h2>
        <form method="GET" action="${pageContext.servletContext.contextPath}/mvc/upravljanjeAerodromima/zahtjev">

            <p>Aerodromi koje pratite:</p>
            <select name="aerodrom" size='4'>
                <c:forEach items="${aerodromi}" var="a">
                    <c:set var="aerodrom"value="${odabraniAerodrom}" />
                    <option value="${a.icao}">${a.icao} - ${a.naziv}</option>
                </c:forEach>
            </select>


            <button type="submit" name="akcija" value="dajKorisnike">Prikaži korisnike koji prate ovaj aerodrom</button>
            <table border="1"> 
                <c:forEach var="k" items="${korisnici}">
                    <tr>
                        <td>${k.korisnik}</td>
                        <td>${k.ime}</td>
                        <td>${k.prezime}</td>
                    </tr>
                </c:forEach>
            </table>
            </br><button type="submit" name="akcija" value="izbrisi">Izbriši</button>
            </br><p>${upravljanjeAerodromimaGreska}</p>
            <p>Filter:</p>
            <input type="text" name="naziv" placeholder="naziv"/>
            <input type="text" name="drzava" placeholder="drzava"/>
            <button type="submit" name="akcija" value="filter">Filtriraj</button>
            </br><select name="sviAerodromi" size='7'>
                <c:forEach items="${sviAerodromi}" var="a">
                    <c:set var="aerodrom"value="${odabraniSviAerodromi}" />
                    <option value="${a.icao}">${a.icao} - ${a.naziv}</option>
                </c:forEach>
            </select></br>
            <button type="submit" name="akcija" value="prati">Prati</button>
        </form>
        </br>

      





        <ul>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/korisnik/izbornik">Početna</a></li>
        </ul>
    </body>

</html>
