package finalproject;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import static org.opencv.core.Core.inRange;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import static org.opencv.imgproc.Imgproc.cvtColor;
import java.util.List;
import org.opencv.utils.Converters;

public class FetchRectangles {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    class Rectangle {

        Point topLeft = new Point();
        Point topRight = new Point();
        Point bottomLeft = new Point();
        Point bottomRight = new Point();

        public void setVar(Point tl, Point tr, Point bl, Point br) {
            this.topLeft = tl;
            this.topRight = tr;
            this.bottomLeft = bl;
            this.bottomRight = br;
        }

        public void reposition() {
            topLeft.x = topLeft.x + 40;
            topLeft.y = topLeft.y + 60;
            ///////////////////////
            topRight.x = topRight.x - 30;
            topRight.y = topRight.y + 60;
            ////////////////////////
            bottomLeft.x = bottomLeft.x + 40;
            bottomLeft.y = bottomLeft.y - 20;
            ////////////////////////
            bottomRight.x = bottomRight.x - 30;
            bottomRight.y = bottomRight.y - 20;
            ////////////////////////
        }

    };

    public static class fullpaper {

        Mat up = new Mat();
        Mat down = new Mat();

        public fullpaper(Mat up, Mat down) {

            this.up = up;
            this.down = down;
        }

        private fullpaper() {

        }
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

    
    
