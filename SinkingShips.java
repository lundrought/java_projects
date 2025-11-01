'''
Sinking Ships Game
---------------------

Developed as part of Programming 2 course at University of Ljubljana.

All code implementation below is my own.
Course materials © Professor Tomaž Dobravec, Faculty of Computer and Information Science, University of Ljubljana.
'''


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SinkingShips {

    public static int visina;
    public static int sirina;

    public static int steviloLadij;
    public static int stLadijprvi;
    public static int stLadijdrugi;

    //zacetna postavitev ladij iz datoteke -> igralna površina

    public static void main(String[] args) {

        int[][] postavitev = preberiZacetnoPostavitev(args[1]);
        if (postavitev == null) return;

        

        int[][] povrsina = izdelajIgralnoPovrsino(postavitev);
        
        if (args[0].equals("postavitev")) {
            izpisiPostavitev(postavitev);
        }

        if (args[0].equals("povrsina")) {
            izrisiIgralnoPovrsino(povrsina);
        }

        if (args[0].equals("povecanje")) {
            String sxv = args[2];
            int[][] povecanaP = povecajIgralnoPovrsino(postavitev, sxv);
            izrisiIgralnoPovrsino(povecanaP);
        }

        if (args[0].equals("zmanjsanje")) {
            int[][] zmanjsanaP = minimizirajIgralnoPovrsino(postavitev, povrsina);
            izrisiIgralnoPovrsino(zmanjsanaP);
        }

        if (args[0].equals("simulacija")) {
            String imeDat = args[2];
            int[][] rezultatP = simulirajIgro(povrsina, imeDat);
            izrisiIgralnoPovrsino(rezultatP);
        }

        if (args[0].equals("zasuk")) {
            String smer = args[2];
            int[][] zasukanaP = zasukajLadje(postavitev, smer);
            izrisiIgralnoPovrsino(zasukanaP);
        }

        if (args[0].equals("zasukBrezKolizij")) {
            String smer = args[2];
            int[][] zasukanaPBK = zasukajLadjeBrezKolizij(postavitev, smer);
            izrisiIgralnoPovrsino(zasukanaPBK);

        }

        
    }

    private static void dolociStLadij(int[][] postavitev) {
        stLadijprvi = 0;
        stLadijdrugi = 0;
        for (int[] p : postavitev) {
            if (p[0] == 0) stLadijprvi++;
            else stLadijdrugi++;
        }

    }

    //NALOGA 1
    public static int[][] preberiZacetnoPostavitev(String dat) {

        /* datoteka:
            sirinaxvisina (int) -> globalna spr.
            st ladij (int) -> velikost tabele
            indeks yzacetka xzacetka dolzina smer (za vsako ladjo)
        */

        /* smer:
            S -> 0
            J -> 1
            V -> 2
            Z -> 3
        */

        try {

            Scanner sc = new Scanner(new File(dat));

            if (!sc.hasNext()) {
                System.out.println( "Napaka: Manjka podatek o dimenzijah igralne povrsine.");
                return null;
            }

            String[] sxv = sc.next().split("x");
            if (sxv.length != 2) {
                System.out.println("Napaka: Nepravilen podatek o dimenzijah igralne povrsine.");
                return null;
            }

            try {
                sirina = Integer.parseInt(sxv[0]);
                visina = Integer.parseInt(sxv[1]);
            } catch (NumberFormatException e) {
                System.out.println("Napaka: Nepravilen podatek o dimenzijah igralne povrsine.");         
                return null;

            }

            if (sirina <= 0 || visina <= 0) {
                System.out.println("Napaka: Dimenzija mora biti pozitivna.");
                return null;
            }
            if (!sc.hasNext()) {
                System.out.println( "Napaka: Manjka podatek o stevilu ladij.");
                return null;
            }

            int n = sc.nextInt();
            steviloLadij = n;
            if (n < 0) {
                System.out.println("Napaka: Stevilo ladij ne sme biti negativno.");
                return null;
            }

            int[][] postavitev = new int[n][5]; //igralec y x d smer
            int i = 0;
        
            while (sc.hasNext() && i < n) {

                postavitev[i][0] = sc.nextInt();
                if (postavitev[i][0] != 0 && postavitev[i][0] != 1) {
                    System.out.println("Napaka: Nepravilen podatek o postavitvi ladje.");
                    return null;
                }
                postavitev[i][1] = sc.nextInt();
                postavitev[i][2] = sc.nextInt();
                postavitev[i][3] = sc.nextInt();

                String smer = sc.next();
                if (smer.equals("S")) postavitev[i][4] = 0;
                else if (smer.equals("J")) postavitev[i][4] = 1;
                else if (smer.equals("V")) postavitev[i][4] = 2;
                else if (smer.equals("Z"))postavitev[i][4] = 3;
                else {
                    System.out.println("Napaka: Nepravilen podatek o postavitvi ladje.");
                    return null;
                }

                i++;
            }

            if (i != n) {
                System.out.println("Napaka: Podatek o stevilu ladij se ne ujema s stevilom vnosov.");
                return null;
            }

            sc.close();

            return postavitev;

        } catch (FileNotFoundException e) {
            System.out.println( "Napaka: datoteka ne obstaja.");
        };

        return null;
    }

    public static void izpisiPostavitev(int[][] p) {

        //igralec y x d smer

        for (int i = 0; i < p.length; i++) {
            System.out.printf("Igralec: %d  Dolzina: %d  Smer: %d  Koordinate premca: (%d,%d)%n", p[i][0], p[i][3], p[i][4], p[i][1], p[i][2]);
        }
        
    }

    public static int[][] izdelajIgralnoPovrsino(int[][] postavitev) {

        /* velikost: (2*sirina) x visina
         * 0 levo, 1 desno
         * (0, 0) zg. levo igralca 0; (sirina, 0) zg. levo igralca 1
         * smer: 0 gor, 1 dol, 2 desno, 3 levo
        */
    
        //oznaka polja: zaporedna st. ladje + x
        /* x:  voda -> 0
            premec ladje -> 1
            trup ladje -> 2
            zadeti premec -> 3
            zadeti trup -> 4
            potopljena ladja -> 5
            zgrešeno polje -> 6
        */
    
        //postavitev: igralec xpr ypr dolzina smer
    
        int[][] povrsina = new int[visina][2 * sirina]; 
        for (int i = 0; i < postavitev.length; i++) { //cez vse ladje
            
            if (!ladjaZnotrajMeja(postavitev[i], povrsina)) {
                continue;
            }

            int x = postavitev[i][1];
            int y = postavitev[i][2];
            int igralec = postavitev[i][0]; //0 [0, sirina) 1 [sirina, sirina+sirina)
            int d = postavitev[i][3]; //dolzina ladje
            int smer = postavitev[i][4]; //0 gor, 1 dol, 2 desno, 3 levo
                
            if (igralec == 1) x += sirina;

           
        
            switch (smer) {
                case 1: 
                    povrsina[y][x] = 10 * i + 1; //premec
                    for (int j = 1; j < d; j++) {
                        povrsina[y - j][x] = 10 * i + 2;           
                    }
                    break;
                
                case 0:
                    povrsina[y][x] = 10 * i + 1; 
                    for (int j = 1; j < d; j++) {
                        povrsina[y + j][x] = 10 * i + 2;               
                    }
                    break;

                case 3:
                    povrsina[y][x] = 10 * i + 1; //premec
                    for (int j = 1; j < d; j++) {
                        povrsina[y][x + j] = 10 * i + 2;              
                    }
                    break;

                case 2:
                    povrsina[y][x] = 10 * i + 1; //premec
                    for (int j = 1; j < d; j++) {
                        povrsina[y][x - j] = 10 * i + 2;             
                    }
                    break;
                
                default:
                    break;        
            }
        }
        return povrsina;
    }

    private static boolean ladjaZnotrajMeja(int[] ladja, int[][] povrsina) {
        int igralec = ladja[0];
        int x = ladja[1];
        int y = ladja[2];
        int d = ladja[3]; // dolzina ladje
        int smer = ladja[4]; // smer ladje
    
        if (igralec == 1) x += sirina;
    
        switch (smer) {
            case 1:
                if (y - d + 1 >= 0) {
                    for (int j = 1; j < d; j++) {
                        if (povrsina[y - j][x] != 0) {
                            return false;
                        }
                    }
                } else return false;
                break;
    
            case 0:
                if (y + d - 1 < visina) {
                    for (int j = 0; j < d; j++) {
                        if (povrsina[y + j][x] != 0) {
                            return false;
                        }
                    }
                } else return false;
                break;
    
            case 3:
                if (x + d - 1 < (igralec == 0 ? sirina : 2 * sirina)) {
                    for (int j = 0; j < d; j++) {
                        if (povrsina[y][x + j] != 0) {
                            return false;
                        }
                    }
                } else return false;
                break;
    
            case 2: 
                if (x - d + 1 >= (igralec == 0 ? 0 : sirina)) {
                    for (int j = 0; j < d; j++) {
                        if (povrsina[y][x - j] != 0) {
                            return false;
                        }
                    }
                } else return false;
                break;
        }
    
        return true; 
    }

    public static void izrisiIgralnoPovrsino(int[][] igralnaPovrsina) {
        
        System.out.println("# ".repeat(sirina*2 + 3));

        for (int[] vrstica : igralnaPovrsina) {
            System.out.print("# ");
            for (int i = 0; i < vrstica.length; i++) {
                if (vrstica[i] % 10 == 1) {
                    System.out.print("p ");
                } else if (vrstica[i] % 10 == 2) {
                    System.out.print("t ");
                } else if (vrstica[i] % 10 == 3) {
                    System.out.print("X ");
                } else if (vrstica[i] % 10 == 4) {
                    System.out.print("x ");
                } else if (vrstica[i] % 10 == 5) {
                    System.out.print("@ ");
                } else if (vrstica[i] % 10 == 6) {
                    System.out.print("o ");
                } else {
                    System.out.print("  ");
                } 
                if (i == sirina-1) System.out.print("# ");
            }
            System.out.println("#");
        }

        System.out.println("# ".repeat(sirina*2 + 3));

    }


    // NALOGA 2
    public static int[][] povecajIgralnoPovrsino(int[][] postavitev, String noveDimenzije) {

        int novaVisina;
        int novaSirina;

        //če so nove dimenzije manjše ali narobe podane, ohrani prejšnje
        String[] sxv = noveDimenzije.split("x");

        if (sxv.length != 2) {
            return izdelajIgralnoPovrsino(postavitev);
        }

        try {
            novaSirina = Integer.parseInt(sxv[0]);
            novaVisina = Integer.parseInt(sxv[1]);
        } catch (NumberFormatException e) {
            return izdelajIgralnoPovrsino(postavitev);
        }

        if (novaSirina == sirina && novaVisina == visina) {
            return izdelajIgralnoPovrsino(postavitev);
        }

        //enakomerno razširi, najprej na gor in levo

        /*spremeni koordinate v postavitvi: 
            sirina: x++ za vsako liho
            visina: y++ za vsako liho  
        */

        if (novaSirina > sirina) {
            int razlikaS = novaSirina - sirina;
            for (int[] vrstica : postavitev) {
                vrstica[1] += (novaSirina % 2 == 1) ? razlikaS / 2 + 1 : razlikaS / 2;
            }
            sirina = novaSirina;
        }

        if (novaVisina > visina) {
            int razlikaV = novaVisina - visina;
            for (int[] vrstica : postavitev) {
                vrstica[2] += (novaVisina % 2 == 1) ? razlikaV / 2 + 1 : razlikaV / 2;
            }
            visina = novaVisina;
        }

        return izdelajIgralnoPovrsino(postavitev);
    }


    //NALOGA 3
    public static int[][] minimizirajIgralnoPovrsino(int[][] postavitev, int[][] igralnaPovrsina) {     
        
        //spremembe koordinat
        int sprx0 = 0;
        int sprx1 = 0;
        int spry = 0;

        //spremeni velikost igralne povrsine: nova sirina in visina

        //VISINA SEVER
        boolean prazno = true;
        for (int i = 0; i < igralnaPovrsina.length; i++) {
            for (int j = 0; j < igralnaPovrsina[0].length; j++) {
                if (igralnaPovrsina[i][j] != 0) {
                    prazno = false;
                    break;
                }
            }
            if (prazno) {
                visina--;
                spry--;
            } else break;
        }

        //VISINA JUG
        prazno = true;
        for (int i = igralnaPovrsina.length-1; i >= 0; i--) {
            for (int j = 0; j < igralnaPovrsina[0].length; j++) {
                if (igralnaPovrsina[i][j] != 0) {
                    prazno = false;
                    break;
                }
            }
            if (prazno) {
                visina--;
                //spry++;
            } else break;
        }


        int sirina0 = sirina;
        int sirina1 = sirina;

        //SIRINA ZAHOD (levo): igralec 0
        prazno = true;
        for (int j = 0; j < igralnaPovrsina[0].length/2; j++) {
            for (int i = 0; i < igralnaPovrsina.length; i++) {
                if (igralnaPovrsina[i][j] != 0) {
                    prazno = false;
                    break;
                }
            }
            if (prazno) {
                sirina0--;
                sprx0--;
            } else break;
        }

        //SIRINA VZHOD (desno): igralec 0
        prazno = true;
        for (int j = igralnaPovrsina[0].length/2-1; j >= 0; j--) {
            for (int i = 0; i < igralnaPovrsina.length; i++) {
                if (igralnaPovrsina[i][j] != 0) {
                    prazno = false;
                    break;
                }
            }
            if (prazno) {
              sirina0--;
              //sprx0++;
            } else break;
        }

        //SIRINA ZAHOD (levo): igralec 1
        prazno = true;
        for (int j = igralnaPovrsina[0].length/2; j < igralnaPovrsina[0].length; j++) {
            for (int i = 0; i < igralnaPovrsina.length; i++) {
                if (igralnaPovrsina[i][j] != 0) {
                    prazno = false;
                    break;
                }
            }
            if (prazno) {
                sirina1--;
                sprx1--;
            } else break;
        }

        //SIRINA VZHOD (desno): igralec 1
        prazno = true;
        for (int j = igralnaPovrsina[0].length-1; j > igralnaPovrsina[0].length/2; j--) {
            for (int i = 0; i < igralnaPovrsina.length; i++) {
                if (igralnaPovrsina[i][j] != 0) {
                    prazno = false;
                    break;
                }
            }
            if (prazno) {
                sirina1--;
                //sprx1++;
            } else break;
        }
        
        sirina = Math.max(sirina0, sirina1);
        
       //System.out.printf("%d %d %d%n%n", sprx0, sprx1, spry);

       int sprx = Math.max(sprx0, sprx1);
        for (int i = 0; i < postavitev.length; i++) {
           // int igralec = postavitev[i][0];
            postavitev[i][2] += spry;
            postavitev[i][1] += sprx;
        }    
                      
        return izdelajIgralnoPovrsino(postavitev);
    }


    //NALOGA 4
    public static int[][] simulirajIgro(int[][] igralnaPovrsina, String imeDatoteke) {

        List<int[]> poteze = preberiPoteze(imeDatoteke, igralnaPovrsina);
        

        boolean prvi = true; //igralec tracking, najprej 0 potem 1
        for (int i = 0; i < poteze.size(); i++) { //čez vse poteze
                
            int x = poteze.get(i)[0];
            int y = poteze.get(i)[1];


            if (prvi) x += sirina;
            
            //zadet premec ali trup, ostane isti igralec
            if (igralnaPovrsina[y][x] % 10 == 1 || igralnaPovrsina[y][x] % 10 == 2) {
                igralnaPovrsina[y][x] += 2; //3 ali 4

                //TU PREVERIT ST POTOPLJENIH LADIJ NASPROTNIKA
                int igralec = (prvi) ? 1 : 0; //katerega se gleda
                if (vsePotopljeno(igralec, igralnaPovrsina)) break; 
                    
            } else if (igralnaPovrsina[y][x] % 10 == 3 || igralnaPovrsina[y][x] % 10 == 4) {
                prvi = !prvi;
                continue;

            } else {
                igralnaPovrsina[y][x] = 6;
                //se spremeni igralec
                prvi = !prvi;
               // System.out.println("spremenjeno v: " + prvi );
            }
        }
        
       // for (int[] p : igralnaPovrsina) {
        //    System.out.println(Arrays.toString(p));
        //}

        //potopljena ladja?
        
        for (int indeks = 0; indeks < steviloLadij; indeks++) {
            boolean potopljena = true; //dokler ni dokazano drugače
            
            zunanja:
            for (int y = 0; y < igralnaPovrsina.length; y++) {
                for (int x = 0; x < igralnaPovrsina[0].length; x++) {
                    int p = igralnaPovrsina[y][x];
                    if (p == 0 || p == 6) continue; //se skippa če je voda

                    if (p / 10 == indeks) { //če je p enak indeksu ladje 
                        //če del ni potopljen
                        if (p % 10 == 1 || p % 10 == 2) {
                            potopljena = false;
                            break zunanja;
                        }
                    }
                }
            }

            if (potopljena) { //za to ladjo

                for (int y = 0; y < igralnaPovrsina.length; y++) {
                    for (int x = 0; x < igralnaPovrsina[0].length; x++) {
                        int p = igralnaPovrsina[y][x];
                        if (p == 0 || p == 6) continue; //se skippa če je voda

                        if (p / 10 == indeks) {
                            igralnaPovrsina[y][x] = indeks*10 + 5;
                        }
                    }
                }
            }
            
        }
      
        return igralnaPovrsina;
    }

    
    private static boolean vsePotopljeno(int igralec, int[][] povrsina) {
        int startX = (igralec == 0) ? 0 : sirina;
        int endX = (igralec == 0) ? sirina : 2 * sirina;
    
        for (int y = 0; y < povrsina.length; y++) {
            for (int x = startX; x < endX; x++) {
                int p = povrsina[y][x];
          
                if (p % 10 == 1 || p % 10 == 2) {
                    return false;
                }
            }
        }

        return true;
    }

    private static List<int[]> preberiPoteze(String imeDat, int[][] povrsina) {

        List<int[]> poteze = new ArrayList<int[]>();

         try {
            Scanner sc = new Scanner(new File(imeDat));

            //branje datoteke: x, y (koordinati zadetka) zmanjsaj za 1, preskoci neveljavne

            boolean ok = true;
            while (sc.hasNext()) {
                ok = true;
                String vrstica = sc.next();
                //if (vrstica.length() != 3) ok = false;

                String[] pS = vrstica.split(",");
                if (pS.length != 2) {
                    ok = false;
                    continue;
                }

                int[] p = new int[2];
                p[0] = Integer.parseInt(pS[0]);
                p[1] = Integer.parseInt(pS[1]);

                if (p[0] <= 0 || p[1] <= 0 || p[0] > sirina || p[1] > visina) {
                    ok = false;
                  //  System.out.println(Arrays.toString(p));
                }
                
                if (ok) {
                    p[0]--;
                    p[1]--;
                    poteze.add(p); 
                } 

            }
            sc.close();

        } catch (FileNotFoundException e) {}

        return poteze;
    }


    //NALOGA 5 
    public static int[][] zasukajLadje(int[][] postavitev, String smerVetra) {

        //int[][] novaPostavitev = originalPostavitev(postavitev);
        postavitev = zamenjajSmer(postavitev, smerVetra);

        return izdelajIgralnoPovrsino(postavitev);
    }
    
    /*
    private static int[][] originalPostavitev(int[][] postavitev) {
        int[][] povrsina = new int[visina][2*sirina];
        boolean[] ok = new boolean[postavitev.length]; //true, ce je v original postavitvi noter
        
        int count = 0;
        for (int i = 0; i < postavitev.length; i++) {
            ok[i] = ladjaZnotrajMeja(postavitev[i], povrsina);
            if (ok[i]) count++;
        }

        int[][] novaPostavitev = new int[count][5];
        int indeks = 0;
        for (int i = 0; i < postavitev.length; i++) {
            if (ok[i]) {
                novaPostavitev[indeks] = postavitev[i];
                indeks++;
            }
        }
        
        return novaPostavitev;

    }
        */

    private static int[][] zamenjajSmer(int[][] postavitev, String smerVetra) {
        int smer = -1;
        //1. smer vetra
        switch (smerVetra) {
            case "S": smer = 0; break; //premec gor
            case "J": smer = 1; break; //dol
            case "V": smer = 2; break; //desno
            case "Z": smer = 3; break; //levo
            default:
                System.out.println("Napaka: Neveljavna smer vetra.");
                return null; 
        }

        //postavitve z novo smerjo
        for (int i = 0; i < postavitev.length; i++) {
            postavitev[i][4] = smer;
        }

        return postavitev;

    }
    
    
    //NALOGA 6
    public static int[][] zasukajLadjeBrezKolizij(int[][] postavitev, String smerVetra) {

        //spremenim smer
        postavitev = zamenjajSmer(postavitev, smerVetra);

        //spet pogledam katere ladje zdej pašejo v postavitev
        /*int[][] novaPostavitev = originalPostavitev(postavitev);
        System.out.println(novaPostavitev.length);
        for (int[] p : postavitev) {
            System.out.println(Arrays.toString(p));
        }
        System.out.println();
        */

       // izrisiIgralnoPovrsino(izdelajIgralnoPovrsino(postavitev));

        int[][] povrsina = new int[visina][sirina*2];
        int[][] novaPostavitev = new int[0][5];
        
        List<int[]> ladjeZunaj = new ArrayList<>();
        for (int[] ladja : postavitev) {
           // System.out.println(Arrays.toString(ladja)); 

            if (!ladjaZnotrajMeja(ladja, povrsina)) {
                ladjeZunaj.add(ladja);
              //  System.out.println(ladjeZunaj.size());   
            } else {
                int[][] temp = new int[novaPostavitev.length + 1][5];
                for (int i = 0; i < novaPostavitev.length; i++) {
                    temp[i] = novaPostavitev[i];
                }
                temp[novaPostavitev.length] = ladja; 
                novaPostavitev = temp;
                povrsina = izdelajIgralnoPovrsino(novaPostavitev);
            }
        }

       // izrisiIgralnoPovrsino(izdelajIgralnoPovrsino(novaPostavitev));

        //DO SEM OK
        //postavitev 12 ladij, novaPostavitev 8 -> delam naprej z novo postavitvijo, uporabljam postavitev.length

        //v ladjeZunaj so ladje, ki jim je treba zamenjat koordinate in jih dodat v koncnoPostavitev
        
        int[][] koncnaPovrsina = izdelajIgralnoPovrsino(postavitev);
        //izrisiIgralnoPovrsino(koncnaPovrsina);
      //  int stevec = novaPostavitev.length; //indeks praznega prostora v koncnaPostavitev:

        for (int[] ladja : ladjeZunaj) {
            boolean done = false; //ladja postavljena
            
            for (int y = 0; y < visina && !done; y++) {
                ladja[2] = y;

                for (int x = 0; x < sirina && !done; x++) {
                    
                    ladja[1] = x;

                    if (!ladjaZnotrajMeja(ladja, koncnaPovrsina)) {
                        continue;
                    }
                    
                    //System.out.println(x + " " + y);
                   
                    int[][] temp = new int[novaPostavitev.length + 1][5];
                    for (int i = 0; i < novaPostavitev.length; i++) {
                        temp[i] = novaPostavitev[i];
                    }
                    temp[novaPostavitev.length] = ladja; 
                    novaPostavitev = temp;
                    koncnaPovrsina = izdelajIgralnoPovrsino(novaPostavitev);
                    done = true;
                }
            }
        } 
            

        return izdelajIgralnoPovrsino(novaPostavitev);
    }


}