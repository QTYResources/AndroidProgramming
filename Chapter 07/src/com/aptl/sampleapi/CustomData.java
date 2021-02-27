package com.aptl.sampleapi;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Erik Hellman
 */
public class CustomData implements Parcelable {
    public static final Parcelable.Creator<CustomData> CREATOR
            = new Parcelable.Creator<CustomData>() {

        @Override
        public CustomData createFromParcel(Parcel parcel) {
            CustomData customData = new CustomData();
            customData.mName = parcel.readString();
            customData.mReferences = new ArrayList<String>();
            parcel.readStringList(customData.mReferences);
            customData.mCreated = new Date(parcel.readLong());
            return customData;
        }

        @Override
        public CustomData[] newArray(int size) {
            return new CustomData[size];
        }
    };
    private String mName;
    private List<String> mReferences;
    private Date mCreated;

    public CustomData() {
        mName = ""; // Defaults to empty string
        mReferences = new ArrayList<String>();
        mCreated = new Date(); // Defaults to now
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<String> getReferences() {
        return mReferences;
    }

    public void setReferences(List<String> references) {
        mReferences = references;
    }

    public Date getCreated() {
        return mCreated;
    }

    public void setCreated(Date created) {
        mCreated = created;
    }

    @Override
    public int describeContents() {
        return 0;

    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mName);
        parcel.writeStringList(mReferences);
        parcel.writeLong(mCreated.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomData that = (CustomData) o;

        return mCreated.equals(that.mCreated) && mName.equals(that.mName);
    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mCreated.hashCode();
        return result;
    }
}
