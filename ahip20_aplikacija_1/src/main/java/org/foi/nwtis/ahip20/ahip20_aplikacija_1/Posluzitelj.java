package org.foi.nwtis.ahip20.ahip20_aplikacija_1;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ahip20.ahip20_aplikacija_1.podaci.Sjednica;
import org.foi.nwtis.ahip20.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

public class Posluzitelj extends Thread {

    private PostavkeBazaPodataka pbp;
    private int port;
    private int brojDretvi;
    private int brojac;
    private ServerSocket ss;
   

    public Posluzitelj(PostavkeBazaPodataka pbp) {
        this.pbp = pbp;
    }

    @Override
    public void run() {
        if (!zauzetostPorta(this.port)) {
            System.out.println("ERROR 18: Port se vec koristi");
            return;
        } else {
            this.pokreniServer();
        }

    }

    @Override
    public void interrupt() {
        try {
            ss.close();
        } catch (IOException ex) {
            Logger.getLogger(Posluzitelj.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void start() {
        this.port = Integer.parseInt(pbp.dajPostavku("port"));
        this.brojDretvi = Integer.parseInt(pbp.dajPostavku("broj.dretvi"));
        super.start();
    }

    private void pokreniServer() {

        System.out.println("Pokrecem server");
        List<Thread> dretve = new ArrayList<>();
        try {
            ss = new ServerSocket(this.port, 5);
            while (true) {
                Socket uticnica = ss.accept();
                if (!dretve.isEmpty()) {
                    dretve = izbrisiGotoveDretve(dretve);
                }
                if (this.brojac < this.brojDretvi) {
                    Thread dretva = new DretvaZahtjeva(uticnica, this.port, this.pbp);
                    brojac++;
                    dretve.add(dretva);
                    dretva.setName("ahip20_" + (brojac - 1));
                    dretva.start();

                } else {
                    OutputStream os = uticnica.getOutputStream();
                    os.write("ERROR 01: Dretve zauzete".getBytes());
                    os.flush();
                    uticnica.shutdownOutput();
                    System.out.println("ERROR 01: Dretve zauzete");
                }
                if (!dretve.isEmpty()) {
                    dretve = izbrisiGotoveDretve(dretve);
                }

            }

        } catch (IOException ex) {
            System.out.println("ERROR 18: Uticnica zatvorena");
        } catch (RuntimeException ex) {
            System.out.println("Trebam zatvorit port");
        }

    }

    private List<Thread> izbrisiGotoveDretve(List<Thread> dretve) {
        List<Thread> t = new ArrayList<>();
        for (Thread d : dretve) {
            if (d.getState() == Thread.State.TERMINATED) {
                t.add(d);
                this.brojac--;
            }
        }
        for (Thread d : t) {
            dretve.remove(d);
        }
        return dretve;
    }

    private static boolean zauzetostPorta(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }
}
