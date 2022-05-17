package com.xiaoxin.project_android.fragment.main;

import static android.app.Activity.RESULT_OK;

import static com.blankj.utilcode.util.UriUtils.file2Uri;
import static com.blankj.utilcode.util.UriUtils.uri2File;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.soundcloud.android.crop.Crop;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.activity.MainActivity;
import com.xiaoxin.project_android.activity.login.LoginActivity;
import com.xiaoxin.project_android.adapter.CourseAdapter;
import com.xiaoxin.project_android.dialog.CourseAddDialog;
import com.xiaoxin.project_android.dialog.CourseCreateDialog;
import com.xiaoxin.project_android.dialog.FaceUploadDialog;
import com.xiaoxin.project_android.dialog.LoadingDialog;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;

@SuppressLint("NonConstantResourceId")
public class CourseListFragment extends Fragment{
    @BindView(R.id.fragment_course_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.course_list_prompt)
    TextView promptText;
    @BindView(R.id.search1)
    EditText searchEdit;
    @BindView(R.id.content_not_found_layout)
    LinearLayout notFoundLayout;

    private CourseListViewModel viewModel;
    private CourseCreateDialog createDialog;
    private FaceUploadDialog faceUploadDialog;
    private LoadingDialog dialog;
    private final ImageView imageView;
    private final ImageView headerImage;

    public String pictureDir;
    public String imagePath;
    public File imageFile;
    public Uri imageUri;
    public String albumPath;
    public Uri albumUri;
    private String userType;
    private String id;

    Unbinder unbinder;

    Handler courseListHandler = new Handler(message -> {
        if (message.what == 1){
            viewModel.updateCourses(message.getData().getString("data"));
        }
        Toast.makeText(getActivity(), message.getData().getString("msg"), Toast.LENGTH_SHORT).show();

        refreshLayout.setRefreshing(false);
        return false;
    });

