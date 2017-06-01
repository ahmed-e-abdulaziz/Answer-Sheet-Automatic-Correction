package finalproject;

import finalproject.FetchRectangles.fullpaper;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import javax.swing.JOptionPane;
import static org.opencv.core.Core.countNonZero;
import static org.opencv.core.Core.inRange;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import static org.opencv.imgproc.Imgproc.cvtColor;
import java.util.Arrays;

public class IdentCircles {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public class circl {

        double x = 0;
        double y = 0;
        double radius = 0;

        public void setVar(double x, double y, double radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public circl() {
            x = 0.0;
            y = 0.0;
            radius = 0.0;
        }

    };
    
    public int[][] getAnswers(String s) {
        Mat imageBGR = Highgui.imread(s);
        FetchRectangles fetcher = new FetchRectangles();
        
        fullpaper fp=fetcher.detectLines(imageBGR);
        imageBGR=fp.up;
        Mat image = new Mat(imageBGR.size(), CvType.CV_8UC1);
        Mat gray = new Mat(imageBGR.size(), CvType.CV_8UC1);
        cvtColor(imageBGR, image, Imgproc.COLOR_BGR2HSV);//Normal RGB image
        cvtColor(imageBGR, gray, Imgproc.COLOR_BGR2GRAY);

        inRange(image, new Scalar(0, 0, 0), new Scalar(255, 255, 243), image);

        Imgproc.GaussianBlur(image, image, new Size(7, 7), 2, 2);
        Mat circles = new Mat(imageBGR.size(), CvType.CV_8UC1);
        Imgproc.HoughCircles(image, circles, Imgproc.CV_HOUGH_GRADIENT, 1, image.rows() / 64, 100, 22, 13, 25);

        circl[][] arrCircles = new circl[50][5];
        for (int k = 0; k < 50; k++) {
            for (int l = 0; l < 5; l++) {
                arrCircles[k][l] = new circl();
            }
        }
        int j = 0;
        int l = 0;
        for (int i = 0; i < circles.cols(); i++) {
            double vCircle[] = circles.get(0, i);
            if (j == 5) {
                j = 0;
                l++;
            }
            
            if (vCircle == null) {
                break;
            }

            double x = vCircle[0];
            double y = vCircle[1];
            double r = vCircle[2];
            if(x==0&y==0){x=1200;y=900;r=10;}
            else{
//          arrCircles[dim.y][dim.x].setVar(x, y, r);
            arrCircles[l][j].setVar(x, y, r);
            }
            j++;

        }
        arrCircles = order(arrCircles);
        int[][] answers = new int[50][5];
        for (int a = 0; a < 50; a++) {
            for (int b = 0; b < 5; b++) {
                
                double x = arrCircles[a][b].x;
                double y = arrCircles[a][b].y;
                double r = arrCircles[a][b].radius;
                
                Rect rect = new Rect((int) (x - r), (int) (y - r), (int) (2 * r), (int) (2 * r));
                
                Mat submat = new Mat(gray, rect);
                
                inRange(submat, new Scalar(0, 0, 0), new Scalar(180, 180, 180), submat);
                double p = (double) countNonZero(submat) / (submat.size().width * submat.size().height);
                
                int choosen = -1;

                if (p >= 0.3) {
                    choosen = b;
                }
                int radius = (int) Math.round(r);
                if (choosen == -1) {
                    
                } else {
                    answers[a][b] = 1;
//                    JOptionPane.showMessageDialog(null, a + 1 + Character.toString((char) ('A' + choosen))+"X = "+x+"Y = "+y );
                }
//                JOptionPane.showMessageDialog(null, a + 1 + Character.toString((char) ('A' + choosen))+"X = "+x+"Y = "+y+"R = "+r );
                Point pt = new Point(Math.round(x), Math.round(y));
                Core.circle(imageBGR, pt, radius, new Scalar(0, 255, 0), 2);
                Core.circle(imageBGR, pt, 3, new Scalar(0, 0, 0), 2);
            }
        }

      return answers;
    }

     public void showFrameImage(BufferedImage bufImage) {
        BufferedImage img = resize(bufImage, 768, 543);
        JFrame frame = new JFrame();
        ImageIcon i = new ImageIcon(img);
        JLabel jl = new JLabel(i);
        frame.getContentPane().add(jl);
        frame.pack();
        frame.setVisible(true);
    }

