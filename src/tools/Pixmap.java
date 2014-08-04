package tools;

import android.graphics.Bitmap;



public interface Pixmap {
	public static enum PixmapFormat {
        ARGB8888, ARGB4444, RGB565
    }
	
    public int getWidth();

    public int getHeight();

    public void dispose();
    
    public Bitmap getBitmap();
}