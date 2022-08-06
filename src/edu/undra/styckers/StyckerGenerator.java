package edu.undra.styckers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * This class creates styckers from an input stream's imageable bytes<br>
 * It draws a given text (styckerText) into stycker's foot.<br>
 * <br>
 * Then it saves the new image (a stycker) into storage refered by styckerName.
 * <br>Or<br>
 * writes the stycker's bytes to a given output stream.
 *
 * @date 19 de jul. de 2022 17.46.25
 * @author alexandre
 */
public class StyckerGenerator {

    /**
     * The thumbnail image.
     */
    private static BufferedImage thumbNail;
    private static final int FONT_SIZE_BASE = 64;

    /**
     * Creates styckers from an input stream's imageable bytes (source),<br>
     * draws styckerText into stycker's foot.<br>
     * and saves the new image (a stycker) into storage refered by
     * styckerName.
     *
     * @param source the input stream's images bytes
     * @param styckerName storage location to save new image to
     * @param styckerText the text to write into the stycker
     * @throws IOException I'm a risky method. Please, handle exception if I
     * fail.
     */
    public static void generate(InputStream source, String styckerName, String styckerText) throws IOException {

        if (source == null || styckerName == null || styckerText == null) {
            throw new NullPointerException("source AND styckerName AND styckerText MUST BE NOT NULL");
        }

        File f = new File(styckerName);
        FileOutputStream fos = new FileOutputStream(f);

        generate(source, fos, styckerText);

    }

    /**
     * Creates styckers from an input stream's imageable bytes, the source,<br>
     * draws styckerText into stycker's foot.<br>
     * and writes the stycker's bytes to an output stream.
     *
     * @param source the input stream's images bytes
     * @param oPS the out put stream to write the bytes to
     * @param styckerText the text to write into the stycker
     * @throws IOException I'm a risky method. Please, handle exception if I
     * fail.
     */
    public static void generate(InputStream source, OutputStream oPS, String styckerText) throws IOException {

        if (source == null || oPS == null || styckerText == null) {
            throw new NullPointerException("source AND oPS AND styckerText MUST BE NOT NULL");
        }

        BufferedImage original = ImageIO.read(source);

        int footerHeight = getFooterHight(original);

        int width = original.getWidth();
        int height = original.getHeight();
        int styckerHeight = height + footerHeight;

        //Uma imagem em branco, um pouco mais alta do que a original
        BufferedImage stycker = new BufferedImage(width, styckerHeight, BufferedImage.TRANSLUCENT);
        //copia imagem original para nova imagem em branco; 
        //operação feita em memória (Buffered <-> bytes)
        Graphics2D pen = (Graphics2D) stycker.getGraphics();
        pen.drawImage(original, 0, 0, null);

        if (styckerText.isEmpty()) {
            styckerText = "empty text";
        }

        Font font = getFont(original, stycker, styckerText);
        pen.setFont(font);
        pen.setColor(Color.YELLOW);
        //the text's x coordinate (centralizes all the stuffs)
        int textX = getTextX(font, original, stycker, styckerText);
        //the text's y coordinate (centralizes all the stuffs)
        int textY = getTextY(font, original, stycker, styckerText);
        if(styckerText.equals("empty text")){
            styckerText="";
        }
        pen.drawString(styckerText, textX, textY);
        if (styckerText.isEmpty()) {
            styckerText = "empty text";
        }


        //thumbnail stuff
        BufferedImage thumb = getThumbNail(original);
        int thumbX = getThumbX(font, original, stycker, styckerText);
        int thumbY = height + footerHeight / 2 - thumb.getHeight() / 2;
        pen.drawImage(thumb, thumbX, thumbY, null);

        ImageIO.write(stycker, "png", oPS);

    }

    /**
     * The x gap between elements.( x-axis direction )
     *
     * @return
     */
    private static int getXGap() {
        return 30;
    }

    /**
     * Gets a fraction of the original image height
     *
     * @param original the original image.
     * @return a fraction of original image height
     */
    private static int getFooterHight(BufferedImage original) {
        return (int) (original.getHeight() * 0.20);
    }

    /**
     * The adequate thumbnail's x cordinate
     *
     * @param font
     * @param original
     * @param stycker
     * @param styckerText
     * @return The adequate thumbnail's x cordinate
     */
    private static int getThumbX(Font font, BufferedImage original, BufferedImage stycker, String styckerText) {
        Graphics2D pen = (Graphics2D) stycker.getGraphics();
        FontRenderContext frc = pen.getFontRenderContext();
        TextLayout layout = new TextLayout(styckerText, font, frc);

        int thumbX = getTextX(font, original, stycker, styckerText) + (int) layout.getBounds().getBounds2D().getWidth() + ((int) (0.65 * getXGap()));

        if (thumbX + getThumbNail(original).getWidth() >= original.getWidth()) {
            thumbX = original.getWidth() - getXGap() - getThumbNail(original).getWidth();
        }

        return thumbX;
    }

    /**
     * Gets the thumbnail image or an empty avatar
     *
     * @return the thumbnail or an avatar
     */
    private static BufferedImage getThumbNail() {

        if (thumbNail == null) {

            BufferedImage empty;
            try {
                thumbNail = ImageIO.read(new File("images/oie-rotate.png"));
            } catch (IOException ex) {
                try {
                    empty = ImageIO.read(new File("images/empty.png"));
                    thumbNail = empty;
                } catch (IOException ex1) {
                }
            }

        }

        return thumbNail;
    }

