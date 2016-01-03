package com.plusline.medialarm.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;


/**
 * Select image file class
 */
public class FileSelector {

    public static final int REQUEST_SEND_CONNECTED_IMAGE = 4990;
    public static final int REQUEST_SEND_CONNECTED_MUSIC = 4991;

    private final Activity activity;
    private final Fragment fragment;
    private final boolean overLollipop;


    //
    //  LOLLIPOP
    //
    public FileSelector(Fragment fr) {
        this.fragment = fr;
        this.activity = null;
        overLollipop = Build.VERSION.SDK_INT >= 21; // ( == Build.VERSION_CODE.LOLLIPOP )
    }

    public FileSelector(Activity activity) {
        this.activity = activity;
        this.fragment = null;
        overLollipop = Build.VERSION.SDK_INT >= 21; // ( == Build.VERSION_CODE.LOLLIPOP )
    }


    public void showSelectImageView() { // For only connected fragment
        Intent selectIntent = getSelectImageIntent();
        fragment.startActivityForResult(selectIntent, REQUEST_SEND_CONNECTED_IMAGE);
    }


    public void showImageSelector(int requestCode) {
        Intent selectIntent = getSelectImageIntent();
        if(null != activity) {
            activity.startActivityForResult(selectIntent, requestCode);
        } else {
            fragment.startActivityForResult(selectIntent, requestCode);
        }
    }




    private Intent getSelectImageIntent() {
        if(overLollipop) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            return intent;
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            return intent;
        }
    }


    public void showSelectMusicView() {
        Intent selectIntent = getSelectMusicIntent();
        fragment.startActivityForResult(selectIntent, REQUEST_SEND_CONNECTED_MUSIC);
    }

    private Intent getSelectMusicIntent() {
        if(overLollipop) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
            return intent;
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Audio.Media.CONTENT_TYPE);
            intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            return intent;
        }
    }


    public String getPath(Uri uri) {
        // Check Google Drive.
        if(isGooglePhotoUri(uri)) {
            return uri.getLastPathSegment();
        }

        // 1. 안드로이드 버전 체크
        // com.android.providers.media.documents/document/image :: uri로 전달 받는 경로가 킷캣으로 업데이트 되면서 변경 됨.
        if(overLollipop && DocumentsContract.isDocumentUri(uri)) {

            //com.android.providers.media.documents/document/image:1234 ...
            //
            if(isMediaDocument(uri) && DocumentsContract.isDocumentUri(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    return null;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = MediaStore.Images.Media._ID + "=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                Activity tempActivity = (null != activity) ? activity : fragment.getActivity();
                return getDataColumn(tempActivity.getApplicationContext(), contentUri, selection, selectionArgs);
            }
        }

        return getPathFromUri(uri);

        // content://media/external/images/media/....
        // 안드로이드 버전에 관계없이 경로가 com.android... 형식으로 집히지 않을 수 도 있음.
//        if(isPathSDCardType(uri)) {
//            final String selection = MediaStore.Images.Media._ID + "=?";
//            final String[] selectionArgs = new String[] {
//                    uri.getLastPathSegment()
//            };
//
//            return getDataColumn(fragment.getActivity().getApplicationContext(),  MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
//        }       // File 접근일 경우
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//
//        return null;
    }

    public String getPathFromUri(Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();

        return path;
    }


    // URI 를 받아서 Column 데이터 접근.
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs ,null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return null;
    }

    // 킷캣에서 추가된  document식 Path
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    // 기본 경로 ( 킷캣 이전버전 )
    public static boolean isPathSDCardType(Uri uri) {
        // Path : external/images/media/ID(1234...)
        return "external".equals(uri.getPathSegments().get(0));
    }

    // 구글 드라이브를 통한 업로드 여부 체크.
    public static boolean isGooglePhotoUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
