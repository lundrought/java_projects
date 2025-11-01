'''
Data Structure Implementation and RPN Calculator
---------------------

Developed as part of Algorithms and Data Structures 1 course at University of Ljubljana.

All code implementation below is my own.
Course materials © Professor Janez Demšar, Faculty of Computer and Information Science, University of Ljubljana.
'''


import java.util.Scanner;

public class RPNCalculator {
    
    private static final int STEVILO_SKLADOV = 42;
    private Sequence<Stack<String>> ss;
    private boolean pogoj = false;

    interface Collection {
        static final String ERR_MSG_EMPTY = "Collection is empty.";
        static final String ERR_MSG_FULL = "Collection is full.";

        boolean isEmpty();
        boolean isFull();
        int size();
        String toString();
    }

    interface Stack<T> extends Collection {
        T top() throws CollectionException;
        void push(T x) throws CollectionException;
        T pop() throws CollectionException;
    }

    interface Sequence<T> extends Collection {
        static final String ERR_MSG_INDEX = "Wrong index in sequence.";
        T get(int i) throws CollectionException;
        void add(T x) throws CollectionException;
    }

    class CollectionException extends Exception {
        public CollectionException(String msg) {
            super(msg);
        }
    }

    class ArrayDeque<T> implements Stack<T>, Sequence<T> {
        private static final int DEFAULT_CAPACITY = 64;
        private T[] array;
        private int size;
        private int front;
        private int back;

        @SuppressWarnings("unchecked")
        public ArrayDeque() {
            array = (T[]) new Object[DEFAULT_CAPACITY]; //array z dolžino 64
            size = 0;
            front = 0;
            back = 0;
        }
        //za vmesnik Collection
        @Override
        public boolean isEmpty() {
            return size == 0; //vrne true, če je array prazen
        }

        @Override
        public boolean isFull() {
            return size == array.length; //vrne true, če je array poln
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < size; i++) { 
                sb.append(array[(front + i) % array.length]); 
                if (i < size - 1) {
                    sb.append(", ");
                }   
            }
            sb.append("]");
            return sb.toString();
        }

        //za stack
        @Override
        public T top() throws CollectionException {
            if (isEmpty()) {
                throw new CollectionException(Collection.ERR_MSG_EMPTY);
            }
            return array[(back - 1 + array.length) % array.length];
        }

        @Override
        public void push (T x) throws CollectionException {
            if (isFull()) {
                throw new CollectionException(Collection.ERR_MSG_FULL);
            }
            array[back] = x;
            back = (back + 1) % array.length;
            size++;
        }

        @Override
        public T pop() throws CollectionException {
            if (isEmpty()) {
                throw new CollectionException(Collection.ERR_MSG_EMPTY);
            }
            back = (back - 1 + array.length) % array.length;
            T x = array[back];
            array[back] = null; //izbriše element, da ni postopanja
            size--;
            return x;
        }


        //sequence
        @Override
        public T get(int i) throws CollectionException {
            if (i < 0 || i >= size) {
                throw new CollectionException(Sequence.ERR_MSG_INDEX);
            }
            return array[(front + i) % array.length];
        }

