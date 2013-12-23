package com.neteast.videotv.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Json Builder.
 * @author xKF75472
 * 
 */
public class JsonBuilder
{

    public static final String KEY_CMD = "cmd";

    public static final String KEY_DATA = "data";

    public static final String KEY_KEYWORD = "keyword";
    
    public static final String START_SEARCH = "startSearch";

    public static final String STOP_SEARCH = "stopSearch";

    public static final String PLAY = "play";

    public static final String ENTRY_FOR_SEARCH = "entryForSearch";
    
    public static final String GET_CURRENT_POSITION = "getCurrentPosition";

    public static final String GET_DURATION = "getDuration";

    public static final String START = "start";

    public static final String PAUSE = "pause";

    public static final String STOP = "stop";

    public static final String NEXT = "next";

    public static final String PREVIOS = "previos";

    public static final String SEEK_TO = "seekTo";

    public static final String GET_PLAY_STATE = "getPlayState";

    public static final String SET_VOLUME = "setVolume";

    // VoiceInput ----------------------------------
    public static final String IS_VOICE_INPUT_ACTIVITY = "isVoiceInputActivity";

    public static final String VOICE_INPUT_START = "voiceInputStart";
    
    public static final String VOICE_INPUT_END = "voiceInputEnd";
    
    public static final String VOICE_INPUT_EXIT = "voiceInputExit";

    public static final String VOICE_INPUT_RESULT = "voiceInputResult";

    public static final String VOICE_INPUT_ERROR = "voiceInputError";

    // Callback ----------------------------------
    public static final String ON_ERROR = "onError";

    public static final String ON_SEARCH_RESULTS = "onSearchResults";

    public static final String ON_COMPLETE = "onComplete";
    
    public static final String ON_VOICE_INPUT_REQUEST = "onVoiceInputRequest";
    
