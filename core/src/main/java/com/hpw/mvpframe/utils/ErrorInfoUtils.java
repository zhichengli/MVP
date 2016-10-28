package com.hpw.mvpframe.utils;

import com.google.gson.Gson;
import com.hpw.mvpframe.entity.CoreErrorResponse;
import com.hpw.mvpframe.net.CoreErrorConstants;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import java.net.UnknownHostException;

import retrofit.HttpException;

public class ErrorInfoUtils {

    /**
     * 解析服务器错误信息
     */
    public static String parseHttpErrorInfo(Throwable throwable) {
        String errorInfo = throwable.getMessage();

        if (throwable instanceof HttpException) {
            // 如果是Retrofit的Http错误,则转换类型,获取信息
            HttpException exception = (HttpException) throwable;
            ResponseBody responseBody = exception.response().errorBody();
            MediaType type = responseBody.contentType();

            // 如果是application/json类型数据,则解析返回内容
            if (type.type().equals("application") && type.subtype().equals("json")) {
                try {
                    // 这里的返回内容是Bmob/AVOS/Parse等RestFul API文档中的错误代码和错误信息对象
                    CoreErrorResponse errorResponse = new Gson().fromJson(
                            responseBody.string(), CoreErrorResponse.class);

                    errorInfo = getLocalErrorInfo(errorResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (throwable instanceof UnknownHostException) {
                errorInfo = "无法连接到服务器";
            }
        }

        return errorInfo;
    }

    /**
     * 获取本地预设错误信息
     */
    private static String getLocalErrorInfo(CoreErrorResponse error) {
        String s = CoreErrorConstants.errors.get(error.getCode());
        if (StringUtils.isEmpty(s)) {
            return error.getError();
        } else {
            return s;
        }
    }
}