    public circl[][] order(circl[][] x) {
        circl temp[] = new circl[x[0].length * x.length];
        circl temp2[][] = new circl[10][25];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 25; j++) {
                temp2[i][j] = new circl();
            }
        }
        for (int i = 0; i < temp.length; i++) {
            temp[i] = new circl();
        }
        int k = 0;
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                temp[k++] = x[i][j];
            }
        }
        Arrays.sort(temp, new Comparator<circl>() {
            @Override
            public int compare(circl o1, circl o2) {
                
                if (o1.y > o2.y) {
                    return 1;
                } else if (o1.y < o2.y) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        int count = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 25; j++) {
                temp2[i][j] = temp[count++];
            }
        }
        for (int i = 0; i < 10; i++) {
            Arrays.sort(temp2[i], new Comparator<circl>() {
                @Override
                public int compare(circl o1, circl o2) {
                    if (o1.x > o2.x) {
                        return 1;
                    } else if (o1.x < o2.x) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 25; j++) {
                if (j < 5) {
                    x[i][j] = temp2[i][j];
                } else if (j < 10) {
                    x[i + 10][j % 5] = temp2[i][j];
                } else if (j < 15) {
                    x[i + 20][j % 5] = temp2[i][j];
                } else if (j < 20) {
                    x[i + 30][j % 5] = temp2[i][j];
                } else if (j < 25) {
                    x[i + 40][j % 5] = temp2[i][j];
                }

            }

        }
        return x;
    }
    
     
    public circl[][] orderID(circl[][] x) {
        circl temp[] = new circl[x[0].length * x.length];
        circl temp2[][] = new circl[9][10];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                temp2[i][j] = new circl();
            }
        }
        for (int i = 0; i < temp.length; i++) {
            temp[i] = new circl();
        }
        int k = 0;
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                temp[k++] = x[i][j];
            }
        }
        Arrays.sort(temp, new Comparator<circl>() {
            @Override
            public int compare(circl o1, circl o2) {
                if (o1.x > o2.x) {
                    return 1;
                } else if (o1.x < o2.x) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        int count = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 10; j++) {
                temp2[i][j] = temp[count++];
            }
        }
        for (int i = 0; i < 9; i++) {
            Arrays.sort(temp2[i], new Comparator<circl>() {
                @Override
                public int compare(circl o1, circl o2) {
                    if (o1.y > o2.y) {
                        return 1;
                    } else if (o1.y < o2.y) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

        }
        return temp2;
    }
    
    public circl[] orderModelandYear(circl[] x) {

        Arrays.sort(x, new Comparator<circl>() {
            @Override
            public int compare(circl o1, circl o2) {
                if (o1.x > o2.x) {
                    return 1;
                } else if (o1.x < o2.x) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        return x;
    }
    
     public String getID(String s) {
        Mat imageBGR = Highgui.imread(s);
        FetchRectangles fetcher = new FetchRectangles();

        fullpaper fp = fetcher.detectID(imageBGR);
        imageBGR = fp.up;
        Mat image = new Mat(imageBGR.size(), CvType.CV_8UC1);
        Mat gray = new Mat(imageBGR.size(), CvType.CV_8UC1);
        cvtColor(imageBGR, image, Imgproc.COLOR_BGR2HSV);//Normal RGB image
        cvtColor(imageBGR, gray, Imgproc.COLOR_BGR2GRAY);

        inRange(image, new Scalar(0, 0, 0), new Scalar(255, 255, 243), image);

        Imgproc.GaussianBlur(image, image, new Size(5, 5), 2, 2);
        Mat circles = new Mat(imageBGR.size(), CvType.CV_8UC1);
        Imgproc.HoughCircles(image, circles, Imgproc.CV_HOUGH_GRADIENT, 1, image.rows() / 64, 100, 17, 14, 20);

        circl[][] arrCircles = new circl[9][10];
        for (int k = 0; k < 9; k++) {
            for (int l = 0; l < 10; l++) {
                arrCircles[k][l] = new circl();
            }
        }
        int j = 0;
        int l = 0;
        for (int i = 0; i < circles.cols(); i++) {
            double vCircle[] = circles.get(0, i);
            if (l == 9) {
                l = 0;
                j++;
            }

            if (vCircle == null) {
                break;
            }

            double x = vCircle[0];
            double y = vCircle[1];
            double r = vCircle[2];

//            Point pt = new Point(Math.round(x), Math.round(y));
//            Core.circle(imageBGR, pt, (int) r, new Scalar(255, 255, 123), 2);
//            Core.circle(imageBGR, pt, 3, new Scalar(0, 0, 0), 2);
            arrCircles[l][j].setVar(x, y, r);
            l++;

        }
        circl[][] idCircles = new circl[9][10];
        for (int k = 0; k < 9; k++) {
            for (int h = 0; h < 10; h++) {
                idCircles[k][h] = new circl();
            }
        }
        idCircles = orderID(arrCircles);
        String ident= "";
        int[][] ID = new int[10][9];
        for (int a = 0; a < 9; a++) {
            for (int b = 0; b < 10; b++) {
                double x = idCircles[a][b].x;
                double y = idCircles[a][b].y;
                double r = idCircles[a][b].radius;

                Rect rect = new Rect((int) (x - r), (int) (y - r), (int) (2 * r), (int) (2 * r));

                Mat submat = new Mat(gray, rect);

                inRange(submat, new Scalar(0, 0, 0), new Scalar(180, 180, 180), submat);
                double p = (double) countNonZero(submat) / (submat.size().width * submat.size().height);

                int choosen = -1;

                if (p >= 0.25) {
                    choosen = b;
                }
                
                if (choosen == -1) {

                } else {
                    ID[b][a] = 1;
                    ident=ident+choosen;
//                    JOptionPane.showMessageDialog(null, a + 1 + Character.toString((char) ('A' + choosen - 1)));
//                    Point pt = new Point(Math.round(x), Math.round(y));
//                    Core.circle(imageBGR, pt, radius, new Scalar(0, 0, 0), 2);
//                    Core.circle(imageBGR, pt, 3, new Scalar(0, 0, 0), 2);
                }

            }
        }

        return ident;
    }

    public int getModelAndYear(String s, int choice) {
        //choice 1 for year //choice 2 for model
        Mat imageBGR = Highgui.imread(s);
        FetchRectangles fetcher = new FetchRectangles();
        fullpaper fp = fetcher.detectModelAndYear(imageBGR, choice);
        imageBGR = fp.up;
        Mat image = new Mat(imageBGR.size(), CvType.CV_8UC1);
        Mat gray = new Mat(imageBGR.size(), CvType.CV_8UC1);
        cvtColor(imageBGR, image, Imgproc.COLOR_BGR2HSV);//Normal RGB image
        cvtColor(imageBGR, gray, Imgproc.COLOR_BGR2GRAY);

        inRange(image, new Scalar(0, 0, 0), new Scalar(255, 255, 243), image);

        Imgproc.GaussianBlur(image, image, new Size(5, 5), 2, 2);
        Mat circles = new Mat(imageBGR.size(), CvType.CV_8UC1);
        Imgproc.HoughCircles(image, circles, Imgproc.CV_HOUGH_GRADIENT, 1, image.rows() / 5, 100, 17, 14, 20);

        circl[] arrCircles = new circl[5];
        for (int k = 0; k < 5; k++) {
            arrCircles[k] = new circl();
        }

        for (int i = 0; i < circles.cols(); i++) {
            double vCircle[] = circles.get(0, i);

            if (vCircle == null) {
                break;
            }

            double x = vCircle[0];
            double y = vCircle[1];
            double r = vCircle[2];

//            Point pt = new Point(Math.round(x), Math.round(y));
//            Core.circle(imageBGR, pt, (int) r, new Scalar(255, 255, 123), 2);
//            Core.circle(imageBGR, pt, 3, new Scalar(0, 0, 0), 2);
            arrCircles[i].setVar(x, y, r);

        }

        arrCircles = orderModelandYear(arrCircles);
        int result = 0;
        for (int a = 0; a < 5; a++) {

            double x = arrCircles[a].x;
            double y = arrCircles[a].y;
            double r = arrCircles[a].radius;

            Rect rect = new Rect((int) (x - r), (int) (y - r), (int) (2 * r), (int) (2 * r));

            Mat submat = new Mat(gray, rect);

            inRange(submat, new Scalar(0, 0, 0), new Scalar(180, 180, 180), submat);
            double p = (double) countNonZero(submat) / (submat.size().width * submat.size().height);

            int choosen = -1;

            if (p >= 0.3) {
                choosen = a;
            }
            int radius = (int) Math.round(r);
            if (choosen == -1) {

            } else {
                result = choosen;
//                JOptionPane.showMessageDialog(null, Character.toString((char) ('A' + choosen )) );
//                Point pt = new Point(Math.round(x), Math.round(y));
//                Core.circle(imageBGR, pt, radius, new Scalar(0, 0, 0), 2);
//                Core.circle(imageBGR, pt, 3, new Scalar(0, 0, 0), 2);
            }

        }

        return result;
    }
    
    
public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

}
