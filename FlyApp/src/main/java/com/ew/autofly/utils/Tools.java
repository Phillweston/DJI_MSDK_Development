package com.ew.autofly.utils;

import java.io.File; 
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Tools {
	
	public static void CloseMethod(Context context, EditText myEditText) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
	}

	public static void closeVirtual(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive())
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static boolean isEmail(String strEmail) {// ^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$
		strEmail = strEmail.trim();


		

		String strPattern = "[A-Z0-9a-z._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
		
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	public static boolean isCN(String str) {
		try {
			byte[] bytes = str.getBytes("UTF-8");
			if (bytes.length == str.length()) {
				return false;
			} else {
				return true;
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		return false;
	}

	public static boolean isBlank(String str) {
		boolean mat = str.matches("^(\\s|.*\\s+.*)$");
		if (mat) {
			return true;
		} else {
			return false;
		}
	}
	
    public static boolean isHexString(String str) {
         String strPattern = "[A-F0-9a-f]*";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }
    
    public static boolean RepeaterSsidCheck(String str) {
        String strPattern = "[A-Z0-9a-z_]*";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }
    
    public static boolean RepeaterPasswordCheck(String str) {
        String strPattern = "[A-Z0-9a-z]*";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }

	public static boolean PasswordStrengthCheck(String passwordString) {

		if (passwordString.length() < 6) {
			return false;
		}
		
	    if(passwordString.contains(" ") || passwordString.contains("&") || passwordString.contains("+")){
	    	 return false;
	    }
	    
		return true;

	}
	
	public static boolean QueryOpenNet(Context contex) {
		ConnectivityManager manager = (ConnectivityManager) contex
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		boolean isWifiConnected = false;
		int ConnectNetCnt = 0;
		NetworkInfo[] info;
		
		info = manager.getAllNetworkInfo();
		
		if (info != null) {
		
			 for (int i = 0; i < info.length; i++) {
			
				 if (info[i].isConnected()) {
					 Log.d("Tools","info[i].getTypeName() = "+ info[i].getTypeName());
					 ConnectNetCnt++; 
					 if(info[i].getTypeName().equals("WIFI"))
						 isWifiConnected = true;
				 }
			 }
		}
		else{
			return false;
		}
		

		if(ConnectNetCnt == 0){
			return false;
		}
		else if(isWifiConnected == true){
			String SSID = "";
			WifiManager mWifiManager = (WifiManager) contex.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
			if (wifiInfo == null) {

				return false;
			}

			SSID = wifiInfo.getSSID();
            if(SSID == null){

            	return false;
            }
            else{
            	if(SSID.toLowerCase().startsWith("phantom") || SSID.toLowerCase().startsWith("fc200")){
            		Log.d("Tools","Phantom wifi not connect network!!!");
            		return false;
            	}
            }
		}

		return true;
	}

	public boolean checkFileExit(String filePath) {
		File fileDir = new File(filePath);
		return fileDir.exists();
	}

	@SuppressLint("SimpleDateFormat")
	public String getSystemData() {
		String time;
		Date date = new Date();
		SimpleDateFormat bartDateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");

		time = bartDateFormat.format(date);
		return time;
	}

	public static void editTextInoutLimitLine(final EditText edit,
			final int number) {
		edit.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				int lines = edit.getLineCount();
				
				if (lines > number) {
					String str = s.toString();
					int cursorStart = edit.getSelectionStart();
					int cursorEnd = edit.getSelectionEnd();
					if (cursorStart == cursorEnd && cursorStart < str.length()
							&& cursorStart >= 1) {
						str = str.substring(0, cursorStart - 1)
								+ str.substring(cursorStart);
					} else {
						str = str.substring(0, s.length() - 1);
					}
					
					edit.setText(str);
					
					edit.setSelection(edit.getText().length());
				}
			}
		});
	}

	public static void EditTextInputLimit(final EditText edit,
			final boolean isCN, final boolean isBlank) {

		edit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(256),
				new InputFilter() {
					public CharSequence filter(CharSequence source, int start,
							int end, Spanned dest, int dstart, int dend) {
						if (isCN && Tools.isCN(source.toString())) {// �ж��Ƿ�Ϊ����
							return "";
						} else if (isBlank && Tools.isBlank(source.toString())) {// �ж��Ƿ�Ϊ�ո�
							return "";
						} else {
							return source;
						}
					}
				} });
	}
	
    public static void EditTextInputHexLimit(final EditText edit,int length) {

        edit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length),
                new InputFilter() {
                    public CharSequence filter(CharSequence source, int start,
                            int end, Spanned dest, int dstart, int dend) {
                        
                        if (isHexString(source.toString())) {
                            return source;
                        }
                        else{
                            return "";
                        }
                    }
                } });
    }

    /**
     * 缩放图片
     * @param bgimage
     * @param newWidth
     * @param newHeight
     * @param flag 1:表示按钮高度比例来缩放；其它就使用宽度的比例来缩放
     * @return
     */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,double newHeight, int flag) {
		try {

			float width = bgimage.getWidth();
			float height = bgimage.getHeight();

			Matrix matrix = new Matrix();

			float scaleRate = ((float) newWidth) / width;
			if(flag == 1){
				scaleRate = ((float) newHeight) / height;
			}

			matrix.postScale(scaleRate, scaleRate);
			Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
			return bitmap;
		} catch (Exception ex) {
			return bgimage;
		}
	}
	
	/**
	 * 转换任务状态
	 * @param status
	 * @return
	 */
	public static String convertMissionStatus(int status){
		String strReturn = "未完成";
		switch(status){
		case 0:
			strReturn = "未完成";
			break;
		case 1:
			strReturn = "已完成";
			break;
		case 2:
			strReturn = "正在同步";
			break;
		case 3:
			strReturn = "已同步";
			break;
		case 4:
			strReturn = "正在上传";
			break;
		case 5:
			strReturn = "已上传";
			break;
		case 6:
			strReturn = "正在拼接";
			break;
		case 7:
			strReturn = "已拼接";
			break;
		case 8:
			strReturn = "已下载";
			break;
		}
		return strReturn;
	}

}
