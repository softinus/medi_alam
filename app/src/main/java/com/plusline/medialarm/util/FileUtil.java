package com.plusline.medialarm.util;

import java.io.File;

/**
 * Utility functions about File
 */
public class FileUtil {

    private FileUtil() {}

    /**
     * 입력되는 경로 및 중간경로에 해당하는 모든 디렉토리를 생성한다.
     */
    public static void makeDir(String filePath) {
        StringBuffer buffer = new StringBuffer(filePath);
        String dirPath = buffer.substring(0, buffer.lastIndexOf(File.separator));

        File directory = new File(dirPath);
        if(!directory.exists()) {
            directory.mkdirs();
        }
    }



    public static boolean exists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static void delete(String filePath) {
        File file = new File(filePath);
        file.delete();
    }


    public static int deleteFolder(String a_path){
        File file = new File(a_path);
        if(file.exists()){
            File[] childFileList = file.listFiles();
            for(File childFile : childFileList){
                if(childFile.isDirectory()){
                    deleteFolder(childFile.getAbsolutePath());

                }
                else{
                    childFile.delete();
                }
            }
            file.delete();
            return 1;
        }else{
            return 0;
        }
    }

}
