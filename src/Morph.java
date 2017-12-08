/* Names: John Miller, Harrison Wainwright
 * Date: 8 November 2017
 * Class: CS 335 - 001
 * Project: Morph
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

public class Morph extends JFrame implements ActionListener{

    // Create two frames for the images
    private Lattice startLattice;
    private Lattice endLattice;
    private int size;
    private Timer animateTimer;
    private boolean same;
    private double t = 0;
    private MorphTools morphTool = new MorphTools();
    private JFileChooser file1, file2;
    static AlphaComposite ac1, ac2;

    Morph(){}

    private Morph(int size){

        super("Morph");
        this.size = size;

        // Set the Lattice frames and add them to the content pane
        createMenu();
        startLattice = new Lattice(size);
        endLattice = new Lattice(size);

        Container c = getContentPane();

        c.add(startLattice, BorderLayout.WEST);
        c.add(endLattice, BorderLayout.EAST);

        // Create Frame
        setSize(1010, 550);
        setVisible(true);
    }

    private void createMenu(){

        // Add gameMenu to the menu bar
        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);

        // Create File tab
        JMenu fileMenu = new JMenu("File");

        file1 = new JFileChooser();
        file2 = new JFileChooser();

        FileNameExtensionFilter filters = new FileNameExtensionFilter("JPEG/JPG, PNG", "jpg", "jpeg", "png");
        file1.setFileFilter(filters);
        file2.setFileFilter(filters);

        // Create new image for start lattice
        JMenuItem newStartFile = new JMenuItem("New Start Image");
        newStartFile.addActionListener(e -> {
            int returnVal = file1.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION){
                startLattice.setImage(file1.getSelectedFile().getPath());
            }
        });
        fileMenu.add(newStartFile);

        // Create new image for end lattice
        JMenuItem newEndFile = new JMenuItem("New End Image");
        newEndFile.addActionListener(e -> {
            int returnVal = file2.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION){
                endLattice.setImage(file2.getSelectedFile().getPath());
            }
        });
        fileMenu.add(newEndFile);
        fileMenu.addSeparator();

        JMenuItem exitMorph = new JMenuItem("Exit");
        exitMorph.addActionListener(this);
        fileMenu.add(exitMorph);

        JMenu morph = new JMenu("Morph");

        JMenuItem newMorph = new JMenuItem("Start Morph");
        newMorph.addActionListener(this);
        morph.add(newMorph);

        morph.addSeparator();

        JMenuItem fiveByFive = new JMenuItem("5x5");
        fiveByFive.addActionListener(this);
        morph.add(fiveByFive);

        JMenuItem tenByTen = new JMenuItem("10x10");
        tenByTen.addActionListener(this);
        morph.add(tenByTen);

        JMenuItem twentyByTwenty = new JMenuItem("20x20");
        twentyByTwenty.addActionListener(this);
        morph.add(twentyByTwenty);

        bar.add(fileMenu);
        bar.add(morph);
    }

    public void actionPerformed(ActionEvent e){

        // Execute action listeners that are created
        if (e.getActionCommand().equals("Exit")) { System.exit(0); }
        else if (e.getActionCommand().equals("Start Morph")) { showAnimateFrame(); }

        // Create a new 5x5 grid
        else if (e.getActionCommand().equals("5x5")){

            dispose();
            size = 5;

            Morph M = new Morph(size);
            M.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                    }
                });
        }

        // Create a new 10x10 grid
        else if (e.getActionCommand().equals("10x10")){

            dispose();
            size = 10;

            Morph M = new Morph(size);
            M.addWindowListener(new WindowAdapter(){
                    public void windowClosing(WindowEvent e){System.exit(0);}
                });
        }

        // Create a new 20x20 grid
        else if (e.getActionCommand().equals("20x20")){

            dispose();
            size = 20;

            Morph M = new Morph(size);
            M.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){System.exit(0);}
            });
        }
    }

    private void showAnimateFrame(){

        // Create a timer and name the animation frame
        JFrame animateFrame = new JFrame("Animation");
        Animation animate = new Animation(startLattice.points, endLattice.points, t, size);
        animate.setImage(file1.getSelectedFile().getPath(), file2.getSelectedFile().getPath());

        // Start an action listener for the timer to show the animation
        ActionListener showAnimation = e -> {

            same = true;

            // Run nested for loop to go through all the points
            for (int i = 0; i < size; i++){
                for (int j = 0; j < size; j++){

                    // Check if the x and y points of the animating frame match the end frame
                    // if they don't equal then set the boolean to false
                    if (!(animate.animatedPoints[i][j].x == endLattice.points[i][j].x &&
                            animate.animatedPoints[i][j].y == endLattice.points[i][j].y)){

                        same = false;
                    }
                }
            }

            // If the points are the same, stop the timer
            // Else call animate again with a new t value each time
            if (same){
                animateTimer.stop();
            }else{
                t += 0.006;
                animate.animatedPoints = animate.animate(animate.animatedPoints, endLattice.points, t, size);

                for(int i = 0; i < this.size; i++) {
                    for(int j = 0; j < this.size; j++) {
                        morphTool.warpTriangle(startLattice.img, animate.srcImg, startLattice.triangles[i][j][0], animate.triangles[i][j][0], null, null);
                        morphTool.warpTriangle(startLattice.img, animate.srcImg, startLattice.triangles[i][j][1], animate.triangles[i][j][1], null, null);

                        morphTool.warpTriangle(endLattice.img, animate.destImg, endLattice.triangles[i][j][0], animate.triangles[i][j][0], null, null);
                        morphTool.warpTriangle(endLattice.img, animate.destImg, endLattice.triangles[i][j][1], animate.triangles[i][j][1], null, null);

                        if (t >= 1) t = 1;

                        animate.srcImg.createGraphics();
                        ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(1 - t));

                        animate.destImg.createGraphics();
                        ac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(t));
                    }
                }
            }

            // Repaint the Panel
            animate.revalidate();
            animate.repaint();
        };

        // Create timer and start it
        animateTimer = new Timer(33, showAnimation);
        animateTimer.setRepeats(true);
        animateTimer.start();

        // Create animation frame
        animateFrame.add(animate);
        animateFrame.setSize(endLattice.img.getWidth(), endLattice.img.getHeight());
        animateFrame.setVisible(true);
    }

    public static void main(String args[]){

        int size = 10;

        Morph M = new Morph(size);
        M.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){System.exit(0);}
        });
    }
}
