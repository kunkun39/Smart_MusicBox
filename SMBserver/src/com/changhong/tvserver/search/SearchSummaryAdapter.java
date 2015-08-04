package com.changhong.tvserver.search;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.changhong.tvserver.R;
import com.xiami.sdk.entities.OnlineAlbum;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.SearchSummaryResult;

/**
 * Created by Administrator on 12月17日.
 */
public class SearchSummaryAdapter extends BaseAdapter {

    private List<OnlineSong> songs;
    private List<OnlineAlbum> albums;
    private List<OnlineArtist> artists;
    private List<OnlineCollect> collects;
    private int count = 0;
    private List<Integer> stepCount = new ArrayList<Integer>();
    private LayoutInflater inflater;

    public SearchSummaryAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void swapData(SearchSummaryResult data) {
        count = 0;
        stepCount.clear();
        songs = data.getSongs();
        count += songs.size();
        stepCount.add(count);
        albums = data.getAlbums();
        count += albums.size();
        stepCount.add(count);
        artists = data.getArtists();
        count += artists.size();
        stepCount.add(count);
        collects = data.getCollects();
        count += collects.size();
        stepCount.add(count);
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (stepCount.size() == 0) {
            return null;
        } else {
            if (stepCount.get(0) > position) {
                return songs.get(position);
            } else if (stepCount.get(1) > position) {
                return albums.get(position - stepCount.get(0));
            } else if (stepCount.get(2) > position) {
                return artists.get(position - stepCount.get(1));
            } else {
                return collects.get(position - stepCount.get(2));
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.search_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = ViewHolder.getFromView(convertView);
        }
        viewHolder.render(getItem(position));
        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView subtitle;

        public ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
            view.setTag(this);
        }

        public static ViewHolder getFromView(View view) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            } else {
                return new ViewHolder(view);
            }
        }

        public void render(Object item) {
            String titleStr = null;
            String subtitleStr = null;
            if (item instanceof OnlineSong) {
                titleStr = "Song:" + ((OnlineSong) item).getSongName();
                subtitleStr = ((OnlineSong) item).getSingers();
            } else if (item instanceof OnlineAlbum) {
                titleStr = "Album:" + ((OnlineAlbum) item).getAlbumName();
                subtitleStr = ((OnlineAlbum) item).getArtistName();
            } else if (item instanceof OnlineArtist) {
                titleStr = "Artist:" + ((OnlineArtist) item).getName();
                subtitleStr = null;
            } else if (item instanceof OnlineCollect) {
                titleStr = "Collect:" + ((OnlineCollect) item).getCollectName();
                subtitleStr = ((OnlineCollect) item).getUserName();
            }
            title.setText(titleStr);
            subtitle.setText(subtitleStr);
        }
    }

}
