package org.foi.nwtis.ahip20.ahip20_aplikacija_3.controller;

import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

@Path("korisnik")
@Controller
public class KorisniciKontroler_1 {

    @Inject
    private Models model;
    @Inject
    ServletContext context;
    @Inject
    HttpServletRequest request;

    @Path("registracijaKorisnika")
    @GET
    @View("korisnikUnos.jsp")
    public void registracija() {
        return;
    }

    @Path("prijavaKorisnika")
    @GET
    @View("korisnikPrijava.jsp")
    public void prijava() {
        return;
    }

    @Path("dodavanjePodrucja")
    @GET
    @View("dodavanjePodrucja.jsp")
    public void dodavanjePodrucja() {
        return;
    }

    @Path("upravljanjeAerodromima")
    @GET
    @View("upravljanjeAerodromima.jsp")
    public void upravljanjeAerodromima() {
        return;
    }

    @Path("slanjeKomande")
    @GET
    @View("slanjeKomande.jsp")
    public void slanjeKomande() {
        return;
    }

    @Path("izbornik")
    @GET
    @View("izbornik.jsp")
    public void vratiSe() {
        return;
    }
    
    @Path("odjava")
    @GET
    @View("index.jsp")
    public void odjaviSe() {
        return;
    }


}
