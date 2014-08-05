package screen;

import palette.EditBlock;
import tools.Alert;
import tools.obb_tools;
import tools.Recall;

import com.moviebypassage.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.LinearLayout;

public class game extends Activity implements OnPreparedListener,
 SurfaceHolder.Callback
{
	private MediaPlayer mediaPlayer;//медиа плеер для видео
	private EditBlock editBlock;//блок в буквами и ячейками
	private SharedPreferences settings;//настройки: уровни и монеты
	
	private boolean played = false;//true если видео играеться, false если нет
	private int lvl = 1;//номер уровня
	private int money = 0;//количество монет
	
	obb_tools obb;//класс для работы с файлами расширения
	Recall recall;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		obb = new obb_tools(this);//для работы с файлами расширения
		recall = new Recall(this);// просба оценить приложение
		
		//загружаем настройки
		settings = getSharedPreferences("MovieByPassage", 0);
		lvl = settings.getInt("lvl", 1);
		money = settings.getInt("money", 0);
		//создаём первое слово
		editBlock = new EditBlock(this, WordName(lvl), 0);//создаём едитблок
		editBlock.UpdateInfo(lvl, money);//обновляем информация статистики
		
		((LinearLayout)findViewById(R.id.edit_block)).addView(editBlock);//добавляем блок к экрану
		
		//создаём блок просмотра видео
		SurfaceHolder holder = ((SurfaceView)findViewById(R.id.surfaceView1)).getHolder();
		holder.addCallback((Callback) this);
		
		mediaPlayer = new MediaPlayer();//создаём объект медиа плеера
		//------------загружаем видео---------------------
		load_film();//загружаем видео
		
		correctWordStart();//отдельный поток для проверки правильности введённого слова 
		
		mediaPlayer.setOnPreparedListener(this);//оброботчик. Вызовиться когда видео будет готово
	}
	
	@Override
	public void onDestroy(){
		editBlock.onDestroy();
		obb.onDestroy();
		mediaPlayer.release();
		super.onDestroy();
	}

	private void correctWordStart() {
		//начинаем проверку правильности слова
		new Thread(new Runnable() {
			@Override
			public void run() {
				RightWord();
			}
		}).start();
	}
	
	//загрузка фильма
	private void load_film() {
		
		if(settings.getInt("recall", 0) == 0){
			if(((int)(Math.random()*5)) == 2){
				recall.show();
			}
			
		}
		
		
		mediaPlayer.reset();//убираем старые данные
		try {
			AssetFileDescriptor afd = obb.getAssetFileDescriptor(lvl+".3gp");
			mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	        mediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//функция старта проигрывания или переигрывания
	public void ReplayMovie(){
        if(!mediaPlayer.isPlaying()){mediaPlayer.start();}//если видео не играет стартуем
        mediaPlayer.seekTo(0);//возвращаемся в начало
        played = true;//сообщаем о том что видео играеться
        //поток для остановки в нужный момент
        new Thread(new Runnable() {
			@Override
			public void run() {
				stop_film();
			}
		}).start();        
	}
	
	//как только видео загруженно стартуем
	@Override
	public void onPrepared(MediaPlayer mp) {
		ReplayMovie();
	}
	
	//останавливает проигрывание фильма на нужном моменте
	private void stop_film() {
		while(played){//если видео не играеться то не ждём конца
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//останавливаем видео если оно играеться больше 15 секунд 
			if(mediaPlayer.getCurrentPosition() > 15000 && mediaPlayer.isPlaying()){
				if(mediaPlayer != null)mediaPlayer.pause();//останавливаем видео
				played = false;//видео не играеться
			} else if(!mediaPlayer.isPlaying()){//если видео не играеться
				played = false;//обновляем статус
			}
		}
	}

	//событие клика по экрану с видео
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		ReplayMovie();
		return super.onTouchEvent(event);
	}
	
	//событие правильного слова
	private void RightWord(){
		while(true){
			if(editBlock.WordCorrect()){
				if(lvl == 24)return;//если достигнут конец
				mediaPlayer.pause();//останавливаем видео
				editBlock.MediaCorrect();//воспроизводим звук привильного слова
				//показываем блок с продолжением
				Intent correct = new Intent(game.this,correct.class);
				//передаём информацию
				correct.putExtra("lvl", lvl+1);
				correct.putExtra("money", money+15);
				startActivityForResult(correct, 1);//говорим что ждём результат
				break;
			}
			if(editBlock.tips){
				editBlock.tips = false;
				Intent bonus = new Intent(game.this,bonus.class);
				startActivityForResult(bonus, 2);//говорим что ждём результат
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//событие результата от окна
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Editor editor;
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				lvl +=1;
				money+=15;
				load_film();//загружаем новый фильм
				//обновляем блок с буквами и информацией
				editBlock.UpdateWord(WordName(lvl), 0);
				editBlock.UpdateInfo(lvl, money);
				editBlock.invalidate();
				//обновляем настройки
				editor = settings.edit();
				editor.putInt("lvl", lvl);
				editor.putInt("money", money);
				editor.commit();//готово
				correctWordStart();//стартуем новый поток проверки правильности слова
				break;
			case 2:
				if (data == null) {return;}
				int activ = data.getIntExtra("activ", 0);
				if(activ == 1){
					if(money >= 5){
						editBlock.MediaMoney();
						editBlock.RemoveLetter();
						money-=5;
					} else {
						StartAlert("Не хватает монет");
					}
				} else if(activ == 2){
					if(money >= 15){
						editBlock.MediaMoney();
						editBlock.OpenLetter();
						money-=15;
					} else {
						StartAlert("Не хватает монет");
					}
				} else if(activ == 3){
					if(money >= 40){
						editBlock.MediaMoney();
						Intent poster = new Intent(game.this,poster.class);
						poster.putExtra("lvl", lvl);
						startActivity(poster);
						money-=40;
					} else {
						StartAlert("Не хватает монет");
					}
				} else if(activ == 4){
					if(money >= 60){
						editBlock.MediaMoney();
						Intent contents = new Intent(game.this,Contents.class);
						contents.putExtra("lvl", lvl);
						startActivity(contents);
						money-=60;
					} else {
						StartAlert("Не хватает монет");
					}
				}
				
				editor = settings.edit();
				editor.putInt("money", money);
				editor.commit();//готово
				
				editBlock.UpdateInfo(lvl, money);
				editBlock.invalidate();
				
				break;
			}
	  }
	}

	private void StartAlert(String text) {
		Intent alert = new Intent(game.this,Alert.class);
		alert.putExtra("text", text);
		startActivity(alert);
	}
	
	//Названия фильмов по уровням
	private String WordName(int lvl){
		switch (lvl) {
		case 1:
			return "ОДИН ДОМА";
		case 2:
			return "РОБОКОП";
		case 3:
			return "СВЕРХЕСТЕСТВЕННОЕ";
		case 4:
			return "МСТИТЕЛИ";
		case 5:
			return "АВАТАР";
		case 6:
			return "ВО ВСЕ ТЯЖКИЕ";
		case 7:
			return "ОПЕРАЦИЯ Ы";
		case 8:
			return "ДЕКСТЕР";
		case 9:
			return "КИНГ КОНГ";
		case 10:
			return "ПЯТЫЙ ЭЛЕМЕНТ";
		case 11:
			return "ЛЮДИ В ЧЁРНОМ";
		case 12:
			return "УБИТЬ БИЛЛА";
		case 13:
			return "МАТРИЦА";
		case 14:
			return "СВАДЕБНЫЙ РАЗГРОМ";
		case 15:
			return "МАЧО И БОТАН";
		case 16:
			return "ДОКТОР КТО";
		case 17:
			return "ГРИММ";
		case 18:
			return "ШЕРЛОК ХОЛМС";
		case 19:
			return "ТИТАНИК";
		case 20:
			return "ВОЛЧОНОК";
		case 21:
			return "БОЙЦОВСКИЙ КЛУБ";
		case 22:
			return "БЛУДЛИВАЯ КАЛИФОРНИЯ";
		case 23:
			return "ДЖЕЙН ОСТИН";
		case 24:
			return "ПРОЕКТ Х";
		}
		StartAlert("Видео почему то не найденно. Попробуйте перезайти в игру");
		return "ВИДЕО НЕТУ";
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mediaPlayer.setDisplay(holder);//присваеваем нашему медиа плееру экран для трансляции
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
}
