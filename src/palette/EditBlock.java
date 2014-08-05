package palette;

import java.util.Random;

import com.moviebypassage.R;

import tools.Pixmap;
import tools.PixmapGame;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import static screen.Main.images;


public class EditBlock extends View implements OnLoadCompleteListener {
	
	String word;
	Paint paint = new Paint();
	Matrix matrix = new Matrix();
	char[] letters = new char[20];//������ ����
	int[] discarded_letters = new int[20];//����������� �����
	int[] open_letters;//������ ����������� �����
	int[] cells;//������ ����������� �����
	char[] cells_final;//������ �����
	int lvl = 1;
	int money = 0;
	
	public boolean tips = false;
	
	SoundPool sp;
	final int MAX_STREAMS = 4;
	int soundIdClick;
	int soundIdCorrect;
	int soundIdError;
	int soundIdVictory;
	int soundIdMoney;
	
	//������������������ ����
	char ru_letters[] = {'�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�',
			'�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�', '�'};
	char en_letters[] = {'A', 'B', 'C', 'D'};
	
	final Random gen = new Random();
	
	Context context;
	
	public EditBlock(Context context) {
		super(context);
	}
	//�����������
	public EditBlock(Context context, String word, int language) {
		super(context);
		this.word = word;
		this.context = context;
		
		sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
		sp.setOnLoadCompleteListener(this);
		
		soundIdClick = sp.load(context, R.raw.click, 1);;
		soundIdCorrect = sp.load(context, R.raw.correct, 1);;
		soundIdError = sp.load(context, R.raw.error, 1);;
		soundIdVictory = sp.load(context, R.raw.victory, 1);;
		soundIdMoney = sp.load(context, R.raw.money, 1);;
				
		UpdateWord(word, language);
		
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        invalidate();
	}
	
	public void UpdateInfo(int lvl, int money){
		this.lvl = lvl;
		this.money = money;
	}
	
	public void UpdateWord(String word, int language){
		//���������� ������� ��� ���������� �� �������
		cells = new int[word.length()];
		open_letters = new int[word.length()];
		cells_final = new char[word.length()];
		cells_final = word.toCharArray();
		
		//��������� �������
		for(int i=0; i < 20; i++){
			if(i >= cells_final.length || cells_final[i] == ' '){
				//���� ����� ������ 20 �� ��������� ����� ���������
				if(language == 0){//������� ����
					letters[i] = ru_letters[gen.nextInt(ru_letters.length)];
				} else
				if(language == 1){//���������� ����
					letters[i] = en_letters[gen.nextInt(en_letters.length)];
				}
			} else {
				//��������� �����
				letters[i] = cells_final[i];
			}
		}
		//��������� ������������ ����
	    int n = letters.length;
	    while (n > 1) {
	        int k = gen.nextInt(n--);
	        char temp = letters[n];
	        letters[n] = letters[k];
	        letters[k] = temp;
	    }
	    
	    //��������� ������ ����� �������������� �������
	    for(int j = 0; j < cells.length; j++){
			cells[j] = -1;
		}
	    
	    //��������� ������ ��������� ���� �������������� �������
	    for(int i = 0; i < discarded_letters.length; i++){
	    	discarded_letters[i] = -1;
		}
	    
	    for(int i = 0; i < open_letters.length; i++){
	    	open_letters[i] = -1;
		}
	}
	
	//��������� �����
	public boolean OpenLetter(){
		
		for(int i = 0; i < cells_final.length; i++){
			int n = gen.nextInt(cells_final.length);
			if(cells_final[n] == ' ')continue;
			if(open_letters[n] != -1)continue;
			if(cells[n] == -1){
				open_letters[n] = getLetter(cells_final[n]);
				return true;
			}
		}
		return false;
	}
	
	private int getLetter(char lett){
		for(int i = 0; i < letters.length; i++){
			if(letters[i] == lett){
				del_cells(i);//������� �� ������ ��� �����
				return i;
			}
		}
		return -1;
	}
	
