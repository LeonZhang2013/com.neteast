package com.neteast.clouddisk.fragment;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.neteast.clouddisk.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UploadFinishedFragment extends Fragment {
	private TextView tvTime;
	private TextView tvVideo;
	private TextView tvAudio;
	private TextView tvPic;
	private TextView tvText;
	private TextView tvSummarize;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_upload_finished, null);
		tvTime = (TextView) root.findViewById(R.id.upload_time);
		tvVideo = (TextView) root.findViewById(R.id.upload_video);
		tvAudio = (TextView) root.findViewById(R.id.upload_audio);
		tvPic = (TextView) root.findViewById(R.id.upload_pic);
		tvText = (TextView) root.findViewById(R.id.upload_text);
		tvSummarize = (TextView) root.findViewById(R.id.upload_summarize);
		return root;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	/**
	 * 测试方法
	 */
	private void init() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		tvTime.setText(String.format(getString(R.string.upload_finished_time), format.format(new Date())));
		tvVideo.setText(String.format(getString(R.string.upload_finished_video), 256));
		tvAudio.setText(String.format(getString(R.string.upload_finished_audio), 256));
		tvPic.setText(String.format(getString(R.string.upload_finished_pic), 556));
		tvText.setText(String.format(getString(R.string.upload_finished_text), 56));
		tvSummarize.setText(String.format(getString(R.string.upload_finished_summarize), 2.4,2.6));
	}
}
