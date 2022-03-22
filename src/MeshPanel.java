import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

public class MeshPanel extends JPanel implements MouseListener, MouseMotionListener {

    // location of control points
    private int[][] x;
    private int[][] y;
    private int numCP;
    private int gridDim;
    private float brightScale = 1.0f;

    private BufferedImage bim;
    private Triangle[] S, T;

    // Image dimensions
    private int xsize;
    private int ysize;

    // Vars for moving points
    private boolean selected;
    private int selectPointIndex;

    private final int THRESHOLD_DISTANCE = 10;

    public MeshPanel(BufferedImage imagePath, int gridRes) {

        bim = imagePath;
        gridDim = gridRes;
        numCP = gridRes * gridRes;

        xsize = bim.getWidth();
        ysize = bim.getHeight();

        selected = false;
        selectPointIndex = -1;



        initControlPoints();

        this.setPreferredSize(new Dimension(xsize, ysize));
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void initControlPoints() {
        x = new int[numCP][5];
        y = new int[numCP][5];

        int xInc = xsize / (2 * gridDim);
        int yInc = ysize / (2 * gridDim);

        int index = 0;
        for (int i = 1 ; i < gridDim + 1 ; i++)
            for(int j = 1 ; j < gridDim + 1 ; j++) {
                x[index][0] = (j * 2 * xInc) - xInc;
                y[index][0] = (i * 2 * yInc) - yInc;
                index++;
            }

        xInc = xsize / (gridDim * 2);
        yInc = ysize / (gridDim * 2);
        for(int i = 0 ; i < numCP ; i++){
            x[i][1] = x[i][0] - xInc;
            y[i][1] = y[i][0] - yInc;

            x[i][2] = x[i][0] + xInc;
            y[i][2] = y[i][0] - yInc;

            x[i][3] = x[i][0] - xInc;
            y[i][3] = y[i][0] + yInc;

            x[i][4] = x[i][0] + xInc;
            y[i][4] = y[i][0] + yInc;
        }
    }

    public void updateGrid(int numPoints) {
        gridDim = numPoints;
        numCP = numPoints * numPoints;
        initControlPoints();
        repaint();
    }



    public void paint(Graphics g) {
        Graphics2D big = (Graphics2D) g;
        RescaleOp op = new RescaleOp(brightScale, 0, null);
        BufferedImage img = op.filter(bim, null);
        big.drawImage(img, 0, 0, this);
        for (int i = 0 ; i < numCP ; i++) {
            g.fillOval(x[i][0]-5, y[i][0]-5,10,10);
            for (int j = 1 ; j < 5 ; j++) {
                g.drawLine(x[i][0], y[i][0], x[i][j], y[i][j]);
            }
            g.drawLine(x[i][1], y[i][1], x[i][2], y[i][2]);
            g.drawLine(x[i][4], y[i][4], x[i][2], y[i][2]);
            g.drawLine(x[i][4], y[i][4], x[i][3], y[i][3]);
            g.drawLine(x[i][1], y[i][1], x[i][3], y[i][3]);
        }
        /*int xInc, yInc;
        xInc = xsize / gridDim;
        yInc = ysize / gridDim;
        for (int i = 0 ; i < gridDim + 1 ; i++) {
            g.drawLine(i*xInc, 0, i*xInc, ysize);
            g.drawLine(0, i*yInc, xsize, i*yInc);
        }
        for(int i = 0; i < 5; i++) {
            System.out.println(i + " " + x[0][i] + " " + y[0][i]);
        }*/

    } // paint

    public int[][] getXPoints() {
        return x;
    }

    public int[][] getYPoints() {
        return y;
    }

    public int getGridDim() {
        return gridDim;
    }

    public int getXsize() {return xsize;}

    public int getYsize() {return ysize;}

    public void setBrightScale(float scalingFactor) {
        brightScale = scalingFactor;
        repaint();
    }

    public void setImage(BufferedImage newImage) {
        bim = newImage;
        repaint();
    }

    public BufferedImage getImageForMorph() {
        RescaleOp op = new RescaleOp(brightScale, 0, null);
        BufferedImage img = op.filter(bim, null);
        return img;
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int curX = e.getX();
        int curY = e.getY();

        for (int i = 0 ; i < numCP ; i++) {
            double distance = Math.sqrt((curX-x[i][0])*(curX-x[i][0])+(curY-y[i][0])*(curY-y[i][0]));
            if (distance < THRESHOLD_DISTANCE) {
                selected = true;
                selectPointIndex = i;
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(selected) {
            selected = false;
            selectPointIndex = -1;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(selected) {
            selected = false;
            selectPointIndex = -1;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int xcoor = e.getX();
        int ycoor = e.getY();
        if (selected && selectPointIndex != -1) {
            if (xcoor > x[selectPointIndex][1] && xcoor < x[selectPointIndex][4] && ycoor > y[selectPointIndex][1] && ycoor < y[selectPointIndex][4]) {
                x[selectPointIndex][0] = xcoor;
                y[selectPointIndex][0] = ycoor;
                repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}


