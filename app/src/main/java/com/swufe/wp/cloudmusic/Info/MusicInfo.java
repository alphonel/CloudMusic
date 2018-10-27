package com.swufe.wp.cloudmusic.Info;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 MediaStore.Audio.Media._ID,                 //歌曲ID
 MediaStore.Audio.Media.TITLE,               //歌曲名称
 MediaStore.Audio.Media.SINGER,              //歌曲歌手
 MediaStore.Audio.Media.ALBUM,               //歌曲的专辑名
 MediaStore.Audio.Media.DURATION,            //歌曲时长
 MediaStore.Audio.Media.DISPLAY_NAME,        //歌曲文件的名称
 MediaStore.Audio.Media.DATA};               //歌曲文件的全路径
 */

public class MusicInfo implements Comparable, Parcelable {

    private int id;
    private String name;
    private String singer;
    private String album;
    private String duration;
    private String path;
    private String parentPath; //父目录路径
    private int love; //1设置我喜欢 0未设置
    private String firstLetter;
    private int albumID;
    private Bitmap albumBip;




    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public int getLove() {
        return love;
    }

    public void setLove(int love) {
        this.love = love;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int albumID) {
        this.albumID = albumID;
    }

    public Bitmap getAlbumBip(){
        return albumBip;
    }

    public void setAlbumBip(Bitmap albumBip) {
        this.albumBip = albumBip;
    }


    @Override
    public int compareTo(Object o) {
        MusicInfo info = (MusicInfo)o;
        if (info.getFirstLetter().equals("#"))
            return -1;
        if (firstLetter.equals("#"))
            return 1;
        return firstLetter.compareTo(info.getFirstLetter());
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", singer='" + singer + '\'' +
                ", album='" + album + '\'' +
                ", duration='" + duration + '\'' +
                ", path='" + path + '\'' +
                ", parentPath='" + parentPath + '\'' +
                ", love='" + love +
                ", firstLetter='" + firstLetter + '\'' +
                ", albumID='" + albumID +'\'' +
                ", albumBip='"+ albumBip +'\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.singer);
        dest.writeString(this.album);
        dest.writeString(this.duration);
        dest.writeString(this.path);
        dest.writeString(this.parentPath);
        dest.writeInt(this.love);
        dest.writeString(this.firstLetter);
        dest.writeInt(this.albumID);
        albumBip.writeToParcel(dest, 0);
    }

    public MusicInfo() {
    }

    protected MusicInfo(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.singer = in.readString();
        this.album = in.readString();
        this.duration = in.readString();
        this.path = in.readString();
        this.parentPath = in.readString();
        this.love = in.readInt();
        this.firstLetter = in.readString();
        this.albumID=in.readInt();
        this.albumBip = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Parcelable.Creator<MusicInfo> CREATOR = new Parcelable.Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel source) {
            MusicInfo ab = new MusicInfo();
            ab.name = source.readString();
            ab.singer= source.readString();
            ab.album=source.readString();
            ab.duration=source.readString();
            ab.path=source.readString();
            ab.parentPath=source.readString(); //父目录路径
            ab.love=source.readInt(); //1设置我喜欢 0未设置
            ab.firstLetter=source.readString();
            ab.albumID=source.readInt();
            ab.albumBip = Bitmap.CREATOR.createFromParcel(source);
            return ab;
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };
}

