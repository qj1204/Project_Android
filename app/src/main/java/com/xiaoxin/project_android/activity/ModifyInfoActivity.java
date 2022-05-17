package com.xiaoxin.project_android.activity;

import static com.blankj.utilcode.util.UriUtils.file2Uri;

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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.request.RequestOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.soundcloud.android.crop.Crop;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.activity.login.LoginActivity;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.dialog.ConfirmDialog;
import com.xiaoxin.project_android.dialog.LoadingDialog;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 修改信息页面
 */
@SuppressLint("NonConstantResourceId")
public class ModifyInfoActivity extends BaseActivity {
    @BindView(R.id.info_detail_class1)
    EditText classText;
    @BindView(R.id.info_detail_name1)
    EditText name;
    @BindView(R.id.info_detail_phone1)
    EditText phone;
    @BindView(R.id.info_detail_email1)
    EditText email;
    @BindView(R.id.face_layout1)
    LinearLayout faceLayout;
    @BindView(R.id.info_detail_class_layout1)
    LinearLayout classLayout;
    @BindView(R.id.info_detail_face1)
    ImageView face;
    @BindView(R.id.info_detail_avatar1)
    ImageView avatar;

    private SharedPreferences preferences;

    public String pictureDir;
    public String albumPath;
    public String cameraPath;
    public Uri cameraUri;
    public File cameraFile;
    private String type;
    private String id;
    private String oldPhone;

    private String newName;
    private int sex;
    private String newPhone;
    private String newEmail;
    private String newClass;
    private Map<String, String> map = new HashMap<>();
    private LoadingDialog dialog;

    private boolean isCamera;
    //判断哪个图像更改了
    private boolean avatarChanged = false;
    private boolean faceChanged = false;

