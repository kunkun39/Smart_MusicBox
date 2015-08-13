package com.changhong.tvserver.httpdownload;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.changhong.tvserver.touying.image.loader.task.TaskExecutorFactory;
import com.changhong.tvserver.utils.StringUtils;

public class DownLoadController {

	public static final String TAG = "fileDownLoadControl";

	private static DownLoadController instance;
	
  //创建一个可重用固定线程数的线程池
    private static  ExecutorService threadPool = null;
    
    
    private DownLoadController(){
    	
    	 
    }
    
    
	public static DownLoadController getInstance() {
		if (null == instance) {
			instance = new DownLoadController();
		}
		return instance;
	}
	
	

	public void gotoDownload(Handler handler,FileUtil  fileUtil, String fileType,String fileUri) {
		
		if (StringUtils.hasLength(fileUri)) {
			//获取文件名
			String fileName=gotFileNameByUrl(fileUri);
			FileDownloadTask task = new FileDownloadTask(handler, fileUtil,fileUri,	fileName, fileType);	
			//执行任务
			 if(null == threadPool){
	 			 //创建一个可重用固定线程数为3的线程池
	 			   threadPool = Executors. newFixedThreadPool(3);
	 		  }
			threadPool.execute(task);
		}
	}
	
	

	 private String gotFileNameByUrl(String url){
		    String fileName="";
		    int fileSeparator=url.lastIndexOf(File.separator);
		    if(fileSeparator>0){
		    	fileName=url.substring(fileSeparator);
		    }
		    return fileName;
	 }

	/**********************************************************  file  downLoad task *******************************************************************/

	private class FileDownloadTask implements Runnable {
		private  String fileType;
          private String  fileName;
          private  String   fileUrl;
          private Handler handler;        
          private FileUtil  fileUtil;
          
		  public  FileDownloadTask(Handler handler,FileUtil  fileUtil,String fileUrl, String fileName, String fileType){
			      this.fileName=fileName;
			      this.fileType = fileType;
			      this.fileUrl = fileUrl;
			      this.handler=handler;
			      this.fileUtil= fileUtil;
		  }
		
		 
		@Override
		public void run() {
			
			String downLoadResult;
			
			 if (fileUrl.toLowerCase().startsWith("http://") || fileUrl.toLowerCase().startsWith("https://")) {
		            try {
		            	
		            	downLoadResult=HttpDownloader.download(fileUrl, fileType, fileName);
		            	fileUtil.checkMaxFileItemExceedAndProcess(fileType);
		                if (handler != null) {
		                	Message msg=new Message();
		                	
		                	  Bundle bundle = new Bundle();
		                      //当前文件类型: music;
		                      bundle.putString("fileType", fileName);
		                      bundle.putString("fileUrl", fileUrl);
		                      msg.setData(bundle);                      
	                		msg.what=2;
		                	if(downLoadResult.equals("downloadError")){
		                		msg.what=3;
		                	}else if(downLoadResult.equals("fileExist")){
		                		msg.what=4;
		                	}
		                    handler.sendMessage(msg);
		                }
		                
		                Log.e(TAG, "finish download file " + fileUrl);
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
		        }
		    }		
		}

 
	// 销毁线程池,该方法保证在所有任务都完成的情况下才销毁所有线程，否则等待任务完成才销毁  
    public void destroy() {  
        if(null != threadPool){
	        threadPool.shutdown();	
	        threadPool=null;  
        }
    }  
	
}