      public fullpaper detectLines(Mat imageBGR) {
//        inRange(imageBGR, new Scalar(0, 0, 0), new Scalar(30, 30, 30), blackrange);
//        inRange(imageBGR, new Scalar(160, 40, 30), new Scalar(220, 100, 80), bluerange);
        Mat gray = new Mat(imageBGR.size(), CvType.CV_8UC1);
        cvtColor(imageBGR, gray, Imgproc.COLOR_BGR2GRAY);
        Mat blackrange = new Mat(imageBGR.size(), CvType.CV_8UC1);
        Mat bluerange = new Mat(imageBGR.size(), CvType.CV_8UC1);
        Mat image = new Mat(imageBGR.size(), CvType.CV_8UC1);
        cvtColor(imageBGR, image, Imgproc.COLOR_BGR2HSV);//Normal RGB image
        inRange(imageBGR, new Scalar(0, 0, 0), new Scalar(30, 30, 30), blackrange);
        inRange(imageBGR, new Scalar(230, 0, 0), new Scalar(255, 30, 30), bluerange);

        Imgproc.GaussianBlur(image, image, new Size(5, 5), 2, 2);

        Mat circlesBlue = new Mat();
        Mat circlesBlack = new Mat();
        Imgproc.HoughCircles(blackrange, circlesBlack, Imgproc.CV_HOUGH_GRADIENT, 1, image.rows() / 64, 100, 14, 8, 50);
        Imgproc.HoughCircles(bluerange, circlesBlue, Imgproc.CV_HOUGH_GRADIENT, 1, image.rows() / 64, 100, 14, 8, 50);

        circl[] arrCirclesBlack = new circl[2];
        circl[] arrCirclesBlue = new circl[2];
        for (int l = 0; l < 2; l++) {
            arrCirclesBlack[l] = new circl();
        }
        for (int l = 0; l < 2; l++) {
            arrCirclesBlue[l] = new circl();
        }

        for (int i = 0; i < circlesBlack.cols(); i++) {
            double vCircle[] = circlesBlack.get(0, i);

            if (vCircle == null) {
                break;
            }

            double x = vCircle[0];
            double y = vCircle[1];
            double r = vCircle[2];

//            Point pt = new Point(Math.round(x), Math.round(y));
//            Core.circle(imageBGR, pt, (int) r, new Scalar(0, 255, 255), 5);
//            Core.circle(imageBGR, pt, 3, new Scalar(0, 255, 0), 5);
            arrCirclesBlack[i].setVar(x, y, r);

        }

        for (int i = 0; i < circlesBlue.cols(); i++) {
            double vCircle[] = circlesBlue.get(0, i);

            if (vCircle == null) {
                break;
            }

            double x = vCircle[0];
            double y = vCircle[1];
            double r = vCircle[2];

//            Point pt = new Point(Math.round(x), Math.round(y));
//            Core.circle(imageBGR, pt, (int) r, new Scalar(0, 0, 255), 2);
//            Core.circle(imageBGR, pt, 3, new Scalar(0, 0, 0), 2);
            arrCirclesBlue[i].setVar(x, y, r);

        }

        orderGeneral(arrCirclesBlack);
        orderGeneral(arrCirclesBlue);
        Rectangle up = new Rectangle();
        Rectangle down = new Rectangle();

        Point blackBottomRight = new Point(arrCirclesBlack[1].x, arrCirclesBlack[1].y);
        Point blackBottomLeft = new Point(arrCirclesBlack[0].x, arrCirclesBlack[1].y);
        Point blackTopRight = new Point(arrCirclesBlack[1].x, arrCirclesBlack[0].y);
        Point blackTopLeft = new Point(arrCirclesBlack[0].x, arrCirclesBlack[0].y);

        up.setVar(blackTopLeft, blackTopRight, blackBottomLeft, blackBottomRight);
//        Core.circle(imageBGR, up.topLeft, 30, new Scalar(0, 255, 255), 3);//Yellow
//        Core.circle(imageBGR, up.topRight, 30, new Scalar(255, 0, 255), 3);//Magenta
//        Core.circle(imageBGR, up.bottomLeft, 30, new Scalar(255, 255, 0), 3);//Cyan
//        Core.circle(imageBGR, up.bottomRight, 30, new Scalar(0, 0, 0), 3);//Black
         Mat quadUp = warp(imageBGR, up.topLeft, up.topRight, up.bottomLeft, up.bottomRight);
        Point blueBottomRight = new Point(arrCirclesBlue[1].x, arrCirclesBlue[1].y);
        Point blueBottomLeft = new Point(arrCirclesBlue[0].x, arrCirclesBlue[1].y);
        Point blueTopRight = new Point(arrCirclesBlue[1].x, arrCirclesBlue[0].y);
        Point blueTopLeft = new Point(arrCirclesBlue[0].x, arrCirclesBlue[0].y);
        up.setVar(blueTopLeft, blueTopRight, blueBottomLeft, blueBottomRight);
        down.setVar(blueTopLeft, blueTopRight, blueBottomLeft, blueBottomRight);
        
        
        
       
        Mat quadDown = warp(imageBGR, down.topLeft, down.topRight, down.bottomLeft, down.bottomRight);
//        Mat quadDown = warp(imageBGR, down.topLeft, down.topRight, down.bottomLeft, down.bottomRight);
        fullpaper fp = new fullpaper(quadUp, quadDown);

        return fp;
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

       public circl[] orderGeneral(circl[] x) {
        circl temp[] = new circl[2];

        for (int i = 0; i < temp.length; i++) {
            temp[i] = new circl();
        }

        temp = x;
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
        return temp;
    }
    public fullpaper detectModelAndYear(Mat imageBGR, int choice) {
        Mat gray = new Mat(imageBGR.size(), CvType.CV_8UC1);
        cvtColor(imageBGR, gray, Imgproc.COLOR_BGR2GRAY);
        Mat range = new Mat(imageBGR.size(), CvType.CV_8UC1);
        Mat image = new Mat(imageBGR.size(), CvType.CV_8UC1);
        if (choice == 1) {//Model
            inRange(imageBGR, new Scalar(240, 0, 146), new Scalar(255, 20, 170), range);//Modify
        } else if (choice == 2) {//year
            inRange(imageBGR, new Scalar(0, 225, 230), new Scalar(40, 255, 255), range);
        }
        
        Imgproc.GaussianBlur(image, image, new Size(5, 5), 2, 2);

        Mat circlesBlack = new Mat();
        Imgproc.HoughCircles(range, circlesBlack, Imgproc.CV_HOUGH_GRADIENT, 1, image.rows() / 64, 100, 10, 8, 25);

        circl[] arrCircles = new circl[2];
        for (int l = 0; l < 2; l++) {
            arrCircles[l] = new circl();
        }
      
        for (int i = 0; i < circlesBlack.cols(); i++) {
            double vCircle[] = circlesBlack.get(0, i);

            if (vCircle == null) {
                break;
            }

            double x = vCircle[0];
            double y = vCircle[1];
            double r = vCircle[2];

//            Point pt = new Point(Math.round(x), Math.round(y));
//            Core.circle(imageBGR, pt, (int) r, new Scalar(0, 255, 255), 5);
//            Core.circle(imageBGR, pt, 3, new Scalar(0, 255, 0), 5);
            arrCircles[i].setVar(x, y, r);

        }


        orderGeneral(arrCircles);
        Rectangle up = new Rectangle();
        double r = arrCircles[1].radius;
        Point BottomRight = new Point(arrCircles[1].x, arrCircles[1].y+r);
        Point BottomLeft = new Point(arrCircles[0].x, arrCircles[1].y+r);
        Point TopRight = new Point(arrCircles[1].x, arrCircles[0].y);
        Point TopLeft = new Point(arrCircles[0].x, arrCircles[0].y);

        up.setVar(TopLeft, TopRight, BottomLeft, BottomRight);
//        Core.circle(imageBGR, up.topLeft, 30, new Scalar(0, 255, 255), 3);//Yellow
//        Core.circle(imageBGR, up.topRight, 30, new Scalar(255, 0, 255), 3);//Magenta
//        Core.circle(imageBGR, up.bottomLeft, 30, new Scalar(255, 255, 0), 3);//Cyan
//        Core.circle(imageBGR, up.bottomRight, 30, new Scalar(0, 0, 0), 3);//Black
      
        
        
        
        Mat quadUp = warp(imageBGR, up.topLeft, up.topRight, up.bottomLeft, up.bottomRight);
//        Mat quadDown = warp(imageBGR, down.topLeft, down.topRight, down.bottomLeft, down.bottomRight);
        fullpaper fp = new fullpaper(quadUp, quadUp);

        return fp;}


   public fullpaper detectID(Mat imageBGR) {
        
        Mat gray = new Mat(imageBGR.size(), CvType.CV_8UC1);
        cvtColor(imageBGR, gray, Imgproc.COLOR_BGR2GRAY);
        Mat range = new Mat(imageBGR.size(), CvType.CV_8UC1);
        Mat image = new Mat(imageBGR.size(), CvType.CV_8UC1);
        cvtColor(imageBGR, image, Imgproc.COLOR_BGR2HSV);//Normal RGB image
        inRange(imageBGR, new Scalar(0, 200, 0), new Scalar(100, 255, 100), range);

        Imgproc.GaussianBlur(image, image, new Size(5, 5), 2, 2);

        Mat circlesBlack = new Mat();
        Imgproc.HoughCircles(range, circlesBlack, Imgproc.CV_HOUGH_GRADIENT, 1, image.rows() / 64, 100, 14, 8, 50);

        circl[] arrCircles = new circl[2];
        for (int l = 0; l < 2; l++) {
            arrCircles[l] = new circl();
        }
      
        for (int i = 0; i < circlesBlack.cols(); i++) {
            double vCircle[] = circlesBlack.get(0, i);

            if (vCircle == null) {
                break;
            }

            double x = vCircle[0];
            double y = vCircle[1];
            double r = vCircle[2];

//            Point pt = new Point(Math.round(x), Math.round(y));
//            Core.circle(imageBGR, pt, (int) r, new Scalar(0, 255, 255), 5);
//            Core.circle(imageBGR, pt, 3, new Scalar(0, 255, 0), 5);
            arrCircles[i].setVar(x, y, r);

        }


        orderGeneral(arrCircles);
        Rectangle up = new Rectangle();

        Point BottomRight = new Point(arrCircles[1].x, arrCircles[1].y);
        Point BottomLeft = new Point(arrCircles[0].x, arrCircles[1].y);
        Point TopRight = new Point(arrCircles[1].x, arrCircles[0].y);
        Point TopLeft = new Point(arrCircles[0].x, arrCircles[0].y);

        up.setVar(TopLeft, TopRight, BottomLeft, BottomRight);
//        Core.circle(imageBGR, up.topLeft, 30, new Scalar(0, 255, 255), 3);//Yellow
//        Core.circle(imageBGR, up.topRight, 30, new Scalar(255, 0, 255), 3);//Magenta
//        Core.circle(imageBGR, up.bottomLeft, 30, new Scalar(255, 255, 0), 3);//Cyan
//        Core.circle(imageBGR, up.bottomRight, 30, new Scalar(0, 0, 0), 3);//Black
   
        Mat quadUp = warp(imageBGR, up.topLeft, up.topRight, up.bottomLeft, up.bottomRight);
//        Mat quadDown = warp(imageBGR, down.topLeft, down.topRight, down.bottomLeft, down.bottomRight);
        fullpaper fp = new fullpaper(quadUp, quadUp);

        return fp;}

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    Mat warp(Mat inputMat, Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
        int resultWidth = (int) (topRight.x - topLeft.x);
        int bottomWidth = (int) (bottomRight.x - bottomLeft.x);
        if (bottomWidth > resultWidth) {
            resultWidth = bottomWidth;
        }

        int resultHeight = (int) (bottomLeft.y - topLeft.y);
        int bottomHeight = (int) (bottomRight.y - topRight.y);
        if (bottomHeight > resultHeight) {
            resultHeight = bottomHeight;
        }

        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC1);

        List<Point> source = new ArrayList<>();
        source.add(topLeft);
        source.add(topRight);
        source.add(bottomLeft);
        source.add(bottomRight);
        Mat startM = Converters.vector_Point2f_to_Mat(source);

        Point ocvPOut1 = new Point(0, 0);
        Point ocvPOut2 = new Point(resultWidth, 0);
        Point ocvPOut3 = new Point(0, resultHeight);
        Point ocvPOut4 = new Point(resultWidth, resultHeight);
        List<Point> dest = new ArrayList<>();
        dest.add(ocvPOut1);
        dest.add(ocvPOut2);
        dest.add(ocvPOut3);
        dest.add(ocvPOut4);
        Mat endM = Converters.vector_Point2f_to_Mat(dest);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight));

        return outputMat;
    }

}
