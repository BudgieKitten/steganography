package ca.budgiekitten.steganography;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class EmbedExtractTest {
    File img;
    BufferedReader msg;
    File outputLocation;
    File decryptedText;

    @BeforeEach
    void setUpFile() throws IOException{
        img = new File("./src/test/java/ca/budgiekitten/steganography/cat.png");
        File txt = new File("./src/test/java/ca/budgiekitten/steganography/msg.txt");
        msg = new BufferedReader(new FileReader(txt));
        outputLocation = new File("./src/test/java/ca/budgiekitten/steganography/cat-output.png");
    }

    @AfterEach
    void cleanUpFile() throws IOException{
        if (outputLocation != null && outputLocation.exists()) {
            outputLocation.delete();
        }

        if (decryptedText != null && decryptedText.exists()) {
            decryptedText.delete();
        }
        img = null;
        msg = null;
        outputLocation = null;
        decryptedText = null;
    }

    @Test
    @DisplayName("Check if embed can create a new img file")
    void testEmbed() throws IOException {
        Embed.embed(img, msg, outputLocation);
        assertEquals(true, outputLocation.exists(), "Fails to write to output location");
    }

    @Test
    @DisplayName("Check if extract can create a new txt file")
    void testExtract() throws IOException {
        Embed.embed(img, msg, outputLocation);
        assertEquals(true, outputLocation.exists(), "Fails to write to output location");

        decryptedText = new File("./src/test/java/ca/budgiekitten/steganography/extracted.txt");
        Extract.extract(outputLocation, decryptedText);
        assertEquals(true, decryptedText.exists(), "Fails to write to decrypted file");
    }

}