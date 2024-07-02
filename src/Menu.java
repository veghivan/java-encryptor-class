import java.io.IOException;
import java.util.Scanner;

public class Menu {
    private static final String APP_NAME = "Titkosító és Dekódoló Alkalmazás";
    private static final String APP_INFO = "Verzió: 1.0 ===== Készítette: Végh Iván (PT9860)";
    private static final String[] MENUES = { "Pillanatnyi menü: Fömenü",
            "Pillanatnyi menü: Fömenü/Titkosítási beállítások",
            "Pillanatnyi menü: Fömenü/Titkosítási beállítások/Titkosítás-Dekódolás" };
    private static final int MAINMENU = 0, IOMENU = 1, CRYPTICMENU = 2;

    public static Scanner scanner;
    private Encryptor XorCoding;


    /**
     * Konstruktor, amely inicializálja a {@link Scanner} objektumot, amelyet a felhasználói bemenet olvasására használunk.
     */
    public Menu() {
        scanner = new Scanner(System.in);
    }


    /**
     * Bekéri a felhasználótól egy egész számot. Amíg a felhasználó nem ad be egy érvényes egész számot,
     * addig folyamatosan kéri az újabb bevitelt.
     * @return Az olvasott egész szám.
     */
    public static int GetUserInputInt() {
        while (!scanner.hasNextInt()) {
            scanner.next(); // Elutasítja a nem egész inputot
            System.out.print("Kérlek, adj meg egy számot: ");
        }
        int input = scanner.nextInt();
        scanner.nextLine(); // Eltávolítja az újsor karaktert a bemeneti pufferről
        System.out.println();
        return input;
    }


    /**
     * Bekéri a felhasználótól a titkosítandó vagy dekódolandó szöveget. Ha a bemenet üres, folyamatosan kéri az újabb bevitelt.
     * @param isEncryption Igaz, ha a szöveg titkosításra kerül, hamis, ha dekódolásra.
     * @return A beolvasott szöveg.
     */
    public String getUserInputString(boolean isEncryption) {
        if (isEncryption) {
            System.out.print("\nKérlek, add meg a titkosítandó szöveget: ");
        } else {
            System.out.print("\nKérlek, add meg a dekódolandó titkosított szöveget: ");
        }

        String input = scanner.nextLine().trim(); // Beolvassa a felhasználó által megadott sort és eltávolítja a
                                                  // felesleges szóközöket

        while (input.isEmpty()) { // Ellenőrzi, hogy a beírt szöveg nem üres-e
            if (isEncryption) {
                System.out.print("Nem adtál meg titkosítandó szöveget. Kérlek, próbáld újra: ");
            } else {
                System.out.print("Nem adtál meg dekódolandó titkosított szöveget. Kérlek, próbáld újra: ");
            }
            input = scanner.nextLine().trim(); // Újra beolvassa a bemenetet
        }

        return input; // Visszaadja a megfelelően beírt szöveget
    }

    /**
     * Törli a terminál tartalmát, OS-függő parancssal.
     * Felhasználása fontos szerepet játszik az esztitikusabb konzolos felület
     * létrehozásában.
     */
    public void ClearScreen() {
        try {
            new ProcessBuilder(Main.CleanTerminalCommand).inheritIO().start().waitFor();
            // inheritIO összeköti az Java folyamattal az új processt. start() futtatja a
            // processt, Waitfor blokkolja a kód futását a szálon amég nem végzett a
            // process.
        } catch (IOException | InterruptedException e) {
            System.out.println("Hiba történt a képernyő tisztítása közben: " + e.getMessage());
        }

    }

