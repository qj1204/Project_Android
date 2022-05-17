package com.xiaoxin.project_android.activity.student;

import static com.blankj.utilcode.util.UriUtils.file2Uri;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;
import com.soundcloud.android.crop.Crop;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.dialog.LoadingDialog;
import com.xiaoxin.project_android.dialog.SetGestureDialog;
import com.xiaoxin.project_android.dialog.ShowFaceDialog;
import com.xiaoxin.project_android.entity.Attend;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class StudentDoRecordActivity extends AppCompatActivity {
    @BindView(R.id.mapView1)
    MapView mapView;

    private Attend attend;
    private LatLng attendLocation;
    private LatLng currentLocation;
    private Boolean isFirst = true;

    private String pictureDir;
    public String imagePath;
    public File imageFile;
    public Uri imageUri;
    private LoadingDialog dialog;

    private LocationClient locationClient;
    private BaiduMap baiduMap;
    private GeoCoder search = GeoCoder.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_student_do_record);
        ButterKnife.bind(this);
        ViewUtils.initActionBar(this,"开始签到");

        Intent intent = getIntent();
        attend = (Attend)intent.getExtras().getSerializable("attend");
        attendLocation = new LatLng(attend.getLatitude(),attend.getLongitude());

        pictureDir = PathUtils.getExternalAppPicturesPath();
        FileUtils.createOrExistsDir(pictureDir);
        imagePath = pictureDir + "/temp.png";

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        search.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        requestLocation();
    }

    public void requestLocation(){
        initLocation();
        locationClient.start();
    }

    public void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationPurpose(LocationClientOption.BDLocationPurpose.SignIn);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }

    public void navigateTo(BDLocation location){
        if (isFirst){
            isFirst = false;
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll,19f);
            baiduMap.animateMapStatus(update);
        }
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData locationData = builder.build();
        baiduMap.setMyLocationData(locationData);

        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(ImageUtils.getBitmap(R.drawable.ic_location_on));
        OverlayOptions option = new MarkerOptions().position(attendLocation)
                .icon(bitmap).animateType(MarkerOptions.MarkerAnimateType.grow)
                .alpha(1f).visible(true);
        baiduMap.addOverlay(option);
        drawCircle(attendLocation);
    }

    private void drawCircle(LatLng point){
        CircleOptions circle = new CircleOptions().fillColor(0x384d73b3).
                center(point).stroke(new Stroke(3,0x784d73b3))
                .radius(Static.DISTANCE).visible(true);
        baiduMap.addOverlay(circle);
    }

    //获取当前位置的名称
    public OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            List<PoiInfo> poiList = reverseGeoCodeResult.getPoiList();
            if (!poiList.isEmpty()){
                PoiInfo poiInfo = poiList.get(0);
                String text = poiInfo.name + poiInfo.direction + poiInfo.distance + "米";
                if (attend.getType() == 1) {
                    String name = getSharedPreferences("localRecord", MODE_PRIVATE).getString("id", "") + "_" + attend.getAttendId();
                    ShowFaceDialog dialog = new ShowFaceDialog(StudentDoRecordActivity.this, imagePath, name, text);
                    dialog.setOnDismissListener(dialog1 -> {
                        finish();
                    });
                    dialog.show();
                } else {
                    LoadingDialog dialog = new LoadingDialog(StudentDoRecordActivity.this);
                    dialog.setTitle("正在签到");
                    dialog.show();
                    Map<String, String> map = new HashMap<>();
                    map.put("attendId",String.valueOf(attend.getAttendId()));
                    map.put("studentId",getSharedPreferences("localRecord",MODE_PRIVATE).getString("id",""));
                    map.put("result","0");
                    map.put("time",new Timestamp(System.currentTimeMillis()).toString());
                    map.put("location",text);
                    NetUtil.getNetData("record/updateRecord", map, new Handler(message -> {
                        dialog.showSingleButton();
                        if (message.what == 1){
                            dialog.setMessage("签到成功");
                            dialog.setOnDismissListener(dialog2 -> finish());
                        } else {
                            dialog.setMessage("签到失败");
                        }
                        return false;
                    }));
                }
            }
        }
    };

    //定位我的位置
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                currentLocation = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                Log.d("location-->",bdLocation.getLatitude() + "," +bdLocation.getLongitude());
                navigateTo(bdLocation);
            } else {
                LoadingDialog loadingDialog = new LoadingDialog(getApplicationContext());
                loadingDialog.setTitle("定位错误");
                loadingDialog.setMessage("无法定位，请打开GPS或网络重试");
                loadingDialog.showSingleButton();
                loadingDialog.show();
                loadingDialog.setOnDismissListener(dialog -> finish());
            }
        }
    }


    @OnClick({R.id.map_start_record, R.id.map_my_location})
    public void onClick(View v){
        LoadingDialog dialog = new LoadingDialog(this);
        dialog.setTitle("提示");
        switch (v.getId()){
            case R.id.map_start_record:
                //获取当前位置并与签到位置比对
                if (DistanceUtil.getDistance(currentLocation,attendLocation) > Static.DISTANCE){
                    dialog.setMessage("不在考勤范围内，请在考勤范围内签到");
                    dialog.showSingleButton();
                    dialog.show();
                } else {
                    //打开相机
                    if (attend.getType() == 1) {
                        openCamera();
                    } else if (attend.getType() == 2){
                        showGestureDialog();
                    } else {
                        search.reverseGeoCode(new ReverseGeoCodeOption().location(currentLocation));
                    }
                }
                break;
            case R.id.map_my_location:
                Toast.makeText(this, "我的位置", Toast.LENGTH_SHORT).show();
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(currentLocation,19f);
                baiduMap.animateMapStatus(update);
                break;
        }
    }

    private void openCamera(){
        imageFile = new File(imagePath);
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = file2Uri(imageFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Static.OPEN_CAMERA);
    }

    public void showGestureDialog(){
        SetGestureDialog gestureDialog = new SetGestureDialog(this);
        gestureDialog.setRightGesture(attend.getGesture());
        gestureDialog.setCancelable(true);
        gestureDialog.setCanceledOnTouchOutside(true);
        gestureDialog.setYesClickedListener(new SetGestureDialog.onYesClickedListener() {
            @Override
            public void yesClicked(String list) {

            }

            @Override
            public void yesClicked() {
                search.reverseGeoCode(new ReverseGeoCodeOption().location(currentLocation));
                gestureDialog.dismiss();
            }
        });
        gestureDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Static.OPEN_CAMERA:
                if (resultCode == RESULT_OK) {
                    dialog = new LoadingDialog(this);
                    dialog.setTitle("截图操作");
                    dialog.setMessage(StringUtils.getString(R.string.image_wait_message));
                    dialog.show();
                    //旋转处理后的bitmap
                    new Thread(() -> {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));
                            bitmap = rotateIfRequired(bitmap, imageUri, Static.OPEN_CAMERA);
                            saveBitmap(bitmap);
                            Message message = new Message();
                            mHandler.sendMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                break;
            case Crop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    //获得当前位置的中文描述
                    search.reverseGeoCode(new ReverseGeoCodeOption().location(currentLocation));
                }
                break;
        }
    }

    Handler mHandler = new Handler(message -> {
        dialog.dismiss();
        Crop.of(imageUri,imageUri).asSquare().withAspect(600,800).start(this);
        return false;
    });

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationClient.stop();
        search.destroy();
        baiduMap.setMyLocationEnabled(false);
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