package com.neteast.videotv.bean;

import android.text.TextUtils;
import com.neteast.lib.bean.StreamingMedia;
import com.neteast.lib.bean.StreamingMediaRaw.MediaRaw;
import com.neteast.lib.bean.VideoDetailRaw;
import com.neteast.lib.bean.VideoInfoRaw;
import com.neteast.lib.config.VideoType;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-1
 * Time: 上午11:32
 * To change this template use File | Settings | File Templates.
 */
public abstract class VideoDetail {
    protected static final int SHORT_DESCRIPTION_LENGTH=103;
    protected long movieId;
    protected String title;
    protected String mark;
    protected String imageUrl;
    protected List<String> baseInfo;
    protected String description;
    protected final StreamingMedia streamingMedia;
    protected boolean showMask;
    private String currentOrigin;
    protected final VideoInfoRaw videoInfo;

    public static final VideoDetail newVideoDetail(VideoDetailRaw rawData){
    	System.out.println("电影类型  "+rawData.getVideoInfo().getTypeId());
        switch (rawData.getVideoInfo().getTypeId()){
            case VideoType.MOVIE:
                return new Movie(rawData);
            case VideoType.TV:
                return new TV(rawData);
            case VideoType.CARTOON:
                return new Cartoon(rawData);
            case VideoType.VARIETY:
                return new Variety(rawData);
            case VideoType.RECORD:
                return new Record(rawData);
            default:
                return new Movie(rawData);
        }
    }
    protected VideoDetail(VideoDetailRaw rawData) {

        videoInfo = rawData.getVideoInfo();
        streamingMedia = new StreamingMedia(rawData.getStreamingMediaRaw());
        if (rawData.getVideoInfo().getTypeId()==VideoType.RECORD) {
			streamingMedia.setSinglePageCount(30);
		}
        
        this.movieId= videoInfo.getMovieId();

        showMask= videoInfo.getMark()!=null;
        this.mark= videoInfo.getMark()+"分";


        this.description=safeGetString(videoInfo.getDescription());
        if (videoInfo.getTypeId() == VideoType.SHORT_VIDEO) {
			this.imageUrl = safeGetString(videoInfo.getPoster2()); 
		}else{
			this.imageUrl=safeGetString(videoInfo.getPoster());
		}
        List<String> origin = getOrigin();
        if (origin.size()>0){
            currentOrigin = origin.get(0);
            for (String s : origin) {
                if ("1080".equals(s)){
                    currentOrigin = s;
                }
            }
        }
        setTitle(videoInfo.getMovieName());
        setBaseInfo(videoInfo);
    }

    protected String safeGetString(String input){
        return input==null? "":input;
    }

    protected String safeGetBaseInfo(String prefix,String input){
        if (TextUtils.isEmpty(input)){
            return "";
        }else {
            return prefix+ input;
        }
    }

    protected abstract void setBaseInfo(VideoInfoRaw videoInfo);

    /**
     * 将用‘;’分隔的字符替换成用"、"分隔，开头和结尾不包含"、"
     * @param input
     * @return
     */
    public static final String formatString(String input){
        if (input==null){
            return "";
        }
        String[] array = input.split(";");
        if (array.length==1){
            return array[0];
        }
        StringBuilder result=new StringBuilder();
        for(String s:array){
            if(!"".equals(s)&&!";".equals(s)){
                result.append(s).append("、");
            }
        }
        if (result.length()>0){
            result.deleteCharAt(result.length()-1);
        }
        return result.toString();
    }

    public int getCount(){
        return videoInfo.getCount();
    }

    public int getType(){
        return videoInfo.getTypeId();
    }

    public String getOriginTitle(){
        return videoInfo.getMovieName();
    }

    public abstract void setTitle(String originTitle);

    public String getShortDescription(){
        return showDescriptionButton()?description.substring(0,SHORT_DESCRIPTION_LENGTH-1)+"…":description;
    }

    public boolean showDescriptionButton(){
        return description.length()>SHORT_DESCRIPTION_LENGTH;
    }


    public boolean showMask(){
        return showMask;
    }

    public abstract String getPlayButtonText();

    public abstract String getCollectionText();

    public abstract String getHasCollectionText();

    public String getTitle() {
        return title;
    }

    public long getMovieId() {
        return movieId;
    }

    public String getMark() {
        return mark;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getBaseInfo() {
        return baseInfo;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getOrigin(){
        return streamingMedia.getOrigins();
    }

    public List<String> getPageTitles(String origin){
        return streamingMedia.getPageTitles(origin);
    }

    public List<MediaRaw> getMedias(String origin,String pageTitle){
        return streamingMedia.getMedias(origin,pageTitle);
    }

    public boolean needSpit(){
        return  streamingMedia.needSpit();
    }

    public boolean hasPlayResource(){
        return getLastestMedia()!=null;
    }

    public abstract MediaRaw getLastestMedia();

    public MediaRaw getDefaultPlayMedia(){
        return getLastestMedia();
    }

    public void setCurrentOrigin(String currentOrigin) {
        this.currentOrigin = currentOrigin;
    }

    public String getCurrentOrigin(){
       return currentOrigin;
    }

    /**
     * 在不按照月份分类的情况下，都可以视作按照集数分类
     * 注，在不分类的情况下，分类按钮是隐藏的，所以不会调用到该方法。
     * @return
     */
    public boolean isSpitByCount(){
        return streamingMedia.getCurrentSpitState()!=StreamingMedia.SPIT_BY_MONTH;
    }
}
