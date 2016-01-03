package com.plusline.medialarm.management;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.plusline.medialarm.util.Path;
import com.plusline.medialarm.util.RoundedAvatarDrawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 사용자 프로필 데이터 관리
 */
public class Profile extends Option {

    private static final String PROFILE_NAME = "MediAlarmProfileOptions";

    //
    //
    //
    private Profile() {
        // do nothing...
    }

    public void init(Context context) {
        super.init(context, PROFILE_NAME);
    }

    private static final String IMAGE_PATH = "imagePath";
    private static final String FAMILY_NAME = "familyName";
    private static final String LAST_NAME = "lastName";
    private static final String GENDER = "gender";
    private static final String BIRTHDAY = "birthday";


    public Bitmap getProfileImage() {
        String profilePath = getString(IMAGE_PATH, "");
        if(profilePath.isEmpty()) {
            return null;
        }

        return BitmapFactory.decodeFile(profilePath);
    }

    public Drawable getRoundedProfileImage() {
        Bitmap profileBitmap = getProfileImage();
        if(null == profileBitmap) {
            return null;
        }
        return new RoundedAvatarDrawable(profileBitmap);
    }

    public void setProfileImage(Bitmap profileImage) {
        String dirPath = Path.getProfilePath();
        saveBitmapToFile(profileImage, dirPath, "profile.png");

        setStringValue(IMAGE_PATH, Path.combine(dirPath, "profile.png"));
        dispatchChangedMessage();
    }

    private void saveBitmapToFile(Bitmap bitmap, String strFilePath, String filename) {
        File file = new File(strFilePath);

        if (!file.exists()) {
            file.mkdirs();
        }

        File fileCacheItem = new File(Path.combine(strFilePath, filename));
        OutputStream out = null;

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            Log.e("Profile", "Failed to save profile image. " + e.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFullName() {
        String familyName = getFamilyName();
        String lastName = getLastName();
        if(familyName.isEmpty() && lastName.isEmpty()) {
            return "";
        }

        return familyName + " " + lastName;
    }

    public String getFamilyName() {
        return getString(FAMILY_NAME, "");
    }

    public void setFamilyName(String familyName) {
        setStringValue(FAMILY_NAME, familyName);
        dispatchChangedMessage();
    }

    public String getLastName() {
        return getString(LAST_NAME, "");
    }

    public void setLastName(String lastName) {
        setStringValue(LAST_NAME, lastName);
        dispatchChangedMessage();
    }


    public String getGender() {
        return getString(GENDER, "M");  // M, F
    }

    public void setGender(String gender) {
        setStringValue(GENDER, gender);
    }


    public String getBirthday() {
        return getString(BIRTHDAY, "");
    }

    public void setBirthday(String birthday) {
        setStringValue(BIRTHDAY, birthday);
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    // static variables & methods

    private static Profile profile = new Profile();
    public static Profile get() {
        return profile;
    }

}
