import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MorphUtil {
    BufferedImage startImage;
    BufferedImage endImage;

    int[][] xStart, yStart;     //[CPindex][surrounding]
    int[][] xEnd, yEnd;

    ArrayList<BufferedImage> morphed;

    int numFrames;

    int numCP;

    public MorphUtil(int frames, int grid) {
        numFrames = frames;
        numCP = grid * grid;
    }

    public void setImages(BufferedImage start, BufferedImage end) {
        startImage = start;
        endImage = end;
    }

    public void loadCPs(int[][] xs, int[][] ys, int[][] xe, int[][] ye) {
        xStart = xs;
        yStart = ys;
        xEnd = xe;
        yEnd = ye;
    }

    public void morphing() {
        ArrayList<BufferedImage> startWarp = new ArrayList<>();
        ArrayList<BufferedImage> endWarp = new ArrayList<>();
        int[] x = new int[numCP];
        int[] y = new int[numCP];
        double[] stepX = new double[numCP];
        double[] stepY = new double[numCP];
        for (int i = 0 ; i < numCP ; i++){
            x[i] = xStart[i][0];
            y[i] = yStart[i][0];
            stepX[i] = (double) (xEnd[i][0] - xStart[i][0]) / (double) numFrames;
            stepY[i] = (double) (yEnd[i][0] - yStart[i][0]) / (double) numFrames;
        }

        for (int j = 0 ; j < numFrames ; j++){
            BufferedImage startWarped = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            BufferedImage endWarped = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            for (int i = 0 ; i < numCP ; i++) {
                Triangle S = new Triangle(xStart[i][0], yStart[i][0], xStart[i][1], yStart[i][1],xStart[i][2], yStart[i][2]);
                Triangle T = new Triangle(((int) (x[i]+(j*stepX[i]))), ((int) (y[i]+(j*stepY[i]))), xStart[i][1], yStart[i][1],xStart[i][2], yStart[i][2]);
                MorphTools.warpTriangle(startImage, startWarped, S, T, null, null, false);
                MorphTools.warpTriangle(endImage, endWarped, S, T, null, null, false);

                S = new Triangle(xStart[i][0], yStart[i][0], xStart[i][4], yStart[i][4],xStart[i][2], yStart[i][2]);
                T = new Triangle(((int) (x[i]+(j*stepX[i]))), ((int) (y[i]+(j*stepY[i]))), xStart[i][4], yStart[i][4],xStart[i][2], yStart[i][2]);
                MorphTools.warpTriangle(startImage, startWarped, S, T, null, null, false);
                MorphTools.warpTriangle(endImage, endWarped, S, T, null, null, false);

                S = new Triangle(xStart[i][0], yStart[i][0], xStart[i][4], yStart[i][4],xStart[i][3], yStart[i][3]);
                T = new Triangle(((int) (x[i]+(j*stepX[i]))), ((int) (y[i]+(j*stepY[i]))), xStart[i][4], yStart[i][4],xStart[i][3], yStart[i][3]);
                MorphTools.warpTriangle(startImage, startWarped, S, T, null, null, false);
                MorphTools.warpTriangle(endImage, endWarped, S, T, null, null, false);

                S = new Triangle(xStart[i][0], yStart[i][0], xStart[i][1], yStart[i][1],xStart[i][3], yStart[i][3]);
                T = new Triangle(((int) (x[i]+(j*stepX[i]))), ((int) (y[i]+(j*stepY[i]))), xStart[i][1], yStart[i][1],xStart[i][3], yStart[i][3]);
                MorphTools.warpTriangle(startImage, startWarped, S, T, null, null, false);
                MorphTools.warpTriangle(endImage, endWarped, S, T, null, null, false);
            }
            startWarp.add(startWarped);
            endWarp.add(endWarped);
        }
        BufferedImage[] bim = new BufferedImage[numFrames];
        for (int i = 0 ; i < numFrames ; i++) {
            bim[i] = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            Graphics g = bim[i].getGraphics();
            Graphics2D big = (Graphics2D) g;
            float alpha = (float) i / (float) numFrames;
            big.drawImage(startWarp.get(i), 0, 0, null);
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            big.setComposite(ac);
            big.drawImage(endWarp.get(numFrames-i-1), 0, 0, null);
        }
        File startOut = new File("src/morph/0.jpeg");
        try {
            ImageIO.write(startImage, "jpg", startOut);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        for (int i = 0 ; i < bim.length ; ++i) {
            startOut = new File("src/morph/"+ (i + 1)+ ".jpeg");
            try {
                ImageIO.write(bim[i], "jpg", startOut);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        startOut = new File("src/morph/" + (numFrames + 1) + ".jpeg");
        try {
            ImageIO.write(endImage, "jpg", startOut);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
