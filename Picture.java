//Picture.java
//very similar to BufferedImage, but includes a constructor for filenames
// adapted from Introduction to Programming in Java: An Interdisciplinary Approach</i>
//  by Robert Sedgewick and Kevin Wayne.
 
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;



public final class Picture  {
    private BufferedImage image;               // the rasterized image
    private String filename;                   // name of file
    private boolean isOriginUpperLeft = true;  // location of origin
    private final int width, height;           // width and height


   //constructs a picture by reading from a file
    public Picture(String filename) {
        this.filename = filename;
        try {
            // try to read from file in working directory
            File file = new File(filename);
            if (file.isFile()) {
                image = ImageIO.read(file);
            }
            width  = image.getWidth(null);
            height = image.getHeight(null);
        }
        catch (IOException e) {
            // e.printStackTrace();
            throw new RuntimeException("Could not open file: " + filename);
        }

        // check that image was read in
        if (image == null) {
            throw new RuntimeException("Invalid image file: " + filename);
        }
    }


   // returns the height of the picture
    public int height() {
        return height;
    }

   // returns the width of the picture
    public int width() {
        return width;
    }

   // given a column and row, returns color of pixel with given coordinate to new color
    public Color get(int col, int row) {

        if (col < 0 || col >= width())  throw new IndexOutOfBoundsException("col must be between 0 and " + (width()-1));
        if (row < 0 || row >= height()) throw new IndexOutOfBoundsException("row must be between 0 and " + (height()-1));
        if (isOriginUpperLeft) return new Color(image.getRGB(col, row));
        else                   return new Color(image.getRGB(col, height - row - 1));
    }

   // given a column, row, and color, sets the pixel with given coordinate to new color
    public void set(int col, int row, Color color) {
        if (col < 0 || col >= width())  throw new IndexOutOfBoundsException("col must be between 0 and " + (width()-1));
        if (row < 0 || row >= height()) throw new IndexOutOfBoundsException("row must be between 0 and " + (height()-1));
        if (color == null) throw new NullPointerException("can't set Color to null");
        if (isOriginUpperLeft) image.setRGB(col, row, color.getRGB());
        else                   image.setRGB(col, height - row - 1, color.getRGB());
    }
}

