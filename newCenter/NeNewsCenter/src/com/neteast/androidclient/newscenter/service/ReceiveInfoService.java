package com.neteast.androidclient.newscenter.service;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.domain.CloudAccount;
import com.neteast.androidclient.newscenter.domain.Information;
import com.neteast.androidclient.newscenter.domain.Packet;
import com.neteast.androidclient.newscenter.domain.PacketBootStrap;
import com.neteast.androidclient.newscenter.domain.PacketBootStrapError;
import com.neteast.androidclient.newscenter.domain.PacketBootStrapSuccess;
import com.neteast.androidclient.newscenter.domain.PacketInfoData;
import com.neteast.androidclient.newscenter.domain.PacketInfoDataReceived;
import com.neteast.androidclient.newscenter.domain.PacketKeepLive;
import com.neteast.androidclient.newscenter.domain.PacketLogin;
import com.neteast.androidclient.newscenter.domain.PacketLoginError;
import com.neteast.androidclient.newscenter.domain.PacketLoginSuccess;
import com.neteast.androidclient.newscenter.domain.PacketLogout;
import com.neteast.androidclient.newscenter.exception.PacketDamageException;
import com.neteast.androidclient.newscenter.provider.CloudAccountColumns;
import com.neteast.androidclient.newscenter.receiver.AlarmReceiver;
import com.neteast.androidclient.newscenter.utils.ImageUtil;
import com.neteast.androidclient.newscenter.utils.LogUtil;

