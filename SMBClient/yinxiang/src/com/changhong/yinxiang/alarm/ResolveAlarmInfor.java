package com.changhong.yinxiang.alarm;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class ResolveAlarmInfor {

	/**
	 * The content:// 为这个表定义一个共享的Url
	 */
	public static final Uri MUSIC_URL = Uri
			.parse("content://com.changhong.provider.musicprovider/musics");

	public static final String[] MUSIC_QUERY_COLUMNS = { "mId", "id", "title",
			"album", "duration", "size", "artist", "url" };

	/* Default sort order */
	public static final String DEFAULT_SORT_ORDER = "_id asc";

	/*
	 * 将多个闹铃json转换成list
	 * 
	 * @param str a group of alarms
	 * 
	 * @return the object to ArraysList<Alarm>
	 */
	public static ArrayList<Alarm> strToList(String str) {
		ArrayList<Alarm> mAlarmList = new ArrayList<Alarm>();
		try {

			// JsonReader reader = new JsonReader(new StringReader(str));
			// reader.beginArray();
			JSONObject json = new JSONObject(str);
			JSONArray alarms = json.getJSONArray("alarms");
			if (alarms != null && alarms.length() > 0) {
				for (int i = 0; i < alarms.length(); i++) {
					Alarm alarm = new Alarm();
					JSONObject alarmJson = (JSONObject) alarms.get(i);
					alarm.setId(alarmJson.getInt("id"));
					alarm.setEnabled(alarmJson.getBoolean("enabled"));
					alarm.setHour(alarmJson.getInt("hour"));
					alarm.setMinutes(alarmJson.getInt("minutes"));
					alarm.setDaysOfWeek(alarmJson.getInt("daysOfWeek"));
					alarm.setTime(alarmJson.getLong("time"));
					alarm.setVibrate(alarmJson.getBoolean("vibrate"));
//					alarm.setLabel(alarmJson.getString("label"));
					 alarm.setAlert(Uri.parse(alarmJson.get("alert").toString()));
					alarm.setSilent(alarmJson.getBoolean("silent"));

					JSONArray musicsJA = alarmJson.getJSONArray("musics");
					ArrayList<MusicBean> musicsList = new ArrayList<MusicBean>();
					for (int j = 0; j < musicsJA.length(); j++) {

						JSONObject musicsJson = (JSONObject) musicsJA.get(j);
						MusicBean musicBean = new MusicBean();
						musicBean.setmId(musicsJson.getInt("mId"));
						musicBean.setId(musicsJson.getLong("id"));
						musicBean.setTitle(musicsJson.getString("title"));
//						musicBean.setAlbum(musicsJson.getString("album"));
						musicBean.setDuration(musicsJson.getInt("duration"));
						musicBean.setSize(musicsJson.getLong("size"));
						musicBean.setArtist(musicsJson.getString("artist"));
						musicBean.setUrl(musicsJson.getString("url"));
						musicsList.add(musicBean);
					}
					alarm.setMusicBean(musicsList);
					mAlarmList.add(alarm);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mAlarmList;
	}

	/*
	 * 将获取到的数据打包成json
	 * 
	 * @param con the context
	 * 
	 * @param alarms a group of alarms
	 * 
	 * @return the object to String
	 */
	public static String alarmsToJson(Context con, ArrayList<Alarm> alarms) {

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
					member.put("daysOfWeek", alarm.daysOfWeek.getCoded());
					member.put("time", alarm.time);
					member.put("vibrate", alarm.vibrate);
					member.put("label", alarm.label);
					member.put("alert", alarm.alert);
					member.put("silent", alarm.silent);

					// 打包音乐信息
					JSONArray musics = new JSONArray();

					List<MusicBean> musicBeans = getMusics(con, alarm.id);
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

					// 将组装好的闹铃放进数组里
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

	// 根据闹铃ID获取音乐信息
	public static List<MusicBean> getMusics(Context con, int id) {
		ContentResolver contentResolver = con.getContentResolver();
		Cursor musicCursor = contentResolver.query(MUSIC_URL, null, "mId=?",
				new String[] { String.valueOf(id) }, DEFAULT_SORT_ORDER);
		List<MusicBean> musicBeans = new ArrayList<MusicBean>();

		// while (musicCursor.moveToNext()) {
		// String
		// nameString=musicCursor.getString(musicCursor.getColumnIndex("title"));
		// Log.i("mmmm", id+"="+nameString);
		//
		// String
		// nameString1=musicCursor.getString(musicCursor.getColumnIndex("url"));
		// Log.i("mmmm", id+"="+nameString1);
		// }
		while (musicCursor.moveToNext()) {
			MusicBean bean = new MusicBean();
			bean.setmId(musicCursor.getInt(musicCursor.getColumnIndex("mId")));
			bean.setId(musicCursor.getInt(musicCursor.getColumnIndex("id")));
			bean.setTitle(musicCursor.getString(musicCursor
					.getColumnIndex("title")));
			bean.setAlbum(musicCursor.getString(musicCursor
					.getColumnIndex("album")));
			bean.setDuration(musicCursor.getInt(musicCursor
					.getColumnIndex("duration")));
			bean.setSize(musicCursor.getInt(musicCursor.getColumnIndex("size")));
			bean.setArtist(musicCursor.getString(musicCursor
					.getColumnIndex("artist")));
			bean.setUrl(musicCursor.getString(musicCursor.getColumnIndex("url")));
			musicBeans.add(bean);
		}
		musicCursor.close();
		return musicBeans;
	}

	/*
	 * 将一个闹铃转换为json
	 * 
	 * @param alarm a single alarm
	 */

	public static String alarmToStr(Alarm alarm) {
		JSONObject json = new JSONObject();
		try {
			json.put("id", alarm.id);

			json.put("enabled", alarm.enabled);
			json.put("hour", alarm.hour);
			json.put("minutes", alarm.minutes);
			json.put("daysOfWeek", alarm.daysOfWeek.getCoded());
			json.put("time", alarm.time);
			json.put("vibrate", alarm.vibrate);
			json.put("label", alarm.label);
			json.put("alert", alarm.alert);
			json.put("silent", alarm.silent);
			
			// 打包音乐信息
			JSONArray musics = new JSONArray();

			List<MusicBean> musicBeans = alarm.getMusicBean();
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
			json.put("musics", musics);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json.toString();
	}
	
	/*
	 * 将一个闹铃的json转回为alarm
	 * 
	 * @param alarm a single alarm
	 */

	public static Alarm jsonToAlarm(String str){
		Alarm alarm=new Alarm();
		try {
			JSONObject json = new JSONObject(str);
			alarm.setId(json.getInt("id"));
			alarm.setEnabled(json.getBoolean("enabled"));
			alarm.setHour(json.getInt("hour"));
			alarm.setMinutes(json.getInt("minutes"));
			alarm.setDaysOfWeek(json.getInt("daysOfWeek"));
			alarm.setTime(json.getLong("time"));
			alarm.setVibrate(json.getBoolean("vibrate"));
//			alarm.setLabel(json.getString("label"));
			// alarm.setAlert(json.get("alert"));
			alarm.setSilent(json.getBoolean("silent"));

			JSONArray musicsJA = json.getJSONArray("musics");
			ArrayList<MusicBean> musicsList = new ArrayList<MusicBean>();
			for (int j = 0; j < musicsJA.length(); j++) {

				JSONObject musicsJson = (JSONObject) musicsJA.get(j);
				MusicBean musicBean = new MusicBean();
				musicBean.setmId(musicsJson.getInt("mId"));
				musicBean.setId(musicsJson.getLong("id"));
				musicBean.setTitle(musicsJson.getString("title"));
//				musicBean.setAlbum(musicsJson.getString("album"));
				musicBean.setDuration(musicsJson.getInt("duration"));
				musicBean.setSize(musicsJson.getLong("size"));
				musicBean.setArtist(musicsJson.getString("artist"));
				musicBean.setUrl(musicsJson.getString("url"));
				musicsList.add(musicBean);
			}
			alarm.setMusicBean(musicsList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return alarm;
	}
}
