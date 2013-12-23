package com.neteast.clouddisk.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.model.MovieInfo;
import com.neteast.clouddisk.param.Params;

public class UploadSingleGridAdapter extends BaseAdapter {

	private Context context;
	/** 列表. */
	//private List<String> lstDate;
	private LinkedList<MovieInfo> lstDate;
	/** 名字. */
	private TextView txtName;
	/** 性别. */
	private TextView txtSex;
	/** 年龄. */
	private TextView txtAge;
	private ImageView avatarImg;
	
	private LibCloud libCloud;

	// 每页显示的Item个数
	public static final int SIZE = 18;

	public UploadSingleGridAdapter(Context mContext, LinkedList<MovieInfo> list, int page) {
		Log.i("UploadSingleGridAdapter","list.size= " + list.size() + "page =" +page);
		this.context = mContext;
		libCloud = LibCloud.getInstance(mContext);
		lstDate = new LinkedList<MovieInfo>();
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < list.size()) && (i < iEnd)) {
			lstDate.add(list.get(i));
			i++;
		}
	}

	public int getCount() {
		return lstDate.size();
	}

	public Object getItem(int position) {
		return lstDate.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("UploadSingleGridAdapter",  "position =" + position);
		MovieInfo info = lstDate.get(position);
		ImageView imageView=null;
		convertView = LayoutInflater.from(context).inflate(R.layout.item_upload_single_file, null);

		txtName = (TextView) convertView.findViewById(R.id.uploadItemName);
		imageView = (ImageView) convertView.findViewById(R.id.uploadItemImage);
		if(info.type == Params.UPLOAD_VIDEO){
			libCloud.DisplayVideoThumbnail(info.path, imageView);
			//imageView.setImageResource(R.drawable.ico_audio);
			//imageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(info.path,Video.Thumbnails.MICRO_KIND));
		}else if(info.type == Params.UPLOAD_MUSIC){
			imageView.setImageResource(R.drawable.ico_audio);
		}else if(info.type == Params.UPLOAD_PICTURE){
			//imageView.setImageResource(R.drawable.ico_audio);
			libCloud.DisplayLimitedImage("file://"+info.path,imageView,100,100);
			/*
			if(info.thumbnail==null){
				AsyncLoadImage loadImage = new AsyncLoadImage();
				loadImage.execute(imageView,info.path);
			}
			imageView.setImageBitmap(decodeImage(info.path,100,100));
			*/
		}else if(info.type == Params.UPLOAD_DOC){
			imageView.setImageResource(R.drawable.ico_text);
		}
	
		//		txtSex = (TextView) convertView.findViewById(R.id.nickName);
//		txtAge = (TextView) convertView.findViewById(R.id.phone);
//		avatarImg = (ImageView) convertView.findViewById(R.id.imageView);
		txtName.setText(info.displayName);
//		txtSex.setText(user.nickName);
//		txtAge.setText("鐢佃瘽锛?11111111111111");
//		avatarImg.setImageResource(R.drawable.default_photo);

		return convertView;
	}
	/** 
	* 异步读取网络图片 
	* 
	*/  
	class AsyncLoadImage extends AsyncTask<Object, Object, Void> {  
	       @Override  
	       protected Void doInBackground(Object... params) {  
	           try {  
	               ImageView imageView=(ImageView) params[0];  
	               String url=(String) params[1];  
	               Bitmap bitmap = decodeImage(url,100,100);
	               //imageView.setImageBitmap(decodeImage(url,100,100));
	               publishProgress(new Object[] {imageView, bitmap});
	            } catch (Exception e) {  
	                Log.e("error",e.getMessage());  
	                e.printStackTrace();  
	            } 
	            return null;  
	       }
	       protected void onProgressUpdate(Object... progress) {  
	    	   ImageView imageView = (ImageView) progress[0];  
	    	   imageView.setImageBitmap((Bitmap) progress[1]);           
	       }  
	} 
	
	
    private Bitmap decodeImage(String path,int dstwidth,int dstheight){
        try {
        	Bitmap bitmap;
        	File f=new File(path); 
        	InputStream is = new FileInputStream(f);
        	BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            int width=o.outWidth, height=o.outHeight;
            int BytePerPixel = 4; // 32bit/8
            long sizeReqd = 0;
            float scale_tmp =0;
            int scale =0;
            Log.i("decodeImage", "srcWidth = " + width + "srcHeight=" + height + "dstWidth" + dstwidth + "dstHeight" + dstheight);
	        if(dstwidth > 0 && dstheight > 0){
	        	float scale_width=0,scale_height=0;
	        	if(width > dstwidth){
	        		scale_width = width/dstwidth;
	        	}
	        	if(height > dstheight){
	        		scale_height = height/dstheight;
	        	}
	        	scale_tmp = scale_width >= scale_height ? scale_width : scale_height;
	        	//Log.i("decodeImage","scale_tmp = " + scale_tmp + "scale_width=" + scale_width + "scale_height=" + scale_height);
	        	if(scale_tmp ==0){
	        		scale =0;
	        	}
	        	else if(scale_tmp < 2){
	        		scale = 2;
	        	}else if(scale_tmp < 4){
	        		scale = 4;
	        	}else if(scale_tmp < 8){
	        		scale = 8;
	        	}else if(scale_tmp <16){
	        		scale = 16;
	        	}else{
	        		scale = 32;
	        	}
	        	Log.i("decodeImage","scale = " + scale);
	    		BitmapFactory.Options o2 = new BitmapFactory.Options();
		        o2.inSampleSize=scale;
		        bitmap = BitmapFactory.decodeStream(is, null, o2);
	        }else{
	        	BitmapFactory.Options o2 = new BitmapFactory.Options();
	            bitmap = BitmapFactory.decodeStream(is,null,o2);           
	        }
      
            is.close();
            return bitmap;
        } catch (Exception e) {}
        return null;
    }
	
}
