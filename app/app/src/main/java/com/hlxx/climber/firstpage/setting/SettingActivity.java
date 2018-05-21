package com.hlxx.climber.firstpage.setting;


import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;

import com.hlxx.climber.R;
import com.hlxx.climber.transition.ChangeColor;
import com.hlxx.climber.transition.ChangePosition;
import com.hlxx.climber.transition.ShareElemEnterRevealTransition;
import com.hlxx.climber.transition.ShareElemReturnChangePosition;
import com.hlxx.climber.transition.ShareElemReturnRevealTransition;


public class SettingActivity extends AppCompatActivity {
    View frag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        frag=findViewById(R.id.frag);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }
    private void setTransition() {


        getWindow().setSharedElementEnterTransition(buildShareElemEnterSet());
        getWindow().setSharedElementReturnTransition(buildShareElemReturnSet());

    }
    private TransitionSet buildShareElemEnterSet() {
        TransitionSet transitionSet = new TransitionSet();

        Transition changePos = new ChangePosition();
        changePos.setDuration(300);
        changePos.addTarget(R.id.frag);
        transitionSet.addTransition(changePos);

        Transition revealTransition = new ShareElemEnterRevealTransition(frag);
        transitionSet.addTransition(revealTransition);
        revealTransition.addTarget(R.id.frag);
        revealTransition.setInterpolator(new FastOutSlowInInterpolator());
        revealTransition.setDuration(300);

        ChangeColor changeColor = new ChangeColor(getResources().getColor(R.color.primary_text), getResources().getColor(R.color.colorwhite));
        changeColor.addTarget(R.id.frag);
        changeColor.setDuration(350);

        transitionSet.addTransition(changeColor);

        transitionSet.setDuration(900);

        return transitionSet;
    }

    /**
     * 分享元素返回动画
     * @return
     */
    private TransitionSet buildShareElemReturnSet() {
        TransitionSet transitionSet = new TransitionSet();

        Transition changePos = new ShareElemReturnChangePosition();
        changePos.addTarget(R.id.frag);
        transitionSet.addTransition(changePos);

        ChangeColor changeColor = new ChangeColor(getResources().getColor(R.color.colorwhite), getResources().getColor(R.color.primary_text));
        changeColor.addTarget(R.id.frag);
        transitionSet.addTransition(changeColor);


        Transition revealTransition = new ShareElemReturnRevealTransition(frag);
        revealTransition.addTarget(R.id.frag);
        transitionSet.addTransition(revealTransition);

        transitionSet.setDuration(900);

        return transitionSet;
    }
}