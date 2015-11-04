package com.changhong.common.service;

public interface ClientSocketInterface {

	public static final String TAG = "RemoteSocket";

	public static final String STR_IM_SHOW = "INPUT_METHOD_SHOW";
	public static final String STR_IM_HIDE = "INPUT_METHOD_HIDE";
	public static final String STR_IM_SHOWTIPS = "INPUT_METHOD_TIPS";
	public static final String STR_IM_USEDEFAULT = "INPUT_METHOD_DEFAULT";
	public static final String STR_IM_CHANGHONG = "INPUT_METHOD_CHANGHONG";
	public static final String STR_IM_FINISHINPUT = "INPUT_METHOD_INPUTFIN";
	public static final String STR_IM_CONTENT = "content";
	public static final String STR_IM_COMMITE ="INPUT_METHOD_COMMITE";
	public static final String STR_IM_DELCHAR = "INPUT_METHOD_DELCHAR";

	// Intent

	public static final String INTENT_SOCKET ="com.changhong.ott.socket";
	public static final String INTENT_PARM_INT_INPUT = "input_method_state";

	static final  int EVENT_IM = 0x1 << 31;
	public static final int EVENT_DEFAULT_VALUE = EVENT_IM + 0;
	public static final int EVENT_IMLAUNCH = EVENT_IM + 1;
	public static final int EVENT_IMCANCEL = EVENT_IM +2;
	public static final int EVENT_IMSHOW = EVENT_IM + 3;
	public static final int EVENT_IMHIDE = EVENT_IM + 4;
	public static final int EVENT_IMDEFAULT = EVENT_IM + 5;
	public static final int EVENT_IMCHANGHONG = EVENT_IM + 6;
	public static final int EVENT_IMFININPUT = EVENT_IM + 7;
	public static final int EVENT_IMDATA_GET = EVENT_IM + 8;
	public static final int EVENT_IMLIKEDHINT = EVENT_IM + 9;
	public static final int EVENT_IMDATA_SEND = EVENT_IM + 10;
	public static final int EVENT_IMCOMMITE = EVENT_IM + 0xa;
	public static final int EVENT_IMDELETE = EVENT_IM + 0xb;


	// parameter
	// Remote control
	public static final int CONTENT_PORT = 7102;
	public static final int INPUT_IP_GET_PORT = 7100;
	public static final int INPUT_IP_POST_PORT = 7101;

	// others
	public static final int SERVER_IP_GET_PORT = 9000;
	public static final int SERVER_IP_POST_PORT = 9004;
	public static final int KEY_PORT = 9002;
    public static final int SWITCH_KEY_PORT = 9005;

    
	public static final String STR_HEATBEAT = "BONG!";
	public static final char DEVIDE_TOKEN = ':';
	public static final char DEVIDE_MEG = '^';
	public static final int RELAX_TIME = 3000;
	
	//YD add for client heartBeat
    public static final int CLIENT_IP_POST_PORT = 9008;

    //CYM add for TCP
    public static final int TCP_ALARM_PORT=9010;
    public static final String TCP_END="==END==";
}
