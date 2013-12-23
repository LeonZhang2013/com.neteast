package com.neteast.clouddisk.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.model.Info;


/*
 * className��XuanJiList
 * 
 * Description��Define an class to get and set the list data
 * 
 * History�� 1.0 2012-01-06 Created
 */
public class SeriesAdapter extends BaseAdapter {

	private LayoutInflater myInflater=null;
	private int  selectItem=-1;  
	private int playclick=0;
	private List<Info>  listitem;
	private Context myContext=null;
	private Activity activty;
	private Context accontent;
	private LinearLayout jincai;
	private FrameLayout detail;
	private LinearLayout miaosou;
	private WebView mWebView;
	//private LoginRigister mLoginHandler=null;
	private String filmtitle="";
	private int swidth;
	private int listh;
	private LinearLayout loadProgressBar;
	
	private String lasturl="";
	 
	 public SeriesAdapter(Context cc,List<Info>   item)
	 {
		 listitem=item;
		 this.myContext=cc;
		 myInflater=LayoutInflater.from(cc); 
	 }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listitem.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listitem.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	 public void setlistdata(List<Info> item){
		   listitem=item;
	 }
	 
	 public void getPassParams(Activity act,Context ac,LinearLayout jc,FrameLayout dl,LinearLayout ms,WebView wv){
		   this.activty= act;
		   this.accontent = ac;
		   this.jincai = jc;
		   this.detail =dl;
		   this.miaosou = ms;
		   this.mWebView=wv;
	 }
	
	 public  void setSelectItem(int selectItem) {   
        this.selectItem = selectItem;   
     }   

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(listitem==null)
			return arg1;
		final ViewHolder myViewHolder;
		String surl=null;
		if(arg1==null)
		{
			arg1=myInflater.inflate(R.layout.series_item, null);
			//System.out.println("width: "+swidth+"height :"+listh);
			myViewHolder=new ViewHolder();
			//arg1.setLayoutParams(new ListView.LayoutParams(480, 124));   
			myViewHolder.my_titleText=(TextView)arg1.findViewById(R.id.seriesitemid);
			arg1.setTag(myViewHolder);
			//arg1.setLayoutParams(new ListView.LayoutParams(swidth, 57));
			
		}
		else
		{
			myViewHolder=(ViewHolder)arg1.getTag();
		}
		
		// System.out.println("11 id is :"+listitem.get(arg0).get("image").toString());
		myViewHolder.my_titleText.setText(listitem.get(arg0).getName());
		
		//Log.d("SeriesAdapter","arg0 =" +arg0 + "selectItem" + selectItem );
		 if (arg0 == selectItem) {   
			 arg1.setBackgroundResource(R.drawable.series_selected);
			// arg1.setBackgroundColor(0xfbfbf2);
         }    
         else {    
        	// arg1.setBackgroundColor(0x000000);
        	 arg1.setBackgroundDrawable(null);
         }      
         

		return arg1;
	}
	/*
	 * className��ViewHolder
	 * 
	 * Description��Define an class to define adapter element
	 * 
	 * History�� 1.0 2012-01-06 Created
	 */
	public class ViewHolder {
	    public   TextView my_titleText;
	   // public   TextView my_detailText;
	   // public   TextView my_timeText;
	  //  public   ImageView playbtn;
	  //  public  ImageView my_image;
	} 
	
}
