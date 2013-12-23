package com.neteast.androidclient.newscenter.adapter;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.domain.Information;
import com.neteast.androidclient.newscenter.utils.ImageUtil;
import com.neteast.androidclient.newscenter.utils.ImageUtil.ImageCallback;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class InfoAdapter extends CursorAdapter{
    
    private LayoutInflater mInflater;
    private ListView mListView;

    public InfoAdapter(Context context, Cursor c,ListView listView) {
        super(context, c);
        mInflater = LayoutInflater.from(context);
        mListView = listView;
    }
    
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view=mInflater.inflate(R.layout.item_infos, parent, false);
        ViewHolder holder=new ViewHolder();
        holder.title=(TextView) view.findViewById(R.id.item_info_title);
        holder.image=(ImageView) view.findViewById(R.id.item_info_image);
        holder.content=(TextView) view.findViewById(R.id.item_info_content);
        holder.time=(TextView) view.findViewById(R.id.item_info_time);
        holder.delete=(CheckBox) view.findViewById(R.id.item_info_delete);
        holder.showAll=(Button) view.findViewById(R.id.item_info_showAll);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final ViewHolder holder=(ViewHolder) view.getTag();
        final Information information = Information.parseCursor(cursor);
        if (information==null) {
            return;
        }
        if (information.isTimeout()) {
            information.delete(context);
            return;
        }
      //设置标题
        holder.title.setText(Html.fromHtml(information.getTitle()));
        holder.title.getPaint().setFakeBoldText(true);
        //设置图片
        holder.image.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(information.getPicture())) {
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setTag(information.getPicture());
            Bitmap imageBitmap = ImageUtil.getInstance().loadImage(information.getPicture(),new ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageBitmap, String imageUrl) {
                    ImageView imageView=(ImageView) mListView.findViewWithTag(imageUrl);
                    if (imageView!=null) {
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
            });
            if (imageBitmap!=null) {
                holder.image.setImageBitmap(imageBitmap);
            }
        }
        
        //设置发送时间
        holder.time.setText(information.getSendTime());
      //设置内容
        String textContent = information.getTextContent();
        if (information.needDrawTextColor()) {
            SpannableString drawTextColor = drawTextColor(textContent);
            holder.content.setText(drawTextColor);
            holder.content.setMovementMethod(LinkMovementMethod.getInstance());
        }else {
            holder.content.setText(textContent);
        }
        //设置点击文字后跳转
        holder.content.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                information.executeAction(context);
            }
        });
        //设置删除按钮
        if (information.editable()) {
            holder.delete.setVisibility(View.VISIBLE);
        }else {
            holder.delete.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean deleteConfirm = ConfigManager.isDeleteConfirm(context);
                if (deleteConfirm) {
                    CheckBox cb=(CheckBox) v;
                    if (!cb.isChecked()) {
                        deleteInfo(context, cursor, information);
                    }
                }else {
                    deleteInfo(context, cursor, information);
                }
            }
        });
        //设置显示全部按钮
        if (textContent.length()>160) {
            holder.showAll.setVisibility(View.VISIBLE);
        }else {
            holder.showAll.setVisibility(View.GONE);
        }
        holder.showAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showAll.setVisibility(View.GONE);
                InputFilter[] filters=new InputFilter[]{new InputFilter.LengthFilter(Integer.MAX_VALUE)};
                holder.content.setFilters(filters);
            }
        });
    }
    /**
     * 控件缓存 
     * @author emellend
     *
     */
     protected static class ViewHolder {
         TextView title;
         ImageView image;
         TextView content;
         TextView time;
         CheckBox delete;
         Button showAll;
     }
     
     private void deleteInfo(Context context,Cursor cursor,Information info) {
         info.delete(context);
         cursor.requery();
         notifyDataSetChanged();
    }
     /**
      * 为text着色，当text中有<>包裹的内容时，为包裹的文字着色，否则，为全体文字着色。
      * @param content String
      */
     protected SpannableString drawTextColor(String content) {
         final int linkColor = Color.parseColor("#fa3051");
         //字符串包括染色标记
         boolean hasColorMark=content.contains("<")&&content.contains(">");
         int startIndex=0,endIndex=0;
        if (hasColorMark) {
            startIndex=content.indexOf("<");
            //因为index是从0计数的，所以加一
            endIndex=content.indexOf(">")+1;
            //得到标记背后还剩余的字符个数
            int delta=content.length()-endIndex;
            content=content.replaceAll("<|>", "");
            endIndex=content.length()-delta;
        }else {
            //没有染色标记，那么全部染色
            startIndex=0;
            endIndex=content.length();
        }
         final SpannableString ss = new SpannableString(content);
         ss.setSpan(new ForegroundColorSpan(linkColor),startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
         return ss;
     }
}
