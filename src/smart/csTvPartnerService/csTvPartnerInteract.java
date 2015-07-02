package smart.csTvPartnerService;

public class csTvPartnerInteract 
{
	public native int sendTvKey(String Tvkey);

	static
	{
		 System.loadLibrary("csCellphoneConnect");
	}
}
