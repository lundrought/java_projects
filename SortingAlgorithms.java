'''
Sorting Algorithms Implementation
---------------------

Developed as part of Algorithms and Data Structures 1 course at University of Ljubljana.

All code implementation below is my own.
Course materials © Professor Jurij Mihelič, Faculty of Computer and Information Science, University of Ljubljana.
'''



import java.util.Scanner;

@FunctionalInterface
interface AlgoritemZaUrejanje {
    void uredi(int[] zaporedje, boolean narascajoce, boolean sled);
}

interface Collection {
    static final String ERR_MSG_EMPTY = "Collection is empty.";
    static final String ERR_MSG_FULL = "Collection is full.";
    boolean isEmpty();
    int size();
    String toString();
}

interface Sequence<T> extends Collection {
    static final String ERR_MSG_INDEX = "Wrong index in sequence.";
    T get(int i) throws CollectionException;
    void add(T x);
}

class CollectionException extends Exception {
    public CollectionException(String msg) {
        super(msg);
    }
}

public class SortingAlgorithms {

    public static int VELIKOST_ZAPOREDJA;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String nastavitveUrejanja = sc.nextLine();
        String zaporedjeStevil = sc.nextLine();

        String[] nastavitve = nastavitveUrejanja.split("\\s+");
        String nacinDelovanja = nastavitve[0];
        String nacinUrejanja = nastavitve[1];
        String smerUrejanja = nastavitve[2];

        ResizableArray<Integer> zaporedje = new ResizableArray<>();
        String[] zaporedjeS = zaporedjeStevil.split("\\s+");
        for (String stevilo : zaporedjeS) {
            zaporedje.add(Integer.parseInt(stevilo));
        }
        VELIKOST_ZAPOREDJA = zaporedje.size();
        if (nacinDelovanja.equals("count")) {
            prestej(zaporedje.toArray(), nacinUrejanja, smerUrejanja);
        }
        uredi(zaporedje.toArray(), nacinDelovanja, nacinUrejanja, smerUrejanja);

