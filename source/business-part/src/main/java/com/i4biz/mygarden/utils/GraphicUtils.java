package com.i4biz.mygarden.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class GraphicUtils {
    public static BufferedImage loadImage(InputStream imageStream) throws IOException {
        return ImageIO.read(imageStream);
    }

    public static void saveImage(BufferedImage bi, OutputStream os) throws IOException {
        ImageIO.write(bi, "gif", os);
    }

    public static void drawPolygon(BufferedImage image, int rgb, int x, int y, int nCorner, double radius, double startFromAngle, String note, double imgScaleKoef) {
        x = (int)(x * imgScaleKoef);
        y = (int)(y * imgScaleKoef);
        radius = radius * imgScaleKoef;
        Graphics2D gr = (Graphics2D) image.getGraphics();

        double dA = Math.PI * 2 / nCorner;

        double angle = startFromAngle * Math.PI / 180;

        Polygon p = new Polygon();

        for (int i = 0; i < nCorner; i++) {
            double _x = radius * Math.sin(angle) + x;
            double _y = radius * Math.cos(angle) + y;

            p.addPoint((int) _x, (int) _y);

            angle += dA;
        }
        gr.setColor(new Color(rgb));
        float strokeSize = (float) (4 * imgScaleKoef);
        gr.setStroke(new BasicStroke(strokeSize));
        gr.drawPolygon(p);

        if (note != null && !"".equals(note)) {
            int fontSize = (int)(15*imgScaleKoef);
            gr.setFont(new Font("Calibri", Font.PLAIN, fontSize));
            double width = gr.getFontMetrics().stringWidth(note);
            gr.setStroke(new BasicStroke((float)imgScaleKoef));
            gr.drawLine(x, y, (int) (x + radius), (int) (y + radius));
            gr.drawLine((int) (x + radius), (int) (y + radius), (int) (x + radius + width), (int) (y + radius));
            gr.setColor(new Color(rgb));
            gr.drawString(note, (int) (x + radius), (int) (y + radius - 5));
        }
    }


    public static void main(String[] args) throws Exception {
        long st = System.currentTimeMillis();
        BufferedImage bi = loadImage(new FileInputStream("/home/sterehov/Изображения/garderob.png"));
        System.out.println(System.currentTimeMillis() - st);

        st = System.currentTimeMillis();
        drawPolygon(bi, 0xFF0000, 100, 100, 3, 50, 0, "Note 1", 1); // triangle v

//        drawPolygon(bi, 0x00FF00, 200, 100, 3, 50, Math.PI, "Note 1"); // triangle ^
//
//        drawPolygon(bi, 0x0000FF, 300, 100, 4, 50, 0, "Note 1"); // quad type 1
//
//        drawPolygon(bi, 0xFF00FF, 400, 100, 4, 50, Math.PI / 4, "Note 1"); // quad type 2
//
//        drawPolygon(bi, 0xFFFF00, 500, 100, 50, 50, 0, "Note 1"); // circle
        System.out.println(System.currentTimeMillis() - st);

        st = System.currentTimeMillis();
        saveImage(bi, new FileOutputStream("/home/sterehov/Загрузки/garderob.png"));
        System.out.println(System.currentTimeMillis() - st);
    }
}
