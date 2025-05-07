import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Egy egyszerűsített, 5x5-ös táblán játszható sakkjáték.
 * A játékban a felhasználó ('l', 'f', 'k' bábukkal) játszik egy egyszerű AI ellen ('L', 'F', 'K' bábukkal).
 * A bábuk mozgása korlátozott a kis tábla mérete miatt.
 * A játékos a konzolon keresztül adhatja meg a lépéseit, pl. "E5 E4".
 * Az AI egy egyszerű minimax algoritmussal választja a lépéseit.
 */
public class SakkJatek {
    /**
     * A tábla mérete (5x5).
     */
    static final int MERET = 5;
    /**
     * Üres mezőt jelző karakter.
     */
    static final char URES = '.';
    /**
     * A játékos bábuit jelző karakterek.
     */
    static final char[] JATEKOS_BABUK = {'l', 'f', 'k'};
    /**
     * Az AI bábuit jelző karakterek.
     */
    static final char[] AI_BABUK = {'L', 'F', 'K'};
    /**
     * A játéktábla kétdimenziós karaktertömbként.
     */
    static char[][] tabla = new char[MERET][MERET];

    /**
     * A játék fő metódusa. Inicializálja a táblát, kiírja a kezdeti állapotot,
     * fogadja a játékos lépéseit, lépteti az AI-t, és ellenőrzi a játék végét.
     * @param args Parancssori argumentumok (nem használt).
     */
    public static void main(String[] args) {
        tablaInicializalas();
        tablaKiiras();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Játékos lépése
            System.out.println("Add meg a lépésed (pl. E5 E4):");
            String jatekosLepes = scanner.nextLine();

            if (jatekosLepes.equalsIgnoreCase("kilep")) {
                System.out.println("Játék vége.");
                break;
            }

            if (ervenyesLepes(jatekosLepes, JATEKOS_BABUK)) {
                jatekosHuzas(jatekosLepes);
                tablaKiiras();

                // AI lépése
                System.out.println("AI lép...");
                String aiLepes = getAiLepes();
                if (aiLepes != null) {
                    System.out.println("AI lépése: " + aiLepes);
                    aiHuzas(aiLepes);
                    tablaKiiras();
                } else {
                    System.out.println("A játéknak vége!");
                    break;
                }

                // Győzelem ellenőrzése
                if (vesztett(AI_BABUK)) {
                    System.out.println("Gratulálok, nyertél!");
                    break;
                }
                if (vesztett(JATEKOS_BABUK)) {
                    System.out.println("Az AI nyert!");
                    break;
                }

            } else {
                System.out.println("Érvénytelen lépés!");
            }
        }
        scanner.close();
    }

    /**
     * Inicializálja a játéktáblát a kezdőállapotba.
     * Az AI bábui a felső sorban, a játékos bábui az alsó sorban helyezkednek el.
     */
    private static void tablaInicializalas() {
        for (int i = 0; i < MERET; i++) {
            for (int j = 0; j < MERET; j++) {
                tabla[i][j] = URES;
            }
        }

        // Gép bábuk (felső sor)
        tabla[0][0] = 'L';
        tabla[0][1] = 'F';
        tabla[0][2] = 'K';
        tabla[0][3] = 'F';
        tabla[0][4] = 'L';

        // Ember bábuk (alsó sor)
        tabla[4][0] = 'l';
        tabla[4][1] = 'f';
        tabla[4][2] = 'k';
        tabla[4][3] = 'f';
        tabla[4][4] = 'l';
    }

    /**
     * Kiírja a játéktábla aktuális állapotát a konzolra.
     * A tábla sorai számozva, oszlopai betűzve vannak.
     */
    private static void tablaKiiras() {
        System.out.println("  A B C D E");
        System.out.println("  +---+---+---+---+---+");
        for (int i = 0; i < MERET; i++) {
            System.out.print((i + 1) + " | ");
            for (int j = 0; j < MERET; j++) {
                System.out.print(tabla[i][j] + " | ");
            }
            System.out.println();
            System.out.println("  +---+---+---+---+---+");
        }
    }

    /**
     * Ellenőrzi, hogy a megadott lépés érvényes-e a kiválasztott játékos szempontjából.
     * A lépés formátumának "KezdőPozíció CélPozíció" kell lennie (pl. "E5 E4").
     * A metódus ellenőrzi a formátumot, a kezdő pozíción lévő bábut,
     * és hogy a cél pozícióra a bábu szabályosan léphet-e.
     * @param lepes A játékos által megadott lépés.
     * @param babuk A lépést végrehajtó játékos bábukészlete.
     * @return True, ha a lépés érvényes, egyébként false.
     */
    private static boolean ervenyesLepes(String lepes, char[] babuk) {
        if (lepes.length() != 5) return false;

        String[] reszek = lepes.split(" ");
        if (reszek.length != 2) return false;

        int[] kezdo = pozicioAtvaltas(reszek[0]);
        int[] cel = pozicioAtvaltas(reszek[1]);

        if (kezdo == null || cel == null) return false;
        char babu = tabla[kezdo[0]][kezdo[1]];
        boolean sajatBabu = false;
        for (char b : babuk) {
            if (babu == b) {
                sajatBabu = true;
                break;
            }
        }
        if (!sajatBabu || babu == URES) return false;

        List<int[]> lehetsegesLepesek = getLehetsegesLepesek(kezdo[0], kezdo[1], babu, tabla);

        for (int[] lehetsegesCel : lehetsegesLepesek) {
            if (lehetsegesCel[0] == cel[0] && lehetsegesCel[1] == cel[1]) {
                char celBabu = tabla[cel[0]][cel[1]];
                boolean ellensegesBabu = false;
                char[] ellenfelBabui = (babuk == JATEKOS_BABUK) ? AI_BABUK : JATEKOS_BABUK;
                for (char b : ellenfelBabui) {
                    if (celBabu == b) {
                        ellensegesBabu = true;
                        break;
                    }
                }
                return celBabu == URES || ellensegesBabu;
            }
        }

        return false; // A lépés nem érvényes a bábu szabályai szerint
    }

    /**
     * Végrehajtja a játékos lépését a táblán.
     * @param lepes A játékos által megadott érvényes lépés.
     */
    private static void jatekosHuzas(String lepes) {
        String[] reszek = lepes.split(" ");
        int[] kezdo = pozicioAtvaltas(reszek[0]);
        int[] cel = pozicioAtvaltas(reszek[1]);

        char babu = tabla[kezdo[0]][kezdo[1]];
        tabla[kezdo[0]][kezdo[1]] = URES;
        tabla[cel[0]][cel[1]] = babu;
    }

    /**
     * Végrehajtja az AI lépését a táblán.
     * @param lepes Az AI által választott érvényes lépés.
     */
    private static void aiHuzas(String lepes) {
        String[] reszek = lepes.split(" ");
        int[] kezdo = pozicioAtvaltas(reszek[0]);
        int[] cel = pozicioAtvaltas(reszek[1]);

        char babu = tabla[kezdo[0]][kezdo[1]];
        tabla[kezdo[0]][kezdo[1]] = URES;
        tabla[cel[0]][cel[1]] = babu;
    }

    /**
     * Átvált egy sakk pozíciót (pl. "A1") egy tábla koordinátára (pl. [4, 0]).
     * @param pozicio A sakk pozíció string formátumban.
     * @return Egy kételemű int tömb a tábla koordinátáival [sor, oszlop], vagy null, ha érvénytelen a pozíció.
     */
    private static int[] pozicioAtvaltas(String pozicio) {
        if (pozicio.length() != 2) return null;

        char oszlop = pozicio.toUpperCase().charAt(0);
        char sor = pozicio.charAt(1);

        int x = sor - '1';
        int y = oszlop - 'A';

        if (x < 0 || x >= MERET || y < 0 || y >= MERET) return null;

        return new int[]{x, y};
    }

    /**
     * Lekéri az AI által választott lépést a minimax algoritmus segítségével.
     * @return Az AI által választott lépés string formátumban, vagy null, ha a játéknak vége.
     */
    private static String getAiLepes() {
        int legjobbErtek = Integer.MIN_VALUE;
        String legjobbLepes = null;

        List<String> lepesek = osszesLehetosegesLepes(AI_BABUK);

        for (String lepes : lepesek) {
            char[][] ideiglenesTabla = tablaMasolas();
            huzas(lepes, AI_BABUK, ideiglenesTabla);
            int ertek = minimax(ideiglenesTabla, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, false); // 3 mélység
            if (ertek > legjobbErtek) {
                legjobbErtek = ertek;
                legjobbLepes = lepes;
            }
        }
        return legjobbLepes;
    }

    /**
     * A minimax algoritmus rekurzív implementációja a legjobb lépés megtalálásához.
     * @param aktualisTabla A tábla aktuális állapota.
     * @param melyseg A keresés mélysége.
     * @param alfa Alfa-béta vágáshoz használt alfa érték.
     * @param beta Alfa-béta vágáshoz használt béta érték.
     * @param gepLep True, ha az AI lép, false, ha a játékos lép.
     * @return A táblaállapot értéke az AI szempontjából.
     */
    private static int minimax(char[][] aktualisTabla, int melyseg, int alfa, int beta, boolean gepLep) {
        if (melyseg == 0 || vegeVan(aktualisTabla)) {
            return ertekel(aktualisTabla);
        }

        if (gepLep) {
            int legjobbErtek = Integer.MIN_VALUE;
            List<String> lepesek = osszesLehetosegesLepes(AI_BABUK, aktualisTabla);
            for (String lepes : lepesek) {
                char[][] ujTabla = tablaMasolas(aktualisTabla);
                huzas(lepes, AI_BABUK, ujTabla);
                legjobbErtek = Math.max(legjobbErtek, minimax(ujTabla, melyseg - 1, alfa, beta, false));
                alfa = Math.max(alfa, legjobbErtek);
                if (beta <= alfa) {
                    break; // Alfa-béta vágás
                }
            }
            return legjobbErtek;
        } else {
            int legrosszabbErtek = Integer.MAX_VALUE;
            List<String> lepesek = osszesLehetosegesLepes(JATEKOS_BABUK, aktualisTabla);
            for (String lepes : lepesek) {
                char[][] ujTabla = tablaMasolas(aktualisTabla);
                huzas(lepes, JATEKOS_BABUK, ujTabla);
                legrosszabbErtek = Math.min(legrosszabbErtek, minimax(ujTabla, melyseg - 1, alfa, beta, true));
                beta = Math.min(beta, legrosszabbErtek);
                if (beta <= alfa) {
                    break; // Alfa-béta vágás
                }
            }
            return legrosszabbErtek;
        }
    }

    /**
     * Lekéri az összes lehetséges lépést egy adott játékos számára az aktuális táblaállapotban.
     * @param babuk A lépni kívánó játékos bábukészlete.
     * @return Egy lista a lehetséges lépésekkel string formátumban.
     */
    private static List<String> osszesLehetosegesLepes(char[] babuk) {
        return osszesLehetosegesLepes(babuk, tabla);
    }

    /**
     * Lekéri az összes lehetséges lépést egy adott játékos számára egy adott táblaállapotban.
     * @param babuk A lépni kívánó játékos bábukészlete.
     * @param aktualisTabla A tábla aktuális állapota.
     * @return Egy lista a lehetséges lépésekkel string formátumban.
     */
    private static List<String> osszesLehetosegesLepes(char[] babuk, char[][] aktualisTabla) {
        List<String> lepesek = new ArrayList<>();
        for (int i = 0; i < MERET; i++) {
            for (int j = 0; j < MERET; j++) {
                char babu = aktualisTabla[i][j];
                boolean sajatBabu = false;
                for (char b : babuk) {
                    if (babu == b) {
                        sajatBabu = true;
                        break;
                    }
                }
                if (sajatBabu) {
                    List<int[]> lepesLehetosegek = getLehetsegesLepesek(i, j, babu, aktualisTabla);
                    for (int[] cel : lepesLehetosegek) {
                        lepesek.add(pozicioString(new int[]{i, j}) + " " + pozicioString(cel));
                    }
                }
            }
        }
        return lepesek;
    }

    /**
     * Lekéri egy adott bábu összes lehetséges lépését egy adott pozícióról az aktuális táblaállapotban.
     * Figyelembe veszi a bábu típusát és a tábla határait.
     * @param sor A bábu sorának indexe.
     * @param oszlop A bábu oszlopának indexe.
     * @param babu A bábut jelző karakter.
     * @param aktualisTabla A tábla aktuális állapota.
     * @return Egy lista a lehetséges célpozíciók koordinátáival.
     */
    private static List<int[]> getLehetsegesLepesek(int sor, int oszlop, char babu, char[][] aktualisTabla) {
        List<int[]> lepesek = new ArrayList<>();
        int[][] atlosIranyok = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        int[][] loIranyok = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        int[][] kiralyIranyok = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        switch (Character.toLowerCase(babu)) {
            case 'l': // Ló
                for (int[] irany : loIranyok) {
                    int ujSor = sor + irany[0];
                    int ujOszlop = oszlop + irany[1];
                    if (ervenyesPozicio(ujSor, ujOszlop) && (aktualisTabla[ujSor][ujOszlop] == URES || Character.isUpperCase(babu) != Character.isUpperCase(aktualisTabla[ujSor][ujOszlop]))) {
                        lepesek.add(new int[]{ujSor, ujOszlop});
                    }
                }
                break;
            case 'f': // Futó (több lépés átlósan)
                for (int[] irany : atlosIranyok) {
                    int aktualisSor = sor + irany[0];
                    int aktualisOszlop = oszlop + irany[1];
                    while (ervenyesPozicio(aktualisSor, aktualisOszlop)) {
                        char celBabu = aktualisTabla[aktualisSor][aktualisOszlop];
                        if (celBabu == URES || Character.isUpperCase(babu) != Character.isUpperCase(celBabu)) {
                            lepesek.add(new int[]{aktualisSor, aktualisOszlop});
                            if (celBabu != URES) { // Ha ellenséges bábura léptünk, nem mehetünk tovább azon az irányon
                                break;
                            }
                            aktualisSor += irany[0];
                            aktualisOszlop += irany[1];
                        } else { // Ha saját bábuba ütköztünk, nem mehetünk tovább
                            break;
                        }
                    }
                }
                break;
            case 'k': // Király
                for (int[] irany : kiralyIranyok) {
                    int ujSor = sor + irany[0];
                    int ujOszlop = oszlop + irany[1];
                    if (ervenyesPozicio(ujSor, ujOszlop) && (aktualisTabla[ujSor][ujOszlop] == URES || Character.isUpperCase(babu) != Character.isUpperCase(aktualisTabla[ujSor][ujOszlop]))) {
                        lepesek.add(new int[]{ujSor, ujOszlop});
                    }
                }
                break;
        }
        return lepesek;
    }

    /**
     * Ellenőrzi, hogy a megadott sor és oszlop indexek a táblán belül vannak-e.
     * @param sor A vizsgált sor indexe.
     * @param oszlop A vizsgált oszlop indexe.
     * @return True, ha a pozíció érvényes, egyébként false.
     */
    private static boolean ervenyesPozicio(int sor, int oszlop) {
        return sor >= 0 && sor < MERET && oszlop >= 0 && oszlop < MERET;
    }

    /**
     * Átvált egy tábla koordinátát (pl. [4, 0]) egy sakk pozícióra (pl. "A1").
     * @param pozicio A tábla koordinátái egy kételemű int tömbben [sor, oszlop].
     * @return A sakk pozíció string formátumban.
     */
    private static String pozicioString(int[] pozicio) {
        return (char) ('A' + pozicio[1]) + "" + (pozicio[0] + 1);
    }

    /**
     * Kiértékeli a tábla aktuális állapotát az AI szempontjából.
     * Egy egyszerű heurisztika: az AI bábuk számának és a játékos bábuk számának különbsége.
     * @param aktualisTabla A tábla aktuális állapota.
     * @return A tábla értéke. Pozitív érték az AI számára előnyös, negatív a játékos számára.
     */
    private static int ertekel(char[][] aktualisTabla) {
        int aiPontszam = 0;
        int jatekosPontszam = 0;
        for (int i = 0; i < MERET; i++) {
            for (int j = 0; j < MERET; j++) {
                for (char aiBabu : AI_BABUK) {
                    if (aktualisTabla[i][j] == aiBabu) {
                        aiPontszam++;
                    }
                }
                for (char jatekosBabu : JATEKOS_BABUK) {
                    if (aktualisTabla[i][j] == jatekosBabu) {
                        jatekosPontszam++;
                    }
                }
            }
        }
        return aiPontszam - jatekosPontszam;
    }

    /**
     * Ellenőrzi, hogy a játéknak vége van-e (az egyik játékos elvesztette az összes bábuját).
     * @param aktualisTabla A tábla aktuális állapota.
     * @return True, ha a játéknak vége, egyébként false.
     */
    private static boolean vegeVan(char[][] aktualisTabla) {
        return vesztett(AI_BABUK, aktualisTabla) || vesztett(JATEKOS_BABUK, aktualisTabla);
    }

    /**
     * Ellenőrzi, hogy egy adott játékos elvesztette-e az összes bábuját az aktuális táblán.
     * @param babuk A vizsgált játékos bábukészlete.
     * @return True, ha a játékos elvesztette az összes bábuját, egyébként false.
     */
    private static boolean vesztett(char[] babuk) {
        return vesztett(babuk, tabla);
    }

    /**
     * Ellenőrzi, hogy egy adott játékos elvesztette-e az összes bábuját egy adott táblaállapotban.
     * @param babuk A vizsgált játékos bábukészlete.
     * @param aktualisTabla A tábla aktuális állapota.
     * @return True, ha a játékos elvesztette az összes bábuját, egyébként false.
     */
    private static boolean vesztett(char[] babuk, char[][] aktualisTabla) {
        for (int i = 0; i < MERET; i++) {
            for (int j = 0; j < MERET; j++) {
                for (char babu : babuk) {
                    if (aktualisTabla[i][j] == babu) {
                        return false; // Van még bábu
                    }
                }
            }
        }
        return true; // Nincs több bábu
    }

    /**
     * Létrehozza a játéktábla egy másolatát.
     * @return A tábla másolata.
     */
    private static char[][] tablaMasolas() {
        return tablaMasolas(tabla);
    }

    /**
     * Létrehozza egy adott táblaállapot másolatát.
     * @param eredeti A másolandó tábla.
     * @return A tábla másolata.
     */
    private static char[][] tablaMasolas(char[][] eredeti) {
        char[][] masolat = new char[MERET][MERET];
        for (int i = 0; i < MERET; i++) {
            System.arraycopy(eredeti[i], 0, masolat[i], 0, MERET);
        }
        return masolat;
    }

    
    private static void huzas(String lepes, char[] babuk, char[][] aktualisTabla) {
        String[] reszek = lepes.split(" ");
        int[] kezdo = pozicioAtvaltas(reszek[0]);
        int[] cel = pozicioAtvaltas(reszek[1]);

        if (kezdo != null && cel != null) {
            char babu = aktualisTabla[kezdo[0]][kezdo[1]];
            boolean sajatBabu = false;
            for (char b : babuk) {
                if (babu == b) {
                    sajatBabu = true;
                    break;
                }
            }
            if (sajatBabu) {
                aktualisTabla[kezdo[0]][kezdo[1]] = URES;
                aktualisTabla[cel[0]][cel[1]] = babu;
            }
        }
    }
}