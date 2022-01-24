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
        <title>Prijava korisnika</title>
    </head>
    <body>
        <p>${registracijaUspjeh}</p>
        <h1>Prijava korisnika</h1>
        <form  method="POST" action="${pageContext.servletContext.contextPath}/mvc/prijavaKorisnika">
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
                    <td></td>
                    <td><input type="submit" value="Prijavi me" /></td>
                </tr>
            </table>
        </form>
        <p>${prijavaPoruka}</p>
        <div rendered="${prikaziIzbornik==1}">
          
        </div>
        <ul>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/index">Početna</a></li>
        </ul>
    </body>
</body>
</html>