    public static final String ON_VOICE_INPUT_EXIT = "onVoiceInputExit";
    
    
    /**
     * 开始搜索
     * @param keyword
     * @return
     */
    public static String buildSearch(String keyword)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, START_SEARCH);
            JSONObject obj = new JSONObject();
            obj.put("keyword", keyword);

            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 停止搜索
     * @return
     */
    public static String buildStopSearch()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_DATA, new JSONObject());
            jsonObject.put(KEY_CMD, STOP_SEARCH);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 播放
     * @param mediaInfo
     * @return
     */
    public static String buildPlay(MediaInfo mediaInfo)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            JSONObject mediaInfoJson = buildVideoInfo(mediaInfo);
            jsonObject.put(KEY_DATA, mediaInfoJson);
            jsonObject.put(KEY_CMD, PLAY);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 进入应用场景查询
     * @param keyword
     * @return
     */
    public static String buildEntryForSearch(String keyword)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, ENTRY_FOR_SEARCH);
            JSONObject obj = new JSONObject();
            obj.put("keyword", keyword);

            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 
     * @param isVoiceInput 
     * @return
     */
    public static String buildIsVoiceInput(boolean isVoiceInput)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_DATA, new JSONObject());
            jsonObject.put("isVoiceInput", isVoiceInput);
            jsonObject.put(KEY_CMD, IS_VOICE_INPUT_ACTIVITY);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 开始语音
     * @param language
     * @return
     */
    public static String buildVoiceInputStart(String language)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, VOICE_INPUT_START);
            JSONObject obj = new JSONObject();
            obj.put("language", language);

            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
    
    /**
     * 结束语音
     * @param language
     * @return
     */
    public static String buildVoiceInputEnd(String language)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, VOICE_INPUT_END);
            JSONObject obj = new JSONObject();
            obj.put("language", language);

            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
    
    /**
     * 退出语音
     * @param language
     * @return
     */
    public static String buildVoiceInputExit(String language)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, VOICE_INPUT_EXIT);
            JSONObject obj = new JSONObject();
            obj.put("language", language);

            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }
    
    /**
     * 传输识别结果
     * @param whole
     * @param keyword
     * @param language
     * @return
     */
    public static String buildVoiceInputResult(String whole, String keyword, String language)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, VOICE_INPUT_RESULT);
            JSONObject obj = new JSONObject();
            obj.put("whole", whole);
            obj.put("keyword", keyword);
            obj.put("language", language);
            
            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 传输语音出错信息
     * @param code
     * @param desc
     * @param language
     * @return
     */
    public static String buildVoiceInputError(String code, String desc, String language)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, VOICE_INPUT_ERROR);
            JSONObject obj = new JSONObject();
            obj.put("code", code);
            obj.put("desc", desc);
            obj.put("language", language);
            
            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 客户端请求语音
     * @return
     */
    public static String buildOnVoiceInputRequest(boolean isVoiceInput)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, ON_VOICE_INPUT_REQUEST);
            JSONObject obj = new JSONObject();
            obj.put("voiceInputRequest", isVoiceInput);
            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 客户端退出语音输入
     * @return
     */
    public static String buildOnVoiceInputExit()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, ON_VOICE_INPUT_EXIT);
            jsonObject.put(KEY_DATA, new JSONObject());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    
    
    /**
     * 获取播放位置
     * @param position
     * @return
     */
    public static String buildGetCurrentPosition(int position)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, GET_CURRENT_POSITION);
            JSONObject obj = new JSONObject();
            obj.put("position", position);

            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 获取视频长度
     * @param duration
     * @return
     */
    public static String buildGetDuration(int duration)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, GET_DURATION);
            JSONObject obj = new JSONObject();
            obj.put("duration", duration);

            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 开始播放
     * @return
     */
    public static String buildStart()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_DATA, new JSONObject());
            jsonObject.put(KEY_CMD, START);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 暂停播放
     * @return
     */
    public static String buildPause()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_DATA, new JSONObject());
            jsonObject.put(KEY_CMD, PAUSE);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 停止播放
     * @return
     */
    public static String buildStop()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_DATA, new JSONObject());
            jsonObject.put(KEY_CMD, STOP);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 播放下一个
     * @return
     */
    public static String buildNext()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_DATA, new JSONObject());
            jsonObject.put(KEY_CMD, NEXT);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 播放上一个
     * @return
     */
    public static String buildPrevios()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_DATA, new JSONObject());
            jsonObject.put(KEY_CMD, PREVIOS);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * SeekTO
     * @param msec
     * @return
     */
    public static String buildSeekTo(int msec)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, SEEK_TO);
            JSONObject obj = new JSONObject();
            obj.put("msec", msec);

            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 获取播放状态
     * @param state
     * @return
     */
    public static String buildPlayState(int state)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, GET_PLAY_STATE);
            JSONObject obj = new JSONObject();
            obj.put("state", state);

            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 设置音量
     * @param leftVolume
     * @param rightVolume
     * @return
     */
    public static String buildSetVolume(int leftVolume, int rightVolume)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, GET_PLAY_STATE);
            JSONObject obj = new JSONObject();
            obj.put("leftVolume", leftVolume);
            obj.put("rightVolume", rightVolume);

            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 错误提示
     * @param errorCode
     * @param errorDesc
     * @return
     */
    public static String buildOnError(String errorCode, String errorDesc, String keyword)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, ON_ERROR);
            jsonObject.put(KEY_KEYWORD, keyword);
            
            JSONObject obj = new JSONObject();
            obj.put("errorCode", errorCode);
            obj.put("errorDesc", errorDesc);
            
            jsonObject.put(KEY_DATA, obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 搜索结果
     * @param mediaInfos
     * @param keyword
     * @return
     */
    public static String buildOnSearchResults(List<MediaInfo> mediaInfos, String keyword)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_CMD, ON_SEARCH_RESULTS);
            jsonObject.put(KEY_KEYWORD, keyword);
            
            JSONArray jsonArray = new JSONArray();
            int count = mediaInfos.size();
            MediaInfo mediaInfo = null;
            for (int i = 0; i < count; i++)
            {
                mediaInfo = mediaInfos.get(i);
                jsonArray.put(buildVideoInfo(mediaInfo));
            }
            
            jsonObject.put(KEY_DATA, jsonArray);
            
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 搜索完成
     * @return
     */
    public static String buildOnComplete(String keyword)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put(KEY_DATA, new JSONObject());
            jsonObject.put(KEY_CMD, ON_COMPLETE);
            jsonObject.put(KEY_KEYWORD, keyword);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return jsonObject.toString();
    }
    
    /**
     * 媒体信息
     * @param mediaInfo
     * @return
     * @throws JSONException
     */
    public static JSONObject buildVideoInfo(MediaInfo mediaInfo) throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", mediaInfo.getId());
        jsonObject.put("contentId", mediaInfo.getContentId());
        jsonObject.put("title", mediaInfo.getTitle());
        jsonObject.put("url", mediaInfo.getUrl());
        jsonObject.put("app", mediaInfo.getSourceApp());
        jsonObject.put("episodeNumber", mediaInfo.getEpisodeNumber());
        jsonObject.put("length", mediaInfo.getLength());
        jsonObject.put("definition", mediaInfo.getDefinition());
        jsonObject.put("playactor", mediaInfo.getPlayactor());
        jsonObject.put("director", mediaInfo.getDirector());
        jsonObject.put("summary", mediaInfo.getSummary());
        jsonObject.put("type", mediaInfo.getType());
        jsonObject.put("source", mediaInfo.getSourceVideo());
        jsonObject.put("image", mediaInfo.getThumbImageUrl());
        jsonObject.put("score", mediaInfo.getScore());
        jsonObject.put("programCategory", mediaInfo.getProgramCategory());
        
        return jsonObject;
    }
    
}
