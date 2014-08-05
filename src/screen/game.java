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
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.LinearLayout;

public class Game extends Activity implements OnPreparedListener,
 SurfaceHolder.Callback
{
	private MediaPlayer mediaPlayer;//����� ����� ��� �����
	private EditBlock editBlock;//���� � ������� � ��������
	private SharedPreferences settings;//���������: ������ � ������
	
	private boolean played = false;//true ���� ����� ���������, false ���� ���
	private int lvl = 1;//����� ������
	private int money = 0;//���������� �����
	
	ObbTools obb;//����� ��� ������ � ������� ����������
	Recall recall;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		obb = new ObbTools(this);//��� ������ � ������� ����������
		recall = new Recall(this);// ������ ������� ����������
		
		//��������� ���������
		settings = getSharedPreferences("MovieByPassage", 0);
		lvl = settings.getInt("lvl", 1);
		money = settings.getInt("money", 0);
		//������ ������ �����
		editBlock = new EditBlock(this, WordName(lvl), 0);//������ ��������
		editBlock.UpdateInfo(lvl, money);//��������� ���������� ����������
		
		((LinearLayout)findViewById(R.id.edit_block)).addView(editBlock);//��������� ���� � ������
		
		//������ ���� ��������� �����
		SurfaceHolder holder = ((SurfaceView)findViewById(R.id.surfaceView1)).getHolder();
		holder.addCallback((Callback) this);
		
		mediaPlayer = new MediaPlayer();//������ ������ ����� ������
		//------------��������� �����---------------------
		load_film();//��������� �����
		
		correctWordStart();//��������� ����� ��� �������� ������������ ��������� ����� 
		
		mediaPlayer.setOnPreparedListener(this);//����������. ���������� ����� ����� ����� ������
	}
	
	@Override
	public void onDestroy(){
		editBlock.onDestroy();
		obb.onDestroy();
		mediaPlayer.release();
		super.onDestroy();
	}

	private void correctWordStart() {
		//�������� �������� ������������ �����
		new Thread(new Runnable() {
			@Override
			public void run() {
				RightWord();
			}
		}).start();
	}
	
	//�������� ������
	private void load_film() {
		
		if(settings.getInt("recall", 0) == 0){
			if(((int)(Math.random()*5)) == 2){
				recall.show();
			}
			
		}
		
		
		mediaPlayer.reset();//������� ������ ������
		try {
			AssetFileDescriptor afd = obb.getAssetFileDescriptor(lvl+".3gp");
			mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	        mediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//������� ������ ������������ ��� �������������
	public void ReplayMovie(){
        if(!mediaPlayer.isPlaying()){mediaPlayer.start();}//���� ����� �� ������ ��������
        mediaPlayer.seekTo(0);//������������ � ������
        played = true;//�������� � ��� ��� ����� ���������
        //����� ��� ��������� � ������ ������
        new Thread(new Runnable() {
			@Override
			public void run() {
				stop_film();
			}
		}).start();        
	}
	
	//��� ������ ����� ���������� ��������
	@Override
	public void onPrepared(MediaPlayer mp) {
		ReplayMovie();
	}
	
	//������������� ������������ ������ �� ������ �������
	private void stop_film() {
		while(played){//���� ����� �� ��������� �� �� ��� �����
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//������������� ����� ���� ��� ��������� ������ 15 ������ 
			if(mediaPlayer.getCurrentPosition() > 15000 && mediaPlayer.isPlaying()){
				if(mediaPlayer != null)mediaPlayer.pause();//������������� �����
				played = false;//����� �� ���������
			} else if(!mediaPlayer.isPlaying()){//���� ����� �� ���������
				played = false;//��������� ������
			}
		}
	}

	//������� ����� �� ������ � �����
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		ReplayMovie();
		return super.onTouchEvent(event);
	}
	
	//������� ����������� �����
	private void RightWord(){
		while(true){
			if(editBlock.WordCorrect()){
				if(lvl == 24)return;//���� ��������� �����
				mediaPlayer.pause();//������������� �����
				editBlock.MediaCorrect();//������������� ���� ����������� �����
				//���������� ���� � ������������
				Intent correct = new Intent(Game.this, Correct.class);
				//������� ����������
				correct.putExtra("lvl", lvl+1);
				correct.putExtra("money", money+15);
				startActivityForResult(correct, 1);//������� ��� ��� ���������
				break;
			}
			if(editBlock.tips){
				editBlock.tips = false;
				Intent bonus = new Intent(Game.this, Bonus.class);
				startActivityForResult(bonus, 2);//������� ��� ��� ���������
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//������� ���������� �� ����
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Editor editor;
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				lvl +=1;
				money+=15;
				load_film();//��������� ����� �����
				//��������� ���� � ������� � �����������
				editBlock.UpdateWord(WordName(lvl), 0);
				editBlock.UpdateInfo(lvl, money);
				editBlock.invalidate();
				//��������� ���������
				editor = settings.edit();
				editor.putInt("lvl", lvl);
				editor.putInt("money", money);
				editor.commit();//������
				correctWordStart();//�������� ����� ����� �������� ������������ �����
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
						StartAlert("�� ������� �����");
					}
				} else if(activ == 2){
					if(money >= 15){
						editBlock.MediaMoney();
						editBlock.OpenLetter();
						money-=15;
					} else {
						StartAlert("�� ������� �����");
					}
				} else if(activ == 3){
					if(money >= 40){
						editBlock.MediaMoney();
						Intent poster = new Intent(Game.this, Poster.class);
						poster.putExtra("lvl", lvl);
						startActivity(poster);
						money-=40;
					} else {
						StartAlert("�� ������� �����");
					}
				} else if(activ == 4){
					if(money >= 60){
						editBlock.MediaMoney();
						Intent contents = new Intent(Game.this,Contents.class);
						contents.putExtra("lvl", lvl);
						startActivity(contents);
						money-=60;
					} else {
						StartAlert("�� ������� �����");
					}
				}
				
				editor = settings.edit();
				editor.putInt("money", money);
				editor.commit();//������
				
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
	
	//�������� ������� �� �������
	private String WordName(int lvl){
		switch (lvl) {
		case 1:
			return "���� ����";
		case 2:
			return "�������";
		case 3:
			return "�����������������";
		case 4:
			return "��������";
		case 5:
			return "������";
		case 6:
			return "�� ��� ������";
		case 7:
			return "�������� �";
		case 8:
			return "�������";
		case 9:
			return "���� ����";
		case 10:
			return "����� �������";
		case 11:
			return "���� � ר����";
		case 12:
			return "����� �����";
		case 13:
			return "�������";
		case 14:
			return "��������� �������";
		case 15:
			return "���� � �����";
		case 16:
			return "������ ���";
		case 17:
			return "�����";
		case 18:
			return "������ �����";
		case 19:
			return "�������";
		case 20:
			return "��������";
		case 21:
			return "���������� ����";
		case 22:
			return "��������� ����������";
		case 23:
			return "����� �����";
		case 24:
			return "������ �";
		}
		StartAlert("����� ������ �� �� ��������. ���������� ��������� � ����");
		return "����� ����";
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mediaPlayer.setDisplay(holder);//����������� ������ ����� ������ ����� ��� ����������
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
}
