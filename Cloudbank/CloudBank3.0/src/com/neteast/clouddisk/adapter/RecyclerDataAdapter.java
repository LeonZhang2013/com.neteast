package com.neteast.clouddisk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.param.Params;

public class RecyclerDataAdapter  extends BaseAdapter{
	private List<DataInfo> list;
	private Context context;
	private LibCloud libCloud;
	private DataInfo dataInfo;
	private int mpage ;
	int type = 0;
	// 每页显示的Item个数
	public static final int SIZE = Params.MYUPLOAD_DATA_PER_PAGE_NUM;
	public RecyclerDataAdapter(Context context, List<DataInfo> result,int page,int type) {
		this.context = context;
		this.type = type;
		this.mpage = page;
		list = new ArrayList<DataInfo>();
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < result.size()) && (i < iEnd)) {
			list.add(result.get(i));
			i++;
		}
		libCloud = LibCloud.getInstance(context);
	}
	public List<DataInfo> getList(){
		return list;
	}
	public DataInfo getDataInfo() {
		return dataInfo;
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
		//final Map<String,Object> map  = (Map<String,Object>)list.get(position);
		final DataInfo  dataInfo = (DataInfo)list.get(position);
		dataInfo.setPosition(position);
		convertView = LayoutInflater.from(context).inflate(
				R.layout.recyclerelementlayout, null);
		TextView textName = (TextView) convertView.findViewById(R.id.nameTextView);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
		ImageView imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
		CheckBox cb1 = (CheckBox) convertView.findViewById(R.id.uploadItemCheckBox);
		CheckBox cb2 = (CheckBox) convertView.findViewById(R.id.uploadItemCheckBox1);
	
		FrameLayout fileitemView = (FrameLayout) convertView.findViewById(R.id.fileitemView);
		FrameLayout folderitemView = (FrameLayout) convertView.findViewById(R.id.folderitemView);
		
		if(type ==1){
			imageView.setImageResource(R.drawable.videodefaultico);
			imageView1.setImageResource(R.drawable.videodefaultico);
		}else if(type==2){
			imageView.setImageResource(R.drawable.audiodefaultico);
			imageView1.setImageResource(R.drawable.audiodefaultico);
		}else if(type ==3){
			imageView.setImageResource(R.drawable.picturedefaultico);
			imageView1.setImageResource(R.drawable.picturedefaultico);
		}else if(type ==4){
			imageView.setImageResource(R.drawable.ico_text);
			imageView1.setImageResource(R.drawable.ico_text);
		}
		if ((dataInfo.GetIsDir() != null) && (dataInfo.GetIsDir().equals("1"))) {
			//imageView.setImageResource(R.drawable.ico_video);
			folderitemView.setVisibility(View.VISIBLE);
			fileitemView.setVisibility(View.GONE);
			//libCloud.DisplayImage(dataInfo.getThumb(), imageView1);
			if(dataInfo.getIsChecked()==1){
				cb2.setChecked(true);
			}else {
				cb2.setChecked(false);
			}
			
			cb2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton button,
                        boolean isSelected) {
                	System.out.println("cb2 on CheckedChanged  isSelected =" + isSelected + "position = " + position);
                    if (isSelected == true) {
                    	dataInfo.setIsChecked(1);
                    } 
                    else {
                    	dataInfo.setIsChecked(0);
                    }
                }
            });
		
		} else {
			//libCloud.DisplayImage(dataInfo.getThumb(), imageView);
			if(dataInfo.getIsChecked()==1){
				cb1.setChecked(true);
			}else {
				cb1.setChecked(false);
			}
			cb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton button,
                        boolean isSelected) {
                	System.out.println("cb1 on CheckedChanged  isSelected =" + isSelected + "position = " + position );
                    if (isSelected == true) {
                    	dataInfo.setIsChecked(1);
                    } 
                    else {
                    	dataInfo.setIsChecked(0);
                    }
                }
            });

		}		
		textName.setText(dataInfo.getName());
		convertView.setTag(dataInfo);
		
		return convertView;
	}
	
}
