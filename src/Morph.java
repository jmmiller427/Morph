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
    private MorphTools morphTool = new MorphTools();
    private Timer animateTimer;
    private JFileChooser file1, file2;
    static AlphaComposite ac1, ac2;
    private float startBrightness = 1, endBrightness = 1;
    private double t = 0;
    private int size;
    private boolean same;

    Morph(){}

    private Morph(int size){

        super("Morph");
        this.size = size;

        // Set the Lattice frames and add them to the content pane
        createMenu();
        startLattice = new Lattice(size);
        endLattice = new Lattice(size);

        Container c = getContentPane();

        // Add both panels to the content pane
        c.add(startLattice, BorderLayout.WEST);
        c.add(endLattice, BorderLayout.EAST);

        // Create Frame
        setSize(1025, 555);
        setVisible(true);
    }

    private void createMenu(){

        // Add gameMenu to the menu bar
        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);

        // Create File tab
        JMenu fileMenu = new JMenu("File");

        // Make two file choosers, for start and end images
        file1 = new JFileChooser();
        file2 = new JFileChooser();

        // Create a file extension filter, only allowing images
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

        // Create exit button in menu
        JMenuItem exitMorph = new JMenuItem("Exit");
        exitMorph.addActionListener(this);
        fileMenu.add(exitMorph);

        JMenu morph = new JMenu("Morph");

        // Create start morph in menu
        JMenuItem newMorph = new JMenuItem("Start Morph");
        newMorph.addActionListener(this);
        morph.add(newMorph);

        morph.addSeparator();

        // Allow user to change to 5x5 control points
        JMenuItem fiveByFive = new JMenuItem("5x5");
        fiveByFive.addActionListener(this);
        morph.add(fiveByFive);

        // Allow user to change to 10x10 control points
        JMenuItem tenByTen = new JMenuItem("10x10");
        tenByTen.addActionListener(this);
        morph.add(tenByTen);

        // Allow user to change to 20x20 control points
        JMenuItem twentyByTwenty = new JMenuItem("20x20");
        twentyByTwenty.addActionListener(this);
        morph.add(twentyByTwenty);

        JMenu brightness = new JMenu("Brightness");

        // Make a button to brighten the start image
        JMenuItem brightenStart = new JMenuItem("Brighten Start", KeyEvent.VK_T);
        brightenStart.setAccelerator(KeyStroke.getKeyStroke('=', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        brightenStart.addActionListener(this);
        brightness.add(brightenStart);

        // Make a button to dim the start image
        JMenuItem dimStart = new JMenuItem("Dim Start", KeyEvent.VK_T);
        dimStart.setAccelerator(KeyStroke.getKeyStroke('-', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        dimStart.addActionListener(this);
        brightness.add(dimStart);

        // Make a button to brighten the end image
        JMenuItem brightenEnd = new JMenuItem("Brighten End", KeyEvent.VK_T);
        brightenEnd.setAccelerator(KeyStroke.getKeyStroke(']', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        brightenEnd.addActionListener(this);
        brightness.add(brightenEnd);

        // Make a button to dim the end image
        JMenuItem dimEnd = new JMenuItem("Dim End", KeyEvent.VK_T);
        dimEnd.setAccelerator(KeyStroke.getKeyStroke('[', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
        dimEnd.addActionListener(this);
        brightness.add(dimEnd);

        // Create instructions
        JMenu instructions = new JMenu("Instructions");

        // Make read instructions pop up window
        JMenuItem newInstructions = new JMenuItem("Read Instructions");
        newInstructions.addActionListener(e -> JOptionPane.showMessageDialog(
                Morph.this,
                "- To start, choose a start and end image from\n" +
                         "   the file menu button\n" +
                         "- Change the number of control points.\n" +
                         "- Drag the points to their desired location.\n" +
                         "- Increase or decrease brightness of both images,\n" +
                         "   as you desire. \n" +
                         "- Click start morph to start the morph\n",
                "Instructions", JOptionPane.PLAIN_MESSAGE));
        instructions.add(newInstructions);

        // Add all the menus to the menu bar
        bar.add(fileMenu);
        bar.add(morph);
        bar.add(brightness);
        bar.add(instructions);
    }

    public void actionPerformed(ActionEvent e){

        // Execute action listeners that are created, exit on exit and animate on start morph
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

        // Change the brightness of the images
        else if (e.getActionCommand().equals("Brighten Start")){ startLattice.setBrightness(startBrightness += 0.25); }
        else if (e.getActionCommand().equals("Dim Start")){ startLattice.setBrightness(startBrightness -= 0.25); }
        else if (e.getActionCommand().equals("Brighten End")){ endLattice.setBrightness(endBrightness += 0.25); }
        else if (e.getActionCommand().equals("Dim End")){ endLattice.setBrightness(endBrightness -= 0.25); }
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

                // Run for loop to check each triangle
                for(int i = 0; i < this.size; i++) {
                    for(int j = 0; j < this.size; j++) {

                        // Warp the start triangles to then source image destination
                        morphTool.warpTriangle(startLattice.img, animate.srcImg, startLattice.triangles[i][j][0], animate.triangles[i][j][0], null, null);
                        morphTool.warpTriangle(startLattice.img, animate.srcImg, startLattice.triangles[i][j][1], animate.triangles[i][j][1], null, null);

                        // Warp the end image to the final picture
                        morphTool.warpTriangle(endLattice.img, animate.destImg, endLattice.triangles[i][j][0], animate.triangles[i][j][0], null, null);
                        morphTool.warpTriangle(endLattice.img, animate.destImg, endLattice.triangles[i][j][1], animate.triangles[i][j][1], null, null);

                        if (t >= 1) t = 1;

                        // Create alpha composite for each of the source and destination images
                        animate.srcImg.createGraphics();
                        animate.destImg.createGraphics();
                        ac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(t));
                        ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(1 - t));
                    }
                }
            }

            /* * * * * * * MAKE MOVIE HERE * * * * * * */

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
        animateFrame.setSize(endLattice.img.getWidth() + 10, endLattice.img.getHeight() + 10);
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
