package com.neteast.clouddisk.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.neteast.clouddisk.R;



public class MyDownloadSourceDataAdapter extends BaseAdapter {

	private LayoutInflater myInflater=null;
	private int  selectItem=0;   

	private List<String>  listitem;//=new ArrayList<HashMap<String,Object>>();
		
	
	 Context myContext=null;
	 public MyDownloadSourceDataAdapter(Context cc,List<String> item)
	 {
		 listitem=item;
		 this.myContext=cc;
		 myInflater=LayoutInflater.from(cc); 
		// System.out.println("listitme"+listitem);
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
	
	
	 public  void setSelectItem(int selectItem) {   
        this.selectItem = selectItem;   
   }   


	@Override
	public View getView(int pos, View view, ViewGroup grpview) {
		// TODO Auto-generated method stub
		ViewHolder myViewHolder;
		if(view==null)
		{
			view=myInflater.inflate(R.layout.source_grid_item, null);
			myViewHolder=new ViewHolder();
		
			myViewHolder.mysourceico=(ImageView)view.findViewById(R.id.sourceItem);
			view.setTag(myViewHolder);	
		}
		else
		{
			myViewHolder=(ViewHolder)view.getTag();
		}
		String tag =  listitem.get(pos);
		if(tag.equals("56")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btn56);
		}else if(tag.equals("6")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btn6);
		}else if(tag.equals("hualu5")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnhualu5);
		}else if(tag.equals("joy")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnjoy);
		}else if(tag.equals("ku6")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnku6);
		}else if(tag.equals("letv")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnletv);
		}else if(tag.equals("m1905")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnm1905);
		}else if(tag.equals("pptv")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnpptv);
		}else if(tag.equals("qiyi")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnqiyi);
		}else if(tag.equals("qq")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnqq);
		}else if(tag.equals("sina")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnsina);
		}else if(tag.equals("smgbb")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnsmgbb);
		}else if(tag.equals("sohu")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnsohu);
		}else if(tag.equals("tudou")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btntudou);
		}else if(tag.equals("youku")){
			myViewHolder.mysourceico.setImageResource(R.drawable.btnyouku);
		}
		return view;
	}
	public class ViewHolder {
	    public   ImageView mysourceico;
	}

}
