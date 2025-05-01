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

    @BeforeEach
    void setUpFile() throws IOException{
        img = new File("./src/test/java/ca/budgiekitten/steganography/cat.png");
        File txt = new File("./src/test/java/ca/budgiekitten/steganography/msg.txt");
        msg = new BufferedReader(new FileReader(txt));
        outputLocation = new File("./src/test/java/ca/budgiekitten/steganography/cat-output.png");
    }

    @AfterEach
    void cleanUpFile() throws IOException{
        if (outputLocation.exists()) {
            outputLocation.delete();
        }
        img = null;
        msg = null;
        outputLocation = null;
    }

    @Test
    @DisplayName("Check if embed can create a new file")
    void testEmbed() throws IOException {
        Embed.embed(img, msg, outputLocation);
        assertEquals(true, outputLocation.exists(), "Fails to write to output location");
    }

    void testExtract() {

    }



}