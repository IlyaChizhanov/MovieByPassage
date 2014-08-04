package tools;

import android.graphics.Bitmap;

public class PixmapGame implements Pixmap {
    private Bitmap bitmap;
    PixmapFormat format;
    
    public PixmapGame(Bitmap bitmap) {
        this.setBitmap(bitmap);
    }

    public int getWidth() {
        return getBitmap().getWidth();
    }

    public int getHeight() {
        return getBitmap().getHeight();
    }

    public void dispose() {
        getBitmap().recycle();
    }

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
    
}
