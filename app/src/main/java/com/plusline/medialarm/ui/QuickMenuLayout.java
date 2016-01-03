package com.plusline.medialarm.ui;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.plusline.medialarm.R;

/**
 *
 *
 */
public class QuickMenuLayout {

    private final MainActivity mainActivity;
    private final View thisLayout;
    private View quickMenuList;
    private View background;

    //
    //  constructor
    //
    public QuickMenuLayout(MainActivity activity, View layout) {
        mainActivity = activity;
        thisLayout = layout;
        thisLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        thisLayout.setVisibility(View.GONE);

        background = thisLayout.findViewById(R.id.background);
        quickMenuList = thisLayout.findViewById(R.id.quick_menu_list);
        setMenuClickListeners();
    }


    public void show() {
        thisLayout.setVisibility(View.VISIBLE);
        quickMenuList.setVisibility(View.VISIBLE);

        Animation alphaAnim = new AlphaAnimation(0, 1);
        alphaAnim.setDuration(500);
        background.startAnimation(alphaAnim);

        Animation showAnim = AnimationUtils.loadAnimation(thisLayout.getContext(), R.anim.push_left_in);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainActivity.setQuickOptionButton(R.drawable.btn_before);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        quickMenuList.startAnimation(showAnim);
    }

    public void hide() {
        Animation hideAnim = AnimationUtils.loadAnimation(thisLayout.getContext(), R.anim.push_right_out);
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                thisLayout.setVisibility(View.GONE);
                mainActivity.setQuickOptionButton(R.drawable.btn_show_quick);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        quickMenuList.startAnimation(hideAnim);

        Animation alphaAnim = new AlphaAnimation(1, 0);
        alphaAnim.setDuration(500);
        background.startAnimation(alphaAnim);
    }


    public void toggle() {
        if(View.VISIBLE == thisLayout.getVisibility()) {
            hide();
        } else {
            show();
        }
    }


    private void setMenuClickListeners() {
        // 보고서
        thisLayout.findViewById(R.id.report_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showReport();
                hide();
            }
        });

        // 약물 추가
        thisLayout.findViewById(R.id.add_drug_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showSelectCartridgePage();
                hide();
            }
        });

        // 알람 수정
        thisLayout.findViewById(R.id.modify_alarm_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showSelectAlarmPage();
                //mainActivity.showSelectCartridgePage();
                hide();
            }
        });

        // 보호자 추가
        thisLayout.findViewById(R.id.add_protector_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

}
