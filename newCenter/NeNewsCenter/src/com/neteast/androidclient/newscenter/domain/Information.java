
package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.provider.InformationColumns;
import com.neteast.androidclient.newscenter.utils.ImageUtil;
import com.neteast.androidclient.newscenter.utils.JsonHelper;
import com.neteast.androidclient.newscenter.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Information {
    
    /** 系统消息 */
    public static final int SYS_INFO = 1;
    /** 应用消息 */
    public static final int APP_INFO = 2;
    /** 互动消息 */
    public static final int USER_INFO = 3;
    /**
     * 将Cursor所指的该行数据库记录解析成Information实体
     * @param cursor
     * @return
     */
    public static Information parseCursor(Cursor cursor) {
        int type = cursor.getInt(cursor.getColumnIndex(InformationColumns.INFO_TYPE_ID));
        switch (type) {
            case Information.SYS_INFO:
                return new SysInformation(cursor);
            case Information.APP_INFO:
                return new AppInformation(cursor);
            case Information.USER_INFO:
                return new UserInformation(cursor);
            default:
                LogUtil.printLog("type " + type + "不合法");
                return null;
        }
    }
    /**
     * 将Json字符串解析成Information实体
     * @param json
     * @return
     * @throws JSONException
     */
    public static Information parseJson(String json) throws JSONException {
        JSONObject jobj = new JSONObject(json);
        int type = JsonHelper.readInt(jobj, "Info_type_id");
        switch (type) {
            case Information.SYS_INFO:
                return new SysInformation(jobj);
            case Information.APP_INFO:
                return new AppInformation(jobj);
            case Information.USER_INFO:
                return new UserInformation(jobj);
            default:
                LogUtil.printLog("type " + type + "不合法");
                return null;
        }
    }
    /**
     * 从数据库中查询当前用户，所拥有的某种类型的全部消息
     * @param context
     * @param infoType
     * @return
     */
    public static Cursor queryInformation(Context context, int infoType) {
        StringBuilder where = new StringBuilder();
        where.append(InformationColumns.INFO_TYPE_ID).append("=?");
        if (infoType != SYS_INFO) { // 系统广播，所有用户共享
            where.append(" AND ").append(InformationColumns.USERID).append("=?");
        }
        String[] whereArgs = null;
        if (infoType == SYS_INFO) {
            whereArgs = new String[] {
                String.valueOf(infoType)
            };
        } else {
            whereArgs = new String[] {
                    String.valueOf(infoType),
                    String.valueOf(CloudAccount.getInstance(context).getUserId())
            };
        }
        Cursor cursor = context.getContentResolver().query(InformationColumns.CONTENT_URI, null,
                where.toString(), whereArgs, InformationColumns.DEFAULT_SORT_ORDER);
        return cursor;
    }
    /**
     * 删除当前用户某种类型的所有消息
     * @param context
     * @param infoType
     */
    public static void deleteAllInformation(Context context, int infoType) {
        context.getContentResolver().delete(
                InformationColumns.CONTENT_URI,
                InformationColumns.INFO_TYPE_ID + "=? AND " + InformationColumns.USERID + "=?",
                new String[] {
                        String.valueOf(infoType),
                        String.valueOf(CloudAccount.getInstance(context).getUserId())
                });
    }
    
    protected Information(Cursor cursor) {
        this.infoId = cursor.getLong(cursor.getColumnIndex(InformationColumns.INFO_ID));
        this.sendTime = cursor.getLong(cursor.getColumnIndex(InformationColumns.SEND_TIME));
        this.limitTime = cursor.getLong(cursor.getColumnIndex(InformationColumns.LIMIT_TIME));
        this.fromUserId = cursor.getLong(cursor.getColumnIndex(InformationColumns.FROM_USER_ID));
        this.fromUserName = cursor.getString(cursor.getColumnIndex(InformationColumns.FROM_USER_NAME));
        this.applicationName = cursor.getString(cursor.getColumnIndex(InformationColumns.APPLICATION_NAME));
        this.textContent = cursor.getString(cursor.getColumnIndex(InformationColumns.TEXTCONTENT));
        this.url = cursor.getString(cursor.getColumnIndex(InformationColumns.URL));
        this.picture = cursor.getString(cursor.getColumnIndex(InformationColumns.PICTURE));

        this.applicationId = cursor.getInt(cursor.getColumnIndex(InformationColumns.APPLICATION_ID));
        this.interactiveInfoType = cursor.getInt(cursor.getColumnIndex(InformationColumns.INTERACTIVE_INFO_TYPE));
        this.actionCode = getActionCode();
    }

    protected Information(JSONObject jobj) {
        this.infoId = JsonHelper.readLong(jobj, "Info_id");
        this.sendTime = JsonHelper.readLong(jobj, "Send_time") * 1000;
        this.limitTime = JsonHelper.readLong(jobj, "Limit_time") * 1000;
        this.fromUserId = JsonHelper.readLong(jobj, "From_User_id");
        this.fromUserName = JsonHelper.readString(jobj, "From_User_name");
        this.applicationName = JsonHelper.readString(jobj, "Application_name");
        String textcontent = "";
        try {
            textcontent = URLDecoder.decode(JsonHelper.readString(jobj, "TextContent"), "UTF-8");
        } catch (Exception e) {
            LogUtil.e("Information(142)--->" + e.getMessage());
        }
        this.textContent = textcontent;
        this.url = JsonHelper.readString(jobj, "Url");
        this.picture = JsonHelper.readString(jobj, "Picture");

        this.applicationId = JsonHelper.readInt(jobj, "Application_id");
        this.interactiveInfoType = JsonHelper.readInt(jobj, "Interactive_info_type");
        this.actionCode = getActionCode();
    }
    
    protected long infoId, fromUserId;
    protected long sendTime, limitTime;
    protected String fromUserName, applicationName, textContent, url, picture;
    protected int actionCode, applicationId, interactiveInfoType;


    /**
     * 得到对应的动作编码
     * 
     * @return
     */
    protected abstract int getActionCode();

    /**
     * 格式化时间，输入时间戳长度，返回"于2012-12-21"这种形式的字符串
     * 
     * @param milliseconds
     * @return
     */
    protected static String formatDate(long milliseconds) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(milliseconds));
        return "于" + date;
    }

    public long getInfoId() {
        return infoId;
    }

    /**
     * 消息标题
     * 
     * @return
     */
    public abstract String getTitle();

    /**
     * 发送时间
     * 
     * @return
     */
    public String getSendTime() {
        return formatDate(sendTime);
    }

    /**
     * 得到图片的链接地址
     * 
     * @return
     */
    public String getPicture() {
        return picture;
    }

    /**
     * 是否应该为文字着色
     * 
     * @return
     */
    public boolean needDrawTextColor() {
        return true;
    }

    /**
     * 该条消息是否可以被编辑
     * 
     * @return
     */
    public boolean editable() {
        return true;
    }

    /**
     * 得到文字内容
     * 
     * @return
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * 删除消息
     * 
     * @param context
     */
    public void delete(Context context) {
        ContentResolver resolver = context.getContentResolver();
        String id = String.valueOf(infoId);
        resolver.delete(InformationColumns.CONTENT_URI, InformationColumns.INFO_ID + "=?",
                new String[] {
                    id
                });
        ImageUtil.getInstance().deleteImage(picture);
    }

    /**
     * 判断消息是否过期，若当前时间戳大于消息的limitTime的话，那么消息过期
     * 
     * @return
     */
    public boolean isTimeout() {
        return System.currentTimeMillis() - limitTime > 0;
    }

    /**
     * 执行点击操作
     * 
     * @param context
     */
    public void executeAction(Context context) {
        new InfoAction(context, actionCode).run();
    }

    protected abstract int getInfoType();

    /**
     * 发送广播，告知UI界面有新的消息
     * 
     * @param context
     */
    public abstract void notifyHasNewInfo(Context context);

    /**
     * 将消息存储到数据库
     * 
     * @param context
     * @param values
     */
    public void saveToDataBase(Context context) {
        ContentValues values = new ContentValues();
        values.put(InformationColumns.INFO_ID, infoId);
        values.put(InformationColumns.SEND_TIME, sendTime);
        values.put(InformationColumns.LIMIT_TIME, limitTime);
        values.put(InformationColumns.INFO_TYPE_ID, getInfoType());
        values.put(InformationColumns.FROM_USER_ID, fromUserId);
        values.put(InformationColumns.FROM_USER_NAME, fromUserName);
        values.put(InformationColumns.APPLICATION_ID, applicationId);
        values.put(InformationColumns.APPLICATION_NAME, applicationName);
        values.put(InformationColumns.INTERACTIVE_INFO_TYPE, interactiveInfoType);
        values.put(InformationColumns.TEXTCONTENT, textContent);
        values.put(InformationColumns.URL, url);
        values.put(InformationColumns.PICTURE, picture);
        values.put(InformationColumns.USERID, CloudAccount.getInstance(context).getUserId());

        ContentResolver resolver = context.getContentResolver();
        final Uri uri = InformationColumns.CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, InformationColumns.INFO_ID + "=?", new String[] {
            String.valueOf(infoId)
        }, null);
        if (cursor.moveToFirst()) {
            resolver.update(uri, values, InformationColumns.INFO_ID + "=?", new String[] {
                String.valueOf(infoId)
            });
        } else {
            resolver.insert(uri, values);
        }
        cursor.close();
    }

    @Override
    public String toString() {
        return getTitle() + "，编号为：" + infoId + "的消息，" + "内容是：" + getTextContent() + "发送自"
                + getSendTime();
    }

    class InfoAction implements Runnable {
        /** 跳转到web浏览器 */
        protected static final int ACTION_WEB = 9999;

        /** android云盘，分享任务 */
        protected static final int ACTION_ACLOUD_DISK_SHARE = 18;

        /** pc云盘，分享任务 */
        protected static final int ACTION_PCLOUD_DISK_SHARE = 26;

        /** 应用反馈，跳转任务 */
        protected static final int ACTION_FEEDBACK = 19;

        /** android视频天下，分享任务 */
        protected static final int ACTION_AVIDEO_SHARE = 20;

        /** pc视频天下，分享任务 */
        protected static final int ACTION_PVIDEO_SHARE = 21;

        /** 视频天下，转码任务 */
        protected static final int ACTION_VIDEO_TRANSCODE = 5;

        /** 视频天下，离线任务 */
        protected static final int ACTION_VIDEO_OFFLINE = 6;

        /** 应用仓库，分享任务 */
        protected static final int ACTION_APPSTORE_SHARE = 25;

        /** 好友录，请求成为好友任务 */
        protected static final int ACTION_REQUEST_BECOME_FRIEND = 17001;

        /** 好友录，同意成为好友任务 */
        protected static final int ACTION_AGREE_BECOME_FRIEND = 17002;

        /** 好友录，拒绝成为好友任务 */
        protected static final int ACTION_DENY_BECOME_FRIEND = 17003;

        private Context mContext;

        private int mActionCode;

        public InfoAction(Context context, int actionCode) {
            mContext = context;
            mActionCode = actionCode;
        }

        @Override
        public void run() {
            switch (mActionCode) {
                case ACTION_WEB:
                    jumpWeb();
                    break;
                case ACTION_ACLOUD_DISK_SHARE:
                case ACTION_PCLOUD_DISK_SHARE:
                    cloudDisk();
                    break;
                case ACTION_FEEDBACK:
                    feedback();
                    break;
                case ACTION_AVIDEO_SHARE:
                case ACTION_PVIDEO_SHARE:
                    videoShare();
                    break;
                case ACTION_VIDEO_TRANSCODE:
                    translateCode();
                    break;
                case ACTION_VIDEO_OFFLINE:
                    offlineDownload();
                    break;
                case ACTION_APPSTORE_SHARE:
                    appStore();
                    break;
                case ACTION_REQUEST_BECOME_FRIEND:
                    requestBecomeFriend();
                    break;
                case ACTION_AGREE_BECOME_FRIEND:
                    break;
                case ACTION_DENY_BECOME_FRIEND:
                    break;
                default:
                    break;
            }
        }

        private void requestBecomeFriend() {
            Intent intent = new Intent("com.neteast.action.responsecloud");
            intent.putExtra("userid", (int) fromUserId);
            intent.putExtra("username", fromUserName);
            intent.putExtra("photo", picture);
            intent.putExtra("verify", url);
            mContext.startActivity(intent);
        }

        private void jumpWeb() {
            if (TextUtils.isEmpty(url) || !url.startsWith("http://")) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }

        private void videoShare() {
            if (TextUtils.isEmpty(url)) {
                return;
            }

            Intent intent = new Intent("com.neteast.video.share");
            intent.putExtra("movieid", url);
            mContext.startActivity(intent);
        }

        private void translateCode() {
            if (TextUtils.isEmpty(url)) {
                return;
            }

            Intent intent = new Intent("com.neteast.video.transcoding");
            intent.putExtra("url", url);
            mContext.startActivity(intent);
        }

        private void offlineDownload() {
            Intent intent = new Intent("com.neteast.video.offlinedownload");
            mContext.startActivity(intent);
        }

        private void appStore() {
            if (TextUtils.isEmpty(url)) {
                return;
            }

            Intent intent = new Intent("com.neteast.appstore.activity.share");
            intent.putExtra("movieid", url);
            mContext.startActivity(intent);
        }

        private void feedback() {

            if (TextUtils.isEmpty(url)) {
                return;
            }

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.wasu.feedback",
                    "com.wasu.feedback.FeedbackActivity"));
            intent.putExtra("infoid", url);
            mContext.startActivity(intent);
        }

        private void cloudDisk() {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            Intent intent = new Intent();
            intent.setAction("com.neteast.clouddisk.activity.share");
            String[] args = url.split(";");
            if (args.length > 1) {
                intent.putExtra("movieid", args[0]);
                intent.putExtra("url", args[1]);
            }
            intent.putExtra("picUrl", picture);
            mContext.startActivity(intent);
        }
    }
}
