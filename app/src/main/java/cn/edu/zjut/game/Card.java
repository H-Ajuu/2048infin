package cn.edu.zjut.game;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by zyw on 2020/6/13.
 */
public class Card extends FrameLayout {
    private int num = 0;
    private TextView txt;
    public Card (Context context){
        super(context);

        txt = new TextView(getContext());
        txt.setTextSize(30);
        txt.setBackgroundColor(Color.rgb(152, 245, 205));
        txt.setGravity(Gravity.CENTER);

        LayoutParams lp = new LayoutParams(-1, -1); //设置布局参数 填充满整个父级容器
        lp.setMargins(10, 10, 0, 0);    //使卡片间保持间距

        addView(txt, lp);

        setNum(0);    //无数字时默认设置为0
    }
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;

        if(num <= 0){
            txt.setText("");    //如果卡片中的数字是0则不显示
        }else {
            txt.setText(num + "");    //如果卡片中的数字不是0则显示
        }
    }
    public boolean equals(Card card) {  //判断两个卡片上的数字是否相同
        return this.getNum() == card.getNum();
    }

}
