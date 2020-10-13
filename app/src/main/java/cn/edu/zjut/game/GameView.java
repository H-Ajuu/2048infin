package cn.edu.zjut.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by zyw on 2020/6/13.
 */
public class GameView extends GridLayout {
    private Card[][] Cards = new Card[MainActivity.dimension][MainActivity.dimension];    //界面内卡片大小为4*4
    private List<Point> emptyPoints = new ArrayList<Point>();   //记录所有空卡片的位置
    private int cardLength;
    private int screenW, screenH;
    private float startX, startY, endX, endY;
    private SoundPool pool;
    private int soundId1;
    public static GameView GVinstance;


    public GameView(Context context) {  //构造函数
        super(context);
        setColumnCount(MainActivity.dimension);  //指明GridLayout布局是4列的
        setBackgroundColor(Color.rgb(122, 197, 205));
        pool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 100);
        soundId1 = pool.load(context, R.raw.ding, 1);

    }
    public GameView(Context context, AttributeSet attrs) {  //构造函数
        super(context, attrs);
        setColumnCount(MainActivity.dimension);
        setBackgroundColor(Color.rgb(122, 197, 205));
        pool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 100);
        soundId1 = pool.load(context, R.raw.ding, 1);
    }
    public GameView(Context context, AttributeSet attrs, int defStyle) {    //构造函数
        super(context, attrs, defStyle);
        setColumnCount(MainActivity.dimension);
        setBackgroundColor(Color.rgb(122, 197, 205));
        pool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 100);
        soundId1 = pool.load(context, R.raw.ding, 1);
    }

    public boolean onTouchEvent(MotionEvent event){ //触屏事件处理
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:   //按下时记录开始位置
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:     //松开时记录结束位置
                endX = event.getX() - startX;
                endY = event.getY() - startY;
                if (Math.abs(endX) > Math.abs(endY)) {  //判断滑动方向
                    if (endX < -5) {
                        slideLeft();
                    } else if (endX > 5) {
                        slideRight();
                    }
                } else {

                    if (endY < -5) {
                        slideUp();
                    } else if (endY > 5) {
                        slideDown();
                    }
                }
                pool.play(soundId1, 1, 1, 0, 0, 1); //对滑动操作作出反馈
                break;
        }
        return true;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenW = w;
        screenH = h;
        cardLength = (Math.min(screenW, screenH) - 10) / MainActivity.dimension; //计算卡片边长
        //addCards(cardLength);
        startGame();//开始游戏
        GVinstance=this;
    }

    private void addCards(int cardLength) { //新建空白卡片
        Card newCard;
        for (int y = 0; y < MainActivity.dimension; y++) {
            for (int x = 0; x < MainActivity.dimension; x++) {
                newCard = new Card(getContext());
                addView(newCard, cardLength, cardLength);
                Cards[x][y] = newCard;
            }
        }
    }

    public void startGame() {   //开始游戏
        MainActivity.Minstance.clearScore();//刚开始分数清零
        MainActivity.dimension = MainActivity.nextDimension;    //更新矩阵维度
        this.removeAllViews();      //清除布局中原来的控件
        setColumnCount(MainActivity.dimension);     //重新设置布局列数
        cardLength = (Math.min(screenW, screenH) - 10) / MainActivity.dimension; //重新计算卡片边长
        addCards(cardLength);
        for (int y = 0; y < MainActivity.dimension; y++) {   //清除所有卡片上的数字
            for (int x = 0; x < MainActivity.dimension; x++) {
                Cards[x][y].setNum(0);
            }
        }
        addRandomNum(); //添加两个随机数
        addRandomNum();
    }

    private void addRandomNum() {   //添加随机数

        emptyPoints.clear();    //清空emptyPoints

        for (int y = 0; y < MainActivity.dimension; y++) {
            for (int x = 0; x < MainActivity.dimension; x++) {
                if (Cards[x][y].getNum() <= 0) {
                    emptyPoints.add(new Point(x, y));   //把空卡片的位置添加进去
                }
            }
        }
        if(!emptyPoints.isEmpty()) {
            Point p = emptyPoints.remove((int) (Math.random() * emptyPoints.size()));   //随机移除一个空卡片
            Cards[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);    //给这个空卡片添加一个数，2或4，概率为9：1
        }
    }

    private void slideRight() { //右滑判断
        for (int y = 0; y < MainActivity.dimension; y++) {
            int last = -1,curr = -1;
            for (int x = 0; x < MainActivity.dimension; x++) {
                if(Cards[x][y].getNum() == 0) continue;   //跳过空卡片
                curr = Cards[x][y].getNum();  //记录当前卡片数字
                if(curr != last){ //如果相邻两个卡片数字不同，记录旧卡片数字
                    last = curr;
                    curr = -1;
                }
                else {  //如果相邻两个卡片数字相同
                    MainActivity.Minstance.addScore(curr);  //更新分数
                    for(int x1 = 0;x1 < MainActivity.dimension; x1++){    //按滑动方向合并数字
                        if(Cards[x1][y].getNum() == curr){
                            Cards[x1][y].setNum(0);
                            Cards[x][y].setNum(curr * 2);
                        }
                    }
                    last = -1;
                    curr = -1;
                }
            }
            Stack<Integer> stk = new Stack<Integer>();  //按滑动方向使数字靠拢
            for (int x = 0; x < MainActivity.dimension; x++){
                if(Cards[x][y].getNum() != 0){
                    stk.push(Cards[x][y].getNum());
                }
            }
            for (int x = MainActivity.dimension - 1; x >= 0; x--){
                if(stk.empty()){
                    Cards[x][y].setNum(0);
                }
                else {
                    Cards[x][y].setNum(stk.pop());
                }
            }
        }
        addRandomNum(); //添加一个新的数字
        checkEnd(); //游戏结束检查
    }
    private void slideLeft() { //左滑判断
        for (int y = 0; y < MainActivity.dimension; y++) {
            int last = -1,curr = -1;
            for (int x = MainActivity.dimension - 1; x >= 0; x--) {
                if(Cards[x][y].getNum() == 0) continue;
                curr = Cards[x][y].getNum();
                if(curr != last){
                    last = curr;
                    curr = -1;
                }
                else {
                    MainActivity.Minstance.addScore(curr);
                    for(int x1 = MainActivity.dimension-1; x1 >= 0; x1--){
                        if(Cards[x1][y].getNum() == curr){
                            Cards[x1][y].setNum(0);
                            Cards[x][y].setNum(curr * 2);
                        }
                    }
                    last = -1;
                    curr = -1;
                }
            }
            Stack<Integer> stk = new Stack<Integer>();
            for (int x = MainActivity.dimension-1; x >= 0; x--){
                if(Cards[x][y].getNum() != 0){
                    stk.push(Cards[x][y].getNum());
                }
            }
            for (int x = 0; x < MainActivity.dimension; x++){
                if(stk.empty()){
                    Cards[x][y].setNum(0);
                }
                else {
                    Cards[x][y].setNum(stk.pop());
                }
            }
        }
        addRandomNum();
        checkEnd();
    }
    private void slideDown() { //下滑判断
        for (int x = 0; x < MainActivity.dimension; x++) {
            int last = -1,curr = -1;
            for (int y = 0; y < MainActivity.dimension; y++) {
                if(Cards[x][y].getNum() == 0) continue;
                curr = Cards[x][y].getNum();
                if(curr != last){
                    last = curr;
                    curr = -1;
                }
                else {
                    MainActivity.Minstance.addScore(curr);
                    for(int y1 = 0; y1 < MainActivity.dimension; y1++){
                        if(Cards[x][y1].getNum() == curr){
                            Cards[x][y1].setNum(0);
                            Cards[x][y].setNum(curr * 2);
                        }
                    }
                    last = -1;
                    curr = -1;
                }
            }
            Stack<Integer> stk = new Stack<Integer>();
            for (int y = 0; y < MainActivity.dimension; y++){
                if(Cards[x][y].getNum() != 0){
                    stk.push(Cards[x][y].getNum());
                }
            }
            for (int y = MainActivity.dimension - 1; y >= 0; y--){
                if(stk.empty()){
                    Cards[x][y].setNum(0);
                }
                else {
                    Cards[x][y].setNum(stk.pop());
                }
            }
        }
        addRandomNum();
        checkEnd();
    }
    private void slideUp() { //上滑判断
        for (int x = 0; x < MainActivity.dimension; x++) {
            int last = -1,curr = -1;
            for (int y = MainActivity.dimension - 1; y >= 0; y--) {
                if(Cards[x][y].getNum() == 0) continue;
                curr = Cards[x][y].getNum();
                if(curr != last){
                    last = curr;
                    curr = -1;
                }
                else {
                    MainActivity.Minstance.addScore(curr);
                    for(int y1 = MainActivity.dimension - 1; y1 >= 0; y1--){
                        if(Cards[x][y1].getNum() == curr){
                            Cards[x][y1].setNum(0);
                            Cards[x][y].setNum(curr * 2);
                        }
                    }
                    last = -1;
                    curr = -1;
                }
            }
            Stack<Integer> stk = new Stack<Integer>();
            for (int y = MainActivity.dimension - 1; y >= 0; y--){
                if(Cards[x][y].getNum() != 0){
                    stk.push(Cards[x][y].getNum());
                }
            }
            for (int y = 0; y < MainActivity.dimension; y++){
                if(stk.empty()){
                    Cards[x][y].setNum(0);
                }
                else {
                    Cards[x][y].setNum(stk.pop());
                }
            }
        }
        addRandomNum();
        checkEnd();
    }

    private void checkEnd() {   //检查游戏是否结束
        boolean end = true;

        All: for (int y = 0; y < MainActivity.dimension; y++) {
            for (int x = 0; x < MainActivity.dimension; x++) {

                // 游戏没有结束的判定情况 5种情况：还有空卡片，某一卡片上下左右某一方向仍然可以进行合并
                if (Cards[x][y].getNum() == 0 || (x > 0 && Cards[x][y].equals(Cards[x - 1][y])) || (x < MainActivity.dimension - 1 && Cards[x][y].equals(Cards[x + 1][y])) || (y > 0 && Cards[x][y].equals(Cards[x][y - 1])) || (y < MainActivity.dimension - 1 && Cards[x][y].equals(Cards[x][y + 1]))) {
                    end = false;
                    break All;
                }
            }
        }

        if (end) {
            MainActivity.Minstance.setHighscore();
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("提示").setMessage("游戏结束！").setPositiveButton("重新开始", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog,
                                    int which) {
                    startGame();
                }
            });
            dialog.setNegativeButton("关闭程序", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.Minstance.finish();
                }
            });
            dialog.show();
        }
    }
}
