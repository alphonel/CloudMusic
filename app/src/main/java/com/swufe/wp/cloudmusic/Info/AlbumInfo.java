package com.swufe.wp.cloudmusic.Info;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;



public class AlbumInfo implements Parcelable {
    private String name;
    private String singer;
    private Bitmap bip;
    private int count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public Bitmap getBip() {
        return bip;
    }

    public void setBip(Bitmap bip) {
        this.bip = bip;
    }
    @Override
    public int hashCode() {
        String code = name + singer + count;
        return code.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        AlbumInfo info = (AlbumInfo) (obj);
        return info.getName().equals(name) && info.getSinger().equals(singer) && info.getBip().equals(bip) && info.getCount() == count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.singer);
        dest.writeInt(this.count);
        bip.writeToParcel(dest, 0);
    }

    public AlbumInfo() {
    }

    protected AlbumInfo(Parcel in) {
        this.name = in.readString();
        this.singer = in.readString();
        this.count = in.readInt();
        this.bip = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Parcelable.Creator<AlbumInfo> CREATOR = new Parcelable.Creator<AlbumInfo>() {
        @Override
        public AlbumInfo createFromParcel(Parcel source) {
            AlbumInfo ab = new AlbumInfo();
            ab.name = source.readString();
            ab.singer= source.readString();
            ab.count=source.readInt();
            ab.bip = Bitmap.CREATOR.createFromParcel(source);
            return ab;
        }

        @Override
        public AlbumInfo[] newArray(int size) {
            return new AlbumInfo[size];
        }
    };
}