	//������� �����
	public boolean RemoveLetter(){
		if(kol_discarded_letters() == (20 - cells_final.length)){
			return false;//�� ����� ������ ��������� �����
		}
		
		for(int i = 0; i < letters.length; i++){
			int n = gen.nextInt(letters.length);
			if(!presence_letter(letters[n])){
				if(letters[n] == ' ')continue;
				del_cells(n);//������� �� ������
				letters[n] = ' ';
				return true;
			}
		}
		
		for(int i = 0; i < letters.length; i++){
			if(!presence_letter(letters[i])){
				if(letters[i] == ' ')continue;
				del_cells(i);//������� �� ������
				letters[i] = ' ';
				return true;
			}
		}
		return false;//��������� ����� ����� �� �������
	}
	
	//���������� ���� �� ����� ����� � �����
	private boolean presence_letter(char lett){
		for(int i = 0; i < cells_final.length; i++){
			if(cells_final[i] == lett){
				return true;
			}
		}
		return false;
	}
	
	//������������ ���������� ��������� ����
	private int kol_discarded_letters(){
		int n = 0;
		for(int i = 0; i < discarded_letters.length; i++){
	    	if(discarded_letters[i] != -1){
	    		n++;
	    	}
		}
		return n;
	}
	
	private void del_cells(int i) {
		for(int j = 0; j < cells.length; j++){
			if(cells[j] == i){
				cells[j] = -1;
			}
		}
	}	
	
	@Override 
    public boolean onTouchEvent(MotionEvent event){
		int Action=event.getAction();
		if(Action == 1){
			int w = this.getWidth();
			int h = this.getHeight();
			
			float x_click = event.getX();
			float y_click = event.getY();
			
			float y = (float)(h*0.365);
			float y2 = (float)(y+(w*0.07)*2);
			float y3 = (float)(h*0.084);
			
			if(y_click > y && y_click < y2){//����� ������� �� �����
				//���������� id ��������� �����
				int ID = onClickLetters(w, h, x_click, y_click);
				if(ID==-1)return true;//���� �� ������� ����� ����� �� ������ �� ����������
				if(cells_nul(ID))return true;
				//��������� ������ ������ ��������� ������
				for(int i=0; i < cells.length; i++){
					if(cells_final[i] == ' ') continue;
					if(cells[i] == -1 && open_letters[i] == -1){
						cells[i] = ID;
						MediaClick();//������������� ����
						if(cells_final.length-1 == i && !WordCorrect()){
							MediaError();
						}
						invalidate();
						break;
					}
				}
			}else if(y_click > y3){//����� ������� �� �������
				
				float a = (float)(w/cells_final.length);
				if(a > ((h*0.18))) a = (float)((h*0.18));
				float center = w/2 - a * cells_final.length / 2;
				
				for(int i=0; i < cells_final.length; i++){
					if(cells_final[i] == ' ') continue;
					if(inBounds(x_click, y_click, (float)(center + a * i),y3, a, a)){
						if(cells[i] == -1)break;
						cells[i] = -1;
						MediaClick();//������������� ����
						invalidate();
						break;
					}
				}
			}
			
			if(x_click > (float)(w * 0.895) && y_click > (float)(h * 0.805) ){
				tips = true;
			}
		}
		return true;
		
	}
	private int onClickLetters(int w, int h, float x_click, float y_click) {
		int ID = -1;
		float a = (float)(w*0.07);
		float y = (float)(h*0.365);
		//������ ���
		for(int i=0; i < 10; i++){
			if(inBounds(x_click, y_click, (float)((w*0.105) + a * i + w*0.01 * i), y, a, a)){
				ID = i;
			}
		}
		//������ ���
		for(int i=0; i < 10; i++){
			if(inBounds(x_click, y_click, (float)((w*0.105) + a * i + w*0.01 * i), (float)(y + a + h*0.02), a, a)){
				ID = 10+i;
			}
		}
		return ID;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int w = this.getWidth();
		int h = this.getHeight();
		
		//���
		canvas.drawColor(Color.alpha(0));
		
		//�����
		create_letters(canvas, w, h);
		
		//������
		create_cells(canvas, w, h);
		
		//���
		create_bar(canvas, w, h);
		
		super.onDraw(canvas);
	}
	
