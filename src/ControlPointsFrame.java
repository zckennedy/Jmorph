import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ControlPointsFrame extends JFrame {

    // Mesh Canvases
    private JPanel mp;
    private MeshPanel startMeshPanel;  // change to mesh canvas
    private MeshPanel endMeshPanel;   // change to mesh canvas

    // Controls
    private JPanel controls;
    private JButton previewB;
    private JButton makeWarpB;
    private JSlider startBrightSlider;
    private JSlider endBrightSlider;
    private JSlider numFramesSlider;
    private JSlider gridResSlider;

    // Menu options
    private JMenu menu;
    private JMenuBar menuBar;
    private JMenuItem uploadStart;
    private JMenuItem uploadEnd;

    // From start up settings
    private int numCP;
    private int numFrames;
    private BufferedImage startImage;
    private BufferedImage endImage;
    private int width;
    private int height;

    private PreviewFrame pf;






    public ControlPointsFrame(int frames, int CPs) {
        super("Morph");

        numFrames = 20;
        numCP = 5;

        // Headings
        JLabel header = new JLabel("Adjust the control points for each image");
        header.setFont(new Font(header.getFont().getFontName(), header.getFont().getStyle(), header.getFont().getSize() + 5));
        header.setHorizontalAlignment(JLabel.CENTER);
        JLabel startHeader = new JLabel("Start Image");
        startHeader.setHorizontalAlignment(JLabel.CENTER);
        JLabel endHeader = new JLabel("End Image");
        endHeader.setHorizontalAlignment(JLabel.CENTER);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(startHeader, BorderLayout.WEST);
        headerPanel.add(header, BorderLayout.CENTER);
        headerPanel.add(endHeader, BorderLayout.EAST);
        headerPanel.setBorder(new EmptyBorder(10, 5, 5, 5));

        // Mesh Panels
        mp = new JPanel(new BorderLayout());
        File file = new File("src/start.jpeg");
        startImage = null;
        try {
            startImage = ImageIO.read(file);
            endImage = startImage;
        } catch (IOException e) {
            e.printStackTrace();
        }

        startMeshPanel = new MeshPanel(startImage, numCP);
        startMeshPanel.setBorder(new EmptyBorder(10, 10, 10, 5));
        mp.add(startMeshPanel, BorderLayout.LINE_START);
        endMeshPanel = new MeshPanel(endImage, numCP);
        endMeshPanel.setBorder(new EmptyBorder(10, 5, 10, 10));
        mp.add(endMeshPanel, BorderLayout.LINE_END);

        // Controls

        JPanel btnPanel = new JPanel(new GridLayout(1, 2)); // add col with more buttons
        previewB = new JButton("Open Preview");
        makeWarpB = new JButton("Generate Morph (jpegs can be found in src/morph)");
        btnPanel.add(previewB);
        btnPanel.add(makeWarpB);
        startBrightSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 20, 10);
        startBrightSlider.setSnapToTicks(true);
        startBrightSlider.setMajorTickSpacing(1);
        startBrightSlider.setPaintTicks(true);
        endBrightSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 20, 10);
        endBrightSlider.setSnapToTicks(true);
        endBrightSlider.setMajorTickSpacing(1);
        endBrightSlider.setPaintTicks(true);

        numFramesSlider = new JSlider(SwingConstants.HORIZONTAL,0,50,20);
        numFramesSlider.setSnapToTicks(true);
        numFramesSlider.setMajorTickSpacing(5);
        numFramesSlider.setPaintTicks(true);
        numFramesSlider.setPaintLabels(true);

        gridResSlider = new JSlider(SwingConstants.HORIZONTAL,5,20,5);
        gridResSlider.setSnapToTicks(true);
        gridResSlider.setMajorTickSpacing(1);
        gridResSlider.setPaintTicks(true);
        gridResSlider.setPaintLabels(true);


        JPanel sliderPanel = new JPanel(new GridLayout(3, 2));
        sliderPanel.add(startBrightSlider);
        sliderPanel.add(endBrightSlider);
        sliderPanel.add(new JLabel("Number of frames"));
        sliderPanel.add(new JLabel("Grid resolution"));
        sliderPanel.add(numFramesSlider);
        sliderPanel.add(gridResSlider);



        controls = new JPanel(new BorderLayout());
        controls.add(btnPanel, BorderLayout.SOUTH);
        controls.add(sliderPanel, BorderLayout.NORTH);

        gridResSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                numCP = gridResSlider.getValue();
                startMeshPanel.updateGrid(numCP);
                endMeshPanel.updateGrid(numCP);
            }
        });

        numFramesSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                numFrames = numFramesSlider.getValue();
            }
        });

        startBrightSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float scale = (float) startBrightSlider.getValue() / (float) 10;
                startMeshPanel.setBrightScale(scale);
            }
        });

        endBrightSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float scale = (float) endBrightSlider.getValue() / (float) 10;
                endMeshPanel.setBrightScale(scale);
            }
        });

        // menu options
        menu = new JMenu("Upload");
        menuBar = new JMenuBar();
        uploadStart = new JMenuItem("Upload new start image");
        uploadStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(".");
                int returnVal = fc.showOpenDialog(ControlPointsFrame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        startImage = ImageIO.read(file);
                    } catch (IOException e1){};
                }
                // resize to 500x500
                BufferedImage bimStart = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = bimStart.createGraphics();
                graphics2D.drawImage(startImage, 0, 0, 500,500,null);
                startImage = bimStart;
                graphics2D.dispose();
                // set new image in mesh panel
                startMeshPanel.setImage(startImage);
            }
        });
        uploadEnd = new JMenuItem("Upload new end image");
        uploadEnd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(".");
                int returnVal = fc.showOpenDialog(ControlPointsFrame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        endImage = ImageIO.read(file);
                    } catch (IOException e1){};
                }
                // resize to 500x500
                BufferedImage bim = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = bim.createGraphics();
                graphics2D.drawImage(endImage, 0, 0, 500,500,null);
                endImage = bim;
                graphics2D.dispose();
                // set new image in mesh panel
                endMeshPanel.setImage(endImage);
            }
        });
        menu.add(uploadStart);
        menu.add(uploadEnd);
        menuBar.add(menu);




        /*

        private JMenu menu;
    private JMenuItem restart;

        // Controls
    private JButton previewB;
    private JButton makeWarpB;
    private JButton playB;
    private JSlider startBrightSlider;
    private int startBrightVal;
    private JSlider endBrightSlider;
    private int endBrightVal;
         */






        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(headerPanel, BorderLayout.NORTH);
        c.add(mp, BorderLayout.CENTER);
        c.add(controls, BorderLayout.SOUTH);
        this.setJMenuBar(menuBar);




        this.pack();
        //this.setResizable(false);
        setVisible(true);

        previewB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pf = new PreviewFrame(startMeshPanel.getXPoints(), startMeshPanel.getYPoints(), endMeshPanel.getXPoints(), endMeshPanel.getYPoints(), startMeshPanel.getGridDim(), startMeshPanel.getXsize(), startMeshPanel.getYsize(), numFrames);      // todo: get frames from control panel

                pf.setVisible(true);

            }

        });

        makeWarpB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeWarpB.setEnabled(false);
                MorphUtil mu = new MorphUtil(numFrames, numCP);
                mu.loadCPs(startMeshPanel.getXPoints(), startMeshPanel.getYPoints(), endMeshPanel.getXPoints(), endMeshPanel.getYPoints());
                mu.setImages(startMeshPanel.getImageForMorph(), endMeshPanel.getImageForMorph());
                mu.morphing();
                makeWarpB.setEnabled(true);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                setVisible(false);
                System.exit(0);
            }
        } );

    }

    public static void main(String[] args) {
        ControlPointsFrame cp = new ControlPointsFrame(20, 8);
    }
}


