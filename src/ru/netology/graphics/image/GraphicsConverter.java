package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class GraphicsConverter implements TextGraphicsConverter {

    private int width;
    private int height;
    private double maxRatio;
    private TextColorSchema schema;


    public GraphicsConverter() {
        schema = new ColorSchema();
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));
        double ratio = 0;
        int newWidth = 0;
        int newHeight = 0;
        double coeffWidth = 0;
        double coeffHeidht = 0;

        // вычисление соотношения сторон
        if (img.getWidth() / img.getHeight() > img.getHeight() / img.getWidth()) {
            ratio = (double) img.getWidth() / (double) img.getHeight();
        } else {
            ratio = (double) img.getHeight() / (double) img.getWidth();
        }
        //Выброс исключения
        if (ratio > maxRatio && maxRatio != 0) throw new BadImageSizeException(ratio, maxRatio);

        //регулирование под максимальную высоту и ширину
        if (img.getWidth() > width || img.getHeight() > height) {
            //вычисляем коэффициенты сжатия картинки
            if (width != 0) {
                coeffWidth = img.getWidth() / width;
            } else coeffWidth = 1;
            if (height != 0) {
                coeffHeidht = img.getHeight() / height;
            } else coeffHeidht = 1;

            if (coeffWidth > coeffHeidht) {
                newWidth = (int) (img.getWidth() / coeffWidth);
                newHeight = (int) (img.getHeight() / coeffWidth);
            } else {
                newWidth = (int) (img.getWidth() / coeffHeidht);
                newHeight = (int) (img.getHeight() / coeffHeidht);
            }
        } else {
            newWidth = img.getWidth();
            newHeight = img.getHeight();
        }

        char [][] symbol = new char[newHeight][newWidth];

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = bwImg.createGraphics();

        graphics.drawImage(scaledImage, 0, 0, null);

        WritableRaster bwRaster = bwImg.getRaster();

        for (int h = 0; h < newHeight; h++ ) {
            for (int w = 0; w < newWidth; w++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
            symbol[h][w] = c;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < symbol.length; i++) {
            for (int j = 0; j < symbol[i].length; j++) {
                builder.append(symbol[i][j]);
                builder.append(symbol[i][j]);
            }
            builder.append("\n");

        }
        String image = builder.toString();
        return image;
    }



    @Override
    public void setMaxWidth(int width) {
        this.width = width;

    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;

    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;

    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
