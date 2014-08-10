package screen;

import palette.EditBlock;
import tools.Alert;
import tools.ObbTools;
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
import android.view.SurfaceView;
import android.widget.LinearLayout;

public class Game extends Activity implements OnPreparedListener,
 SurfaceHolder.Callback
{
	private MediaPlayer mediaPlayer;//медиа плеер для видео
	private EditBlock editBlock;//блок в буквами и ячейками
	private SharedPreferences settings;//настройки: уровни и монеты
	
	private boolean played = false;//true если видео играеться, false если нет
	private int lvl = 1;//номер уровня
	private int money = 0;//количество монет
	
	ObbTools obb;//класс для работы с файлами расширения
	Recall recall;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		obb = new ObbTools(this);//для работы с файлами расширения
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
		holder.addCallback(this);
		
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
				try{
				stop_film();
				} catch(Exception e){
					
				}
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
			if(mediaPlayer != null && mediaPlayer.getCurrentPosition() > 15000 && mediaPlayer.isPlaying()){
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
				if(lvl == 151){
					editBlock.MediaVictory();
					StartAlert("Конец игры. Спасибо вам. Вы хороший игрок.");
					return;//если достигнут конец
				}
				mediaPlayer.pause();//останавливаем видео
				editBlock.MediaCorrect();//воспроизводим звук привильного слова
				//показываем блок с продолжением
				Intent correct = new Intent(Game.this,Correct.class);
				//передаём информацию
				correct.putExtra("lvl", lvl+1);
				correct.putExtra("money", money+15);
				startActivityForResult(correct, 1);//говорим что ждём результат
				break;
			}
			if(editBlock.tips){
				editBlock.tips = false;
				Intent bonus = new Intent(Game.this,Bonus.class);
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
						Intent poster = new Intent(Game.this,Poster.class);
						poster.putExtra("lvl", lvl);
						startActivity(poster);
						money-=40;
					} else {
						StartAlert("Не хватает монет");
					}
				} else if(activ == 4){
					if(money >= 60){
						editBlock.MediaMoney();
						Intent contents = new Intent(Game.this,Contents.class);
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
		Intent alert = new Intent(Game.this,Alert.class);
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
		case 25:
			return "СОЛТ";
		case 26:
			return "ИГРА ПРЕСТОЛОВ";
		case 27:
			return "ГРАВИТАЦИЯ";
		case 28:
			return "САШАТАНЯ";
		case 29:
			return "БРИГАДА";
		case 30:
			return "УНИВЕР";
		case 31:
			return "ХАТИКО";
		case 32:
			return "ТРЕТИЙ ЛИШНИЙ";
		case 33:
			return "ГАДКИЙ Я";
		case 34:
			return "БРАТ";
		case 35:
			return "МАСКА";
		case 36:
			return "КОНСТАНТИН";
		case 37:
			return "ГАРРИ ПОТТЕР";
		case 38:
			return "ЗВЁЗДНЫЙ ПУТЬ";
		case 39:
			return "ВОРОНИНЫ";
		case 40:
			return "ДОКТОР ХАУС";
		case 41:
			return "ЛОЛ";
		case 42:
			return "ИНТЕРНЫ";
		case 43:
			return "ЗВЁЗДНЫЕ ВОЙНЫ";
		case 44:
			return "ГРИФФИНЫ";
		case 45:
			return "ВРЕМЯ";
		case 46:
			return "ДРУЗЬЯ";
		case 47:
			return "ГОЛЫЙ ПИСТОЛЕТ";
		case 48:
			return "ТРОН";
		case 49:
			return "ТРАНСФОРМЕРЫ";
		case 50:
			return "ХОДЯЧИЕ МЕРТВЕЦЫ";
		case 51:
			return "ЗВЁЗДНЫЕ ВРАТА";
		case 52:
			return "ТОМ И ДЖЕРРИ";
		case 53:
			return "ТАКСИ";
		case 54:
			return "ТЕРМИНАТОР";
		case 55:
			return "СИД И НЕНСИ";
		case 56:
			return "ОДИН ПЛЮС ОДИН";
		case 57:
			return "КОРОЛЬ ЛЕВ";
		case 58:
			return "ЖИВАЯ СТАЛЬ";
		case 59:
			return "ЖИВОТНОЕ";
		case 60:
			return "ПУНКТ НАЗНАЧЕНИЯ";
		case 61:
			return "ОЧЕНЬ ПЛОХАЯ УЧИЛКА";
		case 62:
			return "ХОЛОДНОЕ СЕРДЦЕ";
		case 63:
			return "ФИЗРУК";
		case 64:
			return "ОКОЛОФУТБОЛА";
		case 65:
			return "СЕКС ПО ДРУЖБЕ";
		case 66:
			return "ПИПЕЦ";
		case 67:
			return "ОТБРОСЫ";
		case 68:
			return "МРАЧНЫЕ ТЕНИ";
		case 69:
			return "ЧЕЛОВЕК ПАУК";
		case 70:
			return "ДИВИРГЕНТ";
		case 71:
			return "МЕТРО";
		case 72:
			return "СТРЕЛА";
		case 73:
			return "ЛЮДИ ИКС";
		case 74:
			return "БОЛЬШОЙ СТЕН";
		case 75:
			return "ДРЕВНИЕ";
		case 76:
			return "МАЙОР ПЕЙН";
		case 77:
			return "ОДНОКЛАССНИКИ";
		case 78:
			return "СОЦИАЛЬНАЯ СЕТЬ";
		case 79:
			return "МУМИЯ";
		case 80:
			return "В ПОИСКАХ НЕМО";
		case 81:
			return "МАЛЬЧИШНИК";
		case 82:
			return "НАЗАД В БУДУЩЕЕ";
		case 83:
			return "АНАТОМИЯ СТРАСТИ";
		case 84:
			return "ДИКТАТОР";
		case 85:
			return "ВЛАСТЕЛИН КОЛЕЦ";
		case 86:
			return "ВАСАБИ";
		case 87:
			return "ГОЛОДНЫЕ ИГРЫ";
		case 88:
			return "БОЙ С ТЕНЬЮ";
		case 89:
			return "МЕДАЛЬОН";
		case 90:
			return "ФОРЕСТ ГАМП";
		case 91:
			return "ЛЕОН";
		case 92:
			return "НЕУДЕРЖИМЫЕ";
		case 93:
			return "ПОГОНЯ";
		case 94:
			return "ИЛЛЮЗИЯ ОБМАНА";
		case 95:
			return "ВПРИТЫК";
		case 96:
			return "ЧЕЛОВЕК ИЗ СТАЛИ";
		case 97:
			return "ГЛАДИАТОР";
		case 98:
			return "АМЕРИКАНСКИЙ ПИРОГ";
		case 99:
			return "ДНЕВНИКИ ВАМПИРА";
		case 100:
			return "НАПРАЛОМ";
		case 101:
			return "ПЛАНЕТА СОКРОВИЩ";
		case 102:
			return "НИКОГДА НЕ СДАВАЙСЯ";
		case 103:
			return "ОСОБО ОПАСЕН";
		case 104:
			return "ТРОЯ";
		case 105:
			return "БОЛЬШЕ ЧЕМ СЕКС";
		case 106:
			return "ХЭНКОК";
		case 107:
			return "БЕЛЫЙ ВОРОТНИЧОК";
		case 108:
			return "БРЮС ВСЕМОГУЩИЙ";
		case 109:
			return "ЧТО ТВОРЯТ МУЖИЧНЫ";
		case 110:
			return "ПРИЗРАЧНЫЙ ГОНЩИК";
		case 111:
			return "КРЁСТНЫЙ ОТЕЦ";
		case 112:
			return "ФОРСАЖ";
		case 113:
			return "ЗЕЛЁНАЯ МИЛЯ";
		case 114:
			return "ПРИВЕДЕНИЕ";
		case 115:
			return "АНАТОМИЯ ЛЮБВИ";
		case 116:
			return "ВЫШИБАЛА";
		case 117:
			return "ОБЛАЧНЫЙ АТЛАС";
		case 118:
			return "НЕВЕСТА ЛЮБОЙ ЦЕНОЙ";
		case 119:
			return "ЧАС ПИК";
		case 120:
			return "БЕЗБРАЧНАЯ НЕДЕЛЯ";
		case 121:
			return "КРАСНАЯ ШАПОЧКА";
		case 122:
			return "МАЛЕФИСЕНТА";
		case 123:
			return "КУХНЯ В ПАРИЖЕ";
		case 124:
			return "БУНТАРКА";
		case 125:
			return "ВОЙНА НЕВЕСТ";
		case 126:
			return "УДАЧИ ЧАК";
		case 127:
			return "ПЛОХИЕ ПАРНИ";
		case 128:
			return "АНАСТАСИЯ";
		case 129:
			return "БЕЛАЯ КОРОЛЕВА";
		case 130:
			return "ВОЛК С УОЛЛ СТРИТ";
		case 131:
			return "ДНЕВНИК НИМФОМАНКИ";
		case 132:
			return "ДОМАШНАЯЯ РАБОТА";
		case 133:
			return "КРАСАВИЦА И ЧУДОВИЩЕ";
		case 134:
			return "РОКЕНРОЛЬЩИК";
		case 135:
			return "ХЛОЯ";
		case 136:
			return "ОРУДИЕ СМЕРТИ";
		case 137:
			return "ПОБЕГ ИЗ ШОУШЕНКА";
		case 138:
			return "ИСТОРИЯ ЗОЛУШКИ";
		case 139:
			return "ПОРТРЕТ ДОРИАНА ГРЕЯ";
		case 140:
			return "СТРАШНО КРАСИВ";
		case 141:
			return "СПЛЕТНИЦА";
		case 142:
			return "РОМЕО И ДЖУЛЬЕТТА";
		case 143:
			return "РАПУНЦЕЛЬ";
		case 144:
			return "НАСТОЯЩАЯ КРОВЬ";
		case 145:
			return "НАРКОЗ";
		case 146:
			return "МОСТ В ТЕРАБИТИЮ";
		case 147:
			return "РЕКВИЕМ ПО МЕЧТЕ";
		case 148:
			return "ЖИЗНЬ АДЕЛЬ";
		case 149:
			return "КОКАИН";
		case 150:
			return "ОДИН ДЕНЬ";
		case 151:
			return "ТЕЛО ДЖЕНИФЕР";
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
