package com.neteast.videotv.dao;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-15
 * Time: 上午10:07
 */
public class Poster {

    private long movieId;
    private String title;
    private int type;
    private int count;
    private int maxSeries;
    private String image;
    private int panelId;
    private long modified;

    public Poster() {
        modified=System.currentTimeMillis();
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPanelId() {
        return panelId;
    }

    public void setPanelId(int panelId) {
        this.panelId = panelId;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public int getMaxSeries() {
        return maxSeries;
    }

    public void setMaxSeries(int maxSeries) {
        this.maxSeries = maxSeries;
    }

    public static final class Result{
        List<Poster> posters;

        public List<Poster> getPosters() {
            return posters;
        }

        public void setPosters(List<Poster> posters) {
            this.posters = posters;
        }
    }

    @Override
    public String toString() {
        return title;
    }
}
