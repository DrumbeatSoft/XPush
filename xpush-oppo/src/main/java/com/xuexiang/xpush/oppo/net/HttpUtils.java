package com.xuexiang.xpush.oppo.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;
import com.xuexiang.xpush.logs.PushLog;
import com.xuexiang.xpush.oppo.DESUtil;
import com.xuexiang.xpush.oppo.bean.BaseBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpUtils {

    //线程池
    private static ExecutorService executor;
    private static Handler mHandler;
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final int CONNECT_TIME_OUT = 50000;
    private static final int READ_TIME_OUT = 50000;

    static {
        executor = Executors.newFixedThreadPool(5);
        mHandler = new Handler();
    }

    public static void postJson(final String url, final Object params, final Callback callback) {
        final String authorization = DESUtil.encrypt("drumbeatpush", "drumbeat2020!@123" + "-" + System.currentTimeMillis());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                OutputStream outputStream = null;
                try {
                    PushLog.d("url:" + url);
                    URL u = new URL(url);
                    connection = (HttpURLConnection) u.openConnection();
                    // 设置输入可用
                    connection.setDoInput(true);
                    // 设置输出可用
                    connection.setDoOutput(true);
                    // 设置请求方式
                    connection.setRequestMethod(METHOD_POST);
                    // 设置连接超时
                    connection.setConnectTimeout(CONNECT_TIME_OUT);
                    // 设置读取超时
                    connection.setReadTimeout(READ_TIME_OUT);
                    // 设置缓存不可用
                    connection.setUseCaches(false);
                    // 设置请求头
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", authorization);
                    PushLog.d("RequestProperty:" + JSONObject.toJSON(connection.getRequestProperties()));
                    // 开始连接
                    connection.connect();

                    if (params != null) {
                        String json = JSONObject.toJSON(params).toString();
                        PushLog.d("params:" + json);
                        outputStream = connection.getOutputStream();
                        outputStream.write(json.getBytes());
                        outputStream.flush();
                    }
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = connection.getInputStream();
                        final String result = inputStream2String(inputStream);
                        if (result != null && callback != null) {
                            postSuccess(result, callback);
                        }
                    } else {
                        if (callback != null) {
                            postFailed(callback, responseCode, new Exception("请求数据失败：" + responseCode));
                        }
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        postFailed(callback, 0, e);
                    }

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        });
    }

    private static void postSuccess(final String result, final Callback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PushLog.d("Success result:" + result);
                BaseBean dataBean = JSONObject.parseObject(result, BaseBean.class);
                if (dataBean != null) {
                    int code = dataBean.getCode();
                    if (code == 200) {
                        if (dataBean.getData() != null) {
                            Object o = JSONObject.parseObject(dataBean.getData().toString(), getInterfaceT(callback, 0));
                            dataBean.setData(o);
                        }
                        callback.onSuccess(dataBean);
                    } else {
                        postFailed(callback, code, new Exception(dataBean.getMessage()));
                    }
                } else {
                    postFailed(callback, 0, new Exception("json 解析失败"));
                }
            }
        });
    }

    private static void postFailed(final Callback callback, final int code, final Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PushLog.d("postFailed code:" + code + "\nException:" + e);
                callback.onFaileure(code, e);
            }
        });
    }

    /**
     * 字节流转换成字符串
     *
     * @param inputStream
     * @return
     */
    private static String inputStream2String(InputStream inputStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            return new String(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 字节流转换成字节数组
     *
     * @param inputStream 输入流
     * @return
     */
    public static byte[] inputStream2ByteArray(InputStream inputStream) {
        byte[] result = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 缓冲区
        byte[] bytes = new byte[1024];
        int len = -1;
        try {
            // 使用字节数据输出流来保存数据
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            result = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 判断是否联网
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {

        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    public interface Callback<T> {
        void onFaileure(int code, Exception e);

        void onSuccess(T dataBean);
    }

    /**
     * 获取接口上的泛型T
     *
     * @param o     接口
     * @param index 泛型索引
     */
    public static Class<?> getInterfaceT(Object o, int index) {
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[index];
        Type type = parameterizedType.getActualTypeArguments()[index];
        return checkType(type, index);
    }

    private static Class<?> checkType(Type type, int index) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type t = pt.getActualTypeArguments()[index];
            return checkType(t, index);
        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType"
                    + ", but <" + type + "> is of type " + className);
        }
    }

}
