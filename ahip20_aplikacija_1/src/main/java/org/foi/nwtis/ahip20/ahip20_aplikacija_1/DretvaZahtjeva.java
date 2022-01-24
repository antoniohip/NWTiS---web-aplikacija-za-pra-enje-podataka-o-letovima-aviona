package org.foi.nwtis.ahip20.ahip20_aplikacija_1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci.Dnevnik;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci.KorisniciDAO;
import org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci.Korisnik;
import org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci.OvlastiDAO;
import org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci.Sjednica;
import org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci.SpremistePodataka;

public class DretvaZahtjeva extends Thread {

    private Socket uticnica;
    private int port;
    private PostavkeBazaPodataka pbp;
    private String korisnikDnevnik = "";
    private String zahtjevDnevnik = "";
    private long vrijemePrimitka;

    public DretvaZahtjeva(Socket uticnica, int port, PostavkeBazaPodataka pbp) {
        this.uticnica = uticnica;
        this.port = port;
        this.pbp = pbp;
    }

    @Override
    public void run() {

        String zahtjev = "";
        this.vrijemePrimitka = System.currentTimeMillis();
        try {
            ObradiZahtjev();
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Ova metoda šalje odgovor preko aktivne uticnice
     *
     * @param os OutputStream aktivne uticnice
     * @param odgovor String koji se vraća kao odgovor
     * @throws IOException
     */
    private void posaljiOdgovor(OutputStream os, String odgovor) throws IOException {
        os.write(odgovor.getBytes());
        String korisnik = dajKorisnika(this.zahtjevDnevnik);
        String dnevnikOdg = dajStatus(odgovor);
  
        zapisiUDnevnik(korisnik, this.zahtjevDnevnik, dnevnikOdg, this.vrijemePrimitka);

        os.flush();
        uticnica.shutdownOutput();

    }

    /**
     * Metoda u kojoj se primaju zahtjevi prema serveru
     *
     * @return metoda vraća se String u kojem je zapisan zapis koji smo primili
     * @throws IOException
     * @throws InterruptedException
     */
    private String ObradiZahtjev() throws IOException, InterruptedException {
        try (InputStream is = uticnica.getInputStream();) {
            StringBuilder text = new StringBuilder();
            while (true) {
                int i = is.read();
                if (i == -1) {
                    break;
                }
                text.append((char) i);
            }

            uticnica.shutdownInput();
            String zahtjev;
            zahtjev = text.toString();
            this.zahtjevDnevnik = zahtjev;
            System.out.println("Zahtjev je : " + zahtjev);
            provjeriZahtjev(zahtjev);
            return text.toString();
        } catch (IOException ex) {

        } catch (ParseException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Metoda prima zahtjev te ga "analizira", tj. provjerava se koju radnju je
     * potrebno poduzeti s obzirom na dobiveni zahtjev
     *
     * @param zahtjev String koji sadrži zahtjev
     * @throws InterruptedException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void provjeriZahtjev(String zahtjev) throws InterruptedException, FileNotFoundException, IOException, ParseException {
        String args[];
        args = zahtjev.split(" ");
        if (args[0].equals("ADD")) {
            dodajKorisnika(zahtjev);
        } else if (args[0].equals("AUTHEN")) {
            autentifikacijaKorisnika(zahtjev);
        } else if (args[0].equals("LOGOUT")) {
            odjaviKorisnika(zahtjev);
        } else if (args[0].equals("GRANT")) {
            dodajPodrucje(zahtjev);
        } else if (args[0].equals("REVOKE")) {
            oduzmiPodrcje(zahtjev);
        } else if (args[0].equals("RIGHTS")) {
            vratiListuPodrucja(zahtjev);
        } else if (args[0].equals("AUTHOR")) {
            provjeriKorisnikuPodrucje(zahtjev);
        } else if (args[0].equals("LIST")) {
            dajPodatkeKorisnika(zahtjev);
        } else if (args[0].equals("LISTALL")) {
            dajPodatkeSvihKorisnika(zahtjev);
        }
    }

    /**
     * provjerava odgovara li zahtjev regEx formatu
     *
     * @param zahtjev
     * @return ture ako odgovara, false ako je komanda zahtjeva neispravna
     */
    private boolean provjeriIspravnostKomande(String zahtjev) {
        String regExSintaksa;
        regExSintaksa = "(USER [A-z0-9_-]{3,} \\d+ AIRPORT "
                + "((LIST)|(SYNC)|(DIST [A-Z]{4} [A-Z]{4})|(CLEAR)|"
                + "([A-Z]{4})|(ADD [A-Z]{4} \"[A-z0-9 čćšđž]+"
                + "\" ((-)|())\\d+.\\d+ ((-)|())\\d+.\\d+))(( SLEEP \\d+)|()))";
        Pattern p = Pattern.compile(regExSintaksa);
        Matcher m = p.matcher(zahtjev);
        return m.matches();
    }

    private void dodajKorisnika(String zahtjev) throws ParseException {
        String args[];
        String odgovor;
        args = zahtjev.split(" ");
        String korisnik = args[1];
        String lozinka = args[2];
        String prezime = args[3];
        prezime = prezime.substring(1, prezime.length() - 1);
        String ime = args[4];
        ime = ime.substring(1, ime.length() - 1);
        KorisniciDAO kdao = new KorisniciDAO();
        Korisnik k = kdao.provjeriPostojanostKorisnika(korisnik, pbp);
        if (k != null) {
            odgovor = "ERROR 18: Vec postoji korisnik";
        } else {
            k = new Korisnik(korisnik, lozinka, prezime, ime);
            Boolean uspjeh = kdao.dodajKorisnika(k, pbp);
            if (uspjeh) {
                odgovor = "OK";
            } else {
                odgovor = "ERROR 18: Pogreska prilikom dodavanja korisnika";
            }
        }
        try {
            OutputStream os = uticnica.getOutputStream();
            posaljiOdgovor(os, odgovor);
            return;
        } catch (IOException ex) {
        }
    }

    private void autentifikacijaKorisnika(String zahtjev) throws ParseException {
        String odgovor;
        String args[];
        args = zahtjev.split(" ");
        String korisnik = args[1];
        String lozinka = args[2];
        KorisniciDAO kdao = new KorisniciDAO();
        Korisnik k = null;
        k = kdao.provjeriKorisnika(korisnik, lozinka, pbp);
        if (k == null) {
            odgovor = "ERROR 11: Ne postoji korisnik za trazeno korime i lozinku";
        } else {
            Sjednica sjed = SpremistePodataka.getInstance().dohvatiSjednicuPoKorisniku(korisnik);
            if (sjed != null) {
                long trajanje = Long.parseLong(pbp.dajPostavku("sjednica.trajanje"));
                long doKadaVrijedi = System.currentTimeMillis() + trajanje;
                SpremistePodataka.getInstance().osvjeziSjednicu(sjed.getId(), doKadaVrijedi);
                odgovor = "OK " + sjed.getId() + " " + doKadaVrijedi + " " + sjed.getBrojZahtjeva();
            } else {
                odgovor = kreirajSjednicu(korisnik);

            }
        }
        try {
            OutputStream os = uticnica.getOutputStream();
            posaljiOdgovor(os, odgovor);
            return;
        } catch (IOException ex) {
        }

    }

    private String kreirajSjednicu(String korIme) {
        int id;
        if (SpremistePodataka.getInstance().sjednice.isEmpty()) {
            id = 0;
        } else {
            Sjednica s = SpremistePodataka.getInstance().sjednice.get(SpremistePodataka.getInstance().sjednice.size() - 1);
            id = s.getId() + 1;
        }
        String korisnik = korIme;
        long vrijemeKreiranja = System.currentTimeMillis();
        long trajanje = Long.parseLong(this.pbp.dajPostavku("sjednica.trajanje"));
        long vrijemeDoKadaVrijedi = vrijemeKreiranja + trajanje;
        boolean aktivna = true;
        int brZahtjeva = Integer.parseInt(this.pbp.dajPostavku("sjednica.broj.zahtjeva"));
        Sjednica nova = new Sjednica(id, korisnik, vrijemeKreiranja, vrijemeDoKadaVrijedi, brZahtjeva, aktivna);
        SpremistePodataka.getInstance().getSjednice().add(nova);
        String odgovor = "OK " + id + " " + vrijemeDoKadaVrijedi + " " + brZahtjeva;
        return odgovor;
    }

    private Sjednica postojiAktivnaSjednicaZaKorId(String korisnik, int id) {
        for (Sjednica s : SpremistePodataka.getInstance().sjednice) {
            if (s.getKorisnik().equals(korisnik) && s.getId() == id
                    && s.getVrijemeDoKadaVrijedi() > System.currentTimeMillis()) {
                return s;
            }
        }
        return null;
    }

    private void odjaviKorisnika(String zahtjev) throws ParseException {
        String odgovor;
        String args[];
        args = zahtjev.split(" ");
        String korisnik = args[1];
        int sjednicaId = Integer.parseInt(args[2]);
        KorisniciDAO kdao = new KorisniciDAO();
        Korisnik k = kdao.provjeriPostojanostKorisnika(korisnik, pbp);
        if (k == null) {
            odgovor = "ERROR 17: Ne postoji korisnik za trazeno korisnicko ime";
        } else {
            Sjednica s = postojiAktivnaSjednicaZaKorId(korisnik, sjednicaId);
            if (s == null) {
                odgovor = "ERROR 15: Nema aktivne sjednice";
            } else {
                azurirajSjednicu(s);
                obrisiSjednicu(s.getId());
                odgovor = "OK";
            }

        }
        try {
            OutputStream os = uticnica.getOutputStream();
            posaljiOdgovor(os, odgovor);
            return;
        } catch (IOException ex) {
        }
    }

    private void azurirajSjednicu(Sjednica sjednica) {
        for (Sjednica s : SpremistePodataka.getInstance().sjednice) {
            if (s.getId() == sjednica.getId()) {
                long doKadaVrijedi = System.currentTimeMillis();
                s.setVrijemeDoKadaVrijedi(doKadaVrijedi);
                s.setBrojZahtjeva(0);
                break;
            }
        }
    }

    private void obrisiSjednicu(int id) {
        for (Sjednica s : SpremistePodataka.getInstance().sjednice) {
            if (s.getId() == id) {
                SpremistePodataka.getInstance().sjednice.remove(s);
                break;
            }
        }
    }

    private void dodajPodrucje(String zahtjev) throws ParseException {
        String odgovor;
        String args[];
        args = zahtjev.split(" ");
        String korisnik = args[1];
        int sjednicaId = Integer.parseInt(args[2]);
        String podrucje = args[3];
        String korisnikTrazi = args[4];
        KorisniciDAO kdao = new KorisniciDAO();
        Korisnik k = kdao.provjeriPostojanostKorisnika(korisnik, pbp);
        Korisnik k2 = kdao.provjeriPostojanostKorisnika(korisnikTrazi, pbp);
        if (k == null || k2 == null) {
            odgovor = "ERROR 17:  Ne postoji korisnik za trazeno korisnicko ime";
        } else {
            Sjednica s = postojiAktivnaSjednicaZaKorId(korisnik, sjednicaId);
            if (s == null) {
                odgovor = "ERROR 15: Nema aktivne sjednice";
            } else {
                if (s.getBrojZahtjeva() == 0) {
                    odgovor = "ERROR 16: Broj preostalih zahtjeva je 0";
                } else {
                    int brojZahtjeva = s.getBrojZahtjeva() - 1;
                    s.setBrojZahtjeva(brojZahtjeva);
                    OvlastiDAO odao = new OvlastiDAO();
                    boolean imaPodrucje = odao.provjeriPostojanostPodrucja(korisnikTrazi, podrucje, this.pbp);
                    if (imaPodrucje) {
                        if (odao.imaAktivnoPodrucje(korisnikTrazi, podrucje, this.pbp)) {
                            odgovor = "ERROR 13: Korisnik ima aktivno podrucje";
                        } else {
                            odao.aktivirajPodrucje(korisnikTrazi, podrucje, this.pbp);
                            odgovor = "OK";
                        }
                    } else {
                        odao.dodajPodrucje(korisnikTrazi, podrucje, this.pbp);
                        odgovor = "OK";
                    }
                }
            }
        }
        try {
            OutputStream os = uticnica.getOutputStream();
            posaljiOdgovor(os, odgovor);
            return;
        } catch (IOException ex) {
        }

    }

    private void oduzmiPodrcje(String zahtjev) throws ParseException {
        String odgovor = "";
        String args[];
        args = zahtjev.split(" ");
        String korisnik = args[1];
        int sjednicaId = Integer.parseInt(args[2]);
        String podrucje = args[3];
        String korisnikTrazi = args[4];
        KorisniciDAO kdao = new KorisniciDAO();
        Korisnik k = kdao.provjeriPostojanostKorisnika(korisnik, pbp);
        Korisnik k2 = kdao.provjeriPostojanostKorisnika(korisnikTrazi, pbp);
        OvlastiDAO odao = new OvlastiDAO();
        if (k == null || k2 == null) {
            odgovor = "ERROR 17:  Ne postoji korisnik za trazeno korisnicko ime";
        } else if (!odao.postojiPodrucje(podrucje, pbp)) {
            odgovor = "ERROR 18: Ne postoji trazeno podrucje u tablici";
        } else {
            Sjednica s = postojiAktivnaSjednicaZaKorId(korisnik, sjednicaId);
            if (s == null) {
                odgovor = "ERROR 15: Nema aktivne sjednice";
            } else {
                if (s.getBrojZahtjeva() == 0) {
                    odgovor = "ERROR 16: Broj preostalih zahtjeva je 0";
                } else {
                    int brojZahtjeva = s.getBrojZahtjeva() - 1;
                    s.setBrojZahtjeva(brojZahtjeva);

                    boolean imaPodrucje = odao.provjeriPostojanostPodrucja(korisnikTrazi, podrucje, this.pbp);
                    if (imaPodrucje) {
                        if (odao.imaAktivnoPodrucje(korisnikTrazi, podrucje, pbp)) {
                            odao.deaktivirajPodrucje(korisnikTrazi, podrucje, pbp);
                            odgovor = "OK";
                        } else {
                            odgovor = "ERROR 14: Podrucje je neaktivno!";
                        }
                    } else {
                        odgovor = "ERROR 14: Podrucje je neaktivno ";
                    }
                }
            }
        }
        try {
            OutputStream os = uticnica.getOutputStream();
            System.out.println("Odgovor: " + odgovor);
            posaljiOdgovor(os, odgovor);
            return;
        } catch (IOException ex) {
        }

    }

    private void vratiListuPodrucja(String zahtjev) throws ParseException {
        String odgovor = "";
        String args[];
        args = zahtjev.split(" ");
        String korisnik = args[1];
        int sjednicaId = Integer.parseInt(args[2]);
        String korisnikTrazi = args[3];
        KorisniciDAO kdao = new KorisniciDAO();
        Korisnik k = kdao.provjeriPostojanostKorisnika(korisnik, pbp);
        Korisnik k2 = kdao.provjeriPostojanostKorisnika(korisnikTrazi, pbp);
        if (k == null || k2 == null) {
            odgovor = "ERROR 17:  Ne postoji korisnik za trazeno korisnicko ime";
        } else {
            Sjednica s = postojiAktivnaSjednicaZaKorId(korisnik, sjednicaId);
            if (s == null) {
                odgovor = "ERROR 15: Nema aktivne sjednice";
            } else {
                if (s.getBrojZahtjeva() == 0) {
                    odgovor = "ERROR 16: Broj preostalih zahtjeva je 0";
                } else {
                    int brojZahtjeva = s.getBrojZahtjeva() - 1;
                    s.setBrojZahtjeva(brojZahtjeva);
                    OvlastiDAO odao = new OvlastiDAO();
                    int brojPodrucja = odao.brojPodrucja(korisnikTrazi, pbp);
                    if (brojPodrucja == 0) {
                        odgovor = "ERROR 13: Ne postoji aktivno podrucje";
                    } else {
                        String podrucja = odao.dohvatiAktivnaPodrucja(korisnikTrazi, pbp);
                        podrucja = podrucja.substring(0, podrucja.length() - 1);
                        odgovor = "OK " + podrucja;
                    }
                }
            }
        }
        try {
            OutputStream os = uticnica.getOutputStream();
            posaljiOdgovor(os, odgovor);
            return;
        } catch (IOException ex) {
        }
    }

    private void provjeriKorisnikuPodrucje(String zahtjev) throws ParseException {
        String odgovor = "";
        String args[];
        args = zahtjev.split(" ");
        String korisnik = args[1];
        int sjednicaId = Integer.parseInt(args[2]);
        String podrucje = args[3];
        OvlastiDAO odao = new OvlastiDAO();
        KorisniciDAO kdao = new KorisniciDAO();
        Korisnik k = kdao.provjeriPostojanostKorisnika(korisnik, pbp);
        if (k == null) {
            odgovor = "ERROR 17:  Ne postoji korisnik za trazeno korisnicko ime";
        } else if (!odao.postojiPodrucje(podrucje, pbp)) {
            odgovor = "ERROR 18: Ne postoji trazeno podrucje u tablici";
        } else {
            Sjednica s = postojiAktivnaSjednicaZaKorId(korisnik, sjednicaId);
            if (s == null) {
                odgovor = "ERROR 15: Nema aktivne sjednice";
            } else {
                if (s.getBrojZahtjeva() == 0) {
                    odgovor = "ERROR 16: Broj preostalih zahtjeva je 0";
                } else {
                    int brojZahtjeva = s.getBrojZahtjeva() - 1;
                    s.setBrojZahtjeva(brojZahtjeva);

                    boolean korisnikImaPodrucje = odao.provjeriKorisnikuPodrucje(korisnik, podrucje, pbp);
                    if (korisnikImaPodrucje) {
                        odgovor = "OK";
                    } else {
                        odgovor = "ERROR 14: Ne postoji aktivno podrucje";
                    }
                }
            }
        }
        try {
            OutputStream os = uticnica.getOutputStream();
            posaljiOdgovor(os, odgovor);
            return;
        } catch (IOException ex) {
        }

    }

    private void dajPodatkeKorisnika(String zahtjev) throws ParseException {
        String odgovor = "";
        String args[];
        args = zahtjev.split(" ");
        String korisnik = args[1];
        int sjednicaId = Integer.parseInt(args[2]);
        String korisnikTrazi = args[3];
        KorisniciDAO kdao = new KorisniciDAO();
        Korisnik k = kdao.provjeriPostojanostKorisnika(korisnik, pbp);
        Korisnik k2 = kdao.provjeriPostojanostKorisnika(korisnikTrazi, pbp);
        if (k2 == null) {
            odgovor = "ERROR 17:  Ne postoji korisnik koji se trazi";
        } else {
            Sjednica s = postojiAktivnaSjednicaZaKorId(korisnik, sjednicaId);
            if (s == null) {
                odgovor = "ERROR 15: Nema aktivne sjednice";
            } else {
                if (s.getBrojZahtjeva() == 0) {
                    odgovor = "ERROR 16: Broj preostalih zahtjeva je 0";
                } else {
                    int brojZahtjeva = s.getBrojZahtjeva() - 1;
                    s.setBrojZahtjeva(brojZahtjeva);
                    String podaci = kdao.dajPodatkeKorisnika(korisnikTrazi, pbp);
                    odgovor = "OK \"" + podaci + "\"";
                }
            }
        }
        try {
            OutputStream os = uticnica.getOutputStream();
            posaljiOdgovor(os, odgovor);
            return;
        } catch (IOException ex) {
        }
    }

    private void dajPodatkeSvihKorisnika(String zahtjev) throws ParseException {
        String odgovor = "";
        String args[];
        args = zahtjev.split(" ");
        String korisnik = args[1];
        int sjednicaId = Integer.parseInt(args[2]);
        KorisniciDAO kdao = new KorisniciDAO();
        Sjednica s = postojiAktivnaSjednicaZaKorId(korisnik, sjednicaId);
        if (s == null) {
            odgovor = "ERROR 15: Nema aktivne sjednice";
        } else {
            if (s.getBrojZahtjeva() == 0) {
                odgovor = "ERROR 16: Broj preostalih zahtjeva je 0";
            } else {
                int brojZahtjeva = s.getBrojZahtjeva() - 1;
                s.setBrojZahtjeva(brojZahtjeva);
                String podaci = kdao.dajSvePodatkeKorisnika(pbp);
                odgovor = "OK " + podaci;
            }
        }

        try {
            OutputStream os = uticnica.getOutputStream();
            posaljiOdgovor(os, odgovor);
            return;
        } catch (IOException ex) {
        }
    }

    private String dajKorisnika(String zahtjev) {
        String args[];
        args = zahtjev.split(" ");
        String kor = args[1];
        return kor;
    }

    private String dajStatus(String odgovor) {
        if (odgovor.startsWith("OK")) {
            return "OK";
        } else {
            String polje[];
            polje = odgovor.split(" ");
            String odg = polje[0] + " " + polje[1];
            odg = odg.substring(0, odg.length() - 1);
            return odg;
        }
    }

    private void zapisiUDnevnik(String korisnik, String zahtjev, String odgovor, long vrijeme) throws UnsupportedEncodingException, IOException {
        if(zahtjev.startsWith("ADD")) return;
        try {
            
                    String req = "{\n"
                    + "\"korisnik\": \""+korisnik+"\",\n"
                    + "\"zahtjev\": \""+zahtjev+"\",\n"
                    + "\"odgovor\": \""+odgovor+"\",\n"
                    + "\"vrijemeZahtjeva\": \""+vrijeme+"\"\n"
                    + "}";
 
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8084/ahip20_aplikacija_2/rest/dnevnik"))
                    .POST(HttpRequest.BodyPublishers.ofString(req))
                    .build();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());
        } catch (InterruptedException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
