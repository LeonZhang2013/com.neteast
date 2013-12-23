package com.neteast.androidclient.newscenter.activity;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.adapter.SelectedContactAdapter;
import com.neteast.androidclient.newscenter.domain.CloudAccount;
import com.neteast.androidclient.newscenter.domain.Contact;
import com.neteast.androidclient.newscenter.domain.SNSEntity;
import com.neteast.androidclient.newscenter.domain.SNSEntity.AuthListener;
import com.neteast.androidclient.newscenter.domain.SNSEntity.PublishListener;
import com.neteast.androidclient.newscenter.domain.SNSEntity.SnsType;
import com.neteast.androidclient.newscenter.exception.AuthorizeDialogError;
import com.neteast.androidclient.newscenter.exception.AuthorizeException;
import com.neteast.androidclient.newscenter.exception.ShareArgumentsException;
import com.neteast.androidclient.newscenter.exception.SnsPublishException;
import com.neteast.androidclient.newscenter.fragment.ShareCloudsFragment;
import com.neteast.androidclient.newscenter.fragment.ShareCloudsFragment.ContactStateObserver;
import com.neteast.androidclient.newscenter.utils.JsonHelper;
import com.neteast.androidclient.newscenter.utils.LogUtil;
import com.neteast.androidclient.newscenter.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShareActivity extends FragmentActivity implements OnClickListener,Observer {
    
    private static final int MAX_INPUT_WORDS = 80;
    private static final String INPUT_PROMPT = "你还可以输入<font color='#f7812b'>%1$s</font>字";
    
    private Activity mContext;
    private CheckBox mQQ;
    private CheckBox mSina;
    private CheckBox mRenRen;
    private CheckBox mKaiXin;
    private View mSnsBlock;
    private GridView mSelectedCloudView;
    private CheckBox mShowCloudPanel;
    private EditText mInput;
    private ShareInfo mShareInfo;
    private int mShareTaskCount;
    private ExecutorService mThreadPools;
    private ProgressDialog mLoading;
    private StringBuilder mShareMessage;
    private UIHandler mUIHandler;
    private ShareCloudsFragment mCloudsFragment;
    private ArrayList<Contact> mSelectedClouds;
    private SelectedContactAdapter  mSelectedAdapter;
    private File mImageFile;
    private ImageView mPhoto;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mContext = this;
        try {
            mShareInfo = new ShareInfo(getIntent().getExtras());
        } catch (ShareArgumentsException e) {
            Utils.showAlert(mContext, "分享数据错误！");
            mContext.finish();
            return;
        }
        initView();
        mThreadPools = Executors.newCachedThreadPool();
        mUIHandler = new UIHandler(this);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_qq:
            case R.id.share_sina:
            case R.id.share_renren:
            case R.id.share_kaixin:
                onClickSnsButton(v);
                break;
            case R.id.share_cloud:
                showCloudShareContent();
                break;
            case R.id.share_showCloudPanel:
                if (mShowCloudPanel.isChecked()) {
                    showCloudShareContent();
                }else {
                    showSnsShareContent();
                }
                break;
            case R.id.share_cancel:
                Utils.showExitConfirmDialog(mContext);
                break;
            case R.id.share_ok:
                share();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCloudsFragment.registerContactStateObserver(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mCloudsFragment.unRegisterContactStateObserver(this);
        
        if (isFinishing()) {
            mThreadPools.shutdown();
            
            if (mImageFile != null) {
                mImageFile.delete();
            }
        }
    }
    
    private void initView() {
        //隐藏云好友列表
        FragmentManager fm = getSupportFragmentManager();
        mCloudsFragment = (ShareCloudsFragment) fm.findFragmentByTag("cloudlist");
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(mCloudsFragment);
        ft.commit();
        
        //选中的云好友
        mSelectedCloudView = (GridView) findViewById(R.id.share_selected_cloud);
        mSelectedClouds = new ArrayList<Contact>();
        mSelectedAdapter = new SelectedContactAdapter(mContext, mSelectedClouds);
        mSelectedCloudView.setAdapter(mSelectedAdapter);
        mSelectedCloudView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact=(Contact) parent.getItemAtPosition(position);
                mCloudsFragment.onContactCheckedChanged(contact);
            }
        });
        
        //SNS平台包含框
        mSnsBlock = findViewById(R.id.share_sns_block);
        //各个SNS分享平台
        mQQ = (CheckBox) findViewById(R.id.share_qq);
        mQQ.setTag(SnsType.QQWeiBo);
        mQQ.setOnClickListener(this);
        
        mSina = (CheckBox) findViewById(R.id.share_sina);
        mSina.setTag(SnsType.SinaWeiBo);
        mSina.setOnClickListener(this);
        
        mRenRen = (CheckBox) findViewById(R.id.share_renren);
        mRenRen.setTag(SnsType.RenRen);
        mRenRen.setOnClickListener(this);
        
        mKaiXin = (CheckBox) findViewById(R.id.share_kaixin);
        mKaiXin.setTag(SnsType.KaiXin);
        mKaiXin.setOnClickListener(this);
        
        //分享给云好友按钮
        findViewById(R.id.share_cloud).setOnClickListener(this);
        //输入框和剩余多少字提示
        final  TextView inputPrompt = (TextView) findViewById(R.id.share_prompt);
        inputPrompt.setText(Html.fromHtml(String.format(INPUT_PROMPT, MAX_INPUT_WORDS)));
        mInput = (EditText) findViewById(R.id.share_input);
        mInput.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                int leftWords = MAX_INPUT_WORDS - value.length();
                if (leftWords < 0) {
                    Utils.showAlert(mContext, "已经超出字数限制");
                    value = value.substring(0, MAX_INPUT_WORDS);
                    mInput.setText(value);
                    leftWords = 0;
                }
                inputPrompt.setText(Html.fromHtml(String.format(INPUT_PROMPT, leftWords)));
            }
        });
        String defalutContent="#"+mShareInfo.title+"#";
        mInput.setText(defalutContent);
        mInput.setSelection(defalutContent.length());
        
        //显示右侧 云好友面板的按钮
        mShowCloudPanel = (CheckBox) findViewById(R.id.share_showCloudPanel);
        mShowCloudPanel.setOnClickListener(this);
        
        
        mPhoto = (ImageView) findViewById(R.id.share_photo);
        if (!TextUtils.isEmpty(mShareInfo.picture)) {
            downLoadImage();
        }
        TextView shareTitle = (TextView) findViewById(R.id.share_title);
        shareTitle.setText("标题："+mShareInfo.title);
        TextView shareDate = (TextView) findViewById(R.id.share_date);
        shareDate.setText("日期："+getCurrentDate());
        TextView shareFrom = (TextView) findViewById(R.id.share_from);
        shareFrom.setText("来源："+getAppNameById(mShareInfo.applicationid));
        
        findViewById(R.id.share_cancel).setOnClickListener(this);
        findViewById(R.id.share_ok).setOnClickListener(this);
    }
    
    
    private void downLoadImage() {
        new Thread(new Runnable() {
            public void run() {
                mImageFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+".png");
                InputStream in =null;
                FileOutputStream out = null;
                for(int i=0;i<3;i++){
                    try {
                        in = new URL(mShareInfo.picture).openStream();
                        out=new FileOutputStream(mImageFile);
                        Utils.downloadFile(in, out);
                        mUIHandler.sendEmptyMessage(UIHandler.IMAGE_DOWNLOAD);
                        break;
                    } catch (IOException e) {
                        LogUtil.e(e);
                        continue;
                    }
                }
                if (in!=null) {
                    try {
                        in.close();
                    } catch (IOException e) { }
                }
                if (out!=null) {
                    try {
                        out.close();
                    } catch (IOException e) { }
                }
            }
        }).start();
    }

    /**
     * 点击SNS平台的按钮</BR>
     * 当已经选择时，取消选择</BR>
     * 当没有选择时，选择分享，此时如果SNS授权过期，那么展示授权页面
     * @param snsType 分享平台的类型，SnsType枚举值
     */
    private void onClickSnsButton(View view) {
        final CheckBox checkBox=(CheckBox) view;
        SNSEntity snsEntity = SNSEntity.getEntity(mContext, (SnsType)checkBox.getTag());
        if (checkBox.isChecked() && !snsEntity.isSessionValid()) {
            snsEntity.authorize(getSupportFragmentManager(),new AuthListener() {
                @Override
                public void onError(AuthorizeDialogError e) {
                    Utils.showToast(mContext,  "Auth error : " + e.getMessage());
                    checkBox.setChecked(false);
                }
                @Override
                public void onComplete() {}
                @Override
                public void onCancel() {
                    Utils.showToast(mContext,  "Auth cancel");
                    checkBox.setChecked(false);
                }
                @Override
                public void onAuthorizeException(AuthorizeException e) {
                    Utils.showToast(mContext,  "Auth exception : " + e.getMessage());
                    checkBox.setChecked(false);
                }
            });
        }
    }
    
    /**
     * 显示分享给云好友时的界面
     */
    private void showCloudShareContent() {
        
        mSelectedCloudView.setVisibility(View.VISIBLE);
        mSnsBlock.setVisibility(View.GONE);
        mShowCloudPanel.setChecked(true);
        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(mCloudsFragment);
        ft.commit();
    }
    
    /**
     * 显示分享到SNS平台时的界面
     */
    private void showSnsShareContent() {
        mSelectedCloudView.setVisibility(View.GONE);
        mSnsBlock.setVisibility(View.VISIBLE);
        mShowCloudPanel.setChecked(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(mCloudsFragment);
        ft.commit();
    }
    
    /**
     * 根据ApplicationId得到对应的应用名，没有对应的"数据错误"提示字符串
     * @param appId
     * @return
     */
    private String getAppNameById(int appId) {
        switch (appId) {
            case 17:
                return "通讯录";
            case 18:
                return "云空间(安卓版)";
            case 26:
                return "云空间(PC版)";
            case 19:
                return "应用反馈";
            case 20:
                return "视频天下(安卓版)";
            case 21:
                return "视频天下(PC版)";
            case 25:
                return "应用仓库";
            default:
                return "数据错误，appId="+appId;
        }
    }
    /**
     * 得到当前时间的字符串，以“2012-12-21”格式显示
     * @return
     */
    private String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    /**
     * 分享
     */
    private void share() {
        String content = mInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Utils.showAlert(mContext, "分享内容不能为空!");
            return;
        }
        String filePath = mImageFile!=null?mImageFile.getAbsolutePath():null;
        if (mShowCloudPanel.isChecked()) {
            shareToCloud(content,filePath);
        }else {
            shareToSNS(content,filePath);
        }
    }
    /**
     * 分享到选中的SNS平台
     * @param filePath 
     * @param content 
     */
    private void shareToSNS(String content, String filePath) {
        mShareTaskCount=0;
        mShareMessage=new StringBuilder();
        //判断哪些分享平台被选中
        final CheckBox[] snsBoxs={mQQ,mSina,mKaiXin,mRenRen};
        for(int i=0,size=snsBoxs.length;i<size;i++){
            CheckBox snsBox=snsBoxs[i];
            if (snsBox.isChecked()) {
                mShareTaskCount++;
            }
        }
        if (mShareTaskCount==0) {
            Utils.showAlert(mContext, "至少要选择一个分享平台");
            return;
        }
        
        showLoading();
        
        //开始分享
        for(int i=0,size=snsBoxs.length;i<size;i++){
            CheckBox snsBox=snsBoxs[i];
            if (snsBox.isChecked()) {
                SnsType snsType=(SnsType) snsBox.getTag();
                mThreadPools.execute(new ShareSnsTask(snsType, content, filePath));
            }
        }
    }
    /**
     * 分享给选中的云好友
     * @param filePath 
     * @param content 
     */
    private void shareToCloud(String content, String filePath) {
        if (mSelectedClouds.isEmpty()) {
            Utils.showAlert(mContext, "至少选择一个云好友");
            return;
        }
        mShareTaskCount=1;
        mShareMessage=new StringBuilder();
        showLoading();
        mThreadPools.execute(new ShareCloudTask(mShareInfo.title+"\n"+content));
    }
    
    
    /**
     * 当分享任务完成时回调改方法，判断当前是否全部分享任务都已经完成。
     * 若完成，则展示分享的结果，并关闭分享对话框
     * @param callBackMessage
     */
   private void onShareTaskCallback(String callBackMessage) {
       LogUtil.i(callBackMessage);
       mShareMessage.append(callBackMessage).append("\n");
       mShareTaskCount--;
       if (mShareTaskCount==0) {
           hiddenLoading();
           Utils.showCancelDialog(mContext, mShareMessage.toString());
       }
   }
   
    private void onImageDownload() {
        if (mImageFile != null) {
            Bitmap bitmap=BitmapFactory.decodeFile(mImageFile.getAbsolutePath());
            mPhoto.setImageBitmap(bitmap);
        }
    }
   /**
    * 显示分享进度条
    */
   private void showLoading() {
       mLoading=ProgressDialog.show(mContext, "提示", "分享中，请等待...", true, false); 
   }
   /**
    * 隐藏分享进度条
    */
    private void hiddenLoading() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }
    /**
     * 分享信息类
     * @author emellend
     *
     */
    public static class ShareInfo {
        
        public ShareInfo(Bundle args) throws ShareArgumentsException {
            try {
                title = args.getString("title")==null?"":args.getString("title");
                url = args.getString("url")==null?"":args.getString("url");
                picture = args.getString("picture")==null?"":args.getString("picture");
                msglevel = args.getString("msglevel")==null?"":args.getString("msglevel");
                applicationid = args.getInt("applicationid");
            } catch (Exception e) {
                LogUtil.printException(e);
                throw new ShareArgumentsException();
            }
        }
        public final String title;
        public final String url;
        public final String picture;
        public final String msglevel;
        public final int applicationid;
        public final String infotype ="3";
    }
    
    public static class UIHandler extends Handler{
        

        /**当分享任务完成时回调，无论成功或是失败*/
        public static final int SHARE_TASK_CALLBACK=1;
        /**分享数据的图片下载完成*/
        public static final int IMAGE_DOWNLOAD = 2;
        
        private ShareActivity mActivity;
        
        public UIHandler(ShareActivity activity) {
            mActivity=activity;
        }
        
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHARE_TASK_CALLBACK:
                    mActivity.onShareTaskCallback((String)msg.obj);
                    break;
                case IMAGE_DOWNLOAD:
                    mActivity.onImageDownload();
                    break;
                default:
                    break;
            }
        }
    }
    
    
    /**
     * 执行分享给SNS平台的任务
     * @author emellend
     *
     */
    private class ShareSnsTask implements Runnable{
        SnsType mSnsType;
        String message;
        String filePath;
        
        public ShareSnsTask(SnsType snsType,String message,String filePath) {
            mSnsType=snsType;
            this.message=message;
            this.filePath=filePath;
        }
        
        @Override
        public void run() {
            if (TextUtils.isEmpty(filePath)) {
                doUpdate();
            }else {
                doUpload();
            }
        }
        /**
         * 分享带图片消息
         */
        private void doUpload() {
            try {
                SNSEntity.getEntity(mContext, mSnsType).publishMessageWithPicture(message,filePath, new PublishListener() {
                    @Override
                    public void onPublishComplete(String msg) {
                        mUIHandler.obtainMessage(UIHandler.SHARE_TASK_CALLBACK, msg).sendToTarget();
                    }
                });
            } catch (SnsPublishException e) {
                mUIHandler.obtainMessage(UIHandler.SHARE_TASK_CALLBACK, e.getMessage()).sendToTarget();
            }
        }
        /**
         * 分享不带图片的消息
         */
        private void doUpdate() {
            LogUtil.i("doUpdate");
            try {
                SNSEntity.getEntity(mContext, mSnsType).publishMessage(message, new PublishListener() {
                    @Override
                    public void onPublishComplete(String msg) {
                        mUIHandler.obtainMessage(UIHandler.SHARE_TASK_CALLBACK, msg).sendToTarget();
                    }
                });
            } catch (SnsPublishException e) {
                mUIHandler.obtainMessage(UIHandler.SHARE_TASK_CALLBACK, e.getMessage()).sendToTarget();
            }
        }
    }
    
    /**
     * 执行分享给云好友的任务
     */
    private class ShareCloudTask implements Runnable{
        private String content;
        
        public ShareCloudTask(String content) {
            this.content = content;
        }

        @Override
        public void run() {
            
            UrlEncodedFormEntity reqEntity = generateRequestParams();
            
            String path = ConfigManager.URL_USERCENTER+"/Message/addsglmsg";
            String json=null;
            try {
                json= Utils.doPost(path, reqEntity);
            } catch (IOException e) {
                LogUtil.printException(e);
                mUIHandler.obtainMessage(UIHandler.SHARE_TASK_CALLBACK, "网络异常，分享失败！").sendToTarget();
            }
            processServerResponse(json);
        }
 

        /**
         * 组拼生成请求服务器所需的参数
         * @return
         */
        private UrlEncodedFormEntity generateRequestParams() {
            StringBuilder userId=new StringBuilder();
            for (Contact contact : mSelectedClouds) {
                userId.append(contact.contactId).append(",");
            }
            userId.deleteCharAt(userId.length()-1);
            
            Map<String, String> params = new HashMap<String, String>();
            params.put("fromuserid", String.valueOf(CloudAccount.getInstance(mContext).getUserId()));
            params.put("content", content);
            params.put("msglevel", mShareInfo.msglevel);
            params.put("applicationid",  String.valueOf(mShareInfo.applicationid));
            params.put("url", mShareInfo.url);
            params.put("picture", mShareInfo.picture);
            params.put("infotype", mShareInfo.infotype);
            params.put("userid", userId.toString());
            
            String data = Utils.encoderUrl(params);
            String verify = Utils.md5(data + ConfigManager.KEY);
            String reqstr = Utils.base64(data);
            
            ArrayList<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            BasicNameValuePair valuePair = new BasicNameValuePair("appcode", ConfigManager.APPCODE);
            valuePairs.add(valuePair);
            valuePair = new BasicNameValuePair("verify", verify);
            valuePairs.add(valuePair);
            valuePair = new BasicNameValuePair("reqstr", reqstr);
            valuePairs.add(valuePair);
            valuePair = new BasicNameValuePair("datatype", "j");
            valuePairs.add(valuePair);
            
            UrlEncodedFormEntity reqEntity=null;
            try {
                reqEntity= new UrlEncodedFormEntity(valuePairs, HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {}//不可能发送，必然支持
            return reqEntity;
        }
        
        /**
         * 处理服务器返回的json
         * @param json
         */
        private void processServerResponse(String json) {
            JSONObject jobj=null;
            try {
                jobj=new JSONObject(json);
            } catch (JSONException e) {
                LogUtil.printException(e);
                mUIHandler.obtainMessage(UIHandler.SHARE_TASK_CALLBACK, "分享失败，服务器返回JSON异常！").sendToTarget();
                return;
            }
            /**
             * 注：服务器返回的description是一个包含了所有成功和失败的用户id字符串
             * 格式为 1,2,3,4;5,6,7  
             * 用 ';'分隔为两部分，之前的为成功的用户id，后面的为失败的用户id
             * 若没有成功或者失败部分， ';'依然存在
             * 用户id 之间用 ','来分隔
             */
            String description = JsonHelper.readString(jobj, "description").trim();
            String shareSuccessUserId = getShareSuccessUserId(description);
            String shareErrorUserId = getShareErrorUserId(description);
            
            StringBuilder message=new StringBuilder();
            message.append("分享成功：").append("\n").append(parseUserArray(shareSuccessUserId));
            message.append("\n分享失败：").append("\n").append(parseUserArray(shareErrorUserId));
            
            mUIHandler.obtainMessage(UIHandler.SHARE_TASK_CALLBACK, message.toString()).sendToTarget();
        }
        
        /**
         * 解析用户id字符串，返回用户可理解的格式化文字信息
         * @return
         */
        private String parseUserArray(String userIdArray) {
            
            if (TextUtils.isEmpty(userIdArray)) {
                return "";
            }
            
            if (isSingleUser(userIdArray)) {
                return getUserNameById(userIdArray);
            }
            
            String[] ids = userIdArray.split(",");
            StringBuilder result=new StringBuilder();
            
            for(int i=0,size=ids.length;i<size;i++){
                result.append(getUserNameById(ids[i]));
                if(i%3==0){
                    result.append("\n");
                }
            }
            return result.toString();
        }
        /**
         * 根据用户id得到用户名称
         * @param userId
         * @return
         */
        private String getUserNameById(String userId) {
            int id=Integer.parseInt(userId);
            String name="";
            for (Contact contact : mSelectedClouds) {
                if (contact.contactId==id) {
                    name=contact.displayName;
                    break;
                }
            }
            return name;
        }
        
        /**
         * 返回的是单个用户
         * @return
         */
        private boolean isSingleUser(String userIdArray) {
            return !userIdArray.contains(",");
        }
        /**
         * 得到分享失败的用户id字符串，多个id用","分隔
         * @param description
         * @return
         */
        private String getShareErrorUserId(String description) {
            if (allError(description)) {
                return description.substring(1);
            }else {
                String[] split = description.split(";");
                if (split.length>1) {
                    return split[1];
                }
                return "";
            }
        }
        /**
         * 得到分享成功的用户id字符串，多个id用","分隔
         * @param description
         * @return
         */
        private String getShareSuccessUserId(String description) {
            if (allSuccess(description)) {
                return description.substring(0,description.length()-1);
            }else {
                return description.split(";")[0];
            }
        }
        
        /**
         * 云好友分享全部失败
         * @param description
         * @return
         */
        private boolean allError(String description) {
            return description.startsWith(";");
        }

        /**
         * 云好友分享全部成功
         * @param description
         * @return
         */
        private boolean allSuccess(String description) {
            return description.endsWith(";");
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        ContactStateObserver observer=(ContactStateObserver) observable;
        ArrayList<Contact> allCheckedContacts = observer.getAllCheckedContacts();
        mSelectedClouds.clear();
        mSelectedClouds.addAll(allCheckedContacts);
        mSelectedAdapter.notifyDataSetChanged();
    }
    
}
