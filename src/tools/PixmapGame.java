package tools;

import android.graphics.Bitmap;

public class PixmapGame implements Pixmap {
    private Bitmap bitmap;
    PixmapFormat format;
    
    public PixmapGame(Bitmap bitmap) {
        this.setBitmap(bitmap);
    }

    @Override
	public int getWidth() {
        return getBitmap().getWidth();
    }

    @Override
	public int getHeight() {
        return getBitmap().getHeight();
    }

    @Override
	public void dispose() {
        getBitmap().recycle();
    }

	@Override
	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
    
}