    public CourseListFragment(ImageView imageView, ImageView headerImage) {
        this.imageView = imageView;
        this.headerImage = headerImage;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        unbinder = ButterKnife.bind(this,view);

        //创建存储图片的临时文件地址
        pictureDir = PathUtils.getExternalAppPicturesPath();
        FileUtils.createOrExistsDir(pictureDir);
        albumPath = pictureDir + "/album.png";
        imagePath = pictureDir + "/faceImg.png";
        //  /storage/emulated/0/Android/data/com.xiaoxin.project_android/files/Pictures/faceImg.png

        CommonUtil.initPhotoError();

        //获取所有与用户相关的课程
        SharedPreferences preferences = getActivity().getSharedPreferences("localRecord", Context.MODE_PRIVATE);
        userType = preferences.getString("userType","");
        id = preferences.getString("id","");

        if (userType.equals("2")) {
            String face = preferences.getString("face", "");
            uploadFace(face);
        }

        refreshLayout.setColorSchemeColors(ViewUtils.getRefreshColor());

        Map<String, String> map = new HashMap<>();
        if (userType.equals("2")){
            promptText.setText("我的课程");
            map.put("studentId",id);
            NetUtil.getNetData("course/findCourseByStudentId", map, courseListHandler);

            refreshLayout.setOnRefreshListener(() -> {
                refreshLayout.setRefreshing(false);
                GlideUtil.load(getContext(),Static.SERVICE_PATH + preferences.getString("avatar",null),imageView,new RequestOptions().circleCrop());
                GlideUtil.load(getContext(),Static.SERVICE_PATH + preferences.getString("avatar",null), headerImage, new RequestOptions().circleCrop());
                NetUtil.getNetData("course/findCourseByStudentId", map, courseListHandler);
            });

            searchEdit.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    String name = searchEdit.getText().toString();
                    Map<String, String> map1 = new HashMap<>();
                    map1.put("studentId",id);
                    map1.put("name",name);
                    NetUtil.getNetData("course/findCourseByStudentIdAndName", map1, courseListHandler);
                }
                return false;
            });
        } else {
            promptText.setText("我教的课");
            map.put("teacherId",preferences.getString("id",""));
            NetUtil.getNetData("course/findCourseByTeacherId", map, courseListHandler);

            refreshLayout.setOnRefreshListener(() -> {
                refreshLayout.setRefreshing(false);
                GlideUtil.load(getContext(),Static.SERVICE_PATH + preferences.getString("avatar",null),imageView,new RequestOptions().circleCrop());
                GlideUtil.load(getContext(),Static.SERVICE_PATH + preferences.getString("avatar",null), headerImage, new RequestOptions().circleCrop());
                NetUtil.getNetData("course/findCourseByTeacherId", map, courseListHandler);
            });

            searchEdit.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    String name = searchEdit.getText().toString();
                    Map<String, String> map1 = new HashMap<>();
                    map1.put("teacherId",id);
                    map1.put("name",name);
                    NetUtil.getNetData("course/findCourseByTeacherIdAndName", map1, courseListHandler);
                }
                return false;
            });
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CourseListViewModel.class);
        // TODO: Use the ViewModel
        viewModel.getCourseList().observe(getViewLifecycleOwner(), courseList -> {
            if (courseList.size() < 1){
                notFoundLayout.setVisibility(View.VISIBLE);
            } else {
                notFoundLayout.setVisibility(View.GONE);
            }
            ViewUtils.setRecycler(getActivity(), R.id.recycler_course_list, new CourseAdapter(courseList));
        });
    }

    public void uploadFace(String face){
        if (face.equals("")){
            faceUploadDialog = new FaceUploadDialog(getContext());
            faceUploadDialog.setNoClickedListener(view -> {
                faceUploadDialog.dismiss();
                LoadingDialog loadingDialog = new LoadingDialog(getContext());
                loadingDialog.setTitle("警告");
                loadingDialog.setMessage("未注册人脸信息，将返回登录");
                loadingDialog.showSingleButton();
                loadingDialog.setOnDismissListener(dialog1 -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                });
                loadingDialog.show();
            });

            faceUploadDialog.setSelectedClickedListener(view -> {
                //跳转到相机界面，在拍完照之后跳转到剪切界面剪切完成后返回到dialog并将照片显示在预览中
                openCamera();
            });

            faceUploadDialog.setYesClickedListener(view -> {
                //执行上传操作
                if (faceUploadDialog.getPicturePath() == null){
                    Toast.makeText(view.getContext(), "未选择图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    sendFace(imageFile);
                } catch (FileNotFoundException e){
                    Toast.makeText(view.getContext(), "未找到文件", Toast.LENGTH_SHORT).show();
                }
            });
            faceUploadDialog.show();
        }
    }

    @OnClick({R.id.course_create_button,R.id.fragment_course_list})
    public void onClick(View v) {
        if (!CommonUtil.isFastClick()) {
            return;
        }
        if (v.getId() == R.id.course_create_button) {
            if (userType.equals("1")) {
                createDialog = new CourseCreateDialog(v.getContext());
                createDialog.setChooseClickListener(this::openAlbum);

                createDialog.show();
            } else {
                CourseAddDialog addDialog = new CourseAddDialog(v.getContext(), id);
                addDialog.show();
            }
        } else if(v.getId() == R.id.fragment_course_list){
            KeyboardUtils.hideSoftInput(getView());
        }
    }

    public void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, Static.OPEN_ALBUM);
    }

    public void openCamera(){
        imageFile = new File(imagePath);
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //imageFile  /storage/emulated/0/Android/data/com.xiaoxin.project_android/files/Pictures/faceImg.png
        imageUri = file2Uri(imageFile);
        //imageUri  content://com.xiaoxin.project_android.fileprovider/images/Android/data/com.xiaoxin.project_android/files/Pictures/faceImg.png
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Static.OPEN_CAMERA);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Static.OPEN_ALBUM:
                if (resultCode == RESULT_OK && data != null){
                    try {
                        albumUri = data.getData();
                        sendImage(uri2File(albumUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Static.OPEN_CAMERA:
                if (resultCode == RESULT_OK) {
                    dialog = new LoadingDialog(getContext());
                    dialog.setTitle("截图操作");
                    dialog.setMessage(StringUtils.getString(R.string.image_wait_message));
                    dialog.show();
                    //旋转处理后的bitmap
                    new Thread(() -> {
                        try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        bitmap = rotateIfRequired(bitmap, imageUri, Static.OPEN_CAMERA);
                        Message message = new Message();
                        message.obj = bitmap;
                        mHandler.sendMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    if (userType.equals("1")) {
                        createDialog.previewImage.setImageBitmap(ImageUtils.getBitmap(albumPath));
                    } else {
                        faceUploadDialog.preview.setImageURI(imageUri);
                        faceUploadDialog.setPicturePath(imagePath);
                        faceUploadDialog.invisibleButton();
                    }
                }
                break;
        }
    }

    Handler mHandler = new Handler(message -> {
        Bitmap bitmap = (Bitmap) message.obj;
        saveBitmap(bitmap);
        dialog.dismiss();
        Crop.of(imageUri,imageUri).asSquare().withAspect(600,800).start(getContext(),this);
        return false;
    });

    public void sendFace(File file) throws FileNotFoundException {
        LoadingDialog waitDialog = new LoadingDialog(getContext());
        waitDialog.setTitle("上传人脸信息");
        waitDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("photo",file);
        params.put("type","4");
        params.put("id",String.valueOf(id));
        client.post(Static.SERVICE_PATH + "document/saveImage", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject object = JSON.parseObject(new String(responseBody));
                String face = object.getString("data");
                Log.d("存储人脸图片路径", face);
                //    image/face/12.png
                Map<String, String> map = new HashMap<>();
                map.put("face", face);
                map.put("studentId", id);
                NetUtil.getNetData("student/update", map, new Handler(message -> {
                    waitDialog.showSingleButton();
                    if (message.what == 1) {
                        waitDialog.setMessage("人脸信息修改成功");
                        getActivity().getSharedPreferences("localRecord", Context.MODE_PRIVATE).edit().putString("face", face).apply();
                        faceUploadDialog.dismiss();
                    } else {
                        waitDialog.setMessage(message.getData().getString("msg"));
                    }
                    return false;
                }));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                waitDialog.setMessage("图片上传失败，请重试");
                waitDialog.showSingleButton();
            }
        });
    }

    public void sendImage(File file) throws FileNotFoundException {
        AlertDialog waitDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage("文件正在上传，请等待")
                .setCancelable(false)
                .create();
        waitDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("photo",file);
        params.put("type","3");
        params.put("id","0");
        client.post(Static.SERVICE_PATH + "document/saveImage", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                waitDialog.dismiss();
                AlertDialog dialog = initResultDialog("文件上传成功");
                JSONObject object = JSON.parseObject(new String(responseBody));
                createDialog.setImgPath(object.getString("data"));
                createDialog.previewImage.setImageURI(albumUri);
                createDialog.imageState.setTextColor(ColorUtils.getColor(R.color.green));
                createDialog.imageState.setText("选取成功");
                dialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                waitDialog.dismiss();
                AlertDialog dialog = initResultDialog("文件上传失败");
                createDialog.previewImage.setImageBitmap(ImageUtils.getBitmap(R.drawable.ic_net_error));
                createDialog.imageState.setTextColor(ColorUtils.getColor(R.color.cancel_red));
                createDialog.imageState.setText("上传失败");
                dialog.show();
            }
        });
    }

    private AlertDialog initResultDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle("创建课程")
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                });
        builder.setCancelable(true);
        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**
     * 将bitmap压缩并写入文件
     * @param bitmap
     * @return
     */
    public void saveBitmap(Bitmap bitmap) {
        FileOutputStream outStream = null;
        //String filePath = PathUtils.getExternalAppPicturesPath() + "/faceImg.png";
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
            outStream = new FileOutputStream(imagePath);
            // 把数据写入文件，100表示不压缩
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 旋转图片，与下一个方法配套使用
     * @param bitmap
     * @param uri
     * @param requestCode
     * @return
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Bitmap rotateIfRequired(Bitmap bitmap,Uri uri,int requestCode) throws IOException {
        ExifInterface exif = null;

        if(requestCode == Static.OPEN_CAMERA){
            exif = new ExifInterface(imageFile.getPath());
            //exif = new ExifInterface(uriToFileApiQ(this,uri));    //文件路径一样，这个会翻转，不知道为啥
        } else if(requestCode == Static.OPEN_ALBUM){
            exif = new ExifInterface(uriToFileApiQ(getContext(),uri));
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
        Log.d("orientation", String.valueOf(orientation));
        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateBitmap(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateBitmap(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateBitmap(bitmap, 270);
            default:
                return bitmap;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float)degree);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return rotateBitmap;
    }

    /**
     * 通过uri获取文件路径
     * @param context
     * @param uri
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static String uriToFileApiQ(Context context, Uri uri) {
        File file = null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                @SuppressLint("Range")
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    File cache = new File(context.getExternalCacheDir().getAbsolutePath(), displayName);
                    FileOutputStream fos = new FileOutputStream(cache);
                    android.os.FileUtils.copy(is, fos);
                    file = cache;
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }
}