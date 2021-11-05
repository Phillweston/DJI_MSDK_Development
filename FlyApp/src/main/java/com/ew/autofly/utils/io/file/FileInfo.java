package com.ew.autofly.utils.io.file;


import android.os.Parcel;
import android.os.Parcelable;



public class FileInfo implements Parcelable {

    String fileName;
    String filePath;
    long fileSize;
    public boolean isDirectory;
    String suffix;
    String time;
    String fileNameWithoutSuffix;

    boolean isCheck = false;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getFileNameWithoutSuffix() {
        return fileNameWithoutSuffix;
    }

    public void setFileNameWithoutSuffix(String fileNameWithoutSuffix) {
        this.fileNameWithoutSuffix = fileNameWithoutSuffix;
    }

    public FileInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeString(this.filePath);
        dest.writeLong(this.fileSize);
        dest.writeByte(this.isDirectory ? (byte) 1 : (byte) 0);
        dest.writeString(this.suffix);
        dest.writeString(this.time);
        dest.writeString(this.fileNameWithoutSuffix);
        dest.writeByte(this.isCheck ? (byte) 1 : (byte) 0);
    }

    protected FileInfo(Parcel in) {
        this.fileName = in.readString();
        this.filePath = in.readString();
        this.fileSize = in.readLong();
        this.isDirectory = in.readByte() != 0;
        this.suffix = in.readString();
        this.time = in.readString();
        this.fileNameWithoutSuffix = in.readString();
        this.isCheck = in.readByte() != 0;
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel source) {
            return new FileInfo(source);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}
