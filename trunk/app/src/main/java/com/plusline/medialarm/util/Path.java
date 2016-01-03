package com.plusline.medialarm.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Path management for Bainil 2.0 class
 *
 */
public class Path {

    private static Context context;

    private Path() {
        // do nothing...
    }


    //
    public static void init(Context c) {
        context = c;
        FileUtil.makeDir(getWorkingPath());
    }


    private static String basicResPath = "";
    private static String workingPath = "";

    //
    public static String getProfilePath() {
        if (basicResPath.isEmpty()) {
            basicResPath = String.format(
                    "%s/Android/data/%s/profile",
                    Environment.getExternalStorageDirectory(),
                    context.getPackageName());
        }
        return basicResPath;
    }

    //
    // 임시 작업 공간 경로를 반환한다.
    public static String getWorkingPath() {
        if (workingPath.isEmpty()) {
            workingPath = String.format(
                    "%s/Android/data/%s/working/",
                    Environment.getExternalStorageDirectory(),
                    context.getPackageName());
        }
        return workingPath;
    }

    //
    public static String getRootPath() {
        return String.format(
                "%s/Android/data/%s/res",
                Environment.getExternalStorageDirectory(),
                context.getPackageName());
    }

    public static String getAlbumPath(int albumId) {
        return String.format(
                "%s/%s",
                getRootPath(),
                String.format("album_%d", albumId));
    }


    //
    // 두 문자열 경로를 합친다.
    public static String combine(String path1, String path2) {
        char lastChar = path1.charAt(path1.length() - 1);
        if(lastChar == '/') {
            return String.format("%s%s", path1, path2);
        } else {
            return String.format("%s/%s", path1, path2);
        }
    }

    public static void makeDirs(String path) {
        File targetDir = new File(path);
        targetDir.mkdirs();
    }

    public static void makeNoMedia(String path) {
        String fullPath = combine(path, ".nomedia");
        File targetDir = new File(fullPath);
        targetDir.mkdir();
    }
}