    //记录当前图像上传状态
    private boolean avatarFinish = false;
    private boolean faceFinish = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_info;
    }

    @Override
    protected void initView() {
        ViewUtils.initActionBar(this,"修改信息");

        preferences = getSharedPreferences("localRecord",MODE_PRIVATE);
        type = preferences.getString("userType", "");
        id = preferences.getString("id","");
        oldPhone = preferences.getString("phone","");

        pictureDir = PathUtils.getExternalAppPicturesPath();
        FileUtils.createOrExistsDir(pictureDir);
        albumPath = pictureDir + "/album.png";
        cameraPath = pictureDir + "/camera.png";

        CommonUtil.initPhotoError();

        dialog = new LoadingDialog(this);

        Log.d("修改信息界面头像路径",Static.SERVICE_PATH + preferences.getString("avatar",""));
        GlideUtil.load(this,Static.SERVICE_PATH + preferences.getString("avatar",""),avatar,new RequestOptions().circleCrop());

        sex = preferences.getInt("sex",0);
        findViewById((sex == 1 ? R.id.male : R.id.female)).performClick();
        name.setText(preferences.getString("name",""));

        phone.setText(preferences.getString("phone",""));
        email.setText(preferences.getString("email",""));

        if (type.equals("2")){
            classLayout.setVisibility(View.VISIBLE);
            classText.setText(preferences.getString("class",""));

            faceLayout.setVisibility(View.VISIBLE);
            String facePath = preferences.getString("face",null);
            if (!StringUtils.isEmpty(facePath)) {
                Log.d("修改信息界面人脸路径",Static.SERVICE_PATH + facePath);
                GlideUtil.load(this,Static.SERVICE_PATH + facePath,face,new RequestOptions());
            }
        }
    }

    @OnClick({R.id.info_detail_avatar1, R.id.info_detail_face1, R.id.info_detail_modify1})
    public void onClick(View v){
        if(!CommonUtil.isFastClick()){
            return;
        }
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.setTitle("提示");
        switch (v.getId()){
            case R.id.info_detail_avatar1:
                loadingDialog.setMessage("是否更改头像");
                loadingDialog.setOnYesClickedListener(view1 -> {
                    openAlbum();
                    loadingDialog.dismiss();
                });
                loadingDialog.show();
                break;
            case R.id.info_detail_face1:
                loadingDialog.setMessage("是否更改人脸信息");
                loadingDialog.setOnYesClickedListener(view1 -> {
                    openCamera();
                    loadingDialog.dismiss();
                });
                loadingDialog.show();
                break;
            case R.id.info_detail_modify1:
                dialog.setTitle("开始修改");
                dialog.setMessage(StringUtils.getString(R.string.wait_message));
                commitModify();
                break;
            case R.id.activity_modify_info:
                hideSoftInput();
                break;
            default:
                break;
        }
    }

    public void openAlbum(){
        isCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, Static.OPEN_ALBUM);
    }

    public void openCamera(){
        isCamera = true;

        cameraFile = new File(cameraPath);
        try {
            if (cameraFile.exists()) {
                cameraFile.delete();
            }
            cameraFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cameraUri = file2Uri(cameraFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, Static.OPEN_CAMERA);
    }

    private void commitModify() {
        newName = name.getText().toString();
        newPhone = phone.getText().toString();
        newEmail = email.getText().toString();

        if ( newName.length() < 1 ){
            Toast.makeText(this, "姓名不可为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if ( !CommonUtil.isPhone(newPhone) ){
            Toast.makeText(this, "手机号格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if ( !CommonUtil.IsEmail(newEmail) ){
            Toast.makeText(this, "邮箱格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (type.equals("2")){
            newClass = classText.getText().toString();
            /*if (newClass.length() < 7){
                Toast.makeText(this, "班级格式错误", Toast.LENGTH_SHORT).show();
                return;
            }*/
        }

        map.put("name",newName);
        map.put("sex",(sex == 1 ? "1" : "0"));
        map.put("phone",newPhone);
        map.put("email",newEmail);

        if (!newPhone.equals(oldPhone)){
            //手机号不同，需要验证
            ConfirmDialog confirmDialog = new ConfirmDialog(this,newPhone);
            confirmDialog.setConfirmSuccessListener(() -> {
                confirmDialog.dismiss();
                dialog.show();
                if (avatarChanged) {
                    sendImage(new File(albumPath),Integer.parseInt(type),id);
                }
                if (faceChanged) {
                    sendImage(new File(cameraPath), 4, id);
                }
            });
            confirmDialog.show();
        } else {
            dialog.show();
            if (avatarChanged) {
                sendImage(new File(albumPath),Integer.parseInt(type),id);
            }
            if (faceChanged) {
                sendImage(new File(cameraPath), 4, id);
            }
            if ( !(avatarChanged || faceChanged)){
                if (type.equals("1")) {
                    map.put("teacherId", id);
                    NetUtil.getNetData("teacher/update",map,handler);
                } else {
                    map.put("studentId", id);
                    NetUtil.getNetData("student/update",map,handler);
                }
            }
        }
    }

    //监听真正的修改
    Handler handler = new Handler(message -> {
        dialog.showSingleButton();
        dialog.setMessage(message.getData().getString("data"));
        if (message.what == 1){
            Map<String,String> map = new HashMap<>();
            map.put("type", type);
            map.put("account",preferences.getString("account",""));
            map.put("password",preferences.getString("password",""));
            NetUtil.getNetData("account/login", map ,new Handler(message1 -> {
                if (message1.what == 1){
                    String data = message1.getData().getString("data");
                    LoginActivity.updateLoginInfo(preferences,data,type);
                } else{
                    Toast.makeText(this, message1.getData().getString("msg"), Toast.LENGTH_SHORT).show();
                }
                return false;
            }));

            dialog.setOnDismissListener(dialog1 -> {
                finish();
            });
        }
        return false;
    });

    Handler uploadHandler = new Handler(msg -> {
        String img = (String) msg.obj;
        switch (msg.what){
            //上传教师头像
            case 1:
                map.put("teacherId",id);
                map.put("avatar",img);
                NetUtil.getNetData("teacher/update",map,handler);
                break;
            //上传学生头像
            case 2:
                avatarFinish = true;
                map.put("major",newClass);
                map.put("studentId",id);
                map.put("avatar",img);
                if (faceChanged){
                    if (faceFinish){
                        NetUtil.getNetData("student/update",map,handler);
                    }
                } else {
                    NetUtil.getNetData("student/update",map,handler);
                }
                break;
            //上传人脸信息
            case 4:
                faceFinish = true;
                map.put("studentId",id);
                map.put("face",img);
                if (avatarChanged){
                    if (avatarFinish){
                        NetUtil.getNetData("student/update",map,handler);
                    }
                } else {
                    NetUtil.getNetData("student/update",map,handler);
                }
                break;
        }
        return false;
    });

    public void sendImage(File file, Integer type, String id){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("photo", file);
            params.put("type", type);
            params.put("id",id);
            client.post(Static.SERVICE_PATH + "document/saveImage", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    JSONObject object = JSON.parseObject(new String(responseBody));
                    String imgPath = object.getString("data");
                    Message message = new Message();
                    message.what = type;
                    message.obj = imgPath;
                    uploadHandler.sendMessage(message);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    dialog.setMessage("文件上传失败，请重试");
                    dialog.showSingleButton();
                }
            });
        } catch (FileNotFoundException e){
            Toast.makeText(this, "未找到文件", Toast.LENGTH_SHORT).show();
        }
    }

    Handler mHandler = new Handler(message -> {
        if (message.what == 1){
            dialog.dismiss();
            Crop.of(cameraUri,cameraUri).asSquare().withAspect(600,800).start(this);
        }
        return false;
    });

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Static.OPEN_ALBUM:
                if (resultCode == RESULT_OK && data != null){
                    File tempFile = new File(albumPath);
                    Crop.of(data.getData(),Uri.fromFile(tempFile)).asSquare().withAspect(600,800).start(this);
                }
                break;
            case Static.OPEN_CAMERA:
                if (resultCode == RESULT_OK) {
                    dialog = new LoadingDialog(this);
                    dialog.setTitle("截图操作");
                    dialog.setMessage(StringUtils.getString(R.string.image_wait_message));
                    dialog.show();
                    //旋转处理后的bitmap
                    new Thread(() -> {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(cameraUri));
                            bitmap = rotateIfRequired(bitmap, cameraUri, Static.OPEN_CAMERA);
                            saveBitmap(bitmap);
                            Message message = new Message();
                            message.what = 1;
                            mHandler.sendMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    if (isCamera) {
                        faceChanged = true;
                        face.setImageURI(cameraUri);
                    } else {
                        avatarChanged = true;
                        avatar.setImageBitmap(ImageUtils.getBitmap(albumPath));
                    }
                }
                break;
        }
    }

    @OnCheckedChanged({R.id.male, R.id.female})
    public void onCheckChanged(CompoundButton view, boolean isChanged){
        switch (view.getId()){
            case R.id.male:
                if (isChanged){
                    sex = 1;
                }
                break;
            case R.id.female:
                if (isChanged){
                    sex = 0;
                }
                break;
        }
    }

    /**
     * 将bitmap压缩并写入文件
     * @param bitmap
     * @return
     */
    public void saveBitmap(Bitmap bitmap) {
        FileOutputStream outStream = null;
        //String filePath = PathUtils.getExternalAppPicturesPath() + "/temp.png";
        try {
            if (cameraFile.exists()) {
                cameraFile.delete();
            }
            cameraFile.createNewFile();
            outStream = new FileOutputStream(cameraPath);
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
            exif = new ExifInterface(cameraFile.getPath());
            //exif = new ExifInterface(uriToFileApiQ(this,uri));    //文件路径一样，这个会翻转，不知道为啥
        } else if(requestCode == Static.OPEN_ALBUM){
            exif = new ExifInterface(uriToFileApiQ(this,uri));
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