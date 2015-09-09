package com.changhong.tvserver.alarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.changhong.tvserver.MyApplication;
import com.changhong.tvserver.fedit.Configure;
import com.changhong.tvserver.fedit.FileEditManager;

public class ClockCommonData {

	public static ClockCommonData myData;
	private Cursor clockCursor;
	private List<Alarm> alarms = null;
	
	/**
     * The content:// 为这个表定义一个共享的Url
     */
    public static final Uri MUSIC_URL =
            Uri.parse("content://com.changhong.provider.musicprovider/musics");

    public static final String[] MUSIC_QUERY_COLUMNS = {
        "mId","id","title","album","duration","size","artist" ,"url"};
    
    /*Default sort order*/  
    public static final String DEFAULT_SORT_ORDER = "_id asc";  
    
	public static ClockCommonData getInstance() {
		if (null == myData) {
			myData = new ClockCommonData();
		}
		return myData;
	}

	//0位不处理，1位为操作类型，2位客户端的IP，3位
	public void dealMsg(String[] keys) {
		if (keys[1].equals("get")) {
			refreshAlarmsData();
			String alarmInfor=formatData();
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Configure.MSG_SEND, alarmInfor);
			params.put(Configure.IP_ADD, keys[2]);
			
			FileEditManager.getInstance().communicationWithClient(null, Configure.ACTION_SOCKET_COMMUNICATION, params);
			
			Log.i("mmmm", "alarms" + alarms);
		} else if (keys[1].equals("delete")) {

		} else if (keys[1].equals("insert")) {

		} else if (keys[1].equals("update")) {

		}
	}

	public List<Alarm> getAlarmsData(Context con) {
		refreshAlarmsData();
		Log.i("mmmm", "alarms" + alarms);
		return alarms;
	}

	// 根据闹铃ID获取音乐信息
	public List<MusicBean> getMusics(int id) {
		ContentResolver contentResolver=MyApplication.getContext().getContentResolver();
		Cursor musicCursor=contentResolver.query(MUSIC_URL, null, "mId=?",new String[] { String.valueOf(id) }, DEFAULT_SORT_ORDER);
		List<MusicBean> musicBeans=new ArrayList<MusicBean>();
		
//		 while (musicCursor.moveToNext()) {
//				String nameString=musicCursor.getString(musicCursor.getColumnIndex("title"));
//				Log.i("mmmm", id+"="+nameString);
//				
//				String nameString1=musicCursor.getString(musicCursor.getColumnIndex("url"));
//				Log.i("mmmm", id+"="+nameString1);
//			}
		while(musicCursor.moveToNext()){
			MusicBean bean=new MusicBean();
			bean.setmId(musicCursor.getInt(musicCursor.getColumnIndex("mId")));
			bean.setId(musicCursor.getInt(musicCursor.getColumnIndex("id")));
			bean.setTitle(musicCursor.getString(musicCursor.getColumnIndex("title")));
			bean.setAlbum(musicCursor.getString(musicCursor.getColumnIndex("album")));
			bean.setDuration(musicCursor.getInt(musicCursor.getColumnIndex("duration")));
			bean.setSize(musicCursor.getInt(musicCursor.getColumnIndex("size")));
			bean.setArtist(musicCursor.getString(musicCursor.getColumnIndex("artist")));
			bean.setUrl(musicCursor.getString(musicCursor.getColumnIndex("url")));
			musicBeans.add(bean);
		}
		return musicBeans;
	}

	// 获取闹铃基本信息
	private void refreshAlarmsData() {
		// TODO Auto-generated method stub
		clockCursor = MyApplication.getContext().getContentResolver().query(Alarm.Columns.CONTENT_URI,
				Alarm.Columns.ALARM_QUERY_COLUMNS, null, null,
				Alarm.Columns.DEFAULT_SORT_ORDER);
		alarms = new ArrayList<Alarm>();
		clockCursor.moveToFirst();
		Alarm alarm;
		// Log.i("mmmm", "refreshAlarmsData alarm size:>>" +
		// clockCursor.getCount());
		if (clockCursor.getCount() > 0) {
			alarm = new Alarm(clockCursor);
			alarms.add(alarm);
			while (!clockCursor.isLast()) {
				clockCursor.moveToNext();
				alarm = new Alarm(clockCursor);
				alarms.add(alarm);
			}
		}
	}

	// 将获取到的数据打包成json
	private String formatData() {

		JSONObject json = new JSONObject();
		JSONArray alarmMembers = new JSONArray();

		if (alarms != null && alarms.size() > 0) {
			try {
				for (int i = 0; i < alarms.size(); i++) {
					JSONObject member = new JSONObject();
					Alarm alarm = alarms.get(i);

					member.put("id", alarm.id);
					member.put("enabled", alarm.enabled);
					member.put("hour", alarm.hour);
					member.put("minutes", alarm.minutes);
					member.put("daysOfWeek", alarm.daysOfWeek);
					member.put("time", alarm.time);
					member.put("vibrate", alarm.vibrate);
					member.put("label", alarm.label);
					member.put("alert", alarm.alert);
					member.put("silent", alarm.silent);

					// 打包音乐信息
					JSONArray musics = new JSONArray();

					List<MusicBean> musicBeans =getMusics(alarm.id);
					for (int j = 0; j < musicBeans.size(); j++) {
						JSONObject music = new JSONObject();
						MusicBean musicBean = musicBeans.get(j);
						music.put("mId", musicBean.getmId());// 和alarmId一样的，表示他们属于一组
						music.put("id", musicBean.getId());
						music.put("title", musicBean.getTitle());
						music.put("album", musicBean.getAlbum());
						music.put("duration", musicBean.getDuration());
						music.put("size", musicBean.getSize());
						music.put("artist", musicBean.getArtist());
						music.put("url", musicBean.getUrl());
						musics.put(music);
					}
					member.put("musics", musics);
					
					//将组装好的闹铃放进数组里
					alarmMembers.put(member);
				}
				json.put("alarms", alarmMembers);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		return json.toString();
	}
}
