import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

class Lattice extends JPanel implements MouseMotionListener, MouseListener{

    // Create a single point and an array of points
    private ControlPoint controlPoint;
    private int size;
    private boolean draggingControlPoint = false;
    private int pointI, pointJ;
    private BufferedImage filteredImg = null;
    BufferedImage img = null;
    Triangle triangles[][][];
    ControlPoint points[][];

    Lattice(int size){

        // Set the size, panel size, and border of the lattice panel
        this.size = size;
        super.setPreferredSize(new Dimension(510, 500));
        super.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));

        // Add mouse listeners to the panel
        super.addMouseListener(this);
        super.addMouseMotionListener(this);

        super.setFocusable(true);
        super.requestFocusInWindow();

        // Initialize the points array
        points = new ControlPoint[size + 1][size + 1];
        triangles = new Triangle[size + 1][size + 1][2];

        // Run through the size of what is being requested
        for (int i = 0; i < size + 1; i++) {
            for (int j = 0; j < size + 1; j++) {

                // Calculate the x and y value for where each point should go
                double x = (i * 500) / size;
                double y = (j * 500) / size;

                // Create that Control point and add it to the array of points
                controlPoint = new ControlPoint(x, y);
                points[i][j] = controlPoint;
            }
        }

        // For loop to create each triangle for each square
        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                triangles[i][j][0] = new Triangle(points[i][j], points[i + 1][j], points[i + 1][j + 1]);
                triangles[i][j][1] = new Triangle(points[i][j], points[i][j + 1], points[i + 1][j + 1]);
            }
        }
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        // Draw the image
        g.drawImage(img, 0, 0, null);

        // Loop through to draw each point
        for (int i = 0; i < size + 1; i++) {
            for (int j = 0; j < size + 1; j++) {

                // Draw the points
                g2d.fill(points[i][j]);

                // Draw a line from the top left corner to each adjacent point
                if ((i + 1) < (size + 1)){
                    g2d.draw(new Line2D.Double(points[i][j].x + 5, points[i][j].y + 5,
                                               points[i+1][j].x + 5, points[i+1][j].y + 5));
                }
                if ((i + 1) < (size + 1) && (j + 1) < (size + 1)){
                    g2d.draw(new Line2D.Double(points[i][j].x + 5, points[i][j].y + 5,
                                               points[i+1][j+1].x + 5, points[i+1][j+1].y + 5));
                }
                if ((j + 1) < (size + 1)){
                    g2d.draw(new Line2D.Double(points[i][j].x + 5, points[i][j].y + 5,
                                               points[i][j+1].x + 5, points[i][j+1].y + 5));
                }
            }
        }
    }

    // Set the image on each lattice
    void setImage(String image){

        // Read the image and repaint the jpanel if it is successful
        try {
            img = ImageIO.read(new File(image));
            filteredImg = img;

            super.removeAll();
            super.revalidate();
            super.repaint();
        }catch (IOException | NullPointerException e){ System.out.println("Error"); }
    }

    void setBrightness(float brightness){

        // Pass in a brightness value to adjust the brightness of the image
        RescaleOp op = new RescaleOp(brightness, 0, null);
        img = op.filter(filteredImg, null);
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

        // When the mouse is pressed
        for (int i = 0; i < size + 1; i++) {
            for (int j = 0; j < size + 1; j++) {
                if (points[i][j].contains(e.getPoint())){
                    draggingControlPoint = true;

                    pointI = i;
                    pointJ = j;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        // When the mouse is released, set dragging control point boolean to false
        draggingControlPoint = false;

        // When the mouse is released, create all new triangles
        for(int i = 0; i < this.size - 1; i++) {
            for(int j = 0; j < this.size - 1; j++) {
                triangles[i][j][0] = new Triangle(points[i][j], points[i + 1][j], points[i + 1][j + 1]);
                triangles[i][j][1] = new Triangle(points[i][j], points[i][j + 1], points[i + 1][j + 1]);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        // Create polygon to constrain points
        Polygon poly;

        // When the mouse is dragged, get the source for which panel it was in
        int CPx, CPy;


        try {
            // If the end lattice is being dragged
            if (draggingControlPoint) {
                CPx = e.getX();
                CPy = e.getY();

                // Arrays for polygon points
                int[] xArray = new int[6];
                int[] yArray = new int[6];

                // X-Points to restrain within
                xArray[0] = (int) points[pointI + 1][pointJ].x;
                xArray[1] = (int) points[pointI + 1][pointJ + 1].x;
                xArray[2] = (int) points[pointI][pointJ + 1].x;
                xArray[3] = (int) points[pointI - 1][pointJ].x;
                xArray[4] = (int) points[pointI - 1][pointJ - 1].x;
                xArray[5] = (int) points[pointI][pointJ - 1].x;

                // Y-Points to restrain within
                yArray[0] = (int) points[pointI + 1][pointJ].y;
                yArray[1] = (int) points[pointI + 1][pointJ + 1].y;
                yArray[2] = (int) points[pointI][pointJ + 1].y;
                yArray[3] = (int) points[pointI - 1][pointJ].y;
                yArray[4] = (int) points[pointI - 1][pointJ - 1].y;
                yArray[5] = (int) points[pointI][pointJ - 1].y;

                poly = new Polygon(xArray, yArray, 6);

                if (poly.contains(e.getPoint())) {

                    // Draw and create a new control point with the new x and y value calculated
                    controlPoint = new ControlPoint(CPx, CPy);
                    points[pointI][pointJ] = controlPoint;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {}

        // Repaint the panel
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {

    }
    @Override
    public void mouseMoved(MouseEvent e) {

    }
    @Override
    public void mouseClicked(MouseEvent e) {

    }
}
