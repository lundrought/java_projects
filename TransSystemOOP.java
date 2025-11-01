'''
Transportation Schedule System (Object-Oriented Programming)
---------------------

Developed as part of Programming 2 course at University of Ljubljana.

All code implementation below is my own.
Course materials © Professor Tomaž Dobravec, Faculty of Computer and Information Science, University of Ljubljana.
'''




import java.util.*;
import java.io.*;

public class TransSystemOOP{

    public static Linija[] linije;
    public static Postaja[] postaje;
    public static Avtobus[] avtobusi;

    public static void main(String[] args) {

        try {
            Scanner sc = new Scanner(new File(args[0]));
            String[] stevila = sc.nextLine().split(",");
            
            int stPostaj = Integer.parseInt(stevila[0]);
            postaje = new Postaja[stPostaj];
            
            int stLinij = Integer.parseInt(stevila[1]);
            linije = new Linija[stLinij];
            int indeksL = 0;
            
            int stAvtobusov = Integer.parseInt(stevila[2]);
            avtobusi = new Avtobus[stAvtobusov];
            int indeksA = 0;

            sc.nextLine();

            //postaje
            for (int i = 0; i < stPostaj; i++) {
                
                String[] p = sc.nextLine().split(","); //podatki o postaji
                postaje[i] = new Postaja(Integer.parseInt(p[0]), p[1], 
                                Integer.parseInt(p[2]), Integer.parseInt(p[3]),
                                Integer.parseInt(p[6]));

                

                //linije 
                String[] linijeID = p[4].split(";");
                zunanja:
                for (int j = 0; j < linijeID.length; j++) {
                    int ID = Integer.parseInt(linijeID[j]);
                    for (Linija l : linije) {
                        if (l != null && l.getID() == ID) {
                            continue zunanja;
                        }
                    }
                    linije[indeksL] = new Linija(ID);
                    indeksL++;
                }

                //avtobusi
                String[] avtobusiID = p[5].split(";"); //id, stpotnikov, trenutnapostaja
                zunanja:
                for (int j = 0; j < avtobusiID.length; j++) {
                    if (avtobusiID[j].equals("")) {
                        continue;
                    }
                    int ID = Integer.parseInt(avtobusiID[j].split("\\(")[0]);
                    int stPotnikov = Integer.parseInt(avtobusiID[j].split("\\(")[1].replace(")",""));
                    for (Avtobus a : avtobusi) { //preverit a je že na seznamu
                        if (a != null && a.getID() == ID) {
                            continue zunanja;
                        }
                    }
                    avtobusi[indeksA] = new Avtobus(ID, stPotnikov);
                    avtobusi[indeksA].setTrenutnaPostaja(postaje[i]);
                    indeksA++;
                }
            }

            sc.nextLine();

            while (sc.hasNextLine()) {

                String[] l = sc.next().split(",");

                for (int i = 0; i < stLinij; i++) {

                    if (linije[i].getID() == Integer.parseInt(l[0])) {
                        linije[i].setBarva(l[1]);

                        String[] avtobusiID = l[2].split(";");
                        for (String aID : avtobusiID) {
                            for (Avtobus a : avtobusi) {
                                if (a != null && a.getID() == Integer.parseInt(aID)) {
                                    linije[i].dodajAvtobus(a);
                                }
                            }
                        }

                        String[] postajeID = l[3].split("\\|");
                        for (String pID : postajeID) {
                            
                            for (Postaja p : postaje) {
                                if (p != null && p.getID() == Integer.parseInt(pID)) {
                                    linije[i].dodajPostajo(p);
                                }
                            }
                        }
                    }   
                }
            }
            sc.close();

            if (args[1].equals("izpisi")) {
                for (Linija l : linije) {
                    l.izpisi();
                }
            } else if (args[1].equals("najboljObremenjena")) {
                izpisNajboljObremenjenePostaje(Integer.parseInt(args[2]));
            } else if (args[1].equals("premik")) {
                izpisiPoNPremikih(Integer.parseInt(args[2]));
            } else if (args[1].equals("ekspres")) {
                izpisiPoNPremikihEkspres(Integer.parseInt(args[2]));
            }


        } catch (FileNotFoundException e) {
            System.out.println("Datoteka ne obstaja.");
        }

    }

