package com.example.playerlibrary.AlivcLiveRoom;

import android.os.AsyncTask;

import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.utils.HttpClientUtil;

import org.json.JSONObject;

/**
 * Created by pengshuang on 31/08/2017.
 */
public class VidStsUtil {


    private static final String TAG = VidStsUtil.class.getSimpleName();

    public static AliyunVidSts getVidSts(String videoId) {

                try {
                    String response = HttpClientUtil.doGet("https://demo-vod.cn-shanghai.aliyuncs.com/voddemo/CreateSecurityToken?BusinessType=vodai&TerminalType=pc&DeviceModel=iPhone9,2&UUID=59ECA-4193-4695-94DD-7E1247288&AppVersion=1.0.0&VideoId=" + videoId);

                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject securityTokenInfo = jsonObject.getJSONObject("SecurityTokenInfo");
                    if (securityTokenInfo == null) {

                        VcPlayerLog.e(TAG, "SecurityTokenInfo == null ");
                        return null;
                    }

                    String accessKeyId = securityTokenInfo.getString("AccessKeyId");
                    String accessKeySecret = securityTokenInfo.getString("AccessKeySecret");
                    String securityToken = securityTokenInfo.getString("SecurityToken");
                    VcPlayerLog.e(TAG, "accessKeyId = " + accessKeyId + " , accessKeySecret = " + accessKeySecret +
                            " , securityToken = " + securityToken);

                    AliyunVidSts vidSts = new AliyunVidSts();
                    vidSts.setVid(videoId);
                    vidSts.setAcId(accessKeyId);
                    vidSts.setAkSceret(accessKeySecret);
                    vidSts.setSecurityToken(securityToken);
                    return vidSts;

                } catch (Exception e) {
                    VcPlayerLog.e(TAG, "e = " + e.getMessage());
                    return null;
                }



    }

    public interface OnStsResultListener {
        void onSuccess(String vid, String akid, String akSecret, String token);

        void onFail();
    }

    public static void getVidSts(final String vid, final OnStsResultListener onStsResultListener) {
        AsyncTask<Void, Void, AliyunVidSts> asyncTask = new AsyncTask<Void, Void, AliyunVidSts>() {

            @Override
            protected AliyunVidSts doInBackground(Void... params) {
                return getVidSts(vid);
            }

            @Override
            protected void onPostExecute(AliyunVidSts s) {
                if (s == null) {
                    onStsResultListener.onFail();
                } else {
                    onStsResultListener.onSuccess(s.getVid(),s.getAcId(), s.getAkSceret(), s.getSecurityToken());
                }
            }
        };
        asyncTask.execute();

        return;
    }


}
