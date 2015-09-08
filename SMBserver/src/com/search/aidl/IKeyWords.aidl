package com.search.aidl;
import com.search.aidl.KeyWords;

interface IKeyWords
{
	// ����һ��Person������Ϊ�������
	void SetKeyWords(in String searchText, in KeyWords keyWords);
}