    /**
     * Gets the thumbnail image resized, depending on original image width.
     *
     * @return the thumbnail resized
     */
    private static BufferedImage getThumbNail(BufferedImage original) {

        BufferedImage thumb = getThumbNail();
        double desiredWidth = original.getWidth() * 0.18;
        double scale = 1.10;
        int appliedScaleCount = 0;

        //finds the appropriate scale to call resize method
        while (Math.pow(scale, appliedScaleCount) * thumb.getWidth() < desiredWidth) {
            appliedScaleCount++;
        }

        double appropriateScale = Math.pow(scale, appliedScaleCount);

        thumb = resize(thumb, appropriateScale);

        //Ajusts thumbnail's height if it comes to be greater than a bound
        if (thumb.getHeight() >= getFooterHight(original)) {
            thumb = resize(thumb, (1.0 * getFooterHight(original)) / (1.0 * thumb.getHeight()));
        }

        return thumb;
    }

    /**
     * Resizes an image, proportionally to scale.<br>
     * Example : a call to resize(img, 0.50) shrinks image's width in half and
     * shrinks image's height in half.<br>
     * Example : a call to resize(img, 2.00) will double image's width and will
     * double image's height.<br>
     * <br>
     * Generally, resized image's area will be proportional to scale².
     *
     * @param image the image
     * @param scale the linear percentage of resizing
     * @return resized image, with area proportional to scale²
     */
    private static BufferedImage resize(BufferedImage image, double scale) {

        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);
        Image tmp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;

    }

    /**
     * A convinience for another method that needs to find an appropriated font
     * size.
     *
     * @param size tre size of the font
     * @return returns a SANS_SERIF, BOLD, size font
     */
    private static Font getFont(int size) {
        return new Font(Font.SANS_SERIF, Font.BOLD, size);
    }

    /**
     * Gets the right fitting font size, making the text and the thumb image fit
     * <br>
     * Perhaps, the method's computation should take in account the heights of
     * the font. But for while, <br>
     * its been shown unnecessary. Only font's width is beeing taken in account.
     *
     * @param original
     * @param stycker
     * @param styckerText
     * @return A font with fitting size, making the text and the thumb image fit
     */
    private static Font getFont(BufferedImage original, BufferedImage stycker, String styckerText) {

        Font font = getFont(FONT_SIZE_BASE);

        //the text's x coordinate
        double textX = getTextX(font, original, stycker, styckerText);

        //gets the right font size, making the text and the thumb image fit 
        //decreases font's size until everything fits in
        while (textX < 0) {

            //we take a smaller font size, if the actual font size blows up the stycker footer
            //we decrease the font's size until everything fits in
            font = getFont(font.getSize() - 1);

            textX = getTextX(font, original, stycker, styckerText);

        }

        font = getFont(font.getSize() + 1);

        textX = getTextX(font, original, stycker, styckerText);

        //we want text starting a bit away from left border.
        while (textX > getXGap()) {
            font = getFont(font.getSize() + 1);
            textX = getTextX(font, original, stycker, styckerText);
            if (textX < 0) {
                font = getFont(font.getSize() - 1);
                break;
            }
        }

        return font;
    }

    /**
     * This method centralizes all the stuffs, horizontally (x direction)
     *
     * @param font
     * @param original
     * @param stycker
     * @param thumb
     * @param styckerText
     * @return the text X coordinate, taking in account centralizing the stuffs
     * at horizontal direction.
     */
    private static int getTextX(Font font, BufferedImage original, BufferedImage stycker, String styckerText) {

        int width = original.getWidth();
        Graphics2D pen = (Graphics2D) stycker.getGraphics();
        pen.setFont(font);

        FontRenderContext frc = pen.getFontRenderContext();
        TextLayout layout = new TextLayout(styckerText, font, frc);

        return (int) ((width - getThumbNail(original).getWidth() - getXGap()) - layout.getBounds().getWidth()) / 2;
    }

    /**
     * This method centralizes the stuffs, vertically (y direction)
     *
     * @param font
     * @param original
     * @param stycker
     * @param styckerText
     * @return the text Y coordinate, taking in account centralizing the stuffs
     * at vertical direction.
     */
    private static int getTextY(Font font, BufferedImage original, BufferedImage stycker, String styckerText) {
        int height = original.getHeight();
        int footerHeight = getFooterHight(original);
        int styckerHeight = height + footerHeight;
        Graphics2D pen = (Graphics2D) stycker.getGraphics();

        FontRenderContext frc = pen.getFontRenderContext();
        TextLayout layout = new TextLayout(styckerText, font, frc);

        return (int) (height - layout.getBounds().getY()) + (int) ((styckerHeight - height) - layout.getBounds().getHeight()) / 2;
    }

    public static void main(String[] args) throws MalformedURLException, IOException {
        InputStream is = new URL("https://m.media-amazon.com/images/M/MV5BMDFkYTc0MGEtZmNhMC00ZDIzLWFmNTEtODM1ZmRlYWMwMWFmXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_UX128_CR0,3,128,176_AL_.jpg".replaceAll("\\._..........................", "")).openStream();
//        InputStream is = new URL("https://m.media-amazon.com/images/M/MV5BMDFkYTc0MGEtZmNhMC00ZDIzLWFmNTEtODM1ZmRlYWMwMWFmXkEyXkFqcGdeQXVyMTMxODk2OTU@.jpg").openStream();

//        StyckerGenerator.generate(new FileInputStream(new File("images/shaw-redenption.jpg")), "saida.png");
        StyckerGenerator.generate(is, "styckers/saida.png", "STICKERS O'MINE");

        Font f = new Font(Font.SERIF, Font.BOLD, -10);

    }

}