import org.json.JSONException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReceiveInfoService extends Service {
    
    /**请求bootstrap*/
    private static final int MSG_BOOTSTRAP = 1;
    /**请求bootstrap服务器成功*/
    private static final int MSG_BOOTSTRAP_SUCCESS = 2;
    /**请求登录*/
    private static final int MSG_LOGIN = 3;
    /**请求消息推送服务器成功*/
    private static final int MSG_LOGIN_SUCCESS = 4;
    /**有新消息*/
    private static final int MSG_HAS_NEW_INFO = 5;
    /**下载图片*/
    private static final int MSG_DOWNLOAD_IMAGE = 6;
    
    protected static final int TIME_OUT = 60*1000;
    protected static final int DEFAULT_BUFFER_SIZE = 65507;
    
    
    private Context mContext;
    private UIHandler mUIHandler;
    
    /**服务是否运行*/
    private boolean run;
    /**监听用户登录的广播接收者*/
    private BroadcastReceiver mLoginChangeReceiver;
    /**执行网络操作的线程池*/
    private ExecutorService mThreadPools;
    /**请求BS任务*/
    private BootStrapTask mBootStrapTask;
    /**登录任务*/
    private LoginTask mLoginTask;
    /**监听服务器任务*/
    private ListeningTask mListeningTask;
    /**电源管理，此处使用它让本服务在黑屏时依然运行*/
    private WakeLock mWakeLock;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //初始化UIHandler
        mUIHandler = new UIHandler(this);
        //初始化用户实体
        CloudAccount.getInstance(mContext);
        //初始化线程池
        mThreadPools =Executors.newFixedThreadPool(2);
        //注册用户状态改变的监听器
        registerUserChangeListener();
        //确保在黑屏情况下，本服务依然运行
        acquireWakeLock();
        //访问BS服务器
        mUIHandler.sendEmptyMessage(MSG_BOOTSTRAP);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }
    
    /**
     * 注册用户状态改变的监听器
     */
    private void registerUserChangeListener() {
        mLoginChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ConfigManager.ACTION_LOGIN_CHANGED.equals(intent.getAction())) {
                    LogUtil.i("received");
                    changeUser(intent.getExtras());
                }
            }
        };
        mContext.registerReceiver(mLoginChangeReceiver, new IntentFilter(ConfigManager.ACTION_LOGIN_CHANGED));
    }
    /**
     * 在黑屏的情况下，服务依然运行
     */
    private void acquireWakeLock() {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            mWakeLock.acquire();
        }
    }
    /**
     * 释放WakeLock
     */
    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
    /**
     * 向BootStrap服务器发送报文，请求获得真正的消息推送服务器地址
     */
    private void getMessagePushServer() {
        if (null==mBootStrapTask) {
            mBootStrapTask = new BootStrapTask();
        }
        mThreadPools.execute(mBootStrapTask);
    }
    
    /**
     * 像消息推送服务器发送登录报文
     * @param infoPushServer
     */
    private void login() {
        if (null!=mLoginTask) {
            mThreadPools.execute(mLoginTask);
        }else {
            //若mLoginTask为空，则意味着没有得到BS服务器正确的反馈
            //那么，将重新走BS服务器流程
            reTakeProcess();
        }
    }
    /**
     * 打开消息推送的通道，监听通道
     */
    private void startListening(){
        run=true;
        if (mListeningTask!=null) {
            mThreadPools.execute(mListeningTask);
        }else {
            reTakeProcess();
        }
    }
    
    /**
     * 当前用户发送退出报文，新用户重走流程</BR>
     * 若newUserArgs==null，则认为没有新用户登录，会在当前用户退出后，由Guest用户登录
     */
    private void changeUser(Bundle newUserArgs) {
        if (mListeningTask!=null&&isRun()) {
          mListeningTask.changeUser(newUserArgs);
        }else {
            refreshLoginUser(newUserArgs);
            reTakeProcess();
        }
    }
    /**
     * 服务是否在运行
     * @return
     */
    public boolean isRun() {
        return run;
    }
    
    /**
     * 下载图片
     * @param path
     */
    public void downLoadImage(final String path) {
        
        if (mThreadPools==null||mThreadPools.isShutdown()) {
            return;
        }
        
        mThreadPools.execute(new Runnable() {
            @Override
            public void run() {
                ImageUtil.getInstance().downloadImageFile(path);
            }
        });
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        run=false;
        mUIHandler.removeMessages(MSG_BOOTSTRAP);
        mUIHandler.removeMessages(MSG_LOGIN);
        
        mThreadPools.shutdown();
        mThreadPools=null;
        
        mContext.unregisterReceiver(mLoginChangeReceiver);
        CloudAccount.getInstance(mContext).release();
        //可能还有些没有得到处理的数据包，此时应该被清除
        PacketInfoData.clearCache();
        releaseWakeLock();
    }
    
    private static class UIHandler extends Handler{
        ReceiveInfoService mService;

        public UIHandler(ReceiveInfoService service) {
            this.mService = service;
        }
        
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BOOTSTRAP:
                    mService.getMessagePushServer();
                    break;
                case MSG_BOOTSTRAP_SUCCESS:
                    mService.login();
                    break;
                case MSG_LOGIN:
                    mService.login();
                    break;
                case MSG_LOGIN_SUCCESS:
                    mService.startListening();
                    break;
                case MSG_HAS_NEW_INFO:
                    Information info=(Information) msg.obj;
                    info.notifyHasNewInfo(mService.mContext);
                    break;
                case MSG_DOWNLOAD_IMAGE:
                    mService.downLoadImage((String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * 当不可恢复的错误发生时，终止服务
     * @param description
     */
    private void onTerribleException(String description){
        LogUtil.printLog("发生严重错误："+description+"\n，服务将于10分钟后重新启动。");
        launchServiceLater();
        stopSelf();
    }
    
    /**
     * 10分钟后再次启动本服务
     */
    private void launchServiceLater() {
        AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(mContext,AlarmReceiver.class);
        intent.putExtra("action", ConfigManager.ACTION_RECEIVE_INFO_SERVICE);
        PendingIntent pendIntent = PendingIntent.getBroadcast(mContext,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
        long triggerAtTime =  System.currentTimeMillis()+10*60*1000;
        alarmMgr.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pendIntent);
    }

    /**
     * 当流程出错，或者服务器要求时，重新开始整个流程
     */
    private void reTakeProcess() {
        run=false;
        mLoginTask=null;
        mListeningTask=null;
        mUIHandler.sendEmptyMessage(MSG_BOOTSTRAP);
    }
    
    private class BootStrapTask implements Runnable{
        @Override
        public void run() {
            DatagramSocket client=null;
            try {
                client=new DatagramSocket();
                client.connect(new InetSocketAddress(ConfigManager.BOOTSTRAP_SERVER_IP, ConfigManager.BOOTSTRAP_SERVER_PORT));
                client.setSoTimeout(TIME_OUT);
            } catch (SocketException e) {
                onTerribleException("BS服务器异常");
                return;
            }
            try {
                PacketBootStrap packetBootStrap = new PacketBootStrap(mContext);
                LogUtil.i(packetBootStrap.toString());
                byte[] bootStrap = packetBootStrap.getPacketData();
                DatagramPacket requestPacket=new DatagramPacket(bootStrap, 0, bootStrap.length);
                client.send(requestPacket);
                byte[] receiveData=new byte[DEFAULT_BUFFER_SIZE];
                DatagramPacket receivePacket=new DatagramPacket(receiveData, receiveData.length);
                client.receive(receivePacket);
                ByteBuffer buffer=ByteBuffer.wrap(receivePacket.getData(), 0, receivePacket.getLength());
                byte cmd = buffer.get();
                switch (cmd) {
                    case Packet.PACKET_BOOTSTRAP_SUCCESS:
                        onBootStrapSuccess(buffer);
                        break;
                    case Packet.PACKET_BOOTSTRAP_ERROR:
                        onBootStrapError(buffer);
                        break;
                    default:
                        onTerribleException("错误的返回码：["+cmd+"]");
                        break;
                }
            } catch (IOException e) {//超时
                LogUtil.i("BS服务器反馈超时");
                mUIHandler.sendMessageDelayed(mUIHandler.obtainMessage(MSG_BOOTSTRAP), TIME_OUT);
            }finally{
                if (null!=client) {
                    client.disconnect();
                    client=null;
                }
            }
        }

        public void onBootStrapSuccess(ByteBuffer buffer) {
            PacketBootStrapSuccess bootStrapSuccess = new PacketBootStrapSuccess(buffer);
            LogUtil.i(bootStrapSuccess.toString());
            InetSocketAddress infoPushServer = bootStrapSuccess.getLoginServerAddress();
            mLoginTask=new LoginTask(infoPushServer);
            mUIHandler.sendEmptyMessage(MSG_BOOTSTRAP_SUCCESS);
        }

        public void onBootStrapError(ByteBuffer buffer) {
            PacketBootStrapError bootStrapError = new PacketBootStrapError(buffer);
            onTerribleException(bootStrapError.getErrorMessage());
        }
    }
    
    private class LoginTask implements Runnable{
        /**消息推送服务器地址*/
        private SocketAddress mInfoPushServer;
        
        public LoginTask(SocketAddress address) {
            this.mInfoPushServer = address;
        }

        @Override
        public void run() {
            DatagramSocket client=null;
            try {
                client=new DatagramSocket();
                client.connect(mInfoPushServer);
                client.setSoTimeout(TIME_OUT);
            } catch (SocketException e) {
                onTerribleException("消息推送服务器异常，登录失败");
                return;
            }
            try {
                PacketLogin packetLogin = new PacketLogin(mContext);
                LogUtil.i(packetLogin.toString());
                byte[] login = packetLogin.getPacketData();
                DatagramPacket requestPacket=new DatagramPacket(login, 0, login.length);
                client.send(requestPacket);
                byte[] receiveData=new byte[DEFAULT_BUFFER_SIZE];
                DatagramPacket receivePacket=new DatagramPacket(receiveData, receiveData.length);
                client.receive(receivePacket);
                ByteBuffer buffer=ByteBuffer.wrap(receivePacket.getData(), 0, receivePacket.getLength());
                byte cmd = buffer.get();
                switch (cmd) {
                    case Packet.PACKET_LOGIN_SUCCESS:
                        onLoginSuccess(buffer);
                        break;
                    case Packet.PACKET_LOGIN_ERROR:
                        onLoginError(buffer);
                        break;
                    default:
                        onTerribleException("错误的返回码：["+cmd+"]");
                        break;
                }
            } catch (IOException e) {//超时
                LogUtil.i("登录超时");
                mUIHandler.sendMessageDelayed(mUIHandler.obtainMessage(MSG_BOOTSTRAP), TIME_OUT);
            }finally{
                if (null!=client) {
                    client.disconnect();
                    client=null;
                }
            }            
        }

        private void onLoginSuccess(ByteBuffer buffer) {
            PacketLoginSuccess loginSuccess = new PacketLoginSuccess(buffer);
            LogUtil.i(loginSuccess.toString());
            mListeningTask=new ListeningTask(mInfoPushServer, loginSuccess.getKeepaliveTime());
            mUIHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
        }
        
        private void onLoginError(ByteBuffer buffer) {
            PacketLoginError loginError = new PacketLoginError(buffer);
            onTerribleException(loginError.getErrorDesc());
        }
    }
    
    private class ListeningTask implements Runnable{
        
        /**消息推送服务器地址*/
        private SocketAddress mInfoPushServer;
        /**消息推送通道*/
        private DatagramChannel mChannel;
        /**心跳保活间隔*/
        private int mKeepLiveTime;
        /**上一次发送心跳的时间戳*/
        private long mLastKeepLiveStamp;
        /**新用户数据*/
        private Bundle mNewUserArgs;
        /**是否应该退出*/
        private boolean logout=false;
        
        public ListeningTask(SocketAddress address, int keepLiveTime) {
            this.mInfoPushServer = address;
            this.mKeepLiveTime = keepLiveTime;
        }
        
        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            Selector selector=null;
            try {
                mChannel=DatagramChannel.open();
                mChannel.configureBlocking(false);
                mChannel.connect(mInfoPushServer);
                selector = Selector.open();
                mChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            } catch (IOException e) {
                onTerribleException("消息推送服务器异常");
                return;
            }
            try {
                while (isRun()) {
                    if (selector.select(5000) > 0) {
                        Set<SelectionKey> selectedKeys = selector.selectedKeys();
                        Iterator<SelectionKey> keys = selectedKeys.iterator();
                        while (keys.hasNext()) {
                            SelectionKey key = (SelectionKey) keys.next();
                            keys.remove();
                            if (key.isReadable()) {
                                receivePacket(buffer);
                            }
                            if (key.isWritable()) {
                                keeplive();
                                if (needLogout()) {
                                    logout();
                                }
                            }
                        }
                    }
                    sleepOneSecond();
                }
            } catch (IOException e) {
                LogUtil.printException(e);
                reTakeProcess();
            }finally{
                if (mChannel!=null) {
                    try {
                        mChannel.disconnect();
                        mChannel=null;
                    } catch (IOException e) {}
                }
            }            
        }
        /**
         * 是否应该退出
         * @return
         */
        private boolean needLogout() {
            return logout;
        }

        private void receivePacket(ByteBuffer buffer) throws IOException {
            buffer.clear();
            mChannel.read(buffer);
            buffer.flip();
            byte cmd = buffer.get();
            switch (cmd) {
                case Packet.PACKET_NEW_INFO_PUSH:
                    onNewInformation(buffer);
                    break;
                case Packet.PACKET_REBOOTSTRAP:
                    LogUtil.i("<==========服务器要求重新连接==========>");
                    reTakeProcess();
                    break;
                default:
                    onTerribleException("错误的返回码：["+cmd+"]");
                    break;
            }
        }
        /**
         * 当接收到服务器推送的消息时触发
         * @param buffer
         * @throws IOException
         */
        private void onNewInformation(ByteBuffer buffer) throws IOException {
            try {
                PacketInfoData packetInfoPush = new PacketInfoData(mContext,buffer);
                LogUtil.i(packetInfoPush.toString());
                responseInfoReceived(packetInfoPush.getMsgId());
                if (packetInfoPush.isDataComplete()) {
                    String data = packetInfoPush.getData();
                    if (data!=null) {
                        saveInformation(data);
                    }
                }
            } catch (PacketDamageException e) {
                //当CRC校验包中数据，发现数据损坏时，会抛出此异常
                //此时，不会继续调用responseInfoReceived方法告诉服务器已经收到对应的数据包
                //因此，服务器会继续推送该数据包
                LogUtil.e("ReceiveInfoService(520)--->"+e.getMessage());
            }
        }

        /**
         * 发送心跳进行保活
         * @throws IOException
         */
        private void keeplive() throws IOException {
            if (needKeepLive()) {
                mLastKeepLiveStamp=System.currentTimeMillis();
                PacketKeepLive packetKeepLive = new PacketKeepLive(mContext);
                byte[] data = packetKeepLive.getPacketData();
                if (mChannel!=null&&isRun()) {
                    mChannel.write(ByteBuffer.wrap(data, 0, data.length));
                    LogUtil.i(packetKeepLive.toString());
                }
            }
            //当发送心跳出错时，由调用他的程序来处理
            //会重新进行整个流程，因为此时的网络通道很有可能已经损坏
        }
        /**
         * 是否需要发送心跳进行保活
         * @return
         */
        private boolean needKeepLive() {
            int deltSecond=(int) ((System.currentTimeMillis()-mLastKeepLiveStamp)/1000);
            return mKeepLiveTime<deltSecond;
        }
        
        /**
         * 将消息解析出来，并存入数据库，并发送广播
         * @param data
         */
        private void saveInformation(String data) {
            if (data!=null) {
                try {
                    Information information = Information.parseJson(data);
                    if (information==null) {
                        return;
                    }
                    if (!information.isTimeout()) {
                        LogUtil.i("消息入库："+information.toString());
                        information.saveToDataBase(mContext);
                        //提示UI界面刷新
                       mUIHandler.obtainMessage(MSG_HAS_NEW_INFO, information).sendToTarget();
                        //若有图片的话，后台预先下载图片
                        mUIHandler.obtainMessage(MSG_DOWNLOAD_IMAGE, information.getPicture()).sendToTarget();
                    }
                } catch (JSONException e) {
                    LogUtil.e("ReceiveInfoService(596)--->"+e.getMessage());
                    LogUtil.printException(e);
                }
            }
        }
        
        /**
         * 告知服务器，对应Id的报文已经收到
         * @param msgId
         * @throws IOException 
         */
        private void responseInfoReceived(int msgId) throws IOException {
            PacketInfoDataReceived receiveResponse = new PacketInfoDataReceived(msgId);
            LogUtil.i(receiveResponse.toString());
            byte[] packetData = receiveResponse.getPacketData();
            if(mChannel!=null&&isRun()){
                mChannel.write(ByteBuffer.wrap(packetData, 0, packetData.length));
            }
        }
        /**
         * 线程休眠1秒
         */
        private void sleepOneSecond() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
        
        /**
         * 改变当前登录的用户
         * @param newUserArgs
         */
        public void changeUser(Bundle newUserArgs) {
            mNewUserArgs=newUserArgs;
            logout=true;
        }
        
        /**
         * 当前用户发送退出报文
         */
        private void logout() {
            sendLogoutPacket();
            refreshLoginUser(mNewUserArgs);
            reTakeProcess();
            logout=false;
        }
        
        /**
         * 当前正在保活的用户发送退出报文
         */
        private void sendLogoutPacket() {
            PacketLogout packetLogout = new PacketLogout(mContext);
            byte[] data = packetLogout.getPacketData();
            try {
                mChannel.write(ByteBuffer.wrap(data, 0, data.length));
                LogUtil.i(packetLogout.toString());
            } catch (IOException e) {
                LogUtil.e("ReceiveInfoService(631)--->"+e.getMessage());
                LogUtil.printException(e);
            }
        }
    }
    /**
     * 将新登录的用户信息写入到数据库
     */
    private void refreshLoginUser(Bundle newUserArgs) {
        if (newUserArgs!=null) {
            int newId=newUserArgs.getInt(CloudAccountColumns.USERID);
            String newToken=newUserArgs.getString(CloudAccountColumns.TOKEN);
            String newAccount=newUserArgs.getString(CloudAccountColumns.ACCOUNT);
            CloudAccount.getInstance(mContext).login(newId, newToken, newAccount); 
        }else {
            CloudAccount.getInstance(mContext).logout();
        }
    }
}
