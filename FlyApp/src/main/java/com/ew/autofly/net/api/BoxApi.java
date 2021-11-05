package com.ew.autofly.net.api;

import com.ew.autofly.constant.AppConstant;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BoxApi {

	private static String actionUrl = "http://"+ AppConstant.BOX_IP+":81/Default.aspx";

	/**
	 * 检查是否已连接盒子
	 * @return
	 */
	public static boolean checkBoxConnect() {
		HttpURLConnection con = null;
		try {

			URL url = new URL(actionUrl);
			con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(5 * 1000);
			con.setReadTimeout(5 * 1000);

			String resultData = "";
			InputStreamReader in = new InputStreamReader(con.getInputStream());
			BufferedReader buffer = new BufferedReader(in);
			String inputLine = null;
			while (((inputLine = buffer.readLine()) != null)) {
				resultData += inputLine + "\n";
			}
			in.close();
			if (!resultData.isEmpty() && (resultData.startsWith("-1") || resultData.startsWith("-2"))) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return false;
	}

	/**
	 * 检查文件是否存在
	 * @return
	 */
	public static boolean checkFileIfExist(String fileUrl){

		HttpURLConnection connection = null;
		try {
			URL url = new URL(fileUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			connection.setConnectTimeout(5 * 1000);
			connection.setReadTimeout(5 * 1000);
			connection.connect();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}

			int fileLength = connection.getContentLength();
			if(fileLength>0){
				return true;
			}

		} catch (Exception e) {
			return false;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		return false;
	}

	/**
	 * 检查是否已连接盒子
	 * @return
	 */
	public static boolean checkUSBConnection() {
		return doCommand("checkusb");
	}

	/**
	 * 启动拷贝图片信息
	 * @param taskID
	 * @param taskBeginTime
	 * @param taskEndTime
	 * @return
	 */
	public static boolean copyPhotoFromUSB(String taskID,String taskBeginTime,String taskEndTime) {
		HttpURLConnection urlConn = null;
		DataOutputStream out = null;
		try {

			URL url = new URL(actionUrl + "?action=copyfromusb");
			urlConn = (HttpURLConnection) url.openConnection();

			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);

			urlConn.setRequestMethod("POST");
			urlConn.setUseCaches(false);

			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


			urlConn.connect();

			out = new DataOutputStream(urlConn.getOutputStream());

			String content = "taskid="+URLEncoder.encode(taskID, "UTF-8")+"&taskTime="+URLEncoder.encode(taskBeginTime+"-"+taskEndTime, "UTF-8");

			out.writeBytes(content);

			out.flush();

			int status = urlConn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_PARTIAL) {
				InputStream is = urlConn.getInputStream();
				int ch;
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1) {
					b.append((char) ch);
				}
				is.close();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (out != null) {
					out.close();
				}
				if (urlConn != null) {
					urlConn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	/**
	 *
	 * @param filePath
	 * @param fileName
	 * @param taskID
	 * @return
	 * @throws Exception
	 */
	public static boolean uploadPhoto(String filePath, String fileName, String taskID) throws Exception {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		FileInputStream fStream = null;
		DataOutputStream ds = null;
		HttpURLConnection con = null;
		try {

			URL url = new URL(actionUrl + "?action=UploadImages&fileName=" + fileName + "&taskID=" + taskID);
			con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(15 * 1000);
			con.setReadTimeout(15 * 1000);


			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			con.connect();

			ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" + fileName + "\"" + end);
			ds.writeBytes(end);


			fStream = new FileInputStream(filePath + "/" + fileName);
			/* 设置每次写入2048bytes */
			int bufferSize = 1024 * 4;
			byte[] buffer = new byte[bufferSize];
			int length = -1;

			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}

			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.flush();

			InputStream is = con.getInputStream();
			/* 取得Response内容 */
			if (con.getResponseCode() == 200) {
				int ch;
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1) {
					b.append((char) ch);
				}
				is.close();
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new Exception("上传文件到盒子失败" + e.toString() + "filePath==" + filePath + "::taskID=" + taskID
					+ ";fileName=" + fileName);
		} finally {
			if (fStream != null) {
				fStream.close();
			}
			if (ds != null) {
				ds.close();
			}
			if (con != null) {
				con.disconnect();
			}
		}
	}

	/**
	 * 启动拼接
	 * @param taskID
	 * @return
	 */
	public static boolean startMosaic(String taskID) {
		HttpURLConnection urlConn = null;
		DataOutputStream out = null;
		try {

			URL url = new URL(actionUrl + "?action=startmosaic");
			urlConn = (HttpURLConnection) url.openConnection();

			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);

			urlConn.setRequestMethod("POST");
			urlConn.setUseCaches(false);

			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


			urlConn.connect();

			out = new DataOutputStream(urlConn.getOutputStream());

			String content = "taskID=" + URLEncoder.encode(taskID, "UTF-8");

			out.writeBytes(content);

			out.flush();

			int status = urlConn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_PARTIAL) {
				InputStream is = urlConn.getInputStream();
				int ch;
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1) {
					b.append((char) ch);
				}
				is.close();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (out != null) {
					out.close();
				}
				if (urlConn != null) {
					urlConn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	/**
	 * 停止拼接
	 * @param taskID
	 * @return
	 */
	public static boolean stopMosaic(String taskID) {
		HttpURLConnection urlConn = null;
		DataOutputStream out = null;
		try {

			URL url = new URL(actionUrl + "?action=stopmosaic");
			urlConn = (HttpURLConnection) url.openConnection();

			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);

			urlConn.setRequestMethod("POST");
			urlConn.setUseCaches(false);

			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


			urlConn.connect();

			out = new DataOutputStream(urlConn.getOutputStream());

			String content = "taskID=" + URLEncoder.encode(taskID, "UTF-8");

			out.writeBytes(content);

			out.flush();

			int status = urlConn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_PARTIAL) {
				InputStream is = urlConn.getInputStream();
				int ch;
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1) {
					b.append((char) ch);
				}
				is.close();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
		return false;
	}

	/**
	 * 获取拼接的步骤
	 * @param taskID
	 * @return
	 */
	public static String getMosaicStep(String taskID) {
		HttpURLConnection urlConn = null;
		DataOutputStream out = null;
		String result = "";
		try {

			URL url = new URL(actionUrl + "?action=getStep");
			urlConn = (HttpURLConnection) url.openConnection();

			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);

			urlConn.setRequestMethod("POST");
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConn.connect();

			out = new DataOutputStream(urlConn.getOutputStream());

			String content = "taskID=" + URLEncoder.encode(taskID, "UTF-8");

			out.writeBytes(content);

			out.flush();

			int status = urlConn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_PARTIAL) {
				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
				String readStr = br.readLine();
				if(readStr!=null){
					if(readStr.contains("INFO")){
						result = readStr.substring(readStr.indexOf("INFO"));
					}else if(readStr.contains("ERROR")){
						result = readStr.substring(readStr.indexOf("ERROR"));
					}else if(readStr.contains("DEBUG")){
						result = readStr.substring(readStr.indexOf("DEBUG"));
					}
				}
				/*int ch;
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1) {
					b.append((char) ch);
				}*/
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
		return result;
	}

	/**
	 * 发送系统关机命令
	 * @return
	 */
	public static boolean shutdownBox() {
		return doCommand("shutdownbox");
	}

	/**
	 * 发送重启盒子命令
	 * @return
	 */
	public static boolean restartBox() {
		return doCommand("restartbox");
	}

	private static boolean doCommand(String action){
		HttpURLConnection con = null;
		try {

			URL url = new URL(actionUrl+"?action="+action);
			con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(2 * 1000);
			con.setReadTimeout(2 * 1000);

			String resultData = "";
			InputStreamReader in = new InputStreamReader(con.getInputStream());
			BufferedReader buffer = new BufferedReader(in);
			String inputLine = null;
			while (((inputLine = buffer.readLine()) != null)) {
				resultData += inputLine + "\n";
			}
			in.close();
			if (!resultData.isEmpty() && resultData.startsWith("1")) {
				return true;
			}

		} catch (Exception e) {
			return false;
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return false;
	}

}
