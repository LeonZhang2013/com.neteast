package com.neteast.clouddisk.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.lib.cloud.LibCloud;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.UploadFileActivity;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.FileItem;
import com.neteast.clouddisk.utils.UIHelper;

public class FileDataAdapter extends BaseAdapter{
	
	private List<FileItem> list;
	private Context context;
	private LibCloud libCloud;
	private File dataInfo;
	//每页显示的Item个数
	public static final int SIZE = Params.UPLOADFILE_PER_PAGE_NUM;
	public FileDataAdapter(Context context, List<FileItem> objects,int page) {
		this.context = context;

		list = new ArrayList<FileItem>();
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < objects.size()) && (i < iEnd)) {
			list.add(objects.get(i));
			i++;
		}
		libCloud = LibCloud.getInstance(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final FileItem dataInfo  = list.get(position);
		convertView = LayoutInflater.from(context).inflate(
				R.layout.item_upload_single_file, null);
		TextView textName = (TextView) convertView.findViewById(R.id.uploadItemName);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.uploadItemImage);
		textName.setText(dataInfo.getFile().getName());
		final CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.uploadItemCheckBox);
		
		//System.out.println(" FileDataAdapter filename " + dataInfo.getName());
		int fileType = DataHelpter.GetFileType(dataInfo.getFile().getName());
		if(dataInfo.getFile().isDirectory()){
			checkbox.setVisibility(View.INVISIBLE);
			imageView.setImageResource(R.drawable.ic_folder_normal);
		}else{
			if(dataInfo.getIsChecked()==1){
				checkbox.setChecked(true);
			}else{
				checkbox.setChecked(false);
			}
		}
		if(fileType ==1){
			if(dataInfo.getFile().getName().matches("^.*?\\.(mp4|3gp|ts|mpg)$")){
				libCloud.DisplayVideoThumbnail(dataInfo.getFile().getAbsolutePath(), imageView);
			}else{
				
			}
		}else if(fileType ==2){
			imageView.setImageResource(R.drawable.ico_audio);
		}else if(fileType ==3){
			libCloud.DisplayLimitedImage("file://"+dataInfo.getFile().getAbsolutePath(),imageView,100,100);
		}else if(fileType == 4){
			imageView.setImageResource(R.drawable.ico_text);
		}
		
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button,
                    boolean isSelected) {
            	System.out.println("cb1 on CheckedChanged  isSelected =" + isSelected + "position = " + position );
                if (isSelected == true) {
                	long filelimited = (2*1024*1024*1024-1);
                	if(dataInfo.getFile().length() >= filelimited){
                		String message = context.getResources().getString(R.string.file_islimited);
                		//String message = String.format(context.getResources().getString(R.string.file_isbigthan2G),dataInfo.getFile().getName());
    					//UIHelper.displayToast(message,context);
    					UIHelper.showToast(context,message);
    					checkbox.setChecked(false);
                	}else{
                		dataInfo.setIsChecked(1);
                	}
                } 
                else {
                	dataInfo.setIsChecked(0);
                }
            }
        });
		
		return convertView;
	}

	public boolean isRoot() {
		FileItem item = (FileItem) getItem(0);
		String root = item.getFile().getParentFile().getParent();
		//String root = ((File) getItem(0).getFile()).getParentFile().getParent();
		
		System.out.println(" FileDataAdapter root " + root );
		if(root.equalsIgnoreCase("/sdcard"))
			return true;
		else return false;
	}
}
