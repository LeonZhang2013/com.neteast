package com.lib.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataInfo implements Serializable{
	
	

	@Override
	public String toString() {
		return "DataInfo [id=" + id + ", name=" + name + ", url=" + url
				+ ", remark=" + remark + ", image=" + image + ", resid="
				+ resid + ", childtype=" + childtype + ", type=" + type
				+ ", desc=" + desc + ", packages=" + packages + ", status="
				+ status + ", version=" + version + ", thumb=" + thumb
				+ ", position=" + position + ", fileid=" + fileid
				+ ", filesize=" + filesize + ", server=" + server
				+ ", progress=" + progress + ", singer=" + singer
				+ ", addtime=" + addtime + ", sourcetime=" + sourcetime
				+ ", slicedone=" + slicedone + ", slicetotal=" + slicetotal
				+ ", isdir=" + isdir + ", security=" + security + ", pkgname="
				+ pkgname + ", tag=" + tag + ", tagName=" + tagName
				+ ", series=" + series + ", movieid=" + movieid + ", passwd="
				+ passwd + ", images=" + images + ", imgdownload="
				+ imgdownload + ", infoHash=" + infoHash + ", ischecked="
				+ ischecked + ", videoid=" + videoid + "]";
	}
	private static final long serialVersionUID = 2759081635397207926L;
	private String id;
	private String name;
	private String url;
	private String remark;
	private String image;
	private String resid="1";
	private String childtype;
	private String type;
	private String desc;
	private String packages;
	private int status = 0; // 0表示没有下载，1表示已经下载。
	private String version;
	private String thumb;
	private int position ;
	private String fileid;
	private long filesize;
	private String server;
	private long progress;
	private String singer ;
	private String addtime;
	private String sourcetime;
	private int slicedone;
	private int slicetotal;
	private  String isdir;
	private String security;
	private String pkgname;
	private String tag;
	private String tagName;
	private String series;
	private String movieid;
	private String passwd;
	private List <String> images = new ArrayList<String>();
	private List <String> imgdownload = new ArrayList<String>();
	private String infoHash;
	private int ischecked = 0;
	private String videoid="";
	
	public String getVideoid(){
		return videoid;
	}
	public void setVideoid(String value){
		videoid = value;
	}
	public int getIsChecked(){
		return ischecked;
	}
	public void setIsChecked(int value){
		this.ischecked = value;
	}
	public String getInfoHash(){
		return infoHash;
	}
	public void setInfoHash(String value){
		this.infoHash = value;
	}
	public String getPasswd(){
		return passwd;
	}
	public void setPasswd(String value){
		this.passwd = value;
	}
	public List<String> getImages(){
		return images;
	}
	public void setImages(String[] imageurl){
		int i=0;
		if(images!=null) images.clear();
		for(i=0;i<imageurl.length ;i++){
			images.add(imageurl[i]);
		}
	}
	public List<String> getImagesDownload(){
		return imgdownload;
	}
	public void setImagesDownload(String[] downloads){
		int i=0;
		if(imgdownload!=null) imgdownload.clear();
		for(i=0;i<downloads.length ;i++){
			imgdownload.add(downloads[i]);
		}
	}
	
	public String getMovieId(){
		return this.movieid;
	}
	public void setMovieId(String value){
		this.movieid = value;
	}
	public String GetSeries(){
		return this.series;
	}
	public void SetSeries(String value){
		this.series = value;
	}
	public String GetTagName(){
		return this.tagName;
	}
	public void SetTagName(String value){
		this.tagName = value;
	}
	public String GetTag(){
		return this.tag;
	}
	public void SetTag(String value){
		this.tag = value;
	}
	public String GetPkgName(){
		return this.pkgname;
	}
	public void SetPkgName(String value){
		this.pkgname = value;
	}
	public String GetIsDir(){
		return this.isdir;
	}
	public void SetIsDir(String value){
		this.isdir = value;
	}
	public int getSliceDone(){
		return this.slicedone;
	}
	public void setSliceDone(int value){
		this.slicedone = value;
	}
	public int getSliceTotal(){
		return this.slicetotal;
	}
	public void setSliceTotal(int value){
		this.slicetotal = value;
	}
	public String getAddTime(){
		return this.addtime;
	}
	public void setAddTime(String value){
		this.addtime = value;
	}
	
	public String getSourceTime(){
		return this.sourcetime;
	}
	public void setSourceTime(String value){
		this.sourcetime = value;
	}
	
	public String getSinger(){
		return this.singer;
	}
	public void setSinger(String value){
		this.singer = value;
	}
	public long getProgress(){
		return this.progress;
	}
	public void setProgress(long value){
		this.progress = value;
	}
	public String getServier(){
		return this.server;
	}
	public void setServer(String value){
		this.server = value;
	}
	public String getFileid(){
		return fileid;
	}
	public void setFileid(String value){
		this.fileid = value;
	}
	public long getFileSize(){
		return this.filesize;
	}
	public void setFileSize(long value){
		this.filesize = value;
	}
	
	public int getPosition(){
		return position;
	}
	public void setPosition(int position){
		this.position = position;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getResid() {
		return resid;
	}
	public void setResid(String resid) {
		this.resid = resid;
	}
	public String getChildtype() {
		return childtype;
	}
	public void setChildtype(String childtype) {
		this.childtype = childtype;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPackages() {
		return packages;
	}
	public void setPackages(String packages) {
		this.packages = packages;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getThumb(){
		return thumb;
	}
	public void setThumb(String value){
		this.thumb = value;
	}
	public String getSecurity() {
		return security;
	}
	public void setSecurity(String security) {
		this.security = security;
	}
	
}