    public static void izpisNajboljObremenjenePostaje(int kapaciteta) {
        /* vsaka linija seznam avtobusov ->
         * če je na tej liniji postaja gre avtobus tam
         *
         * prosta mesta: vsota prostih sedezev na vseh avtobusih
         * / cakajoci potniki na postaji
         */
        double minRazmerje = Double.MAX_VALUE;
        int minProstaMesta = Integer.MAX_VALUE;
        Postaja najObremenjena = postaje[0];
        for (Postaja p : postaje) { //jim dodam avtobuse
            for (Linija l : linije) {
                for (Postaja pOdL : l.getPostaje()) {
                    if (pOdL != null && pOdL.getID() == p.getID()) {
                        for (Avtobus aOdL : l.getAvtobusi()) {
                            if (aOdL != null) p.dodajAvtobus(aOdL);
                        }
                    }
                }
            }
        
            double cakajoci = p.getCakajoci();
            double prostaMesta = 0;
            for (Avtobus a : p.getAvtobusi()) {
                if (a.getSteviloPotnikov() >= kapaciteta) continue;
                prostaMesta += (kapaciteta - a.getSteviloPotnikov());
            }
            double razmerje = 0;
            if (cakajoci == 0) razmerje = prostaMesta;
            else razmerje = prostaMesta / cakajoci;

           // System.out.println(p + " " + prostaMesta + " " + razmerje);
            if (razmerje < minRazmerje) {
                minRazmerje = razmerje;
                najObremenjena = p;
                minProstaMesta = (int) prostaMesta;
            }
           
        }
        System.out.printf("Najbolj obremenjena postaja: %d %s%n", najObremenjena.getID(), najObremenjena.getIme());
        System.out.printf(Locale.US, "Cakajoci: %d, Stevilo prostih mest: %d, Razmerje: %.2f", najObremenjena.getCakajoci(), minProstaMesta, minRazmerje);
    }

    public static void naslednjeStanje() {
        /* za vsako linijo:
         * -pogledam avtobuse in njihove trenutne postaje
         * - if (trenutna postaja == postaje[i]) potem je t. p. = postaje[i+1]
         */

        for (Linija l : linije) {
          //  l.izpisi();
            Postaja[] postajeOdL = l.getPostaje();
            for (Avtobus a : l.getAvtobusi()) {
                
                if (a == null) continue;
              //  System.out.println(a);
                int ixPostaje = -1;              
                for (int i = 0; i < l.getStPostaj(); i++) {
                    if (a.getTrenutnaPostaja().getID() == postajeOdL[i].getID()) {
                        ixPostaje = i;
                    //    System.out.println(a);
                        break;
                    }
                }
                if (ixPostaje == -1) continue;

                //v katero smer premaknit?
                int naslednjiI = ixPostaje + a.getSmer();
                if (naslednjiI < 0 || naslednjiI >= l.getStPostaj()) {
                    a.obrniSmer();
                    naslednjiI = ixPostaje + a.getSmer();
                    if (naslednjiI < 0) naslednjiI = 0;
                    if (naslednjiI >= l.getStPostaj()) naslednjiI = l.getStPostaj() - 1;
                }
                a.setTrenutnaPostaja(postajeOdL[naslednjiI]);   
            }
        }
    }

    public static void izpisiPoNPremikih(int n) {
        System.out.println("Zacetno stanje");
        for (Linija l : linije) {
            l.izpisi();
        }
        System.out.println();
        System.out.printf("Stanje po %d premikih%n", n);
        for (int i = 0; i < n; i++) {
            naslednjeStanje();
        }
        for (Linija l : linije) {
            l.izpisi();
        }
    }

