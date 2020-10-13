package cn.edu.zjut.game;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    public static MainActivity Minstance;
    private TextView scoretv, highscoretv, dimensiontv;
    private Button restartbtn, decreacebtn, increasebtn;
    private int score = 0,highscore = 0;
    private SharedPreferences sp;
    private static final String PREFERENCE_NAME = "highscore_data";
    public static int dimension = 6;  //卡片矩阵维度，当前是6*6
    public static int nextDimension = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoretv = (TextView) findViewById(R.id.scoreText);
        highscoretv = (TextView) findViewById(R.id.highScoreText);
        sp = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        highscore = sp.getInt("highscore", 0);
        highscoretv.setText(highscore + "");
        dimensiontv = (TextView) findViewById(R.id.dimensionText);
        dimensiontv.setText(nextDimension + "");
        Minstance = this;

        restartbtn = (Button) findViewById(R.id.restart);
        View.OnClickListener listener1 = new View.OnClickListener() {
            public void onClick(View v) {   //点击按钮重新开始
                GameView.GVinstance.startGame();
            }
        };
        restartbtn.setOnClickListener(listener1);

        decreacebtn = (Button) findViewById(R.id.decrease);
        View.OnClickListener listener2 = new View.OnClickListener() {
            public void onClick(View v) {   //点击按钮减少维度
                nextDimension = Integer.parseInt(dimensiontv.getText().toString()) - 1;
                if(nextDimension < 2) nextDimension = 2;
                dimensiontv.setText(nextDimension + "");
            }
        };
        decreacebtn.setOnClickListener(listener2);

        increasebtn = (Button) findViewById(R.id.increase);
        View.OnClickListener listener3 = new View.OnClickListener() {
            public void onClick(View v) {   //点击按钮增加维度
                nextDimension = Integer.parseInt(dimensiontv.getText().toString()) + 1;
                if(nextDimension > 6) nextDimension = 6;
                dimensiontv.setText(nextDimension + "");
            }
        };
        increasebtn.setOnClickListener(listener3);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void clearScore() {  //清除当前分数
        score = 0;
        showScore();
    }

    public void addScore(int s){    //更新当前分数
        score += s;
        showScore();
    }

    public void showScore(){    //展示当前分数
        scoretv.setText(score+"");
    }

    public void setHighscore(){ //设置历史最高分
        if(score > highscore){
            highscore = score;
            highscoretv.setText(highscore + "");
            SharedPreferences.Editor editor = sp.edit();    //存储历史最高分
            editor.putInt("highscore", highscore);
        }
    }
}
