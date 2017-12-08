import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.lang.StrictMath.ceil;

class Animation extends JPanel {

    private int size;
    private Morph morph = new Morph();
    ControlPoint animatedPoints[][];
    BufferedImage srcImg = null, destImg = null;
    Triangle triangles[][][];

    Animation(ControlPoint start[][], ControlPoint end[][], double t, int size){

        this.size = size;
        super.setPreferredSize(new Dimension(500, 500));

        // Call the animate function
        animate(start, end, t, size);
    }

    ControlPoint[][] animate(ControlPoint start[][], ControlPoint end[][], double t, int size){

        // Set the size and create a new array of where the new control points should move, create a new
        // array each time the function is called, create triangles array of the same size
        this.animatedPoints = new ControlPoint[size + 1][size + 1];
        triangles = new Triangle[size + 1][size + 1][2];

        // Run through the length of the starting array in both directions
        for (int i = 0; i < start.length; i++) {
            for (int j = 0; j < start[i].length; j++) {

                double x, y;

                // If the points on the start and end lattice don't match, create a new x and y coordinate using formula
                // If they do equal, then that point is the end point, so set x and y as end point
                if (ceil(start[i][j].x) != ceil(end[i][j].x) && ceil(start[i][j].y) != ceil(end[i][j].y)) {
                    x = ceil(start[i][j].x + (t * (end[i][j].x - start[i][j].x)));
                    y = ceil(start[i][j].y + (t * (end[i][j].y - start[i][j].y)));

                } else {
                    x = ceil(end[i][j].x);
                    y = ceil(end[i][j].y);
                }

                // Add the new points to the animated points array
                this.animatedPoints[i][j] = new ControlPoint(x, y);
            }
        }

        // Define all triangles
        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                triangles[i][j][0] = new Triangle(animatedPoints[i][j], animatedPoints[i + 1][j], animatedPoints[i + 1][j + 1]);
                triangles[i][j][1] = new Triangle(animatedPoints[i][j], animatedPoints[i][j + 1], animatedPoints[i + 1][j + 1]);
            }
        }

        return animatedPoints;
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        if (srcImg != null && destImg != null) {

            try {
                g2d.setComposite(morph.ac1);
                g2d.drawImage(srcImg, 0, 0, null);

                g2d.setComposite(morph.ac2);
                g2d.drawImage(destImg, 0, 0, null);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    // Set the image on each lattice
    void setImage(String image1, String image2){

        // Read the image and repaint the jpanel if it is successful
        try {

            // Create two images for animation. Start and end
            srcImg = ImageIO.read(new File(image1));
            destImg = ImageIO.read(new File(image2));

            super.removeAll();
            super.revalidate();
            super.repaint();
        }catch (IOException | NullPointerException e){ System.out.println("error"); }
    }
}
