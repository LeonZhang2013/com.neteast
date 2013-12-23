package com.neteast.clouddisk.model;

public class Info {

	private String name;
	private String duration;
	private String posterPath;
	private String path;

	public String getPosterPath() {
		return posterPath;
	}

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getPath(){
		return path;
	}
	public void setPath(String path){
		this.path = path;
	}
}