    static void dodajEkspresneAvtobuse() {
        //vsaki liniji doda avtobus s poljubnim id
        int id = -1;
        for (Linija l : linije) {
            EkspresniAvtobus a = new EkspresniAvtobus(id, 10);
            a.setTrenutnaPostaja(l.getPostaje()[0]); //zacne na 1. postaji
            a.setPreskocene(l.getPostaje());
            l.dodajAvtobus(a);
            id--;
        }
        
    }
    
    static void naslednjeStanjeEkspres() {

         for (Linija l : linije) {
          //  l.izpisi();
            Postaja[] postajeOdL = l.getPostaje();
            for (Avtobus a : l.getAvtobusi()) {
                
                if (a == null) continue;

                //EKSPRES AVTOBUSI
                if (a instanceof EkspresniAvtobus) {
                   // EkspresniAvtobus e = (EkspresniAvtobus) a;
                    if (a.getTrenutnaPostaja().getID() == postajeOdL[0].getID()) {
                        a.setTrenutnaPostaja(postajeOdL[l.getStPostaj()-1]);
                    } else {
                        a.setTrenutnaPostaja(postajeOdL[0]);
                    }
                } else {

                    //NAVADNI AVTOBUSI
                    int ixPostaje = -1;              
                    for (int i = 0; i < l.getStPostaj(); i++) {
                        if (a.getTrenutnaPostaja().getID() == postajeOdL[i].getID()) {
                            ixPostaje = i;
                        //    System.out.println(a);
                            break;
                        }
                    }
                    if (ixPostaje == -1) continue;

                    //v katero smer premaknit?
                    int naslednjiI = ixPostaje + a.getSmer();
                    if (naslednjiI < 0 || naslednjiI >= l.getStPostaj()) {
                        a.obrniSmer();
                        naslednjiI = ixPostaje + a.getSmer();
                        if (naslednjiI < 0) naslednjiI = 0;
                        if (naslednjiI >= l.getStPostaj()) naslednjiI = l.getStPostaj() - 1;
                    }
                    a.setTrenutnaPostaja(postajeOdL[naslednjiI]);   
                }
            }
        }

    }

    public static void izpisiPoNPremikihEkspres(int n) {

        dodajEkspresneAvtobuse();

        System.out.println("Zacetno stanje");
        for (Linija l : linije) {
            l.izpisi();
        }
        System.out.println();
        System.out.printf("Stanje po %d premikih%n", n);
        for (int i = 0; i < n; i++) {
            naslednjeStanjeEkspres();
        }
        for (Linija l : linije) {
            l.izpisi();
        }
    }

    public void casiPrihodov(int ID, int maxRazdalja) {
        //ID postaje, max razdalja od postaje
        
    }
}


class Postaja {

    private int ID;
    private String ime;
    private int x;
    private int y;
    private int cakajoci;
    private Set<Avtobus> avtobusi;

    public Postaja(int ID, String ime, int x, int y, int cakajoci) {
        this.ID = ID;
        this.ime = ime;
        this.x = x;
        this.y = y;
        this.cakajoci = cakajoci;
        this.avtobusi = new HashSet<>();
        
    }

    public int getID() {
        return ID;
    }

    public String getIme() {
        return ime;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCakajoci() {
        return cakajoci;
    }

    public void dodajAvtobus(Avtobus a) {
        avtobusi.add(a);
    }

    public Set<Avtobus> getAvtobusi() {
        return avtobusi;
    }

    @Override 
    public String toString() {
        return String.format("%d %s [%d,%d] cakajoci: %d", ID, ime, x, y, cakajoci);
    }

}

class Avtobus {

    private int ID;
    private int steviloPotnikov;
    private Postaja trenutnaPostaja;
    private int smer = 1; //1: naprej, -1 nazaj

    public Avtobus(int ID, int steviloPotnikov) {
        this.ID = ID;
        this.steviloPotnikov = steviloPotnikov;
        this.trenutnaPostaja = null;
    }

    public int getID() {
        return ID;
    }

    public int getSteviloPotnikov() {
        return steviloPotnikov;
    }

    public Postaja getTrenutnaPostaja() {
        return trenutnaPostaja;
    }

    public void setTrenutnaPostaja(Postaja p) {
        this.trenutnaPostaja = p;
    }

