package com.changhong.yinxiang.alarm;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResolveAlarmInfor {

	public static ArrayList<Alarm> strToList(String str) {
		ArrayList<Alarm> mAlarmList = new ArrayList<Alarm>();
		try {

//			JsonReader reader = new JsonReader(new StringReader(str));
//			reader.beginArray();
			JSONObject json = new JSONObject(str);
			JSONArray alarms = json.getJSONArray("alarms");
			if (alarms != null && alarms.length() > 0) {
				for (int i = 0; i < alarms.length(); i++) {
					Alarm alarm = new Alarm();
					JSONObject alarmJson=(JSONObject) alarms.get(i);
					alarm.setId(alarmJson.getInt("id"));
					alarm.setEnabled(alarmJson.getBoolean("enabled"));
					alarm.setHour(alarmJson.getInt("hour"));
					alarm.setMinutes(alarmJson.getInt("minutes"));
					alarm.setDaysOfWeek(alarmJson.getInt("daysOfWeek"));
					alarm.setTime(alarmJson.getLong("time"));
					alarm.setVibrate(alarmJson.getBoolean("vibrate"));
					alarm.setLabel(alarmJson.getString("id"));
//					alarm.setAlert(alarmJson.get("alert"));
					alarm.setSilent(alarmJson.getBoolean("silent"));
					
					JSONArray musicsJA=alarmJson.getJSONArray("musics");
					List<MusicBean> musicsList=new ArrayList<MusicBean>();
					for(int j=0;j<musicsJA.length();j++){
						
						JSONObject musicsJson=(JSONObject) musicsJA.get(j);
						MusicBean musicBean=new MusicBean();
						musicBean.setmId(musicsJson.getInt("mId"));
						musicBean.setId(musicsJson.getLong("id"));
						musicBean.setTitle(musicsJson.getString("title"));
						musicBean.setAlbum(musicsJson.getString("album"));
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
}
