package com.plusline.medialarm.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.MainOption;
import com.plusline.medialarm.management.Profile;

/**
 * Main Sliding Menu Layout
 *
 */
public class MainSlidingMenu {

    private final Context context;
    private final View thisLayout;
    private final DrawerLayout drawerLayout;

    private MainActivity mainActivity;
    private ImageView profileImageView;
    private TextView nameText;

    //
    //  constructor
    //
    public MainSlidingMenu(View layout, DrawerLayout drawerLayout) {
        this.context = layout.getContext();
        this.thisLayout = layout;
        this.drawerLayout = drawerLayout;

        profileImageView = (ImageView) thisLayout.findViewById(R.id.profile_image);
        nameText = (TextView) thisLayout.findViewById(R.id.name_text);

        setOnClickListeners();
        updateOptionValues();
        updateProfile();
    }

    public void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    private void showActivity(Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
        drawerLayout.closeDrawer(thisLayout);
    }


    private void setOnClickListeners() {
        thisLayout.findViewById(R.id.modify_profile_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(ProfileActivity.class);
            }
        });

        // 보호자추가
        thisLayout.findViewById(R.id.add_protector_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(thisLayout);
            }
        });

        // 약 보관함
        thisLayout.findViewById(R.id.drug_case_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(DrugStorageActivity.class);
            }
        });

        // 단순모드
        thisLayout.findViewById(R.id.simple_mode_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSimpleMode = MainOption.get().isSimpleMode();
                MainOption.get().setSimpleMode(!isSimpleMode);
                updateOptionValues();
            }
        });

        // 일반설정
        thisLayout.findViewById(R.id.preference_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(PreferenceActivity.class);
            }
        });
    }


    public void updateOptionValues() {
        TextView simpleModeText = (TextView) thisLayout.findViewById(R.id.simple_mode_text);
        if(MainOption.get().isSimpleMode()) {
            simpleModeText.setText("on");
            simpleModeText.setTextColor(0xff7db343);
        } else {
            simpleModeText.setText("off");
            simpleModeText.setTextColor(0xffb1b1b1);
        }
    }


    public void updateProfile() {
        Drawable profileDrawable = Profile.get().getRoundedProfileImage();
        if(null != profileDrawable) {
            profileImageView.setImageDrawable(profileDrawable);
        }

        String fullName = Profile.get().getFullName();
        if(!fullName.isEmpty()) {
            nameText.setText(fullName);
        }
    }
}
