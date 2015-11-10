package com.changhong.xiami.data;

import com.google.gson.JsonElement;

public interface IFJsonItemParser<T> {
	
	 public T parse (JsonElement elment);

}
