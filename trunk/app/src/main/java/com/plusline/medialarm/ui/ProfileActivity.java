package com.plusline.medialarm.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.plusline.medialarm.R;
import com.plusline.medialarm.management.Profile;
import com.plusline.medialarm.util.BitmapUtil;
import com.plusline.medialarm.util.FileSelector;
import com.plusline.medialarm.util.RoundedAvatarDrawable;

import java.util.Calendar;

public class ProfileActivity extends Activity {

    private static final int CHANGE_PROFILE_IMAGE = 7097;
    private FileSelector imageSelector;

    private ImageView profileImageView;
    private EditText familyNameEdit;
    private EditText lastNameEdit;
    private TextView genderText;
    private TextView birthdayText;

    private Bitmap changedImage = null;
    private String changeFamilyName = "";
    private String changeLastName = "";
    private String changedGender = "";
    private String changedBirthday = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = (ImageView) findViewById(R.id.profile_image);
        familyNameEdit = (EditText) findViewById(R.id.family_name_edit);
        lastNameEdit = (EditText) findViewById(R.id.last_name_edit);
        genderText = (TextView) findViewById(R.id.gender_text);
        birthdayText = (TextView) findViewById(R.id.birthday_text);

        imageSelector = new FileSelector(this);

        initUI();
        setOnClickListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();

        familyNameEdit.clearFocus();
        lastNameEdit.clearFocus();
    }

    private void initUI() {
        Drawable profileDrawable = Profile.get().getRoundedProfileImage();
        if(null != profileDrawable) {
            profileImageView.setImageDrawable(profileDrawable);
        }

        familyNameEdit.setText(Profile.get().getFamilyName());
        lastNameEdit.setText(Profile.get().getLastName());

        String gender = Profile.get().getGender();
        if(gender.isEmpty()) {
            genderText.setText("성별");
        } else {
            genderText.setText(gender.equals("M") ? "남성" : "여성");
        }

        String birthday = Profile.get().getBirthday();
        if(!birthday.isEmpty()) {
            birthdayText.setText(String.format("생년월일 - %s", birthday));
        }
    }


    private void setOnClickListeners() {
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 프로필 사진
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelector.showImageSelector(CHANGE_PROFILE_IMAGE);
            }
        });

        // 이름 설정
        familyNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String familyName = familyNameEdit.getText().toString();
                    if (!familyName.isEmpty()) {
                        changeFamilyName = familyName;
                    }
                }
            }
        });

        lastNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String lastName = lastNameEdit.getText().toString();
                    if (!lastName.isEmpty()) {
                        changeLastName = lastName;
                    }
                }
            }
        });

        findViewById(R.id.select_gender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentGender = genderText.getText().toString();
                if (currentGender.equals("남성")) {
                    genderText.setText("여성");
                    changedGender = "F";
                } else {
                    genderText.setText("남성");
                    changedGender = "M";
                }
            }
        });

        findViewById(R.id.select_birthday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        findViewById(R.id.save_profile_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != changedImage) {
                    Profile.get().setProfileImage(changedImage);
                }

                if(!changeFamilyName.isEmpty()) {
                    Profile.get().setFamilyName(changeFamilyName);
                }

                if(!changeLastName.isEmpty()) {
                    Profile.get().setLastName(changeLastName);
                }

                if(!changedGender.isEmpty()) {
                    Profile.get().setGender(changedGender);
                }

                if(!changedBirthday.isEmpty()) {
                    Profile.get().setBirthday(changedBirthday);
                }

                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar today = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String birthday = String.format("%d년 %d월 %d일", year, monthOfYear + 1, dayOfMonth);
                birthdayText.setText(String.format("생년월일 - %s", birthday));
                changedBirthday = birthday;
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(Activity.RESULT_OK != resultCode) {
            return;
        }

        if(CHANGE_PROFILE_IMAGE == requestCode) {
            Uri selectedImageUri = data.getData();
            String imagePath = imageSelector.getPath(selectedImageUri);
            if(null == imagePath || imagePath.isEmpty()) {
                return;
            }

            loadSelectedProfileImage(imagePath);
        }
    }


    private void loadSelectedProfileImage(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap profileImage = BitmapFactory.decodeFile(imagePath, options);
        Bitmap squareBitmap = BitmapUtil.resizeSquare(profileImage, 256);

        RoundedAvatarDrawable roundedDrawable = new RoundedAvatarDrawable(squareBitmap);
        profileImageView.setImageDrawable(roundedDrawable);
        changedImage = squareBitmap;
    }

}
