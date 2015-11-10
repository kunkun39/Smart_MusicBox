package com.changhong.xiami.data;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class SimpleParser<T>  implements  IFJsonItemParser{

    Class<T> mClass;
     Type  mType;
     Gson mGson;
     
	public SimpleParser(Class<T> mclass){
		this.mClass=mclass;
//		this.mType=type;	
		mGson=JsonUtil.getInstance().getGson();
	}

	@Override
	public T  parse(JsonElement elment) {
		try{
			 synchronized (mGson) {
				 
				 String str=elment.toString();
				 Object obj= mGson.fromJson(elment, mClass);			 
				 return   mGson.fromJson(elment, mClass);	
			}
		}catch(JsonParseException ex){
			ex.printStackTrace();
			return null;
		}
	}
	 
	

}
