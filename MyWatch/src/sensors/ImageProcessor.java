package sensors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by David on 11.06.2015.
 */
public class ImageProcessor {

    /**
     * Convert an array of image bytes to a Bitmap
     *
     * @param data image in bytes
     * @return Bitmap that was converted
     */
    private Bitmap toBitmap(byte[] data){
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * Converts YUV420 NV21 to Y888 (RGB8888)
     *
     * @param pixels output array with the converted array of grayscale pixels
     * @param data byte array
     * @param width pixels width
     * @param height pixels height
     */
    public void applyGrayScale(int [] pixels, byte [] data, int width, int height) {
        int p;
        int size = width*height;
        for(int i = 0; i < size; i++) {
            p = data[i] & 0xFF;
            pixels[i] = 0xff000000 | p<<16 | p<<8 | p;
        }
    }

    /**
     * Calculate the average Color of a given image
     *
     * @param data image to be analyzed
     * @return median Color of the image
     */
    private int averageColor(byte[] data, int previewWidth, int previewHeight){
        int avgColor;
        //convert byte array to Bitmap
        int[] pixels = new int[previewWidth*previewHeight];
        applyGrayScale(pixels, data, previewWidth, previewHeight);
        //Bitmap img = this.toBitmap(data);
        Bitmap img = Bitmap.createBitmap(pixels, previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        int width = img.getWidth();
        int height = img.getHeight();
        int amountOfPixels = width*height;
        //create Array to store the Color values of each pixel
        int[] imageColors = new int[amountOfPixels];
        //fill the array with the appropriate data
        img.getPixels(imageColors, 0, width, 0, 0, width, height);
        //extract the different Color channels and calculate the average
        int red = 0, green = 0, blue = 0;
        for (int pixelColor : imageColors) {
            red += Color.red(pixelColor);
            green += Color.green(pixelColor);
            blue += Color.blue(pixelColor);
        }
        int avgRed = red / amountOfPixels;
        int avgGreen = green / amountOfPixels;
        int avgBlue = blue / amountOfPixels;

        //return an argb integer of the median color
        avgColor = Color.rgb(avgRed, avgGreen, avgBlue);
        return avgColor;
    }

    /**
     * Tells if the averageColor of the image is darker than the given threshold color
     *
     * @param data image data to be checked
     * @param valueThreshold value of the threshold(hsv) (0..1)
     * @return boolean of the check
     */
     public boolean darkerThanThreshold(byte[] data, float valueThreshold, int width, int height){
         float[] avgHSVColor = new float[3];
         Color.colorToHSV(this.averageColor(data, width, height), avgHSVColor);
         //check if the average color is darker than the threshold
         return Float.compare(avgHSVColor[2], valueThreshold) < 1;
    }

}
