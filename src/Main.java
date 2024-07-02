

public class Main {
    public static String CleanTerminalCommand;
    public static void main(String[] args) {
        Menu menu = new Menu();
        DetermineClearCommand();   
        menu.displayMainMenu();
    }
    /**
     * Ez a függvény megvizsgálja, az OS-t, hogy később tudjuk melyik terminál parancsot használjuk a terminál törléséhez,
     * ezzel biztosítva egy felhasználóbarátabb, és esztétikusabb konzolos alkalmazást. Windowson a "cls", Unix alapú rendszereken
     * pedig a "clear" fog működni.
     */
    public static void DetermineClearCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Winfos
            CleanTerminalCommand = "cls";
        } else {
            // Linux <3, Mac(&cheese)
            CleanTerminalCommand = "clear";
        }
    }

}
