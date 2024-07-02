import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Iterator;
import java.util.Random;


public class Encryptor implements Iterable<Character> {
    private String key;
    private String text;  // Eredeti vagy titkosított szöveg
    
    private boolean isEncryption = true; // Ez a változó tárolja, hogy a felhasználó titkosítást vagy dekódolást választ
    private boolean isFile = false; // szöveg vagy fájl
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isEncryption() {
        return isEncryption;
    }

    public void setEncryption(boolean isEncryption) {
        this.isEncryption = isEncryption;
    }


    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }   


    /**
     * Konstruktor, mely létrehoz egy Encryptor példányt a megadott paraméterekkel.
     * @param text A titkosítandó vagy dekódolandó szöveg.
     * @param key A titkosítási kulcs.
     * @param isEncription Meghatározza, hogy a művelet titkosítás (true) vagy dekódolás (false).
     * @param isFile Meghatározza, hogy a szöveg fájlból származik-e.
     */
    public Encryptor(String text, String key, boolean isEncription, boolean isFile) {
        this.key = key;
        this.text = text;
        this.isEncryption = isEncription;
        this.isFile = isFile;
    }


    /**
     * Titkosítja a szöveget az osztályban tárolt kulcs segítségével.
     * @return A titkosított szöveg Base64 kódolásban.
     */
    public String encrypt() {
        return Base64.getEncoder().encodeToString(transform(text).getBytes());
    }


    /**
     * Dekódolja a szöveget az osztályban tárolt kulcs segítségével.
     * @return A dekódolt szöveg.
     */
    public String decrypt() {
        byte[] bytes = Base64.getDecoder().decode(text);
        return new String(transform(new String(bytes)));
    }


    /**
     * Végrehajtja a XOR transzformációt a megadott adaton a kulcs alapján.
     * @param data A transzformálandó szöveg.
     * @return A transzformált szöveg.
     */
    public String transform(String data) {
        StringBuilder transformed = new StringBuilder();
        Iterator<Character> keyIterator = this.iterator();
        for (char charToTransform : data.toCharArray()) {
            char keyChar = keyIterator.next();
            char transformedChar = (char) (charToTransform ^ keyChar);  // Csak XOR, nincs korlátozás(reverziblis titkosítás érdekében)
            transformed.append(transformedChar);
        }
        return transformed.toString();
    }


    /**
     * Visszaad egy iterátort, mely ciklikusan ismétli a kulcs karaktereit.
     * @return Az iterátor, mely végigiterál a kulcs karakterein.
     */
    @Override
    public Iterator<Character> iterator() {
        return new Iterator<Character>() {
            private int position = 0;

            @Override
            public boolean hasNext() {
                return true; // Mindig van következő elem, mivel ciklikusan ismételjük a kulcsot
            }

            @Override
            public Character next() {
                if (position >= key.length()) {
                    position = 0; // Ha a kulcs végére értünk, kezdjük újra 
                }
                char result = key.charAt(position);
                position++;
                return result;
            }
        };
    }


    /**
     * Generál egy véletlenszerű titkosítási kulcsot a megadott hosszúság szerint.
     * @return A generált titkosítási kulcs.
     */
    public static String generateEncryptionKey() {
        System.out.print("\nAdd meg a kulcs hosszát (magas védelem 16-tól): ");
        int length = Menu.GetUserInputInt();
        System.out.println("");
        
        String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,@;:!#$%^&*()_+=-[]{}|<>?/";
        StringBuilder key = new StringBuilder();
        Random random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characterSet.length());
            key.append(characterSet.charAt(index));
        }

        return key.toString();
    }
    //@iue+4?)MU

    /**
     * Beolvassa a szöveget egy fájlból, amelynek elérési útját a felhasználó adja meg.
     * A függvény addig kéri a fájl elérési útját, amíg nem sikerül sikeresen beolvasni a fájlt.
     * Ha IO kivétel történik, hibaüzenet jelenik meg és újra kéri az elérési utat.
     * @return A fájl tartalma szöveges formátumban.
     */
    public static String readFileFromPath() {
        while (true) {
            String filePath;
            System.out.print("\nKérlek, add meg a beolvasandó fájl elérési útvonalát és nevét\n(pl. /path/to/file.txt): ");
            filePath = Menu.scanner.nextLine().trim();
            try {
                String content = new String(Files.readAllBytes(Paths.get(filePath)));
                System.out.print("Fájl beolvasása sikeres!\n\nNyomj Enter-t a továbblépéshez...");
                Menu.scanner.nextLine();
                return content;
            } catch (IOException e) {
                System.out.println("\nHiba történt a fájl olvasása közben: " + e.getMessage());
                System.out.println("\nPróbáld újra.\n\nNyomj Enter-t a továbblépéshez...");
                Menu.scanner.nextLine();
            }
        }
    }


    /**
     * A megadott szöveget egy fájlba írja, amelynek elérési útját és nevét a felhasználó adja meg.
     * A függvény addig kéri a fájl mentési helyét és nevét, amíg a szöveg sikeresen nem kerül mentésre.
     * Ha IO kivétel történik a fájl írása során, hibaüzenet jelenik meg és újra kéri a mentési helyet.
     * @param text A fájlba írandó szöveg.
     */
    public static void writeTextToFile(String text) {
        while (true) {
            System.out.print("Kérlek, add meg a fájl mentési helyét és nevét\n(pl. /path/to/file.txt): ");
            String filePath = Menu.scanner.nextLine().trim();
            try {
            Files.write(Paths.get(filePath), text.getBytes());
            System.out.print("\nA szöveg sikeresen elmentve ide: " + filePath + "\n\nNyomj Enter-t a továbblépéshez...");
            Menu.scanner.nextLine();
            return;
            } catch (IOException e) {
                System.out.println("\nHiba történt a fájl írása közben: " + e.getMessage());
                System.out.println("\nNem sikerült a fájlt menteni. Próbáld újra.\n\nNyomj Enter-t a továbblépéshez...");
                Menu.scanner.nextLine();
            }
        }
    }
}