	private void create_bar(Canvas canvas, int w, int h) {
		float height = (float)(h*0.2);
		float x_text = (float)(w*0.2);
		float x_text2 = (float)(w*0.6);
		float y_text = (float)(h-height*0.5);
		float x_money = (float)(w*0.54);
		float y_money = (float)(h*0.87);
		
		float size_text = (float)(height*0.33);
		drawPixmap(canvas, images.getImage("bar"),
				0,
				h-height,
				images.getImage("bar").getWidth(),
				images.getImage("bar").getHeight(),
				w,height, 0);
		
		paint.setTextSize(size_text);
		canvas.drawText(lvl + " �������", x_text, (float)(y_text+(size_text*0.5)), paint);
		canvas.drawText(money + "", x_text2, (float)(y_text+(size_text*0.5)), paint);
		
		
		
		drawPixmap(canvas, images.getImage("money"),
				x_money,
				y_money,
				images.getImage("money").getWidth(),
				images.getImage("money").getHeight(),
				(float)(w*0.05),(float)(h*0.095), 0);
	}
	//�������� ������������ �����
	public boolean WordCorrect(){
		boolean correct = true;
		try{
		for(int i=0; i < cells_final.length; i++){
			if(cells_final[i] == ' ') continue;
			if(cells[i] == -1 && open_letters[i] == -1){
				correct = false;
				break;
			}
			
			int n = cells[i];
			if(cells[i] == -1) n = open_letters[i];
			
			if((int)cells_final[i] != (int)letters[n])
			{
				correct = false;
				break;
			}
		}
		} catch(Exception e){
			Log.d("ERROR:WordCorrect", e.toString());
			return false;
		}
		return correct;
	}
	
	
	private void create_letters(Canvas canvas, int w, int h) {
		float y = (float)(h*0.365);
		float a = (float)(w*0.07);
		
		for(int i=0; i < 10; i++){
			if(cells_nul(i))continue;
			drawPixmap(canvas, getImage(letters[i]),
					(float)((w*0.105) + a * i + w*0.01 * i),
					y,
					images.getImage("ru_a").getWidth(),
					images.getImage("ru_a").getHeight(),
					a,a, 0);
		}
		y = (float)(y + a + h*0.02);
		for(int i=0; i < 10; i++){
			if(cells_nul(10+i))continue;
			drawPixmap(canvas, getImage(letters[i+10]),
					(float)((w*0.105) + a * i + w*0.01 * i),
					y,
					images.getImage("ru_a").getWidth(),
					images.getImage("ru_a").getHeight(),
					a,a, 0);
		}
	}
	
	//��������� ���������
	private boolean cells_nul(int i) {
		for(int j = 0; j < cells.length; j++){
			if(cells[j] == i){
				return true;
			}
		}
		for(int j = 0; j < open_letters.length; j++){
			if(open_letters[j] == i){
				return true;
			}
		}
		
		if(letters[i] == ' '){
			return true;
		}
		
		return false;
	}	
	
	private void create_cells(Canvas canvas, int w, int h) {
		float y = (float)(h*0.084);
		float a = (float)(w/cells_final.length);
		if(a > ((h*0.18))) a = (float)((h*0.18));
		
		float center = w/2 - a * cells_final.length / 2;
		
		for(int i=0; i < cells_final.length; i++){
			if(cells_final[i] == ' ') continue;
			
			drawPixmap(canvas, images.getImage("cells"),
					(float)(center + a * i),y,
					images.getImage("cells").getWidth(),
					images.getImage("cells").getHeight(),
					a,a, 0);
			
			if(cells[i] != -1){
				drawPixmap(canvas, getImage(letters[cells[i]]),
						(float)(center + a * i),y,
						images.getImage("ru_a").getWidth(),
						images.getImage("ru_a").getHeight(),
						a,a, 0);
			}
			
			if(open_letters[i] != -1){
				drawPixmap(canvas, getImage(letters[open_letters[i]]),
						(float)(center + a * i),y,
						images.getImage("ru_a").getWidth(),
						images.getImage("ru_a").getHeight(),
						a,a, 0);
			}
		}
	}
	