        @Override
        public void add(T x) throws CollectionException {
                if (isFull()) {
                throw new CollectionException(Collection.ERR_MSG_FULL);
            }
            array[back] = x;
            back = (back + 1) % array.length;
            size++;
        }

    }





    @SuppressWarnings("unchecked")
    public Naloga1() throws CollectionException {
        ss = new ArrayDeque<>();
        for (int i = 0; i < STEVILO_SKLADOV; i++) {
            ss.add(new ArrayDeque<>());
        }
        pogoj = false;
    }

    public static void main(String[] args) throws CollectionException {
        Naloga1 kalkulator = new Naloga1(); //objekt kalkulator
       

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String vrstica = sc.nextLine(); //prebere vrstico
           
            //pripravi prazne sklade
            for (int i = 0; i < STEVILO_SKLADOV; i++) {
                Stack<String> s = kalkulator.ss.get(i);
                while (!s.isEmpty()) {
                    s.pop();
                }
            }
            kalkulator.pogoj = false;

            //obdela vrstico
            kalkulator.razdeliInput(vrstica);
            
        }
        sc.close();
    }


    private void razdeliInput(String vrstica) throws CollectionException{
        String[] deli = vrstica.split(" ");
        Sequence<String> zaNaSklad = new ArrayDeque<>();
        boolean jeFun = false;
            for (String del : deli) {
            if (del.equals("fun")) {
            
                if (jeFun && zaNaSklad != null && !zaNaSklad.isEmpty()) {
                    fun(zaNaSklad); 
                }
                
                jeFun = true;
                zaNaSklad = new ArrayDeque<>();
            } else if (jeFun) {
                zaNaSklad.add(del); 
            } else {
                obdelaj(del, this.ss.get(0)); 
            }
        }

                
        if (!zaNaSklad.isEmpty()) {
            fun(zaNaSklad);
        }
    }

    private void obdelaj(String del, Stack<String> sklad) throws CollectionException {

        switch(del) {
            case " ":
                break;
            case "":
                break;
            case "echo": 
                if (sklad.isEmpty()) {
                    System.out.println();
                } else {
                    System.out.println(sklad.top());
                }
                break;
            case "pop": 
                sklad.pop();
                break;
            case "dup":
                sklad.push(sklad.top());
                break;
            case "dup2":
                String x = sklad.top();
                sklad.pop();
                String y = sklad.top();
                sklad.push(x);
                sklad.push(y);
                sklad.push(x);
                break;
            case "swap":
                x = sklad.pop();
                y = sklad.pop();
                sklad.push(x);
                sklad.push(y);
                break;
            
            case "char":
                int koda = Integer.parseInt(sklad.pop());
                char c = (char) koda;
                sklad.push(Character.toString(c));
                break;
            case "even":
                int stevilo = Integer.parseInt(sklad.pop());
                if (stevilo % 2 == 0) {
                    sklad.push("1");
                } else {
                    sklad.push("0");
                }
                break;
            case "odd":
                stevilo = Integer.parseInt(sklad.pop());
                if (stevilo % 2 == 1 || stevilo % 2 == -1) {
                    sklad.push("1");
                } else {
                    sklad.push("0");
                }
                break;
            case "!":
                stevilo = Integer.parseInt(sklad.pop());
                if (stevilo == 0) {
                    sklad.push("1");
                } else {
                    for (int i = stevilo - 1; i > 0; i--) {
                        stevilo *= i;
                    }
                    sklad.push(Integer.toString(stevilo));
                }
                break;
            case "len":
                x = sklad.pop();
                sklad.push(Integer.toString(x.length()));
                break;

            //zamenjajo zgornja 2 elementa, rabijo integerje
            case "<>":
                int a = Integer.parseInt(sklad.pop());
                int b = Integer.parseInt(sklad.pop());
                if (a == b) {
                    sklad.push("0");
                } else {
                    sklad.push("1");
                }
                break;
            case "<":
                a = Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                if (b < a) {
                    sklad.push("1");
                } else {
                    sklad.push("0");
                }
                break;
            case "<=":
                a = Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                if (b <= a) {
                    sklad.push("1");
                } else {
                    sklad.push("0");
                }
                break;
            case "==":
                a = Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                if (a == b) {
                    sklad.push("1");
                } else {
                    sklad.push("0");
                }
                break;
            case ">":
                a = Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                if (b > a) {
                    sklad.push("1");
                } else {
                    sklad.push("0");
                }
                break;
            case ">=":
                a = Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                if (b >= a) {
                    sklad.push("1");
                } else {
                    sklad.push("0");
                }
                break;
            case "+":
                a = Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                sklad.push(Integer.toString(a + b));
                break;
            case "-":
                a = Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                sklad.push(Integer.toString(b - a));
                break;
            case "*":
                a = Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                sklad.push(Integer.toString(a * b));
                break;
            case "/":
                a = Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                sklad.push(Integer.toString(b / a));
                break;
            case "%":
                a = Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                sklad.push(Integer.toString(b % a));
                break;
            case ".": //x y -> xy stringi
                x = sklad.pop();
                y = sklad.pop();
                sklad.push(y + x);
                break;
            case "rnd": //random med a in b
                a= Integer.parseInt(sklad.pop());
                b = Integer.parseInt(sklad.pop());
                if (a > b) {
                    int temp = a;
                    a = b;
                    b = temp;
                }
                int random;
                if (a == b) {
                    random = a;
                } else {
                    random = (int) (Math.random() * (b - a + 1) + a);
                }
                sklad.push(Integer.toString(random));
                break;
            
            case "then":
                a = Integer.parseInt(sklad.pop());
                pogoj = (a != 0);
                break;
            case "else":
                pogoj = !pogoj;
                break;

            case "print":
                print();
                break;
            case "clear":
                clear();
                break;
            case "run":
                run();
                break;
            case "loop":
                loop();
                break;
            case "reverse":
                reverse();
                break;
            case "move":
                move();
                break;
            case "fun":
                break;


            default:
                if (del.startsWith("?")) {
                    if (pogoj) { //true
                        obdelaj(del.substring(1), sklad);
                    }
                } else {
                    sklad.push(del);
                }
                break;
        }
    }


    //vsi teji dajo ven iz sklada 0 kar preberejo zase 

    private void print() throws CollectionException { //POPRAVI; od dna do vrha
        Stack<String> sklad = this.ss.get(0);
        int indeks = Integer.parseInt(sklad.pop());
        Stack<String> pomozenSklad = this.ss.get(indeks);
        Stack<String> kopiranSklad = new ArrayDeque<>();
        
        while (!pomozenSklad.isEmpty()) {
            kopiranSklad.push(pomozenSklad.pop());
        }
        
        while (!kopiranSklad.isEmpty()) {
            System.out.print(kopiranSklad.top() + " ");
            pomozenSklad.push(kopiranSklad.pop());
        
        }
        System.out.println();
    }

    private void reverse() throws CollectionException {
        Stack<String> sklad = this.ss.get(0);
        int indeks = Integer.parseInt(sklad.pop());
        Stack<String> pomozenSklad = this.ss.get(indeks);
        Stack<String> kopiranSklad1 = new ArrayDeque<>();
        Stack<String> kopiranSklad2 = new ArrayDeque<>();
        
        while (!pomozenSklad.isEmpty()) {
            kopiranSklad1.push(pomozenSklad.pop());
        }
        
        while (!kopiranSklad1.isEmpty()) {
            kopiranSklad2.push(kopiranSklad1.pop());
        }

        while (!kopiranSklad2.isEmpty()) {
            pomozenSklad.push(kopiranSklad2.pop());
        }
    }



    private void clear() throws CollectionException {
        Stack<String> sklad = this.ss.get(0);
        int indeks = Integer.parseInt(sklad.pop());
        Stack<String> pomozenSklad = this.ss.get(indeks);
        while (!pomozenSklad.isEmpty()) {
            pomozenSklad.pop();
        }
    }



    private void fun(Sequence<String> zaNaSklad) throws CollectionException {
        //dodajanje naslednjih x delov na sklad y
        Stack<String> sklad = this.ss.get(0);
        int indeks = Integer.parseInt(sklad.pop());
        int stUkazov = Integer.parseInt(sklad.pop());
        Stack<String> pomozenSklad = this.ss.get(indeks);
        
        for (int i = 0; i < stUkazov; i++) {
            String del = zaNaSklad.get(i);
            pomozenSklad.push(del);
        }
        for (int i = stUkazov; i < zaNaSklad.size(); i++) {
            String del = zaNaSklad.get(i);
            obdelaj(del, sklad);
        }
    }

    private void move() throws CollectionException {
        Stack<String> sklad = this.ss.get(0);
        int indeks = Integer.parseInt(sklad.pop());
        int zaPremik = Integer.parseInt(sklad.pop());
        Stack<String> pomozenSklad = this.ss.get(indeks);

        for (int i = 0; i < zaPremik; i++) {
            String del = sklad.pop();
            pomozenSklad.push(del);
        }
    }

    private void run() throws CollectionException {
        Stack<String> sklad = this.ss.get(0);
        int indeks = Integer.parseInt(sklad.pop());
        Stack<String> pomozenSklad = this.ss.get(indeks);
        Stack<String> kopiranSklad = new ArrayDeque<>();
        
        while (!pomozenSklad.isEmpty()) {
            kopiranSklad.push(pomozenSklad.pop());
        }
        
        while (!kopiranSklad.isEmpty()) {
            String del = kopiranSklad.pop();
            obdelaj(del, sklad);
            pomozenSklad.push(del);
        }
    }

    private void loop() throws CollectionException {
        Stack<String> sklad = this.ss.get(0);
        int indeks = Integer.parseInt(sklad.pop());
        int stPonovitev = Integer.parseInt(sklad.pop());

        Stack<String> pomozenSklad = this.ss.get(indeks);
        Stack<String> kopiranSklad = new ArrayDeque<>();

        //kopirat pomozen sklad v kopiran
        while (!pomozenSklad.isEmpty()) {
            kopiranSklad.push(pomozenSklad.pop());
            }

        //ponovit kopiranSklad stPonovitev-krat, vedno spet napolnit
        for (int i = 0; i < stPonovitev; i++) {
            while (!kopiranSklad.isEmpty()) {
                String del = kopiranSklad.pop();
                obdelaj(del, sklad);
                pomozenSklad.push(del);
            }
            while (!pomozenSklad.isEmpty()) {
            kopiranSklad.push(pomozenSklad.pop());
            }
        }

        while (!kopiranSklad.isEmpty()) {
            pomozenSklad.push(kopiranSklad.pop());
        }
    }
}

        





    
