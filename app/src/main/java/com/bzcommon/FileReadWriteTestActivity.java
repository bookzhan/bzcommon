package com.bzcommon;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.util.GlideEngine;
import com.bzcommon.utils.BZAssetsFileManager;
import com.bzcommon.utils.BZLogUtil;
import com.bzcommon.utils.BZPermissionUtil;
import com.bzcommon.utils.BZMediaStoreUtil;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.util.ArrayList;

public class FileReadWriteTestActivity extends AppCompatActivity {

    private ImageView mImageView;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_test);
        mImageView = findViewById(R.id.iv_test);
        mVideoView = findViewById(R.id.video_view);
    }

    public void requestVideoImageFileReadPermission(View view) {
        BZPermissionUtil.requestVideoImageFileReadPermission(this);
    }

    public void FileReadTest(View view) {
        boolean hasPermission= BZPermissionUtil.requestVideoImageFileReadPermission(this);
        if(!hasPermission){
            return;   
        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bzmedia/out.mp4");
        BZLogUtil.d(this, "file exists=" + file.exists() + " canRead=" + file.canRead() + " canWrite=" + file.canWrite() + " length=" + file.length());
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofAll())
                .setImageEngine(GlideEngine.createGlideEngine())
                .isDisplayCamera(false)
                .setSelectionMode(SelectModeConfig.SINGLE)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        BZLogUtil.d("PictureSelector onResult");
                        if (null == result || result.isEmpty()) {
                            return;
                        }
                        LocalMedia localMedia = result.get(0);
                        String path = localMedia.getPath();
                        String realPath = localMedia.getRealPath();
                        BZLogUtil.d("path=" + path);
                        BZLogUtil.d("realPath=" + realPath);
                        if (localMedia.getMimeType().contains("video")) {
                            mVideoView.setVideoURI(Uri.parse(path));
                            mVideoView.start();
                        } else {
                            Bitmap bitmap = BZMediaStoreUtil.loadBitmap(FileReadWriteTestActivity.this, path);
                            mImageView.setImageBitmap(bitmap);
                        }
                        BZMediaStoreUtil.getDirectlyReadPath(FileReadWriteTestActivity.this, path, realPath);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BZPermissionUtil.CODE_REQ_VIDEO_IMAGE_FILE_READ_PERMISSION && grantResults.length > 0) {
            boolean hasPermission = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                    break;
                }
            }
            if (hasPermission) {
                FileReadTest(null);
                BZLogUtil.d(FileReadWriteTestActivity.this,"hasPermission CODE_REQ_VIDEO_IMAGE_FILE_READ_PERMISSION");
            }
        } else if (requestCode == BZPermissionUtil.CODE_REQ_VIDEO_IMAGE_FILE_WRITE_PERMISSION && grantResults.length > 0) {
            boolean hasPermission = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                    break;
                }
            }
            if (hasPermission) {
                saveVideo(null);
                BZLogUtil.d(FileReadWriteTestActivity.this,"hasPermission CODE_REQ_VIDEO_IMAGE_FILE_WRITE_PERMISSION");
            }
        }
    }

    public void WriteImage(View view) {
        boolean hasPermission= BZPermissionUtil.requestVideoImageFileWritePermission(this);
        if(!hasPermission){
            return;   
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_11);
        String path = BZMediaStoreUtil.saveBitmapToSdcard(this, bitmap);
        BZLogUtil.d("path=" + path);
    }

    public void saveVideo(View view) {
        boolean hasPermission= BZPermissionUtil.requestVideoImageFileWritePermission(this);
        if(!hasPermission){
            return;   
        }
        String finalPath = BZAssetsFileManager.getFinalPath(this, "video_test.mp4");
        String saveVideo = BZMediaStoreUtil.saveVideoToSdcard(this, finalPath);
        BZLogUtil.d("path=" + saveVideo);
    }
}
