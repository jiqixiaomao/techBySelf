package com.techbyself.vodplay.util;

import android.util.Log;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.constraint.Constraints.TAG;
import com.alibaba.fastjson.JSON;
/**
 * Created by ckb on 18/1/9.
 */

public final class StsAuthHelper {

    // 目前只有"cn-hangzhou"这个region可用, 不要使用填写其他region的值
    public static final String REGION_CN_HANGZHOU = "cn-hangzhou";
   public static final String LOCAL_HOSTSERVER_IP = "http://59.110.228.212:8080/sts/get";
    public static final String LOCAL_HOSTSERVER_URL = "http://59.110.228.212:8080/sts/getVideoUrl";
    //public static final String LOCAL_HOSTSERVER_IP = "http://192.168.1.4:8080/sts/get";
   // public static final String LOCAL_HOSTSERVER_URL = "http://192.168.1.4:8080/sts/getVideoUrl";
    // 当前 STS API 版本
    public static final String STS_API_VERSION = "2015-04-01";

    private static AssumeRoleResponse assumeRole(String accessKeyId, String accessKeySecret,
                                                 String roleArn, String roleSessionName, String policy,
                                                 ProtocolType protocolType) throws ClientException {
        try {
            // 创建一个 Aliyun Acs Client, 用于发起 OpenAPI 请求
            IClientProfile profile = DefaultProfile.getProfile(REGION_CN_HANGZHOU, accessKeyId, accessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);


            // 创建一个 AssumeRoleRequest 并设置请求参数

            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setVersion(STS_API_VERSION);
            request.setMethod(MethodType.POST);
            request.setProtocol(protocolType);
            request.setDurationSeconds(3600L);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy);

            // 发起请求，并得到response
            final AssumeRoleResponse response = client.getAcsResponse(request);

            return response;
        } catch (ClientException e) {
            throw e;
        }
    }

    public static boolean requestStsAuth() {
        // 只有 RAM用户（子账号）才能调用 AssumeRole 接口
        // 阿里云主账号的AccessKeys不能用于发起AssumeRole请求
        // 请首先在RAM控制台创建一个RAM用户，并为这个用户创建AccessKeys
        //TODO 此处需替换真实accessKeyId
        String accessKeyId = "LTAIFKMgbVKNitRb";
        //TODO 此处需替换真实accessKeySecret
        String accessKeySecret = "PX6ptwCeJ4rtv1iZALCi4DEzuX9GJ1";
        //TODO 此处需替换真实roleArn
        String roleArn = "acs:ram::1207688914473752:role/rolevidioplayer";

        // RoleSessionName 是临时Token的会话名称，自己指定用于标识你的用户，主要用于审计，或者用于区分Token颁发给谁
        // 但是注意RoleSessionName的长度和规则，不要有空格，只能有'-' '.' '@' 字母和数字等字符
        // 具体规则请参考API文档中的格式要求
        String roleSessionName = "AliSafePlayer";

        String policy = null;

        // 此处必须为 HTTPS
        ProtocolType protocolType = ProtocolType.HTTPS;
        AssumeRoleResponse response = null;
        try {
            response = assumeRole(accessKeyId, accessKeySecret,
                    roleArn, roleSessionName, policy, protocolType);

            sAccessKeyId = response.getCredentials().getAccessKeyId();
            sAccessKeySecret = response.getCredentials().getAccessKeySecret();
            sSecurityToken = response.getCredentials().getSecurityToken();

            System.out.println("Expiration: " + response.getCredentials().getExpiration());
            System.out.println("Access Key Id: " + sAccessKeyId);
            System.out.println("Access Key Secret: " + sAccessKeySecret);
            System.out.println("Security Token: " + sSecurityToken);
        } catch (ClientException e) {
            System.out.println("Failed to get a token.");
            System.out.println("Error code: " + e.getErrCode());
            System.out.println("Error message: " + e.getErrMsg());
            return false;
        }
        return true;
    }

    public  static   boolean  getStstoken() {

        String url = LOCAL_HOSTSERVER_IP;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(url).get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(
                new Callback() {
            @Override public void onFailure(Call call, IOException e)
            {e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
               // Log.d(TAG, "onResponse: " + response.body().string());
                String  responsebody=response.body().string();
                Map strmap=(Map) JSON.parse(responsebody);
                sAccessKeyId = strmap.get("accessKeyId").toString();
                sAccessKeySecret =strmap.get("accessKeySecret").toString();
                sSecurityToken =strmap.get("securityToken").toString();

                Log.d(TAG, "JSON:  : " + sAccessKeyId);

            } });
           return true;

    }



    public  static   String  getPlayUrl() {

        String url = LOCAL_HOSTSERVER_URL;

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(url).get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(
                new Callback() {
                    @Override public void onFailure(Call call, IOException e)
                    {e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException
                    {
                        // Log.d(TAG, "onResponse: " + response.body().string());
                        String  responsebody=response.body().string();
                        Map strmap=(Map) JSON.parse(responsebody);
                        playurl = strmap.get("playURL").toString();
                        Log.d(TAG, "JSON:  : " + sAccessKeyId);

                    } });
        return "";

    }


    public  static   boolean  getStstokenAscy() {

        String  url = LOCAL_HOSTSERVER_IP;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            String  responsebody=response.body().string();
            Map strmap=(Map) JSON.parse(responsebody);
            sAccessKeyId = strmap.get("accessKeyId").toString();
            sAccessKeySecret =strmap.get("accessKeySecret").toString();
            sSecurityToken =strmap.get("securityToken").toString();
            return true;
        }   catch (Exception e) {
        e.printStackTrace();
        }
        return false;


    }


    public static String playurl;
    public static String sAccessKeyId;
    public static String sAccessKeySecret;
    public static String sSecurityToken;

}
