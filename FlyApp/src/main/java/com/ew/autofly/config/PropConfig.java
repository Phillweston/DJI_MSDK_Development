package com.ew.autofly.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;

import android.content.Context;

import com.ew.autofly.constant.AppConstant;

public class PropConfig {

	private static PropConfig instance = new PropConfig();
	private final String path = AppConstant.PROP_PATH + "/config.ini";
	private Properties properties;
	private final String LOGCAT = "logcat";
	private Context context;
	private boolean isLogcat = false;

	private PropConfig() {

	}

	public static PropConfig getInstance() {
		if (instance == null) {
			instance = new PropConfig();
		}
		return instance;
	}

	public boolean init(Context context) {
		if (this.context == null)
			this.context = context;
		if (properties == null)
			properties = new Properties();
		synchronized (properties) {
			File file = new File(path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileOutputStream out = null;
			try {
		
		
		
		
		
		
				{
					FileInputStream in = null;
					try {
						if (!file.exists()) {
							file.createNewFile();
						}
						in = new FileInputStream(file);
						properties.load(in);
						isLogcat = getLogcat();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (in != null) {
							in.close();
							in = null;
						}
					}
				}
		
				return true;
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					out = null;
				}
			}
		}
		return false;
	}

	public boolean setLogcat(boolean logcat) {
		isLogcat = logcat;
		properties.setProperty(LOGCAT, logcat ? "yes" : "no");
		return store();
	}

	public boolean getLogcat() {
		if (properties.containsKey(LOGCAT)) {
			String logcat = (String) properties.get(LOGCAT);
			if (logcat != null && logcat.equals("yes")) {
				isLogcat = true;
			}
		}
		return isLogcat;
	}

	public boolean store() {
		synchronized (properties) {
			File file = new File(path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
				FileChannel fc = out.getChannel();
				FileLock lock = fc.tryLock();
				if (lock == null) {
					return false;
				} else {
					properties.store(out, "UTF-8");
				}
				lock.release();
				return true;
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					out = null;
				}
			}
		}
		return false;
	}

	
	public void deleteAll() {
		properties.clear();
		setLogcat(isLogcat);

	}

}
