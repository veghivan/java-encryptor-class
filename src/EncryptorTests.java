import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

class EncryptorTests {
    Encryptor encryptor;
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private ByteArrayInputStream testIn;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        encryptor = new Encryptor("Hello World", "SecretKey123456", true, false);
    }

    void setInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        if (Menu.scanner != null) {
            Menu.scanner.close(); // A régi scanner lezárása, ha már létezik
        }
        Menu.scanner = new Scanner(System.in);
    }

    @AfterEach
    void restoreSystem() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }


    // Encrypt metódus tesztek
    @Test
    void encryptBasicUsage() {
        assertNotEquals("Hello World", encryptor.encrypt());
    }

    @Test
    void encryptEmptyString() {
        encryptor.setText("");
        assertEquals("", encryptor.encrypt());
    }

    @Test
    void encryptWithNullText() {
        encryptor.setText(null);
        assertThrows(NullPointerException.class, () -> encryptor.encrypt());
    }

    @Test
    void encryptConsistencyCheck() {
        String firstResult = encryptor.encrypt();
        encryptor.setText("Hello World");
        assertEquals(firstResult, encryptor.encrypt());
    }

    @Test
    void encryptWithLongText() {
        String longText = "a".repeat(1000);
        encryptor.setText(longText);
        assertNotEquals(longText, encryptor.encrypt());
    }

    @Test
    void encryptWithDifferentKeys() {
        Encryptor anotherEncryptor = new Encryptor("Hello World", "DifferentKey", true, false);
        assertNotEquals(encryptor.encrypt(), anotherEncryptor.encrypt());
    }

    @Test
    void encryptWithSpecialCharacters() {
        encryptor.setText("Special!@#$%^&*()");
        assertNotNull(encryptor.encrypt());
    }

    @Test
    void encryptOutputIsBase64() {
        String result = encryptor.encrypt();
        assertTrue(result.matches("^[a-zA-Z0-9+/]+={0,2}$"));
    }

    @Test
    void encryptWithSpaces() {
        encryptor.setText("   ");
        assertEquals(encryptor.encrypt(), "c0VD");
    }

    @Test
    void encryptWithNumeric() {
        encryptor.setText("12345");
        assertNotEquals("12345", encryptor.encrypt());
    }

    // Decrypt metódus tesztek
    @Test
    void decryptBasicUsage() {
        encryptor.setEncryption(false);
        encryptor.setText(encryptor.encrypt());
        assertEquals("Hello World", encryptor.decrypt());
    }

    @Test
    void decryptWithModifiedBase64() {
        encryptor.setEncryption(false);
        encryptor.setText(encryptor.encrypt().substring(1));
        assertThrows(IllegalArgumentException.class, () -> encryptor.decrypt());
    }

    @Test
    void decryptEmptyString() {
        encryptor.setEncryption(false);
        encryptor.setText("");
        assertEquals("", encryptor.decrypt());
    }

    @Test
    void decryptWithNullText() {
        encryptor.setEncryption(false);
        encryptor.setText(null);
        assertThrows(NullPointerException.class, () -> encryptor.decrypt());
    }

    @Test
    void decryptConsistencyCheck() {
        encryptor.setEncryption(false);
        String encrypted = encryptor.encrypt();
        encryptor.setText(encrypted);
        assertEquals("Hello World", encryptor.decrypt());
        encryptor.setText(encrypted);
        assertEquals("Hello World", encryptor.decrypt());
    }

    @Test
    void decryptWithLongText() {
        String longText = "a".repeat(1000);
        encryptor.setText(longText);
        encryptor.setEncryption(true);
        String encrypted = encryptor.encrypt();
        encryptor.setEncryption(false);
        encryptor.setText(encrypted);
        assertEquals(longText, encryptor.decrypt());
    }

    @Test
    void decryptWithSpecialCharacters() {
        String text = "Special!@#$%^&*()";
        encryptor.setText(text);
        String encrypted = encryptor.encrypt();
        encryptor.setEncryption(false);
        encryptor.setText(encrypted);
        assertEquals(text, encryptor.decrypt());
    }

    @Test
    void decryptWithNumeric() {
        String text = "12345";
        encryptor.setText(text);
        String encrypted = encryptor.encrypt();
        encryptor.setEncryption(false);
        encryptor.setText(encrypted);
        assertEquals(text, encryptor.decrypt());
    }

    @Test
    void decryptWithSpaces() {
        String text = "   ";
        encryptor.setText(text);
        String encrypted = encryptor.encrypt();
        encryptor.setEncryption(false);
        encryptor.setText(encrypted);
        assertEquals(text, encryptor.decrypt());
    }

    // Tesztesetek a transform() metódushoz
    @Test
    void transformWithEmptyStringReturnsEmpty() {
        assertEquals("", encryptor.transform(""));
    }

    @Test
    void transformWithNullStringThrowsException() {
        assertThrows(NullPointerException.class, () -> encryptor.transform(null));
    }

    @Test
    void transformWithSimpleText() {
        String original = "Test";
        String transformed = encryptor.transform(original);
        assertNotEquals(original, transformed);
    }

    @Test
    void transformIsReversible() {
        String original = "Reversible Test";
        String transformed = encryptor.transform(original);
        String reversed = encryptor.transform(transformed);
        assertEquals(original, reversed);
    }

    @Test
    void transformWithAllZeroKey() {
        encryptor.setKey("\0\0\0\0\0\0\0\0\0\0");
        assertEquals("Hello World", encryptor.transform("Hello World"));
    }

    @Test
    void transformWithSpecialCharacters() {
        String specialChars = "!@#$%^&*()_+";
        String transformed = encryptor.transform(specialChars);
        assertNotEquals(specialChars, transformed);
    }

    @Test
    void transformWithNonAsciiCharacters() {
        String unicodeText = "こんにちは世界";
        String transformed = encryptor.transform(unicodeText);
        assertNotEquals(unicodeText, transformed);
    }

    @Test
    void transformWithNumericValues() {
        String numeric = "1234567890";
        String transformed = encryptor.transform(numeric);
        assertNotEquals(numeric, transformed);
    }

    @Test
    void transformWithLongString() {
        String longString = "a".repeat(1000);
        String transformed = encryptor.transform(longString);
        assertNotEquals(longString, transformed);
    }

    @Test
    void transformWithCyclicKeyUsage() {
        String longString = "a".repeat(100);
        encryptor.setKey("abc");
        String transformed = encryptor.transform(longString);
        assertNotEquals(longString, transformed);
        assertEquals(longString.length(), transformed.length());
    }

    @Test
    void transformMaintainsLength() {
        String testString = "LengthTest";
        String transformed = encryptor.transform(testString);
        assertEquals(testString.length(), transformed.length());
    }

    // readFile tesztek
    @Test
    void testReadFileIOException() {
        String testFileName = "nonexistent.txt";
        setInput(testFileName + "\n");
        assertThrows(RuntimeException.class, Encryptor::readFileFromPath, "Expected to throw exception for non-existent file");
    }


    //writetextofile tesztek
    @Test
    void testWriteTextToFileIOException() {
        String testFileName = "/invalid/path/to/output.txt";
        String testContent = "Test Content";
        setInput(testFileName + "\n");
        assertThrows(RuntimeException.class, () -> Encryptor.writeTextToFile(testContent), "Expected to throw exception for invalid file path");
    }

    
}