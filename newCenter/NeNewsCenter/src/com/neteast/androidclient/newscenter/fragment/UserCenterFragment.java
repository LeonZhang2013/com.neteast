package com.neteast.androidclient.newscenter.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.activity.MainActivity;
import com.neteast.androidclient.newscenter.domain.CloudAccount;
import com.neteast.androidclient.newscenter.domain.Contact;
import com.neteast.androidclient.newscenter.domain.ContactData;
import com.neteast.androidclient.newscenter.domain.SNSEntity;
import com.neteast.androidclient.newscenter.domain.SNSEntity.SnsType;
import com.neteast.androidclient.newscenter.utils.ImageUtil;
import com.neteast.androidclient.newscenter.utils.LogUtil;
import com.neteast.androidclient.newscenter.utils.Utils;
import com.neteast.androidclient.newscenter.view.ContactCard;

public class UserCenterFragment extends Fragment implements OnClickListener {
    
    private static final int REQUEST_GET_PHOTO_FROM_GALLERY = 99;

    private UIHandler mUIHandler;
    
    private ContactCard mPhoneCard;
    private ContactCard mEmailCard;
    private ContactCard mIMCard;
    private ContactCard mLocationCard;
    
    private CheckBox mEdit;
    private ImageView mPhotoPic;
    private TextView mPhotoName;
    private TextView mAccount;
    private TextView mIdentityCard;
    private EditText mUsername;
    private EditText mFphone;
    private EditText mFemail;
    private EditText mOrg;
    private EditText mJob;
    private Button mAddmore;
    
    private Contact mContact;

    private PopupWindow mAddContactRowDialog;

    private Contact mBackupContact;

    private ProgressDialog mLoading;

    private PopupWindow mSetPhotoDialog;

    private ArrayAdapter<String> mPhotoAdapter;
    
    private ArrayList<String> mPhotoList=new ArrayList<String>();

	private View mScrollView;

	private View mFPhoneBlock;

	private View mFEmailBlock;

	private View mOrgBlock;

	private View mJobBlock;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUIHandler = new UIHandler(this);
        if (savedInstanceState!=null) {
            mContact=savedInstanceState.getParcelable("contact");
            mUIHandler.sendEmptyMessage(UIHandler.LOAD_CONTACT_SUCCESS);
        }else {
            mUIHandler.sendEmptyMessage(UIHandler.LOAD_CONTACT);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_usercenter, container,false);
        
        mEdit = (CheckBox) root.findViewById(R.id.usercenter_edit);
        mEdit.setOnClickListener(this);
        
        mPhotoPic = (ImageView) root.findViewById(R.id.usercenter_photo_pic);
        mPhotoName = (TextView) root.findViewById(R.id.usercenter_photo_name);
        mPhotoPic.setOnClickListener(this);
        mPhotoName.setOnClickListener(this);
        
        mAccount = (TextView) root.findViewById(R.id.usercenter_account);
        mIdentityCard = (TextView) root.findViewById(R.id.usercenter_identityCard);
        
        mUsername = (EditText) root.findViewById(R.id.usercenter_username);
        mFphone = (EditText) root.findViewById(R.id.usercenter_fphone);
        mFemail = (EditText) root.findViewById(R.id.usercenter_femail);
        mOrg = (EditText) root.findViewById(R.id.usercenter_org);
        mJob = (EditText) root.findViewById(R.id.usercenter_job);
        
        mPhoneCard = (ContactCard) root.findViewById(R.id.usercenter_phones);
        mEmailCard = (ContactCard) root.findViewById(R.id.usercenter_emails);
        mIMCard = (ContactCard) root.findViewById(R.id.usercenter_ims);
        mLocationCard = (ContactCard) root.findViewById(R.id.usercenter_locations);
        
        root.findViewById(R.id.usercenter_logout).setOnClickListener(this);
        mAddmore = (Button) root.findViewById(R.id.usercenter_addmore);
        mAddmore.setOnClickListener(this);
        root.findViewById(R.id.usercenter_authorize).setOnClickListener(this);
        
