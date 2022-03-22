import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class PreviewFrame extends JFrame implements Runnable {
    int[] startX, startY;
    int[][] endX, endY;

    int[] x,y;

    int gridDim;

    int xsize, ysize;

    int numFrames;

    int numCPs;

    JPanel previewPanel;

    public PreviewFrame(int[][] sx, int[][] sy, int[][] ex, int[][] ey, int gridRes, int width, int height, int frames) {
        super("Preview");
        endX = ex;
        endY = ey;
        gridDim = gridRes;
        xsize = width;
        ysize = height;
        numFrames = frames;
        numCPs = gridRes * gridRes;
        x = new int[numCPs];
        y = new int[numCPs];
        startX = new int[numCPs];
        startY = new int[numCPs];


        for (int i = 0 ; i < numCPs ; i++) {
            x[i] = sx[i][0];
            y[i] = sy[i][0];
            startX[i] = sx[i][0];
            startY[i] = sy[i][0];
        }
        previewPanel = new JPanel();
        previewPanel.setPreferredSize(new Dimension(xsize, ysize));
        this.setLayout(new BorderLayout());
        this.add(previewPanel,BorderLayout.CENTER);
        JButton playB = new JButton("Play");
        playB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playPreview();
            }
        });
        this.add(playB, BorderLayout.SOUTH);
        this.pack();
    }




    public void paint(Graphics g) {
        Graphics2D big = (Graphics2D) g;
        big.setColor(Color.WHITE);
        big.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        for (int i = 0 ; i < numCPs ; i++) {
            g.fillOval(x[i]-5, y[i]-5,10,10);
            for (int j = 1 ; j < 5 ; j++) {
                g.drawLine(x[i], y[i], endX[i][j], endY[i][j]);
            }
        }
        int xInc, yInc;
        xInc = xsize / gridDim;
        yInc = ysize / gridDim;
        for (int i = 0 ; i < gridDim + 1 ; i++) {
            g.drawLine(i*xInc, 0, i*xInc, ysize);
            g.drawLine(0, i*yInc, xsize, i*yInc);
        }

    } // paint

    public void drawMesh() {
        repaint();
    }


    public void playPreview() {
        new Thread(this).start();

    }

    @Override
    public void run() {
        try {
            double[] stepX = new double[numCPs];
            double[] stepY = new double[numCPs];

            for (int i = 0 ; i < numCPs ; i++) {
                stepX[i] = (double) (endX[i][0] - startX[i]) / (double) numFrames;
                stepY[i] = (double) (endY[i][0] - startY[i]) / (double) numFrames;
            }

            for(int i = 0 ; i <= numFrames ; i++) {
                for (int j = 0 ; j < x.length ; j++) {
                    x[j] = (int) (startX[j] + i * stepX[j]);
                    y[j] = (int) (startY[j] + i * stepY[j]);
                }
                repaint();
                Thread.sleep(50);
            }
            Thread.sleep(1000);
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

        } catch (Exception e) {}
    }
}
