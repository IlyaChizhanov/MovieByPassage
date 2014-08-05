package screen;

import tools.ImagesContainer;

import com.moviebypassage.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Main extends Activity {
	
	public static ImagesContainer images;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		//загружаем изображения
		images = new ImagesContainer(this);
	}
	
	//событие клика
	public void onClickButtomPlay(View view){
		Intent intent = new Intent(Main.this, Game.class);
	    startActivity(intent);
	}
}