    public int getSmer() {
        return smer;
    }

    public void setSmer(int smer) {
        this.smer = smer;
    }

    public void obrniSmer() {
        smer *= -1;
    }

    @Override
    public String toString() {
        return String.format("%d (%d) - %s", ID, steviloPotnikov, trenutnaPostaja.getIme());
    } 
}

class EkspresniAvtobus extends Avtobus {

    private Postaja[] preskociPostaje; //kje se ne bo ustavil

    public EkspresniAvtobus(int ID, int steviloPotnikov) {
        super(ID, steviloPotnikov);
        this.preskociPostaje = null;
    }

    public void setPreskocene(Postaja[] postaje) {
        preskociPostaje = new Postaja[postaje.length-2];
        for (int i = 1; i < postaje.length-1; i++) {
            preskociPostaje[i-1] = postaje[i];
        }
    }

    public Postaja[] getPreskocene() {
        return preskociPostaje;
    }

    
}

class Linija {

    private int ID;
    String barva;
    Postaja[] seznamPostaj;
    Avtobus[] seznamAvtobusov;
    int stPostaj;
    int stAvtobusov;

    public Linija(int ID) {
        this.ID = ID;
        this.seznamPostaj = new Postaja[10];
        this.seznamAvtobusov = new Avtobus[5];
        this.stPostaj = 0;
        this.stAvtobusov = 0;
    }

    boolean dodajPostajo(Postaja postaja) {
        if (stPostaj < 10 && !obstajaPostaja(postaja)) {
            seznamPostaj[stPostaj] = postaja;
            stPostaj++;
            return true;
        }
        return false;
    }

    public boolean dodajAvtobus(Avtobus avtobus) {
        if (stAvtobusov < 5 && !obstajaAvtobus(avtobus) ) {
            seznamAvtobusov[stAvtobusov] = avtobus;
            stAvtobusov++;
            return true;
        }
        return false;
    }

    private boolean obstajaAvtobus(Avtobus a) {
        for (int i = 0; i < seznamAvtobusov.length; i++) {
            if (seznamAvtobusov[i] != null && seznamAvtobusov[i].getID() == a.getID()) {
                return true;
            }
        }
        return false;

    }

    private boolean obstajaPostaja(Postaja p) {
        for (int i = 0; i < seznamPostaj.length; i++) {
            if (seznamPostaj[i] != null && seznamPostaj[i].getID() == p.getID()) {
                return true;
            }
        }
        return false;
    }

    private boolean busNaPostaji(Postaja p) {
        for (Avtobus a : seznamAvtobusov) {
            if (a != null && !(a instanceof EkspresniAvtobus) && a.getTrenutnaPostaja() != null && a.getTrenutnaPostaja().getID() == p.getID()) {
                return true;
            }
        }
        return false;
    }

    private boolean ekspresNaPostaji(Postaja p) {
        for (Avtobus a : seznamAvtobusov) {
            if (a != null && a instanceof EkspresniAvtobus &&
                 a.getTrenutnaPostaja() != null && a.getTrenutnaPostaja().getID() == p.getID()) {
                return true;
            }
        }
        return false;
    }

    public int getID() {
        return ID;
    }

    public String getBarva() {
        return barva;
    }

    public Postaja[] getPostaje() {
        return seznamPostaj;
    }

    public Avtobus[] getAvtobusi() {
        return seznamAvtobusov;
    }

    public int getStPostaj() {
        return stPostaj;
    }

    public int getStAvtobusov() {
        return stAvtobusov;
    }

    public void setBarva(String b) {
        this.barva = b;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Linija %d - ", ID));
        int i = 0;
        for (Postaja p : seznamPostaj) {
            i++;
            if (p != null) {
                sb.append(p.getIme());
                if (ekspresNaPostaji(p)) sb.append(" (ekspres)");
                if (busNaPostaji(p)) sb.append(" (bus)");
                if (i < stPostaj) sb.append(" -> ");                
            }
        }
        return sb.toString();
    }

    public void izpisi() {
        System.out.println(this);
    }

}