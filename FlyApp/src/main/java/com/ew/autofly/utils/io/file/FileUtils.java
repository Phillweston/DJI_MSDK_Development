package com.ew.autofly.utils.io.file;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FileUtils {

    /****
     * 计算文件大小
     *
     * @param length
     * @return
     */
    public static String getFileSzie(Long length) {
        if (length >= 1048576) {
            return (length / 1048576) + "MB";
        } else if (length >= 1024) {
            return (length / 1024) + "KB";
        } else if (length < 1024) {
            return length + "B";
        } else {
            return "0KB";
        }
    }

    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 字符串时间戳转时间格式
     *
     * @param timeStamp
     * @return
     */
    public static String getStrTime(String timeStamp) {
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        long l = Long.valueOf(timeStamp) * 1000;
        timeString = sdf.format(new Date(l));
        return timeString;
    }

    
    public static String getFileLastModifiedTime(File f) {
        Calendar cal = Calendar.getInstance();
        long time = f.lastModified();
        SimpleDateFormat formatter = new
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(time);
        return formatter.format(cal.getTime());
    }

    /**
     * 获取扩展内存的路径
     *
     * @param mContext
     * @return
     */
    public static String getStoragePath(Context mContext) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean checkSuffix(String fileName,
                                      String[] fileSuffix) {
        for (String suffix : fileSuffix) {
            if (fileName != null) {
                if (fileName.toLowerCase().endsWith(suffix)) {
                    return true;
                }
            }
        }
        return false;
    }

    
    public static File[] fileFilter(File file) {
        File[] files = file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return !pathname.isHidden();
            }
        });
        return files;
    }


    public static List<FileInfo> getFilesInfo(List<String> fileDir) {
        List<FileInfo> mlist = new ArrayList<>();
        for (int i = 0; i < fileDir.size(); i++) {
            if (new File(fileDir.get(i)).exists()) {
                mlist = getFilesInfo(new File(fileDir.get(i)));
            }
        }
        return mlist;
    }

    public static List<FileInfo> getFilesInfo(File fileDir) {
        List<FileInfo> videoFilesInfo = new ArrayList<>();
        File[] listFiles = fileFilter(fileDir);
        if (listFiles != null) {
            for (File file : listFiles) {
                FileInfo fileInfo = getFileInfoFromFile(file);
                videoFilesInfo.add(fileInfo);
            }
        }
        return videoFilesInfo;
    }

    public static List<FileInfo> getFilesInfoRecursive(List<String> fileDir) {
        List<FileInfo> mlist = new ArrayList<>();
        for (int i = 0; i < fileDir.size(); i++) {
            if (new File(fileDir.get(i)).exists()) {
                mlist = getFilesInfoRecursive(new File(fileDir.get(i)));
            }
        }
        return mlist;
    }

    public static List<FileInfo> getFilesInfoRecursive(File fileDir) {
        List<FileInfo> videoFilesInfo = new ArrayList<>();
        File[] listFiles = fileFilter(fileDir);
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    getFilesInfoRecursive(file);
                } else {
                    FileInfo fileInfo = getFileInfoFromFile(file);
                    videoFilesInfo.add(fileInfo);
                }
            }
        }
        return videoFilesInfo;
    }

    /**
     * 获取文件信息
     *
     * @param files
     * @param sortCondition 排序条件 {@link SortCondition}
     * @param isAscent      是否升序
     * @return
     */
    public static List<FileInfo> getFileInfosFromFileArray(File[] files, int sortCondition, boolean isAscent) {
        SortFile(files, sortCondition, isAscent);
        List<FileInfo> fileInfos = new ArrayList<>();
        for (File file : files) {
            FileInfo fileInfo = getFileInfoFromFile(file);
            fileInfos.add(fileInfo);
        }

        return fileInfos;
    }

    public static class SortCondition {
        public static final int TIME = 0;
        public static final int NAME = 1;
    }

    /**
     * @param sortCondition {@link SortCondition}
     * @param isAscent
     */
    public static void SortFile(File[] files, int sortCondition, final boolean isAscent) {

        switch (sortCondition) {
            case SortCondition.NAME:
                Arrays.sort(files, new Comparator<File>() {

                    protected final static int
                            FIRST = -1,
                            SECOND = 1;

                    @Override
                    public int compare(File lhs, File rhs) {
                        if (lhs.isDirectory() || rhs.isDirectory()) {
                            if (lhs.isDirectory() == rhs.isDirectory())
                                return isAscent ? compareFileNameByChinese(lhs.getName(), rhs.getName()) :
                                        compareFileNameByChinese(rhs.getName(), lhs.getName());
                            else if (lhs.isDirectory()) return FIRST;
                            else return SECOND;
                        }
                        return isAscent ? compareFileNameByChinese(lhs.getName(), rhs.getName()) :
                                compareFileNameByChinese(rhs.getName(), lhs.getName());
                    }
                });
                break;
            case SortCondition.TIME:
                Arrays.sort(files, new Comparator<File>() {

                    protected final static int
                            FIRST = -1,
                            SECOND = 1;

                    @Override
                    public int compare(File lhs, File rhs) {
                        int value = 0;
                        long diff;
                        if (isAscent) {
                            diff = rhs.lastModified() - lhs.lastModified();
                        } else {
                            diff = lhs.lastModified() - rhs.lastModified();
                        }
                        if (diff > 0) {
                            value = FIRST;
                        } else if (diff < 0) {
                            value = SECOND;
                        }
                        if (lhs.isDirectory() || rhs.isDirectory()) {
                            if (lhs.isDirectory() == rhs.isDirectory()) {
                                return value;
                            } else if (lhs.isDirectory()) return FIRST;
                            else return SECOND;
                        }
                        return value;
                    }
                });
                break;
        }

    }


    public static int compareFileNameByChinese(String file1, String file2) {
        Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
        return com.compare(file1, file2);
    }


    public static FileInfo getFileInfoFromFile(File file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getName());
        fileInfo.setFilePath(file.getPath());
        fileInfo.setFileSize(file.length());
        fileInfo.setDirectory(file.isDirectory());
        fileInfo.setTime(FileUtils.getFileLastModifiedTime(file));
        int lastDotIndex = file.getName().lastIndexOf(".");
        if (lastDotIndex > 0) {
            String fileSuffix = file.getName().substring(lastDotIndex + 1);
            fileInfo.setSuffix(fileSuffix);
            fileInfo.setFileNameWithoutSuffix(file.getName().replace("." + fileSuffix, ""));
        } else {
            fileInfo.setFileNameWithoutSuffix(file.getName());
        }
        return fileInfo;
    }

    public static FolderInfo getImageFolder(String path, List<FolderInfo> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (FolderInfo folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        FolderInfo newFolder = new FolderInfo();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        imageFolders.add(newFolder);
        return newFolder;
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean checkFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 创建文件同时创建文件夹
     *
     * @param filePath
     * @return
     */
    public static void createFileAndFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}