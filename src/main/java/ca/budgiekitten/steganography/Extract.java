package ca.budgiekitten.steganography;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.io.PrintWriter;

public class Extract {

    public static String extract(File imgToExtract, File fileWriteTo) throws IOException {
        PrintWriter output = new PrintWriter(fileWriteTo);

        BufferedImage image = ImageIO.read(imgToExtract);
        int h = image.getHeight();
        int w = image.getWidth();

        boolean msgFound = false;

        String intitialText = "";

        for (int x = 0; x < w; x++) {

            if (msgFound) {
                // All msg are found
                break;
            } else {

                for (int y = 0; y < h; y++) {
                    int alpha = ( (image.getRGB(x, y) >> 24) & 0xff);
                    int red = ( (image.getRGB(x, y) >> 16) & 0xff);
                    int green = ( (image.getRGB(x, y) >> 8) & 0xff);
                    int blue = (image.getRGB(x, y) & 0xff);
                    // Get msg by mod 4 (2-bit embedded)
                    if (getExtractBit(alpha) % 4 == 0
                            && getExtractBit(red) % 4 == 0
                            && getExtractBit(green) % 4 == 0
                            && getExtractBit(blue) % 4 == 0) {
                        //The end of msg
                        msgFound = true;
                        break;
                    } else {
                        int charMsg = getExtractBit(alpha) * 64 + getExtractBit(red) * 16
                                + getExtractBit(green) * 4 + getExtractBit(blue);

                        if (intitialText.length() <= 65) {
                            intitialText += (char) charMsg;
                        }

                        output.print((char) charMsg);
                    }

                }

            }

        }

        // In case file doesn't close by itself
        output.close();

        return intitialText;

    }

    public static int getExtractBit(int color) {
        return color % 4;
    }

}
