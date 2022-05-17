package com.xiaoxin.project_android.utils;

import android.os.StrictMode;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    public static void initPhotoError(){
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    public static String list2String(List<Integer> list){
        String s = "";
        for (Integer integer : list) {
            s = s + integer;
        }
        return s;
    }

    /**
     * 将long转换成时长
     * @param time long型的时长
     * @return 考勤持续时长
     */
    public static String long2String(long time){
        int day = (int) time / (1000 * 3600 * 24);
        time = time % (1000 * 3600 * 24);
        int hour = (int) time / (1000 * 3600);
        time = time % (1000 * 3600);
        int minute = (int) time / (1000 * 60);
        String result = "";
        if (day > 0){
            result = result + day + "天";
        }
        if (hour > 0){
            result = result + hour + "小时";
        }
        if (minute > 0){
            result = result + minute + "分钟";
        }
        return result;
    }

    public static Map<String,String> object2Map(Object object){
        Map<String,String> result=new HashMap<>();
        //获得类的的属性名 数组
        Field[]fields=object.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                result.put(name, String.valueOf(field.get(object)));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 验证手机号码
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone){
        boolean flag = false;
        try{
            String pattern = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(phone);
            flag = m.matches();
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 验证邮箱地址是否正确
     * @param email
     * @return
     */
    public static boolean IsEmail(String email){
        boolean flag = false;
        try{
            String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        }catch(Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 两次点击按钮之间的点击间隔不能少于500ms
     */
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}