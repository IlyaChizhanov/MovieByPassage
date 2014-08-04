package palette;

import tools.Pixmap;
import tools.PixmapGame;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import static screen.main.images;

public class NextBlock extends View {
	
	boolean next = false;
	Matrix matrix = new Matrix();
	Paint paint = new Paint();
	int lvl = 1;
	int money = 0;
	
	public NextBlock(Context context) {
		super(context);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        
	}
	
	public boolean getNext(){
		return next;
	}
	
	public void updateInfo(int lvl, int money){
		this.lvl = lvl;
		this.money = money;
	}
	
	@Override 
    public boolean onTouchEvent(MotionEvent event){
		int Action=event.getAction();
		if(Action == 1){
			int w = this.getWidth();
			int h = this.getHeight();
			
			float x_click = event.getX();
			float y_click = event.getY();
			
			float x = (float)(w*0.075);
			float y = (float)(h*0.597);
			float w_next = (float)(w*0.853);
			float h_next = (float)(h*0.055);
			
			if(inBounds(x_click, y_click, x, y, w_next, h_next)){
				next = true;
			}
			
			
		}
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int w = this.getWidth();
		int h = this.getHeight();
		
		Pixmap fon = images.getImage("win");
		Pixmap button_next = images.getImage("win_next");
		Pixmap money_pix = images.getImage("money");
		
		float x = (float)(w*0.075);
		float y = (float)(h*0.597);
		float w_next = (float)(w*0.853);
		float h_next = (float)(h*0.055);
		
		drawPixmap(canvas, fon,
				0,0,
				(float)fon.getWidth(),(float)fon.getHeight(),
				(float)w, (float)h, 0);
		
		
		drawPixmap(canvas, button_next,
				x,y,
				(float)button_next.getWidth(),(float)button_next.getHeight(),
				(float)w_next, (float)h_next, 0);
		
		float x_text = (float)(w*0.135);
		float y_text = (float)(h*0.52);
		float x_text2 = (float)(w*0.743);
		
		//рисуем текст - самое сложное
		float size_text = (float)(h*0.037);
		paint.setTextSize(size_text);
		
		canvas.drawText(lvl + " уровень", x_text, (float)(y_text+(size_text*0.5)), paint);
		canvas.drawText(money + "", x_text2, (float)(y_text+(size_text*0.5)), paint);
		
		drawPixmap(canvas, money_pix,
				(float)(w*0.682),
				(float)(h*0.51),
				money_pix.getWidth(),
				money_pix.getHeight(),
				(float)(w*0.049),(float)(h*0.033), 0);
		
		
	}
	
	public void drawPixmap(Canvas canvas, Pixmap pixmap, float x, float y,
			float Width, float Height, float newWidth, float newHeight, float rotate) {
		
		float scaleX = (float) newWidth / (float) Width;
	    float scaleY = (float) newHeight / (float) Height;

	    matrix.reset();
	    
		matrix.postScale(scaleX, scaleY);//масштабируем
		matrix.postTranslate(x, y);//задаём координаты
		
		if(rotate != 0)matrix.preRotate(rotate, Width/2, Height/2);//поворачиваем относительно цетра
		
        canvas.drawBitmap( ((PixmapGame) pixmap).getBitmap(), matrix, null);
    }
	
	private boolean inBounds(float TouchX, float TouchY, float x, float y, float width, float height) {
        if(TouchX > x && TouchX < x + width - 1 && TouchY > y && TouchY < y + height - 1)
            return true;
        else
            return false;
    }

}