	private Pixmap getImage(char lettters){
		switch (lettters) {
		case '�':
			return images.getImage("ru_a");
		case '�':
			return images.getImage("ru_b");
		case '�':
			return images.getImage("ru_v");
		case '�':
			return images.getImage("ru_g");
		case '�':
			return images.getImage("ru_d");
		case '�':
			return images.getImage("ru_ye");
		case '�':
			return images.getImage("ru_ye2");
		case '�':
			return images.getImage("ru_zh");
		case '�':
			return images.getImage("ru_z");
		case '�':
			return images.getImage("ru_i");
		case '�':
			return images.getImage("ru_y");
		case '�':
			return images.getImage("ru_k");
		case '�':
			return images.getImage("ru_l");
		case '�':
			return images.getImage("ru_m");
		case '�':
			return images.getImage("ru_n");
		case '�':
			return images.getImage("ru_o");
		case '�':
			return images.getImage("ru_p");
		case '�':
			return images.getImage("ru_r");
		case '�':
			return images.getImage("ru_s");
		case '�':
			return images.getImage("ru_t");
		case '�':
			return images.getImage("ru_u");
		case '�':
			return images.getImage("ru_f");
		case '�':
			return images.getImage("ru_kh");
		case '�':
			return images.getImage("ru_ts");
		case '�':
			return images.getImage("ru_ch");
		case '�':
			return images.getImage("ru_sh");
		case '�':
			return images.getImage("ru_shch");
		case '�':
			return images.getImage("ru_tv");
		case '�':
			return images.getImage("ru_y2");
		case '�':
			return images.getImage("ru_tv2");
		case '�':
			return images.getImage("ru_e");
		case '�':
			return images.getImage("ru_yu");
		case '�':
			return images.getImage("ru_ya");
			
		}
		return images.getImage("ru_a");
		
	}
	
	//������ � ���������
	public void drawPixmap(Canvas canvas, Pixmap pixmap, float x, float y,
			float Width, float Height, float newWidth, float newHeight, float rotate) {
		
		float scaleX = (float) newWidth / (float) Width;
	    float scaleY = (float) newHeight / (float) Height;

	    matrix.reset();
	    
		matrix.postScale(scaleX, scaleY);//������������
		matrix.postTranslate(x, y);//����� ����������
		
		if(rotate != 0)matrix.preRotate(rotate, Width/2, Height/2);//������������ ������������ �����
		
        canvas.drawBitmap( ((PixmapGame) pixmap).getBitmap(), matrix, null);
    }
	
	private boolean inBounds(float TouchX, float TouchY, float x, float y, float width, float height) {
        if(TouchX > x && TouchX < x + width - 1 && TouchY > y && TouchY < y + height - 1)
            return true;
        else
            return false;
    }
	
	public void MediaError(){
		sp.play(soundIdError, 1, 1, 0, 0, 1);
	}
	
	public void MediaCorrect(){
		sp.play(soundIdCorrect, 1, 1, 0, 0, 1);
	}
	
	public void MediaVictory(){
		sp.play(soundIdVictory, 1, 1, 0, 0, 1);
	}
	
	public void MediaClick(){
		sp.play(soundIdClick, 1, 1, 0, 0, 1);
	}
	
	public void MediaMoney(){
		sp.play(soundIdMoney, 1, 1, 0, 0, 1);
	}
	
	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {		
	}
	
	public void onDestroy(){
		sp.release();
	}


}
