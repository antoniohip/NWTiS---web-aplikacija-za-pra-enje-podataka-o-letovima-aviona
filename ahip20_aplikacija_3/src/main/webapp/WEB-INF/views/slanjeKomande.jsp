<%-- 
    Document   : slanjeKomande
    Created on : Jun 13, 2021, 11:22:58 AM
    Author     : NWTiS_2
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Slanje komande</title>
    </head>
    <body>
        <h1>Slanje komande na ahip20_aplikacija_1</h1>
        <form  method="GET" action="${pageContext.servletContext.contextPath}/mvc/slanjeKomande/posalji">
           
                
                    <input type="text" name="komanda" placeholder="upisi komandu" /></br>
                

               
                   
                    </br><input type="submit" value="Pošalji komandu" />
               
           
        </form>
            <p>Odgovor: </p>
        <p>${odgovor}</p>

        <ul>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/korisnik/izbornik">Početna</a></li>
        </ul>
    </body>
</body>
</html>