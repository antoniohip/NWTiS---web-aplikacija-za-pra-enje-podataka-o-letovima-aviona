<%-- 
    Document   : korisnikUnos
    Created on : Apr 27, 2021, 7:47:25 PM
    Author     : NWTiS_2
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registracija korisnika</title>
    </head>
    <body>
        <h1>Registracija korisnika</h1>
        <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/registracijaKorisnika">
            <table>
                <tr>
                    <td>Korisničko ime:</td>
                    <td><input type="text" name="korisnik" /></td>
                </tr>
                <tr>
                    <td>Lozinka:</td>
                    <td><input type="password" name="lozinka" /></td>
                </tr>

                <tr>
                    <td>Ime</td>
                    <td><input type="text" name="ime" /></td>
                </tr>
                <tr>
                    <td>Prezime</td>
                    <td><input type="text" name="prezime" /></td>
                </tr>
                <td></td>
                <td><input type="submit" value="Registriraj me" /></td>
                </tr>
            </table>
        </form>
        <p>${greska}</p>
        <ul>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/korisnik/pocetna">Početna</a></li>
        </ul>

    </body>

</html>