    /**
     * Megjeleníti a menü fejlécét a könnyebb navigáció érdekében. 
     * A fejléc tartalmazza az alkalmazás nevét, verzióját, készítőjének nevét, valamint a jelenlegi menüpont nevét.
     * @param menuid A jelenleg aktív menüpont azonosítója, amely meghatározza, hogy melyik fejléc jelenik meg.
     */
    public void PrintHeader(int menuid) {

        if (menuid < 3 && menuid > -1) {
            ClearScreen();
            System.out.println("\n===== " + APP_NAME + " ===== " + APP_INFO + " =====\n\n\t" + MENUES[menuid] + "\n");
            System.out.println(
                    "===================================================================================================\n");
        } else
            System.out.println("ERROR!!: menuid out of bounds."); // erre majd exception kéne írni és hibakezelni!!!!!

    }


    /**
     * Megjeleníti a főmenüt és kezeli a felhasználó választásait.
     * Lehetőséget ad a felhasználónak a titkosítás, dekódolás és kilépés közötti választásra.
     */
    public void displayMainMenu() {
        while (true) {
            PrintHeader(MAINMENU);

            System.out.println("Válaszd ki a felhasználás típusát:\n");
            System.out.println("1. Titkosítás");
            System.out.println("2. Dekódolás");
            System.out.println("3. Kilépés\n");
            System.out.print("Válassz egy opciót: ");

            int choice = GetUserInputInt(); // It szükség lesz egy safeguard mesurere az inputot illetően!

            switch (choice) {
                case 1:
                    displayEncryptionSetupMenu(true);
                    break;
                case 2:
                    displayEncryptionSetupMenu(false);
                    break;
                case 3:
                    System.out.println("\nKilépés az alkalmazásból...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Érvénytelen választás, kérlek próbáld újra!");
                    System.out.print("\nNyomj Entert a továbblépéshez...");
                    scanner.nextLine(); // Itt vár az Enter megnyomására
                    break;
            }
        }
    }


    /**
     * Megjeleníti a titkosítási vagy dekódolási beállítások menüjét.
     * Lehetővé teszi a felhasználó számára, hogy válassza ki a titkosítási mód (szöveg vagy fájl), majd megadja a szükséges adatokat.
     * @param isEncryption Igaz, ha a művelet titkosítás, hamis, ha dekódolás.
     */
    public void displayEncryptionSetupMenu(boolean isEncryption) {

        while (true) {
            PrintHeader(IOMENU);

            System.out.println("Válaszd ki a titkosítás/dekódolás típusát:\n");
            System.out.println("1. Szöveg");
            System.out.println("2. Fájl");
            System.out.println("3. Visszalépés a Főmenübe\n");
            System.out.print("Válassz egy opciót: ");

            int choice = GetUserInputInt();

            switch (choice) {
                case 1:
                    String key = HandleEncryptionKey(isEncryption);
                    String originalText = getUserInputString(isEncryption);
                    XorCoding = new Encryptor(originalText, key, isEncryption, false);
                    displayCrypticMenu(XorCoding);
                    return;
                case 2:
                    key = HandleEncryptionKey(isEncryption);
                    originalText = Encryptor.readFileFromPath();
                    XorCoding = new Encryptor(originalText, key, isEncryption, true);
                    displayCrypticMenu(XorCoding);
                    return;
                case 3:
                    System.out.println(isEncryption ? "Fájl titkosítása..." : "Fájl dekódolása...");
                    return;
                default:
                    System.out
                            .print("Érvénytelen választás, kérlek próbáld újra!\n\nNyomj Entert-t a továbblépéshez...");
                    scanner.nextLine();
                    break;
            }
        }
    }


