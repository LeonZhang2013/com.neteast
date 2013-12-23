package com.neteast.videotv.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.android.volley.Response;
import com.neteast.lib.bean.StreamingMediaRaw;
import com.neteast.videotv.bean.VideoDetail;
import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-15
 * Time: 下午2:57
 */
public class PosterDao {

    public static final int COLLECT=0;
    public static final int FOLLOW =1;
    public static final int PLAY_HISTORY=2;

    /**
     * 生成栏目数据的请求
     * @param db
     * @return
     */
    public static final AsyncPosterQuery generatePanelDataRequest(Response.Listener listener,SQLiteDatabase db,int panelId){
        return new AsyncPosterQuery(listener,db,"panelId=?",String.valueOf(panelId));
    }

    /**
     * 收藏影片
     * @param db
     * @param videoDetail
     * @return
     */
    public static final boolean collect(SQLiteDatabase db,VideoDetail videoDetail){
        Poster poster = convert(videoDetail, COLLECT);
        long id = cupboard().withDatabase(db).put(poster);
        return id>0;
    }

    /**
     * 判断是否已经收藏
     * @param db
     * @param movieId
     * @return
     */
    public static final boolean hasCollection(SQLiteDatabase db,long movieId){
        return exist(db,movieId,COLLECT);
    }

    /**
     * 追剧
     * @param db
     * @param videoDetail
     * @return
     */
    public static final boolean follow(SQLiteDatabase db,VideoDetail videoDetail){
        Poster poster = convert(videoDetail, FOLLOW);
        long id = cupboard().withDatabase(db).put(poster);
        return id>0;
    }

    /**
     * 判断是否已经追剧
     * @param db
     * @param movieId
     * @return
     */
    public static final boolean hasFollow(SQLiteDatabase db,long movieId){
        return exist(db,movieId, FOLLOW);
    }

    /**
     * 存储播放记录
     * @param db
     * @param videoDetail
     * @return
     */
    public static final boolean savePlayHistory(SQLiteDatabase db,VideoDetail videoDetail){
        Poster poster = convert(videoDetail, PLAY_HISTORY);

        if (exist(db,poster.getMovieId(),PLAY_HISTORY)){
            return updateAccessTime(db,poster.getMovieId(),PLAY_HISTORY);
        }else {
            long id = cupboard().withDatabase(db).put(poster);
            return id>0;
        }
    }

    /**
     * 更新访问时间
     * @param db
     * @param movieId
     * @param panelId
     * @return
     */
    public static final boolean updateAccessTime(SQLiteDatabase db,long movieId,int panelId){
        ContentValues values=new ContentValues();
        values.put("modified",System.currentTimeMillis());
        int update = cupboard().withDatabase(db).update(Poster.class, values, "movieId=? AND panelId=?",
                String.valueOf(movieId), String.valueOf(panelId));
        return update>0;
    }

    /**
     * 将 VideoDetail 转换为 Poster，指定Poster所属的PanelId
     * @param videoDetail
     * @param panelId
     * @return
     */
    private static final Poster convert(VideoDetail videoDetail,int panelId){
        Poster poster=new Poster();
        poster.setImage(videoDetail.getImageUrl());
        poster.setCount(videoDetail.getCount());
        poster.setType(videoDetail.getType());
        poster.setTitle(videoDetail.getOriginTitle());
        poster.setMovieId(videoDetail.getMovieId());
        poster.setModified(System.currentTimeMillis());
        poster.setPanelId(panelId);
        StreamingMediaRaw.MediaRaw lastestMedia = videoDetail.getLastestMedia();
        if (lastestMedia!=null){
            try {
                poster.setMaxSeries(Integer.parseInt(lastestMedia.getSeries()));
            }catch (NumberFormatException e){
                poster.setMaxSeries(0);
            }
        }else {
            poster.setMaxSeries(0);
        }
        return poster;
    }

    private static final boolean exist(SQLiteDatabase db,long movieId, int panelId){
        Poster queryResult = cupboard().withDatabase(db).
                query(Poster.class).withSelection("movieId=? AND panelId=?",
                String.valueOf(movieId), String.valueOf(panelId)).get();

        return queryResult!=null;
    }

}