        sc.close();
    }

    public static void uredi(int[] zaporedje, String nacinDelovanja, String nacinUrejanja, String smerUrejanja) {
        AlgoritemZaUrejanje algoritem = vrniAlgoritem(nacinUrejanja);
        boolean narascajoce = smerUrejanja.equals("up"); //true, če ja
        boolean sled = nacinDelovanja.equals("trace");

        algoritem.uredi(zaporedje, narascajoce, sled);
    }

    public static void prestej(int[] zaporedje, String nacinUrejanja, String smerUrejanja) {
        AlgoritemSStetjem algoritem = (AlgoritemSStetjem) vrniAlgoritem(nacinUrejanja);
        boolean narascajoce = smerUrejanja.equals("up");

        //1. sortiranje danega zaporedja
        int[] kopija1 = new int[zaporedje.length];
        kopiraj(zaporedje, kopija1);
        algoritem.uredi(kopija1, narascajoce, false);
        int premiki1 = algoritem.vrniPremike();
        int primerjave1 = algoritem.vrniPrimerjave();

        //2. sortiranje urejenega v isto smer
        int[] kopija2 = new int[kopija1.length];
        kopiraj(kopija1, kopija2);
        algoritem.uredi(kopija2, narascajoce, false);
        int premiki2 = algoritem.vrniPremike();
        int primerjave2 = algoritem.vrniPrimerjave();

        //3. sortiranje urejenega v drugo smer
        int[] kopija3 = new int[kopija1.length];
        kopiraj(kopija1, kopija3);
        algoritem.uredi(kopija3, !narascajoce, false);
        int premiki3 = algoritem.vrniPremike();
        int primerjave3 = algoritem.vrniPrimerjave();

        System.out.println(premiki1 + " " + primerjave1 + " | " + premiki2 + " " + primerjave2 + " | " + premiki3 + " " + primerjave3);

    }


    public static void kopiraj(int[] vir, int[] kopija) {
        for (int i = 0; i < vir.length; i++) {
            kopija[i] = vir[i];
        }
    }


    public static AlgoritemZaUrejanje vrniAlgoritem(String nacinUrejanja) {
        switch (nacinUrejanja) {
            case "insert":
                return new NavadnoVstavljanje();
            case "select":
                return new NavadnoIzbiranje();
            case "bubble":
                return new NavadnaZamenjava();
            case "heap":
                return new UrejanjeSKopico();
            case "merge":
                return new UrejanjeZZlivanjem();
            case "quick":
                return new HitroUrejanje();
            case "radix":
                return new KorenskoUrejanje();
            case "bucket":
                return new UrejanjeSKosi();
        }
        return null;
    }

    public static void printArray(int[] zaporedje) {
        for (int i = 0; i < zaporedje.length; i++) {
            System.out.print(zaporedje[i]);
            if (i < zaporedje.length - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    public static void printSled(int[] zaporedje, int urejenoIndeks) {
        for (int i = 0; i < zaporedje.length; i++) {
            if (i == urejenoIndeks) {
                System.out.print("| ");
            }
            System.out.print(zaporedje[i] + " ");
        }
        if (urejenoIndeks == zaporedje.length) {
            System.out.print("|");
        }
        System.out.println();
    }


    private static abstract class AlgoritemSStetjem implements AlgoritemZaUrejanje {
        protected int premiki;
        protected int primerjave;

        public int vrniPremike() {
            return this.premiki;
        }

        public int vrniPrimerjave() {
            return this.primerjave;
        }

        protected void ponastaviStetje() {
            premiki = 0;
            primerjave = 0;
        }

        protected void swap(int[] zaporedje, int i, int j) {
            int temp = zaporedje[i];
            zaporedje[i] = zaporedje[j];
            zaporedje[j] = temp;
            
        }

    }

    private static class NavadnoVstavljanje extends AlgoritemSStetjem {

        @Override
        public void uredi(int[] zaporedje, boolean narascajoce, boolean sled) {
            ponastaviStetje();
            if (sled) printArray(zaporedje);
            for (int i = 1; i < zaporedje.length; i++) {
                int trenuten = zaporedje[i];
                premiki++;
                int j = i - 1;
                while (j >= 0) {
                    primerjave++;
                    if (narascajoce ? zaporedje[j] > trenuten : zaporedje[j] < trenuten) {
                        zaporedje[j + 1] = zaporedje[j];
                        premiki++;
                        j--;
                    } else {
                        break;
                    }

                }
                zaporedje[j + 1] = trenuten;
                premiki++;
                if (sled) printSled(zaporedje, i + 1);
            }
        }
    }

    private static class NavadnoIzbiranje extends AlgoritemSStetjem {
        @Override
        public void uredi(int[] zaporedje, boolean narascajoce, boolean sled) {
            ponastaviStetje();
            if (sled) printArray(zaporedje);
            for (int i = 0; i < zaporedje.length - 1; i++) {
                int iMinAliMax = i;
                for (int j = i + 1; j < zaporedje.length; j++) {
                    primerjave++;
                    if (narascajoce ? zaporedje[j] < zaporedje[iMinAliMax] : zaporedje[j] > zaporedje[iMinAliMax]) {
                        iMinAliMax = j;
                    }
                }
                swap(zaporedje, i, iMinAliMax);
                premiki += 3;
                if (sled) printSled(zaporedje, i + 1);
            }
        }
    }

    //izboljsan bubblesort
    private static class NavadnaZamenjava extends AlgoritemSStetjem {
        @Override
        public void uredi(int[] zaporedje, boolean narascajoce, boolean sled) {
            ponastaviStetje();
            int dolzina = zaporedje.length;
            int zIndeks = dolzina - 1;
            boolean preskoceno = false;

            int predzadnji = 0;
            int zadnji = 0;

            if (sled) printArray(zaporedje);


            for (int i = 0; i < dolzina; i++) {
                boolean zamenjano = false;

                for (int j = dolzina - 1; j > predzadnji; j--) {
                    primerjave++;
                    if (narascajoce ? zaporedje[j - 1] > zaporedje[j] : zaporedje[j - 1] < zaporedje[j]) {
                        swap(zaporedje, j - 1, j);
                        premiki += 3;
                        zamenjano = true;
                        zIndeks = j;

                        zadnji = j - 1;
                    }
                }

                predzadnji = zadnji + 1;

                if (!zamenjano) {
                    preskoceno = true;
                    if (sled) {
                        printSled(zaporedje, dolzina - 1);
                    }
                    break;
                }
                if (sled && zIndeks != dolzina - 1) {
                    printSled(zaporedje, zIndeks);
                }
            }
        }
    }


    private static class UrejanjeSKopico extends AlgoritemSStetjem {
        @Override
        public void uredi(int[] zaporedje, boolean narascajoce, boolean sled) {
            ponastaviStetje();
            int n = zaporedje.length;
            if (sled) {
                printArray(zaporedje);
            }

            //naredi kopico
            for (int i = n /2 - 1; i >= 0; i--) {
                urediKopico(zaporedje, n, i, narascajoce);
            }
            if (sled) printSled(zaporedje, n);

            for (int i = n - 1; i > 0; i--) {
                swap(zaporedje, 0, i);
                premiki += 3;
                urediKopico(zaporedje, i, 0, narascajoce);
                if (sled) printSled(zaporedje, i);
            }
        }

        void urediKopico(int[] zaporedje, int n, int i, boolean narascajoce) {
            int najvecji = i;
            int levi = 2 * i + 1;
            int desni = 2 * i + 2;

            if (levi < n) {
                primerjave++;
                if (narascajoce ? zaporedje[levi] > zaporedje[najvecji] : zaporedje[levi] < zaporedje[najvecji]) {
                    najvecji = levi;
                }
            }
            if (desni < n) {
                primerjave++;
                if (narascajoce ? zaporedje[desni] > zaporedje[najvecji] : zaporedje[desni] < zaporedje[najvecji]) {
                    najvecji = desni;
                }
            }

            if (najvecji != i) {
                swap(zaporedje, i, najvecji);
                premiki += 3;
                urediKopico(zaporedje, n, najvecji, narascajoce);
            }
        }
    }

    private static class UrejanjeZZlivanjem extends AlgoritemSStetjem {
        @Override
        public void uredi(int[] zaporedje, boolean narascajoce, boolean sled) {
            ponastaviStetje();
            if(sled) printArray(zaporedje);	
            razdeli(zaporedje, 0, zaporedje.length - 1, sled, narascajoce);

        }

        private void razdeli(int[] zaporedje, int leva, int desna, boolean sled, boolean narascajoce) {
            if (leva < desna) {
                int sredina = leva + (desna - leva) / 2;

                if (sled) {
                    for (int a = leva; a <= desna; a++) {
                        System.out.print(zaporedje[a] + " ");
                        if (a == sredina)
                            System.out.print("| ");
                    }
                    System.out.println();
                }

                razdeli(zaporedje, leva, sredina, sled, narascajoce);
                razdeli(zaporedje, sredina + 1, desna, sled, narascajoce);
                zlij(sredina, leva, desna, zaporedje, sled, narascajoce);

                if (sled) {
                    for (int a = leva; a <= desna; a++) {
                        System.out.print(zaporedje[a] + " ");
                    }
                    System.out.println();
                }
            }
        }

        private void zlij(int sredina, int leva, int desna, int[] zaporedje, boolean sled, boolean narascajoce) {
            int velikostLeve = sredina - leva + 1;
            int velikostDesne = desna - sredina;

            int[] levaPolovica = new int[velikostLeve];
            int[] desnaPolovica = new int[velikostDesne];

            for (int i = 0; i < velikostLeve; ++i) {
                levaPolovica[i] = zaporedje[leva + i];
                premiki += 1;
            }
            for (int j = 0; j < velikostDesne; ++j) {
                desnaPolovica[j] = zaporedje[sredina + 1 + j];
                premiki += 1;
            }

            int i = 0, j = 0, k = leva;

            while (i < velikostLeve && j < velikostDesne) {
                primerjave++;
                if (narascajoce ? levaPolovica[i] <= desnaPolovica[j] : levaPolovica[i] >= desnaPolovica[j]) {
                    zaporedje[k] = levaPolovica[i];
                    i++;
                } else {
                    zaporedje[k] = desnaPolovica[j];
                    j++;
                }
                k++;
                premiki++;
            }

            while (i < velikostLeve) {
                zaporedje[k++] = levaPolovica[i++];
                premiki++;
            }
            while (j < velikostDesne) {
                zaporedje[k++] = desnaPolovica[j++];
                premiki++;
            }
        }
    }

    private static class HitroUrejanje extends AlgoritemSStetjem {
        @Override
        public void uredi(int[] zaporedje, boolean narascajoce, boolean sled) {
            ponastaviStetje();
            if (sled) printArray(zaporedje);
            int zgMeja = zaporedje.length - 1;
            int spMeja = 0;
            razdeli(zaporedje, spMeja, zgMeja, narascajoce, sled);
        }

        private int razdelitev(int[] zaporedje, int levi, int desni, int smer){
            int pivot = zaporedje[levi];
            premiki++;

            int l = levi + 1;
            int r = desni;

            while(true){
                primerjave++;

                while((zaporedje[l] - pivot) * smer < 0 && l < desni){
                    primerjave++; l++;
                }
                primerjave++;

                while((zaporedje[r] - pivot) * smer > 0){
                    primerjave++; r--;
                }

                if(l >= r) break;
        
                swap(zaporedje, l, r);
                premiki += 3;

                l++; r--;
            }

            swap(zaporedje, levi, r);
            premiki += 3;

            return r;
        }

        private int[] razdeli(int[] zaporedje, int spMeja, int zgMeja, boolean narascajoce, boolean sled) {
            if (spMeja < zgMeja) {
                int pivot = razdelitev(zaporedje, spMeja, zgMeja, narascajoce ? 1 : -1);

                if (sled) {
                    for (int a = spMeja; a < zgMeja + 1; a++) {
                        if (a == pivot)
                            System.out.print("| ");
                        System.out.print(zaporedje[a] + " ");
                        if (a == pivot)
                            System.out.print("| ");
                    }
                    System.out.println();
                }

                razdeli(zaporedje, spMeja, pivot - 1, narascajoce, sled);
                razdeli(zaporedje, pivot + 1, zgMeja, narascajoce, sled);

            } if (spMeja == 0 && zgMeja == zaporedje.length - 1) {
                if(sled) printArray(zaporedje);
            }
            return zaporedje;
        }
    }

    private static class KorenskoUrejanje extends AlgoritemSStetjem {
        @Override
        public void uredi(int[] zaporedje, boolean narascajoce, boolean sled) {
            ponastaviStetje();
            if (sled) printArray(zaporedje);

            int max = vrniNajvecjega(zaporedje);
            for (int faktor = 1; max / faktor > 0; faktor *= 10) {
                urediPoStevkah(zaporedje, faktor, narascajoce);
                if (sled) printArray(zaporedje);
            }
        }

        private int vrniNajvecjega(int[] zaporedje) {
            int naj = zaporedje[0];
            for (int i = 0; i < zaporedje.length; i++) {
                //primerjave++;
                if (zaporedje[i] > naj) naj = zaporedje[i];
            }
            return naj;
        }

        private void urediPoStevkah(int[] zaporedje, int faktor, boolean narascajoce) {
            int n = zaporedje.length;
            int[] urejeno = new int[n];
            int[] frekvence = new int[10];


            //zaporedje s frekvencami stevk
            for (int i = 0; i < n; i++) {
                int stevka = (zaporedje[i] / faktor) % 10;
                frekvence[stevka]++;
                primerjave++;
            }

            //zaporedje s kumulativo
            if (narascajoce) {
                for (int i = 1; i < 10; i++) {
                    frekvence[i] += frekvence[i - 1];
                }
            } else {
                for (int i = 8; i >= 0; i--) {
                    frekvence[i] += frekvence[i + 1];
                }
            }

            //urejeno zaporedje, vstavljanje od zadaj
            for (int i = n - 1; i >= 0; i--) {
                int stevka = (zaporedje[i] / faktor) % 10;
                urejeno[frekvence[stevka] - 1] = zaporedje[i];
                frekvence[stevka]--;
                premiki++;
                primerjave++;
            }

            for (int i = 0; i < n; i++) {
                zaporedje[i] = urejeno[i];
                premiki++;
            }





        }
    }

    private static class UrejanjeSKosi extends AlgoritemSStetjem {
        @Override
        public void uredi(int[] zaporedje, boolean narascajoce, boolean sled) {
            ponastaviStetje();
            if (sled) printArray(zaporedje);

            int n = zaporedje.length / 2;

            int max = zaporedje[0];
            for (int i = 0; i < zaporedje.length; i++)
                if (zaporedje[i] > max)
                    max = zaporedje[i];

            int min = max;
            for (int i = 0; i < zaporedje.length; i++)
                if (zaporedje[i] < min)
                    min = zaporedje[i];

            int v = (int) Math.ceil((max - min + 1) / (float)n);

            int[][] bucket = new int[n][zaporedje.length];
            int[] bucketst = new int[n];


            for (int i = 0; i < zaporedje.length; i++) {
                int bucketIndex = (zaporedje[i] - min) / v;
                bucket[bucketIndex][bucketst[bucketIndex]] = zaporedje[i];
                bucketst[bucketIndex]++;

                premiki++;
            }

            int[] bucketzaporedje = new int[zaporedje.length];
            int st = 0;

            if(narascajoce) {
                for (int i = 0; i < n; i++) {
                    for (int o = 0; o < bucketst[i]; o++) {
                        bucketzaporedje[st] = bucket[i][o];

                        if (sled)
                            System.out.print(bucketzaporedje[st] + " ");

                        st++;
                    }
                    if (sled && i != n - 1)
                        System.out.print("| ");
                }
            }
            else{
                for (int i = n-1; i >= 0; i--) {
                    for (int o = 0; o < bucketst[i]; o++) {
                        bucketzaporedje[st] = bucket[i][o];

                        if (sled)
                            System.out.print(bucketzaporedje[st] + " ");

                        st++;
                    }
                    if (sled && i != 0)
                        System.out.print("| ");
                }
            }
            if(sled)
                System.out.println();

            for (int i = 1; i < bucketzaporedje.length; i++) {
                int trenuten = bucketzaporedje[i];
                premiki++;
                int j = i - 1;
                while (j >= 0) {
                    primerjave++;
                    if (narascajoce ? bucketzaporedje[j] > trenuten : bucketzaporedje[j] < trenuten) {
                        bucketzaporedje[j + 1] = bucketzaporedje[j];
                        premiki++;
                        j--;
                    } else {
                        break;
                    }

                }
                bucketzaporedje[j + 1] = trenuten;
                premiki++;
                if (sled) printSled(bucketzaporedje, i + 1);
            }
        }
    }

    private static class ResizableArray<T> implements Sequence<T> {
        private Object[] zaporedje;
        private int velikost;
        private static final int ZACETNA_KAPACITETA = 10;

        public ResizableArray() {
            zaporedje = new Object[10];
            velikost = 0;
        }

        @Override
        public int size() {
            return velikost;
        }

        @Override
        public void add(T vrednost) {
            if (velikost == zaporedje.length) {
                resize();
            }
            zaporedje[velikost++] = vrednost;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T get(int indeks) throws CollectionException {
            if (indeks < 0 || indeks >= velikost) {
                throw new CollectionException(ERR_MSG_INDEX);
            }
            return (T) zaporedje[indeks];
        }

        @Override
        public boolean isEmpty() {
            return velikost == 0;
        }

        private void resize() {
            Object[] temp = new Object[zaporedje.length * 2];
            for (int i = 0; i < zaporedje.length; i++) {
                temp[i] = zaporedje[i];
            }
            zaporedje = temp;
        }

        public int[] toArray() {
            int[] rezultat = new int[velikost];
            for (int i = 0; i < velikost; i++) {
                rezultat[i] = (int) zaporedje[i];
            }
            return rezultat;
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < velikost; i++) {
                sb.append(zaporedje[i]);
                if (i < velikost - 1) {
                    sb.append(" ");
                }
            }
            return sb.toString();
        }
    }
}








   