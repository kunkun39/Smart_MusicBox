package com.changhong.tvserver.search.aidl;
import com.changhong.tvserver.search.aidl.KeyWords;

interface IKeyWords
{
	// ����һ��Person������Ϊ�������
	void SetKeyWords(in String searchText, in KeyWords keyWords);
}