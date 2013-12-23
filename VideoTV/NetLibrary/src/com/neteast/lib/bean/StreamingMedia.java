package com.neteast.lib.bean;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.neteast.lib.utils.DateUtils;
import com.neteast.lib.utils.PageUtils;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-1
 * Time: 下午7:03
 * To change this template use File | Settings | File Templates.
 */
public class StreamingMedia {

    private List<String> origins;
    private Multimap<String,String> pageTitles;
    private Multimap<String,StreamingMediaRaw.MediaRaw> medias;

    private static final String SPECIAL_PAGE_TITLE="分集";
    private static final int SINGLE_PAGE_COUNT=50;

    private int currentSpitState;

    public static final int SPIT_BY_MONTH=1;
    public static final int SPIT_BY_COUNT=2;
    public static final int SPIT_BY_SPECIAL=3;
    public static final int NO_SPIT=4;
    
    private final StreamingMediaRaw rawData;
    private int singlePageCount = SINGLE_PAGE_COUNT;
    
    public StreamingMedia(StreamingMediaRaw rawData) {
    	this.rawData = rawData;
        init();
    }

	private void init() {
		origins = Lists.newArrayList();
        pageTitles = ArrayListMultimap.create();
        medias = ArrayListMultimap.create();

        List<StreamingMediaRaw.MediaRaw> mediaRaws = rawData.getMediaRawList();
        if (mediaRaws==null || mediaRaws.size()==0){
            currentSpitState=NO_SPIT;
            return;
        }

        StreamingMediaRaw.MediaRaw raw = mediaRaws.get(0);
        String series = raw.getSeries();

        if (series!=null){
            try {
                Integer.valueOf(series);
                if (series.length()==8){
                   spitByMonth(mediaRaws);
                }else {
                   spitByCount(mediaRaws);
                }
            }catch (NumberFormatException e){
                spitOnSpecial(mediaRaws);
            }
        }else {
            notNeedSpit(mediaRaws);
        }
	}

    /**
     * 按照月份来分割
     * @param mediaRaws
     */
    private void spitByMonth(List<StreamingMediaRaw.MediaRaw> mediaRaws) {
         currentSpitState=SPIT_BY_MONTH;
         for(StreamingMediaRaw.MediaRaw raw : mediaRaws){
             String series = raw.getSeries();
             if (series == null || series.length()!=8){
                 continue;
             }

             String origin = addOrigin(raw);
             String month = DateUtils.getChineseMonthInfo(series);
             Collection<String> monthList = pageTitles.get(origin);
             if (!monthList.contains(month)){
                 monthList.add(month);
             }
             medias.put(genMediaKey(origin,month),raw);
         }
    }

    public void setSinglePageCount(int singlePageCount) {
		this.singlePageCount = singlePageCount;
		init();
	}

	/**
     * 按照固定数量来分割
     * @param mediaRaws
     */
    private void spitByCount(List<StreamingMediaRaw.MediaRaw> mediaRaws) {
        currentSpitState=SPIT_BY_COUNT;
        Multimap<String,StreamingMediaRaw.MediaRaw> mediaByOrigin=ArrayListMultimap.create();

        for(StreamingMediaRaw.MediaRaw raw : mediaRaws){
            String origin = addOrigin(raw);
            mediaByOrigin.put(origin,raw);
        }

        for(String origin: origins){
            Collection<StreamingMediaRaw.MediaRaw> raws = mediaByOrigin.get(origin);
            int totalCount = raws.size();
            for(StreamingMediaRaw.MediaRaw raw : raws){
                try{
                    Integer series = Integer.valueOf(raw.getSeries());
                    String pageTitle = PageUtils.getItemPageTitle(series, singlePageCount, totalCount);
                    Collection<String> pages = pageTitles.get(origin);
                    if (!pages.contains(pageTitle)){
                        pages.add(pageTitle);
                    }
                    medias.put(genMediaKey(origin,pageTitle),raw);
                }catch (NumberFormatException e){
                    continue;
                }
            }
        }
    }


    /**
     * 按照特殊情况来分割
     * @param mediaRaws
     */
    private void spitOnSpecial(List<StreamingMediaRaw.MediaRaw> mediaRaws) {
        currentSpitState=SPIT_BY_SPECIAL;
        for(StreamingMediaRaw.MediaRaw raw : mediaRaws){
            String origin = addOrigin(raw);
            medias.put(genMediaKey(origin,SPECIAL_PAGE_TITLE),raw);
        }
    }

    /**
     * 不需要进行拆分的情况
     * @param mediaRaws
     */
    private void notNeedSpit(List<StreamingMediaRaw.MediaRaw> mediaRaws) {
        currentSpitState=NO_SPIT;
        for(StreamingMediaRaw.MediaRaw raw : mediaRaws){
            String origin = addOrigin(raw);
            medias.put(genMediaKey(origin,SPECIAL_PAGE_TITLE),raw);
        }
    }


    private String addOrigin(StreamingMediaRaw.MediaRaw raw) {
        String tagName = raw.getTagName();
        if (!origins.contains(tagName)){
            origins.add(tagName);
        }
        return tagName;
    }

    private String genMediaKey(String origin,String pageTitle){
        return origin+":"+pageTitle;
    }

    public List<String> getOrigins(){
        return origins;
    }

    public List<String> getPageTitles(String origin){
        switch (currentSpitState){
            case SPIT_BY_MONTH:
            case SPIT_BY_COUNT:
                return (List<String>) pageTitles.get(origin);
            case SPIT_BY_SPECIAL:
                return Lists.newArrayList(SPECIAL_PAGE_TITLE);
            case NO_SPIT:
                return null;
            default:
                return null;
        }
    }

    public List<StreamingMediaRaw.MediaRaw> getMedias(String origin,String pageTitle){
        switch (currentSpitState){
            case SPIT_BY_MONTH:
            case SPIT_BY_COUNT:
                return (List<StreamingMediaRaw.MediaRaw>) medias.get(genMediaKey(origin,pageTitle));
            case SPIT_BY_SPECIAL:
                return (List<StreamingMediaRaw.MediaRaw>) medias.get(genMediaKey(origin,SPECIAL_PAGE_TITLE));
            case NO_SPIT:
                return (List<StreamingMediaRaw.MediaRaw>) medias.get(genMediaKey(origin,SPECIAL_PAGE_TITLE));
            default:
                return null;
        }
    }


    public boolean needSpit(){
        return currentSpitState!=NO_SPIT;
    }

    public int getCurrentSpitState() {
        return currentSpitState;
    }

	@Override
	public String toString() {
		return "StreamingMedia [origins=" + origins + ", pageTitles=" + pageTitles + ", medias=" + medias + ", currentSpitState="
				+ currentSpitState + "]";
	}
    
    
}
