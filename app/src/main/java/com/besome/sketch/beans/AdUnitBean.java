package com.besome.sketch.beans;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;

public class AdUnitBean implements Parcelable {
    public static final Parcelable.Creator<AdUnitBean> CREATOR = new Parcelable.Creator<AdUnitBean>() {

        @Override // android.os.Parcelable.Creator
        public AdUnitBean createFromParcel(Parcel parcel) {
            return new AdUnitBean(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public AdUnitBean[] newArray(int i) {
            return new AdUnitBean[i];
        }
    };
    @Expose
    public String id;
    @Expose
    public String name;

    public AdUnitBean() {
        this("", "");
    }

    public static Parcelable.Creator<AdUnitBean> getCreator() {
        return CREATOR;
    }

    public void copy(AdUnitBean adUnitBean) {
        id = adUnitBean.id;
        name = adUnitBean.name;
    }

    public int describeContents() {
        return 0;
    }

    public void print() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
    }

    public AdUnitBean(String str, String str2) {
        id = str;
        name = str2;
    }

    @Override // java.lang.Object
    public AdUnitBean clone() {
        AdUnitBean adUnitBean = new AdUnitBean();
        adUnitBean.copy(this);
        return adUnitBean;
    }

    public AdUnitBean(Parcel parcel) {
        id = parcel.readString();
        name = parcel.readString();
    }
}
