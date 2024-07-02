import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuTests {
    private final InputStream originalIn = System.in;
    private ByteArrayInputStream testIn;

    @BeforeEach
    void setUp() {
        // Itt nincs szükség arra, hogy újra beállítsuk a Scanner-t, mert minden teszt előtt külön-külön beállítjuk az inputot
        // Menu.scanner = new Scanner(System.in); // Ezt töröljük, mert minden tesztben külön beállítjuk
    }

    void setUpInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        if (Menu.scanner != null) {
            Menu.scanner.close(); // A régi scanner lezárása, ha már létezik
        }
        Menu.scanner = new Scanner(System.in);
    }

    @AfterEach
    void restoreInput() {
        System.setIn(originalIn);
        if (Menu.scanner != null) {
            Menu.scanner.close(); // Biztosítjuk, hogy minden teszt után lezárjuk a Scannert
        }
        Menu.scanner = new Scanner(System.in);
    }

    @Test
    void testGetUserInputIntWithValidInput() {
        setUpInput("5\n");
        assertEquals(5, Menu.GetUserInputInt());
    }

    @Test
    void testGetUserInputIntWithMultipleInputs() {
        setUpInput("abc\n7\n");
        assertEquals(7, Menu.GetUserInputInt());
    }

    @Test
    void testGetUserInputIntWithNegativeInput() {
        setUpInput("-10\n");
        assertEquals(-10, Menu.GetUserInputInt());
    }

    @Test
    void testGetUserInputIntWithLeadingSpaces() {
        setUpInput("   15\n");
        assertEquals(15, Menu.GetUserInputInt());
    }

    @Test
    void testGetUserInputIntWithInvalidThenValidInput() {
        setUpInput("invalid\n20\n");
        assertEquals(20, Menu.GetUserInputInt());
    }

    @Test
    void testGetUserInputIntWithOnlyInvalidInputs() {
        setUpInput("invalid\nabc\n");
        assertThrows(NoSuchElementException.class, () -> Menu.GetUserInputInt());
    }

    @Test
    void testGetUserInputIntWithMultipleValidInputs() {
        setUpInput("34\n56\n");
        assertEquals(34, Menu.GetUserInputInt());
        // Ellenőrizzük, hogy a második szám még mindig elérhető a streamben a következő olvasásra
        assertEquals(56, Menu.GetUserInputInt());
}

    @Test
    void testGetUserInputIntWithNoInput() {
        setUpInput("\n");
        assertThrows(NoSuchElementException.class, () -> Menu.GetUserInputInt());
    }


    //GetUserInputString tesztek
    @Test
    void testGetUserInputStringWithValidInput() {
        setUpInput("Hello World\n");
        assertEquals("Hello World", new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringWithEmptyInputThenValid() {
        setUpInput("\n\nHello World\n");
        assertEquals("Hello World", new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringWithOnlySpacesThenValid() {
        setUpInput("   \nHello World\n");
        assertEquals("Hello World", new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringWithSpecialCharacters() {
        setUpInput("Hello\nWorld\tTest\n");
        assertEquals("Hello", new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringWithLongInput() {
        String longInput = "a".repeat(10000) + "\n";
        setUpInput(longInput);
        assertEquals("a".repeat(10000), new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringWithMultipleEmptyInputs() {
        setUpInput("\n\n\n\nHello World\n");
        assertEquals("Hello World", new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringValidAfterEmpty() {
        setUpInput("\n\nHello World\n");
        assertEquals("Hello World", new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringWithExcessiveSpaces() {
        setUpInput("     Hello World    \n");
        assertEquals("Hello World", new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringWithVariousLanguageCharacters() {
        setUpInput("Helló Wörld こんにちは 你好\n");
        assertEquals("Helló Wörld こんにちは 你好", new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringWithNoInputThenValid() {
        setUpInput("\n\n\nHello World\n");
        assertEquals("Hello World", new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringWithContinuousSpaces() {
        setUpInput("      \n      \nHello World\n");
        assertEquals("Hello World", new Menu().getUserInputString(true));
    }

    @Test
    void testGetUserInputStringWithMultipleValidInputs() {
        setUpInput("Hello\nWorld\n");
        assertEquals("Hello", new Menu().getUserInputString(true));
        setUpInput("World\n"); // Reset and provide second valid input
        assertEquals("World", new Menu().getUserInputString(true));
    }


    //HandleEncriptionKey függvény tesztjei
    @Test
    void testHandleEncryptionKeyWithGeneratedKey() {
        setUpInput("i\n16\n");
        assertNotNull(new Menu().HandleEncryptionKey(true));
    }

    @Test
    void testHandleEncryptionKeyWithUserInputKey() {
        String userKey = "MyCustomKey";
        setUpInput("n\n" + userKey + "\n");
        assertEquals(userKey, new Menu().HandleEncryptionKey(true));
    }

    @Test
    void testHandleEncryptionKeyWithInvalidOptionThenGeneratedKey() {
        setUpInput("x\ni\n16\n");
        assertNotNull(new Menu().HandleEncryptionKey(true));
    }

    @Test
    void testHandleEncryptionKeyWithEmptyInputThenUserKey() {
        String userKey = "ValidKey";
        setUpInput("\n\nn\n" + userKey + "\n");
        assertEquals(userKey, new Menu().HandleEncryptionKey(true));
    }

    @Test
    void testHandleEncryptionKeyWithMultipleInvalidInputsThenGeneratedKey() {
        setUpInput("invalid\ninvalid\ni\n16\n");
        assertNotNull(new Menu().HandleEncryptionKey(true));
    }

    @Test
    void testHandleEncryptionKeyNoInputAndExit() {
        setUpInput("\n\n\n");
        assertThrows(NoSuchElementException.class, () -> new Menu().HandleEncryptionKey(true));
    }

    @Test
    void testHandleEncryptionKeyWithSpacesAndUserInputKey() {
        String userKey = "KeyWithSpaces";
        setUpInput("n\n   " + userKey + "   \n");
        assertEquals("   " + userKey + "   ", new Menu().HandleEncryptionKey(true));
    }

    @Test
    void testHandleEncryptionKeyIgnoreExtraNewLines() {
        setUpInput("i\n\n\n16\n");
        assertNotNull(new Menu().HandleEncryptionKey(true));
    }

    @Test
    void testHandleEncryptionKeyContinuousInvalidOptions() {
        setUpInput("invalid\ninvalid\nn\nUserKey123\n");
        assertEquals("UserKey123", new Menu().HandleEncryptionKey(true));
    }

    @Test
    void testHandleEncryptionKeyWithImmediateExit() {
        setUpInput("exit\n");
        assertThrows(NoSuchElementException.class, () -> new Menu().HandleEncryptionKey(true));
    }

    @Test
    void testHandleEncryptionKeyWithMultipleOptionsAndFinalUserKey() {
        String userKey = "FinalChoice";
        setUpInput("invalid\nx\nn\n" + userKey + "\n");
        assertEquals(userKey, new Menu().HandleEncryptionKey(true));
    }
}