    /**
     * Megjeleníti a titkosítási vagy dekódolási folyamat eredményét, és kezelési lehetőségeket kínál a felhasználónak.
     * @param encryptor A titkosító objektum, amely tartalmazza az adatokat és a funkciókat a titkosítás/dekódolásra.
     */
    public void displayCrypticMenu(Encryptor encryptor) {

        while (true) {

            PrintHeader(CRYPTICMENU);
            if (encryptor.isEncryption()) {
                String encryptedText = encryptor.encrypt();
                System.out.println("Használt kulcs: " + encryptor.getKey());
                System.out.println("\nEredeti szöveg: " + encryptor.getText() + "\n");
                System.out.println("Titkosított szöveg: " + encryptedText + "\n\n");
                System.out.println("Válassz a következő opciók közül:\n");
                System.out.println("1. Visszalépés a Főmenübe");
                System.out.println("2. Szöveg fájlba irása");
                System.out.println("3. Program bezárása\n");
                System.out.print("Választás: ");
                int choice = GetUserInputInt();

                switch (choice) {
                    case 1:
                        return;
                    case 2:
                        Encryptor.writeTextToFile(encryptedText);
                        return;
                    case 3:
                        System.out.println("Kilépés az alkalmazásból...");
                        System.exit(0); // Program bezárása
                        break;
                    default:
                        System.out.print(
                                "Érvénytelen választás, kérlek próbáld újra!\n\nNyomj Entert-t a továbblépéshez...");
                        scanner.nextLine();
                        break;
                }
            } else {
                String encryptedText = encryptor.decrypt();
                System.out.println("Használt kulcs: " + encryptor.getKey());
                System.out.println("\nTitkosított szöveg: " + encryptor.getText() + "\n");
                System.out.println("Dekódolt szöveg: " + encryptedText + "\n\n");
                System.out.println("Válassz a következő opciók közül:\n");
                System.out.println("1. Visszalépés a Főmenübe");
                System.out.println("2. Program bezárása.\n");
                System.out.print("Választás: ");
                int choice = GetUserInputInt();

                switch (choice) {
                    case 1:
                        return;
                    case 2:
                        System.out.println("Kilépés az alkalmazásból...");
                        System.exit(0); // Program bezárása
                        break;
                    default:
                        System.out.println("Érvénytelen választás, kérlek próbáld újra!");
                        System.out.print("\nNyomj Enter-t a továbblépéshez...");
                        scanner.nextLine();
                        break;
                }
            }
        }
    }


    /**
     * Kezeli a titkosítási kulcs beállítását. A felhasználó dönthet a generált kulcs használata mellett, vagy megadhat egy saját kulcsot.
     * Helytelen bevitel esetén hibaüzetet ad a felhasználónak és újbóli probálkozást enged.
     * @param isEncryption Igaz, ha titkosítás történik, hamis, ha dekódolás.
     * @return Visszaadja a kiválasztott vagy megadott kulcsot.
     */
    public String HandleEncryptionKey(boolean isEncryption) {
        String key = new String();
        while (true) {
            if (isEncryption) {
                System.out.print("Szeretnél generált kulcsot használni a titkosításhoz? (i/n): ");
                String response = scanner.nextLine().trim().toUpperCase();
                if (response.equalsIgnoreCase("i")) {
                    key = Encryptor.generateEncryptionKey();
                    System.out.print("Generált kulcs: " + key);
                    System.out.println("\n\n\nFontos: Írd le és gondoskodj a HELYES kulcs biztonságos tárolásáról!");
                    break;
                } else if (response.equalsIgnoreCase("n")) {
                    System.out.print("\nKérlek, add meg a titkosítási kulcsot: ");
                    key = scanner.nextLine();
                    System.out.println("\n\nÁltalad megadott kulcs: " + key);
                    System.out.println("\n\nFontos: Írd le és gondoskodj a HELYES kulcs biztonságos tárolásáról!");
                    break;
                } else {
                    System.out.println("Hibás bevitel. Kérlek csak 'i' vagy 'n' választ adj!");
                }
            } else {
                System.out.print("\nKérlek, add meg a dekódolási kulcsot: ");
                key = scanner.nextLine();
                System.out.println("\n\nGyőződj meg róla, hogy ez a kulcs az, amivel a titkosítást végezték!");
                System.out.print("\nNyomj Enter-t a folytatáshoz...");
                scanner.nextLine(); // Várakozik az Enter megnyomására
                break;
            }
            
        }
        return key;
    }
}