        mScrollView = root.findViewById(R.id.usercenter_scroll);
        mFPhoneBlock = root.findViewById(R.id.usercenter_fphone_block);
        mFEmailBlock = root.findViewById(R.id.usercenter_femail_block);
        mOrgBlock = root.findViewById(R.id.usercenter_org_block);
        mJobBlock = root.findViewById(R.id.usercenter_job_block);
        return root;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mContact!=null) {
            outState.putParcelable("contact", mContact);
        }
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.usercenter_logout:
                logout();
                break;
            case R.id.usercenter_addmore:
                showAddContactRowDialog();
                break;
            case R.id.usercenter_authorize:
                try {
                    ((MainActivity)getActivity()).openAuthorizeCenter();
                } catch (Exception e) {
                    LogUtil.printException(e);
                }
                break;
            case R.id.usercenter_photo_pic:
            case R.id.usercenter_photo_name:
                showSetPhotoDialog();
                break;
            case R.id.usercenter_edit:
                if (mEdit.isChecked()) {
                    editContact();
                }else {
                    saveContact();
                }
                break;
            default:
                break;
        }
        
    }

    private void logout() {
        final SNSEntity[] snsEntities=new SNSEntity[]{
                SNSEntity.getEntity(getActivity(), SnsType.QQWeiBo),
                SNSEntity.getEntity(getActivity(), SnsType.SinaWeiBo),
                SNSEntity.getEntity(getActivity(), SnsType.RenRen),
                SNSEntity.getEntity(getActivity(), SnsType.KaiXin)
                };
        
        for(int i=0,size=snsEntities.length;i<size;i++){
            snsEntities[i].deauthorize();
        }
        
        getActivity().sendBroadcast(new Intent(ConfigManager.ACTION_LOGIN_CHANGED));        
        ((MainActivity)getActivity()).backSysInfoPage();
       
    }
    
    private void loadContact() {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                String path=ConfigManager.URL_USERCENTER+"/Usercenter/userdetail/datatype/j/contactid/"+
                                    CloudAccount.getInstance(getActivity()).getUserId();
                String data =null;
                for(int i=0;i<3;i++){
                    try {
                        data  = Utils.doGet(path);
                        break;
                    } catch (Exception e) {
                        continue;
                    }
                }
                if (data==null) {
                    mUIHandler.sendEmptyMessage(UIHandler.LOAD_CONTACT_ERROR);
                    return;
                }
                try {
                    JSONObject jobj=new JSONObject(data);
                    mContact = Contact.newInstance(jobj);
                    mUIHandler.sendEmptyMessage(UIHandler.LOAD_CONTACT_SUCCESS);
                } catch (JSONException e) {
                    mUIHandler.sendEmptyMessage(UIHandler.LOAD_CONTACT_ERROR);
                }
            }
        }).start();
    }

    private void onLoadContactSuccess() {
        mEdit.setVisibility(View.VISIBLE);
        fillLayoutWithContact();
        disableUserAction();
    }
    
    private void fillLayoutWithContact() {
        setPhoto();
        mAccount.setText(mContact.account);
        mIdentityCard.setText(mContact.identityCard);
        mUsername.setText(mContact.displayName);
        mFphone.setText(mContact.fphone);
        mFemail.setText(mContact.femail);
        mOrg.setText(mContact.organization);
        mJob.setText(mContact.job);
        
        fillContactCard(mPhoneCard, mContact.phones);
        fillContactCard(mEmailCard, mContact.emails);
        fillContactCard(mIMCard, mContact.ims);
        fillContactCard(mLocationCard, mContact.locations);
    }
    
    private void fillContactCard(ContactCard card,ArrayList<ContactData> datas) {
        for (ContactData data : datas) {
            card.addRow(data);
        }
    }
    
    private void setPhoto() {
        if (mContact.displayName.length()>0) {
            mPhotoName.setText(mContact.displayName.substring(mContact.displayName.length()-1));
        }else {
            mPhotoName.setText("");
        }
        showNamePhoto();
        if (mContact.hasPhoto) {
            Bitmap bitmap = ImageUtil.getInstance().getBitmapFromCache(mContact.photo);
            if (bitmap!=null) {
                mPhotoPic.setImageBitmap(bitmap);
                showPicturePhoto();
            }else {
                mUIHandler.sendEmptyMessage(UIHandler.LOAD_PHOTO);
            }
        }
    }
    
    private void showPicturePhoto() {
        mPhotoPic.setVisibility(View.VISIBLE);
        mPhotoName.setVisibility(View.GONE);
    }
    
    private void showNamePhoto() {
        mPhotoPic.setVisibility(View.GONE);
        mPhotoName.setVisibility(View.VISIBLE);
    }
    
    private void onLoadContactError() {
        new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("加载联系人信息失败，是否重新加载？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mUIHandler.sendEmptyMessage(UIHandler.LOAD_CONTACT);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).create().show();
    }
    
    private void uploadPhoto(final String filePath) {
        new Thread(new Runnable() {
            
            public void run() {
                try {
                    File sysFile = new File(filePath);
                    String newPhoto = Contact.uploadPhoto(getActivity(), sysFile);
                    ImageUtil.getInstance().deleteImage(mContact.photo);
                    mContact.photo=newPhoto;
                    mContact.hasPhoto=true;
                    copyFile(sysFile, newPhoto);
                    mUIHandler.sendEmptyMessage(UIHandler.UPLOAD_PHOTO_SUCCESS);
                } catch (Exception e) {
                    mUIHandler.sendEmptyMessage(UIHandler.UPLOAD_PHOTO_ERROR);
                }
            }

            public void copyFile(File scrFile, String destPath) throws FileNotFoundException,IOException {
                File destFile = ImageUtil.getInstance().getFile(destPath);
                @SuppressWarnings("resource")
                FileChannel inChannel = new FileInputStream(scrFile).getChannel();
                @SuppressWarnings("resource")
                FileChannel outChannel = new FileOutputStream(destFile).getChannel();
                outChannel.transferFrom(inChannel, 0, inChannel.size());
                inChannel.close();
                outChannel.close();
            }
            
        }).start();
    }
    
    private void onUploadPhotoSuccess() {
        dismissLoading();
        mPhotoPic.setImageBitmap(ImageUtil.getInstance().getBitmapFromCache(mContact.photo));
        showPicturePhoto();
    }
    
    private void onUploadPhotoError() {
        dismissLoading();
        setPhotoWithDefaultPicute();
    }
    
    private void editContact() {
    	mScrollView.setBackgroundColor(Color.parseColor("#d7d8d8"));
        showPicturePhoto();
        enableUserAction();
        backupContact();
    }
    
    private void saveContact(){
    	mScrollView.setBackgroundResource(R.drawable.main_bg);
    	if (!validInput()) {
    		return;
		}
        disableUserAction();
        refreshContactFromLayout();
        final JSONObject json = Contact.isContactChanged(mBackupContact, mContact);
        if (json!=null) {
            LogUtil.i(json.toString());
            mLoading = ProgressDialog.show(getActivity(), "提示", "保存修改，请稍候", true, false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mContact=Contact.saveContact(getActivity(), json);
                        mUIHandler.sendEmptyMessage(UIHandler.SAVE_CONTACT_SUCCESS);
                    } catch (Exception e) {
                        mUIHandler.sendEmptyMessage(UIHandler.SAVE_CONTACT_ERROR);
                    }
                }
            }).start();
        }
    }
    
    private boolean validInput() {
    	String userName = mUsername.getText().toString().trim();
    	if (TextUtils.isEmpty(userName)) {
			Utils.showToast(getActivity(), "姓名不能为空");
			mUsername.requestFocus();
			return false;
		}
    	return true;
	}

	private void onSaveContactSuccess() {
        dismissLoading();
    }
    
    private void onSaveContactError() {
        dismissLoading();
        Utils.showToast(getActivity(), "保存联系人信息失败");
        restoreContactBackup();
        fillLayoutWithContact();
    }
    
    private void dismissLoading() {
        if(mLoading!=null&&mLoading.isShowing()){
            mLoading.dismiss();
        }
    }
    
    private void refreshContactFromLayout() {
        setPhoto();
        mContact.displayName=mUsername.getText().toString().trim();
        mContact.fphone=mFphone.getText().toString().trim();
        mContact.femail=mFemail.getText().toString().trim();
        mContact.organization=mOrg.getText().toString().trim();
        mContact.job=mJob.getText().toString().trim();
        refreshContactData(mContact.phones, mPhoneCard);
        refreshContactData(mContact.emails, mEmailCard);
        refreshContactData(mContact.ims, mIMCard);
        refreshContactData(mContact.locations, mLocationCard);
    }
    
    private void refreshContactData(ArrayList<ContactData> infosFromData,ContactCard card) {
        ArrayList<ContactData> infosFromView = card.getContactInfos();
        for (int i = 0, size = Math.min(infosFromData.size(), infosFromView.size()); i < size; i++) {
            ContactData viewInfo = infosFromView.get(i);
            ContactData dataInfo = infosFromData.get(i);
            viewInfo.id = dataInfo.id;
        }
        infosFromData.clear();
        infosFromData.addAll(infosFromView);
    }
    
    private void backupContact() {
        mBackupContact = mContact.clone();
    }
    
    private void restoreContactBackup() {
        mContact=mBackupContact;
    }

    private void enableUserAction() {
        
        mUsername.setEnabled(true);
        mFphone.setEnabled(true);
        mFemail.setEnabled(true);
        mOrg.setEnabled(true);
        mJob.setEnabled(true);
        
        mPhotoPic.setEnabled(true);
        mPhotoName.setEnabled(true);
        
        mPhoneCard.showEditMode();
        mEmailCard.showEditMode();
        mIMCard.showEditMode();
        mLocationCard.showEditMode();
        
        mFPhoneBlock.setVisibility(View.VISIBLE);
        mFEmailBlock.setVisibility(View.VISIBLE);
        mOrgBlock.setVisibility(View.VISIBLE);
        mJobBlock.setVisibility(View.VISIBLE);
        
        mAddmore.setVisibility(View.VISIBLE);
    }
    
    private void disableUserAction() {
        
        mUsername.setEnabled(false);
        mFphone.setEnabled(false);
        mFemail.setEnabled(false);
        mOrg.setEnabled(false);
        mJob.setEnabled(false);
        
        mPhotoPic.setEnabled(false);
        mPhotoName.setEnabled(false);
        
        mPhoneCard.showDisplayMode();
        mEmailCard.showDisplayMode();
        mIMCard.showDisplayMode();
        mLocationCard.showDisplayMode();
        
        if (TextUtils.isEmpty(mFphone.getText().toString().trim())) {
			mFPhoneBlock.setVisibility(View.GONE);
		}
        if (TextUtils.isEmpty(mFemail.getText().toString().trim())) {
        	mFEmailBlock.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(mOrg.getText().toString().trim())) {
        	mOrgBlock.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(mJob.getText().toString().trim())) {
        	mJobBlock.setVisibility(View.GONE);
        }
        mAddmore.setVisibility(View.GONE);
    }
    
    private void showAddContactRowDialog() {
        if (mAddContactRowDialog==null) {
            View contentView = getActivity().getLayoutInflater().inflate(R.layout.dialog_list_view, null);
            ListView selectList=(ListView) contentView.findViewById(R.id.dialog_list);
            ArrayList<String> types=new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.types)));
            selectList.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item_pop_label, types));
            
            selectList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    switch (position) {
                    case 0:
                        mPhoneCard.addEmptyRow();
                        break;
                    case 1:
                        mEmailCard.addEmptyRow();
                        break;
                    case 2:
                        mIMCard.addEmptyRow();
                        break;
                    case 3:
                        mLocationCard.addEmptyRow();
                        break;
                    }
                    mAddContactRowDialog.dismiss();
                }
            });
            int width = getResources().getDimensionPixelSize(R.dimen.dialog_type_width);
            int height = getResources().getDimensionPixelSize(R.dimen.dialog_type_height);
            mAddContactRowDialog = new PopupWindow(contentView, width, height);
            
            mAddContactRowDialog.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            mAddContactRowDialog.setFocusable(true);
        }
        
        mAddContactRowDialog.showAsDropDown(mAddmore);
    }
    
    private void loadPhoto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean downloadSuccess = ImageUtil.getInstance().downloadImageFile(mContact.photo);
                if (downloadSuccess) {
                    mUIHandler.sendEmptyMessage(UIHandler.LOAD_PHOTO_SUCCESS);
                }else {
                    mUIHandler.sendEmptyMessage(UIHandler.LOAD_PHOTO_ERROR);
                }
            }
        }).start();
    }
    
    private void onLoadPhotoSuccess() {
        Bitmap bitmap = ImageUtil.getInstance().getBitmapFromCache(mContact.photo);
        if (bitmap!=null) {
            mPhotoPic.setImageBitmap(bitmap);
            showPicturePhoto();
        }
    }
    
    private void onLoadPhotoError() {
        Utils.showToast(getActivity(), "下载用户头像失败");
        showNamePhoto();
    }
    
    private void showSetPhotoDialog() {
        if (mSetPhotoDialog==null) {
            View root = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_list_view, null);
            ListView list = (ListView) root.findViewById(R.id.dialog_list);
            mPhotoAdapter = new ArrayAdapter<String>(getActivity(),R.layout.item_pop_label, mPhotoList);
            list.setAdapter(mPhotoAdapter);
            list.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    String value = (String) parent.getAdapter().getItem(position);
                    if (value.equals("删除头像")) {
                        deletePhoto();
                    }else {
                        selectPhotoFromGallery();
                    }
                    mSetPhotoDialog.dismiss();
                }
            });
            int width = getResources().getDimensionPixelSize(R.dimen.dialog_photo_width);
            int height = getResources().getDimensionPixelSize(R.dimen.dialog_photo_height);
            mSetPhotoDialog = new PopupWindow(root,width,height);
            mSetPhotoDialog.setFocusable(true);
            mSetPhotoDialog.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        }
        //根据是否有图片变化选项
        mPhotoList.clear();
        if (mContact.hasPhoto) {
            mPhotoList.addAll( Arrays.asList( new String[] { "删除头像", "更换头像" } ) );
        } else {
            mPhotoList.addAll( Arrays.asList( new String[] { "相册图片" } ) );
        }
        mPhotoAdapter.notifyDataSetChanged();
        mSetPhotoDialog.showAsDropDown(mPhotoPic);
    }
    
    private void deletePhoto() {
        ImageUtil.getInstance().deleteImage(mContact.photo);
        setPhotoWithDefaultPicute();
    }
    
    private void setPhotoWithDefaultPicute() {
        mContact.photo="";
        mContact.hasPhoto=false;
        mPhotoPic.setImageResource(R.drawable.usercenter_photo_edit);
    }
    
    private void selectPhotoFromGallery() {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        intent.putExtra("crop", "circle");
//        intent.putExtra("noFaceDetection", true);
//        intent.putExtra("return-data", true);
//        intent.putExtra("outputX", 120);
//        intent.putExtra("outputY", 120);
//        startActivityForResult(intent, REQUEST_GET_PHOTO_FROM_GALLERY);
        Intent i = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_GET_PHOTO_FROM_GALLERY); 
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_PHOTO_FROM_GALLERY && resultCode == Activity.RESULT_OK && null != data) {
//            Bitmap bm = (Bitmap) data.getExtras().get("data");
//            if (bm != null) {
//                mLoading = ProgressDialog.show(getActivity(), "提示", "保存头像中......", true, false);
//                mUIHandler.obtainMessage(UIHandler.UPLOAD_PHOTO, bm).sendToTarget();
//            }
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                mUIHandler.obtainMessage(UIHandler.UPLOAD_PHOTO, filePath).sendToTarget();
            }
            cursor.close();
        }
    }
    
    private static class UIHandler extends Handler{
        
        private static final int LOAD_CONTACT = 0;
        private static final int LOAD_CONTACT_SUCCESS = 1;
        private static final int LOAD_CONTACT_ERROR = 2;
        private static final int UPLOAD_PHOTO = 3;
        private static final int UPLOAD_PHOTO_SUCCESS = 4;
        private static final int UPLOAD_PHOTO_ERROR = 5;
        private static final int SAVE_CONTACT_SUCCESS = 7;
        private static final int SAVE_CONTACT_ERROR = 8;
        private static final int LOAD_PHOTO = 9;
        private static final int LOAD_PHOTO_SUCCESS = 10;
        private static final int LOAD_PHOTO_ERROR = 11;
        
        UserCenterFragment mFragment;
        
        public UIHandler(UserCenterFragment fragment) {
            mFragment = fragment;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_CONTACT:
                    mFragment.loadContact();
                    break;
                case LOAD_CONTACT_SUCCESS:
                    if (mFragment.isResumed()) {
                        mFragment.onLoadContactSuccess();
                    }
                    break;
                case LOAD_CONTACT_ERROR:
                    if (mFragment.isResumed()) {
                        mFragment.onLoadContactError();
                    }
                    break;
                case UPLOAD_PHOTO:
                    mFragment.uploadPhoto((String)msg.obj);
                    break;
                case UPLOAD_PHOTO_SUCCESS:
                    mFragment.onUploadPhotoSuccess();
                    break;
                case UPLOAD_PHOTO_ERROR:
                    mFragment.onUploadPhotoError();
                    break;
                case SAVE_CONTACT_SUCCESS:
                    if (mFragment.isResumed()) {
                        mFragment.onSaveContactSuccess();
                    }
                    break;
                case SAVE_CONTACT_ERROR:
                    if (mFragment.isResumed()) {
                        mFragment.onSaveContactError();
                    }
                    break;
                case LOAD_PHOTO:
                    mFragment.loadPhoto();
                    break;
                case LOAD_PHOTO_SUCCESS:
                    if (mFragment.isResumed()) {
                        mFragment.onLoadPhotoSuccess();
                    }
                    break;
                case LOAD_PHOTO_ERROR:
                    if (mFragment.isResumed()) {
                        mFragment.onLoadPhotoError();
                    }
                    break;
            }
        }
    }
}
