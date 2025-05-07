Dokumentáció a SakkJátékhoz
Ez a dokumentáció a SakkJatek nevű Java programot mutatja be, amely egy egyszerűsített, 5x5-ös táblán játszható sakkjátékot implementál. A játékban egy emberi játékos ('l', 'f', 'k' bábukkal) küzd meg egy egyszerű mesterséges intelligenciával ('L', 'F', 'K' bábukkal). A játék a konzolon keresztül zajlik, ahol a játékos szöveges parancsokkal adhatja meg a lépéseit. Az AI a lehetséges lépések kiértékelésére egy alapvető minimax algoritmust használ.

Általános működés
A program a main metódusban indul. Inicializálja a játéktáblát a kezdőállapotba, majd egy ciklusban fogadja a játékos lépéseit, lépteti az AI-t, és folyamatosan ellenőrzi a játék végét. A játékos a lépéseit a sakk pozícióinak megadásával adhatja meg (pl. "E5 E4"). Az AI a minimax algoritmus segítségével választja ki a legjobb lépését a megadott keresési mélységig. A játék akkor ér véget, ha az egyik fél elveszíti az összes bábuját.

Osztályok és metódusok
SakkJatek osztály
Ez a fő osztály, amely a játék logikáját tartalmazza.

Statikus konstansok:

MERET: A játéktábla mérete (jelenleg 5).
URES: Karakter, amely egy üres mezőt jelöl a táblán ('.').
JATEKOS_BABUK: Karaktertömb a játékos bábuit jelölő karakterekkel ('l' - ló, 'f' - futó, 'k' - király).
AI_BABUK: Karaktertömb az AI bábuit jelölő karakterekkel ('L' - ló, 'F' - futó, 'K' - király).
tabla: Egy statikus, kétdimenziós karaktertömb, amely a játéktáblát reprezentálja.
Statikus metódusok:

main(String[] args): A program belépési pontja. Inicializálja a játékot, kezeli a játékos bemenetét, lépteti az AI-t, és ellenőrzi a játék végét.
tablaInicializalas(): Beállítja a játéktáblát a kezdő pozíciókkal. Az AI bábui a tábla felső sorában, a játékos bábui az alsó sorában helyezkednek el.
tablaKiiras(): Kiírja a játéktábla aktuális állapotát a konzolra, sorokkal és oszlopokkal jelölve.
ervenyesLepes(String lepes, char[] babuk): Ellenőrzi, hogy a megadott lépés érvényes-e a lépni kívánó játékos szempontjából. Ellenőrzi a lépés formátumát, a kezdő pozíción lévő saját bábut, és a bábu szabályos mozgását a cél pozícióra.
jatekosHuzas(String lepes): Végrehajtja a játékos érvényes lépését a játéktáblán.
aiHuzas(String lepes): Végrehajtja az AI érvényes lépését a játéktáblán.
pozicioAtvaltas(String pozicio): Átvált egy sakk pozíciót (pl. "A1") egy tábla koordinátára (int tömb [sor, oszlop]). Érvénytelen pozíció esetén null-t ad vissza.
getAiLepes(): Meghatározza az AI következő lépését a minimax algoritmus segítségével. Végigiterál az AI összes lehetséges lépésén, értékeli azokat a minimax metódussal, és kiválasztja a legjobbat.
minimax(char[][] aktualisTabla, int melyseg, int alfa, int beta, boolean gepLep): A minimax algoritmus rekurzív implementációja alfa-béta vágással. Kiértékeli a táblaállapotokat a megadott mélységig, hogy megtalálja az AI számára optimális lépést.
osszesLehetosegesLepes(char[] babuk): Lekéri az összes lehetséges érvényes lépést a megadott játékos számára az aktuális táblaállapotban.
osszesLehetosegesLepes(char[] babuk, char[][] aktualisTabla): Lekéri az összes lehetséges érvényes lépést a megadott játékos számára egy adott táblaállapotban.
getLehetsegesLepesek(int sor, int oszlop, char babu, char[][] aktualisTabla): Meghatározza egy adott bábu összes lehetséges érvényes lépését egy adott pozícióról, figyelembe véve a bábu típusát és a tábla határait. A futó bábuk (f/F) mostantól több mezőt is léphetnek átlósan.
ervenyesPozicio(int sor, int oszlop): Ellenőrzi, hogy a megadott sor és oszlop indexek a játéktáblán belül esnek-e.
pozicioString(int[] pozicio): Átvált egy tábla koordinátát (int tömb [sor, oszlop]) egy sakk pozícióra (pl. "A1").
ertekel(char[][] aktualisTabla): Egy egyszerű heurisztikus függvény, amely kiértékeli a tábla aktuális állapotát az AI szempontjából (az AI bábuk számának és a játékos bábuk számának különbsége).
vegeVan(char[][] aktualisTabla): Ellenőrzi, hogy a játéknak vége van-e (az egyik játékos elvesztette az összes bábuját).
vesztett(char[] babuk): Ellenőrzi, hogy a megadott játékos elvesztette-e az összes bábuját az aktuális táblán.
vesztett(char[] babuk, char[][] aktualisTabla): Ellenőrzi, hogy a megadott játékos elvesztette-e az összes bábuját egy adott táblaállapotban.
tablaMasolas(): Létrehozza a játéktábla egy mély másolatát.
tablaMasolas(char[][] eredeti): Létrehozza egy adott játéktábla állapot mély másolatát.
huzas(String lepes, char[] babuk, char[][] aktualisTabla): Végrehajt egy lépést egy adott táblaállapotban.
Bábuk mozgása
A játékban a következő bábuk szerepelnek, és a mozgásuk a következőképpen van implementálva:

Ló ('l', 'L'): A hagyományos lóugrás szerint mozog (két mezőt egyenesen, majd egyet merőlegesen).
Futó ('f', 'F'): Átlósan mozoghat a táblán annyi mezőt, amennyit csak szeretne, amíg el nem éri a tábla szélét vagy egy másik bábut. Ha ellenséges bábura lép, azt leüti, és a lépés véget ér. Saját bábun nem léphet át.
Király ('k', 'K'): Egy mezőt léphet bármely irányba (vízszintesen, függőlegesen vagy átlósan).
AI működése
Az AI a minimax algoritmust használja a legjobb lépés kiválasztásához. A getAiLepes() metódusban a jelenlegi táblaállapotból kiindulva az AI megpróbál előre látni a lehetséges lépéseket a megadott mélységig (jelenleg 3). A minimax metódus rekurzívan értékeli a lehetséges táblaállapotokat az ertekel() heurisztikus függvény segítségével. Az AI célja, hogy maximalizálja a saját pontszámát (bábui számát) és minimalizálja a játékos pontszámát. Az alfa-béta vágás optimalizálja a keresést azáltal, hogy kizárja azokat az ágakat, amelyek biztosan nem befolyásolják a végső döntést.

Játék indítása és irányítása
Mentse el a kódot egy .java fájlba (pl. SakkJatek.java).
Fordítsa le a kódot egy Java fejlesztői környezetben (IDE) vagy a parancssorban a javac SakkJatek.java paranccsal.
Futtassa a lefordított kódot a java SakkJatek paranccsal.
A játék a konzolon indul el. A program kiírja a kezdeti táblaállapotot, majd kéri a játékos első lépését. A lépést a sakk pozícióinak megadásával kell megadni, szóközzel elválasztva a kezdő és a cél pozíciót (pl. A5 B4). A program ellenőrzi a lépés érvényességét, és ha érvényes, végrehajtja azt. Ezután az AI következik a saját lépésével. A játék addig folytatódik, amíg az egyik fél elveszíti az összes bábuját, vagy a játékos a kilep paranccsal be nem fejezi a játékot.
