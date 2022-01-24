/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ahip20.ahip20_aplikacija_2.slusaci;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.dretve.PreuzimanjeLetovaAviona;
import org.foi.nwtis.ahip20.ahip20_aplikacija_2.dretve.PreuzimanjeVremena;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.ahip20.vjezba_03.konfiguracije.NeispravnaKonfiguracija;

@WebListener
public class SlusacAplikacije implements ServletContextListener {

    PreuzimanjeLetovaAviona pla;
    PreuzimanjeVremena pv;
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(pla != null && pla.isAlive())
            pla.interrupt();
        if(pv != null && pv.isAlive())
            pv.interrupt();
        ServletContext servletContext = sce.getServletContext();
        servletContext.removeAttribute("Postavke");
        System.out.println("Konfiguracija obrisana!");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext servletContext = sce.getServletContext();

        String putanjaKonfDatoteka = servletContext.getRealPath("WEB-INF")
                + File.separator + servletContext.getInitParameter("konfiguracija");

        PostavkeBazaPodataka pbp = new PostavkeBazaPodataka(putanjaKonfDatoteka);
        try {
            pbp.ucitajKonfiguraciju();
            servletContext.setAttribute("Postavke", pbp);
            pla = new PreuzimanjeLetovaAviona(pbp);
            pla.start();
            pv=new PreuzimanjeVremena(pbp);
            pv.start();
            System.out.println("Konfiguracija ucitana!");
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
