package tools;

import java.util.HashMap;
import java.util.Map;

import com.moviebypassage.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;



public class ImagesContainer {
	Map<String, Pixmap> Images = new HashMap<String, Pixmap>();
	Context context;
	
	public ImagesContainer(Context context){
		this.context = context;
		loadImages();
	}
	
	public Pixmap getImage(String key){//получаем изображение
		if(!Images.containsKey(key)) return null;
		return Images.get(key);
	}
	
	private void loadImages(){//загружаем все изображения
		
		//загружаем русские буквы
		NewImage("ru_a", R.drawable.ru_a);
		NewImage("ru_b", R.drawable.ru_b);
		NewImage("ru_v", R.drawable.ru_v);
		NewImage("ru_g", R.drawable.ru_g);
		NewImage("ru_d", R.drawable.ru_d);
		NewImage("ru_ye", R.drawable.ru_ye);
		NewImage("ru_ye2", R.drawable.ru_ye2);
		NewImage("ru_zh", R.drawable.ru_zh);
		NewImage("ru_z", R.drawable.ru_z);
		NewImage("ru_i", R.drawable.ru_i);
		NewImage("ru_y", R.drawable.ru_y);
		NewImage("ru_k", R.drawable.ru_k);
		NewImage("ru_l", R.drawable.ru_l);
		NewImage("ru_m", R.drawable.ru_m);
		NewImage("ru_n", R.drawable.ru_n);
		NewImage("ru_o", R.drawable.ru_o);
		NewImage("ru_p", R.drawable.ru_p);
		NewImage("ru_r", R.drawable.ru_r);
		NewImage("ru_s", R.drawable.ru_s);
		NewImage("ru_t", R.drawable.ru_t);
		NewImage("ru_u", R.drawable.ru_u);
		NewImage("ru_f", R.drawable.ru_f);
		NewImage("ru_kh", R.drawable.ru_kh);
		NewImage("ru_ts", R.drawable.ru_ts);
		NewImage("ru_ch", R.drawable.ru_ch);
		NewImage("ru_sh", R.drawable.ru_sh);
		NewImage("ru_shch", R.drawable.ru_shch);
		NewImage("ru_tv", R.drawable.ru_tv);
		NewImage("ru_y2", R.drawable.ru_y2);
		NewImage("ru_tv2", R.drawable.ru_tv2);
		NewImage("ru_e", R.drawable.ru_e);
		NewImage("ru_yu", R.drawable.ru_yu);
		NewImage("ru_ya", R.drawable.ru_ya);
		
		NewImage("cells", R.drawable.cells);
		NewImage("cells_error", R.drawable.cells_error);
		
		NewImage("bar", R.drawable.bar);
		
		NewImage("money", R.drawable.money);
		
		NewImage("win", R.drawable.win);
		NewImage("win_next", R.drawable.win_next);
	}
	
	private void NewImage(String key, int id){
		Images.put(key, newPixmap(id));
	}
	
	public Pixmap newPixmap(int id) {
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
		return new PixmapGame(bmp);
	}
}
