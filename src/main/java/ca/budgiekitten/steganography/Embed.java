package ca.budgiekitten.steganography;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;


public class Embed {

    public static void embed(File imgToEmbed, BufferedReader msg, File imgOutputLocation) throws IOException {
        // Read and acquire the matrix of image
        BufferedImage image = ImageIO.read(imgToEmbed);
        int h = image.getHeight();
        int w = image.getWidth();

        Color[][] colors = new Color[w][h];

        // Check if the msg is fully embedded
        boolean finishMsg = false;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                // Get ARGB of the image
                int alpha = ( (image.getRGB(x, y) >> 24) & 0xff);
                int red = ( (image.getRGB(x, y) >> 16) & 0xff);
                int green = ( (image.getRGB(x, y) >> 8) & 0xff);
                int blue = (image.getRGB(x, y) & 0xff);

                int charFromMsg = msg.read();

                // Process line of text char by char and write it to a file
                if (charFromMsg != -1 && !finishMsg) {
                    // Process the first char
                    int bit = charFromMsg;

                    if (bit > 127) {
                        // A character that is not UTF-8 and UTF-16 is not supported
                        // '?' will be substituted to mark this unknown char
                        bit = 63;
                    }

                    // Number of bits to embed
                    int embed;

                    // Check if all ARGB values are embedded
                    boolean embedA = false;
                    boolean embedR = false;
                    boolean embedG = false;
                    boolean embedB = false;

                    boolean embedAllARGB = embedA && embedR && embedG && embedB;

                    while (bit > 0 || !embedAllARGB) {
                        // 2-bit are embedded (base 4 system)
                        if (bit >= 64) {
                            embed = bit / 64;
                            int remainder = bit - embed * 64;
                            bit = remainder;

                            alpha += getEmbedBit(alpha, embed);
                            embedA = true;
                        } else if (bit >= 16 && bit < 64) {
                            embed = bit / 16;
                            int remainder = bit - embed * 16;
                            bit = remainder;

                            red += getEmbedBit(red, embed);
                            embedR = true;
                        } else if (bit >= 4 && bit < 16) {
                            embed = bit / 4;
                            int remainder = bit - embed * 4;
                            bit = remainder;

                            green += getEmbedBit(green, embed);
                            embedG = true;
                        } else if (bit >= 1 && bit < 4) {
                            embed = bit;
                            int remainder = bit - embed;
                            bit = remainder;

                            blue += getEmbedBit(blue, embed);
                            embedB = true;
                        }
                        // Check if any color value is not embedded
                        if (!embedA && bit == 0) {
                            alpha += getEmbedBit(alpha, 0);
                            embedA = true;
                        }

                        if (!embedR && bit == 0) {
                            red += getEmbedBit(red, 0);
                            embedR = true;
                        }

                        if (!embedG && bit == 0) {
                            green += getEmbedBit(green, 0);
                            embedG = true;
                        }

                        if (!embedB && bit == 0) {
                            blue += getEmbedBit(blue, 0);
                            embedB = true;
                        }

                        embedAllARGB = embedA && embedR && embedG && embedB;
                    }

                } else if (charFromMsg == -1 && !finishMsg){
                    /*
                    All parts of the messages have been processed.
                    Add null (0,0,0,0) to signal the end of msg (there is no msg left).
                    Load the original pixels to the remaining image.
                     */
                    alpha += getEmbedBit(alpha, 0);
                    red += getEmbedBit(red, 0);
                    green += getEmbedBit(green, 0);
                    blue += getEmbedBit(blue, 0);

                    finishMsg = true;
                }

                colors[x][y] = new Color(red, green, blue, alpha);

                /*
                try {
                    colors[x][y] = new Color(red, green, blue, alpha);
                } catch (IllegalArgumentException ex) {

                    alpha = getEmbedBit( ( (image.getRGB(x, y) >> 24) & 0xff), 0 );
                    red = getEmbedBit( ( (image.getRGB(x, y) >> 16) & 0xff), 3 );
                    green = getEmbedBit( ( (image.getRGB(x, y) >> 8) & 0xff), 3 );
                    blue = getEmbedBit( (image.getRGB(x, y) & 0xff), 3 );

                    colors[x][y] = new Color(red, green, blue, alpha);
                }
                 */

            }
        }

        // Close the scanner
        msg.close();

        /*
        Write msg to img
         */
        BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                // BufferedImage getRGB doesn't include alpha so have to set manually
                int alpha = colors[x][y].getAlpha() << 24;
                int red = colors[x][y].getRed() << 16;
                int green = colors[x][y].getGreen() << 8;
                int blue = colors[x][y].getBlue();

                newImage.setRGB(x, y, alpha + red + green + blue);
            }
        }

        String outputFile = imgOutputLocation.toString();
        String imgExtension = outputFile.substring(outputFile.lastIndexOf(".") + 1);
        if (!imgExtension.equalsIgnoreCase("jpeg")
                && !imgExtension.equalsIgnoreCase("bmp")) {
            imgExtension = "png";
        }
        ImageIO.write(newImage, imgExtension, imgOutputLocation);

    }

    private static int getEmbedBit(int color, int bit) {
        // Pls try to understand the logic of this function
        if (color % 4 == bit) {
            return 0;
        }

        if (color + 4 <= 131) {
            if (color % 4 < bit) {
                return ( bit - (color % 4)); // Will result in negative number
            } else if (color % 4 > bit) {
                return ( 4 + (bit - (color % 4))); // Move up the bit in its equivalence relation
            }
        } else if (color - 4 > 123) {
            if (color % 4 < bit) {
                return - ( 4 - ( bit - (color % 4)) ) ; // Move down the bit in its equivalence relation
            } else if (color % 4 > bit) {
                return ( bit - (color % 4)); // Will result in negative number
            }
        }

        return 0;

    }

}
