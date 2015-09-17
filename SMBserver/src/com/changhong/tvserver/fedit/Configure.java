package com.changhong.tvserver.fedit;

public class Configure {
	
	
	
	/***********************************************通信方式定义********************************************************/
	public final static int ACTION_HTTP_DOWNLOAD= 1001;
	public final static int ACTION_SOCKET_COMMUNICATION= 1002;
	
	//定义机顶盒---手机 之间 socket通讯端口号
	public final static int SOCKET_PORT= 9009;


	//socket通讯常用关键字定义
	public final static String  MSG_SEND= "sendMsg";
	public final static String  FILE_URL= "fileUrl";
	public final static String  FILE_NAME= "fileName";
	public final static String  IP_ADD= "ipAddress";
	public final static String  FILE_TYPE= "fileType";
	public final static String  FILE_EDIT= "fileEdit";
	public final static String  EDIT_TYPE= "editType";
	public final static String  FILE_EXIST= "fileExist";
	public final static String  MSG_RESPOND= "respond";

	
	
	//文件编辑方式：
	public final static String  EDIT_COPY= "copy";
	public final static String  EDIT_CLOCK= "clockRing";
	public final static String  EDIT_RENAME= "reName";
	public final static String  EDIT_REMOVE= "remove";
	public final static String  EDIT_REQUEST_MUSICS= "requestMusicList";


	
	
	//执行结果定义
	public final static String  ACTION_SUCCESS= "success";
	public final static String  ACTION_FAILED= "failed";

}
