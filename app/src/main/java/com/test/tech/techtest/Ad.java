package com.test.tech.techtest;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ad implements Parcelable {

    private String appId;
    private String productName;
    private String productThumbnailURL;
    private String productRating;
    private String productLink;
    private List<String> args;
    private Uri imageURI;

    public Ad (@NonNull String appId, @NonNull String productName, @NonNull String productThumbnailURL, @NonNull String productRating, @NonNull String productLink) {
        this(appId, productName, productThumbnailURL, productRating, productLink, (String) null);
    }

    public Ad(@NonNull String appId, @NonNull String productName, @NonNull String productThumbnailURL, @NonNull String productRating, @NonNull String productLink, String... vars) {
        this.appId = appId;
        this.productName = productName;
        this.productThumbnailURL = productThumbnailURL;
        this.productRating = productRating;
        this.productLink = productLink;
        args = new ArrayList<>();
        Collections.addAll(args, vars);
    }

    protected Ad(Parcel in) {
        appId = in.readString();
        productName = in.readString();
        productThumbnailURL = in.readString();
        productRating = in.readString();
        productLink = in.readString();
        if (args == null) {
            args = new ArrayList<>();
        }
        while (in.dataSize() > 0) {
            args.add(in.readString());
        }
    }

    public Ad(String appId, String productName, String thumbNail, String productRating, String link, String productID, String productDescription, ArrayList<String> varargs) {
        this(appId, productName, thumbNail, productRating, link, productID);
        if (args == null) {
            args = new ArrayList<>();
        }
        for (String s : varargs) {
            args.add(s);
        }
    }

    public Object getAppId() {
        return appId;
    }

    public String getImage() {
        return productThumbnailURL;
    }

    public String getName() {
        return productName;
    }

    public String getRating() {
        return productRating;
    }

    public String getProductLink() {
        return productLink;
    }

    public List<String> getStrings() {
        return args;
    }

    public void setImageURI(Uri uri) {
        this.imageURI = uri;
    }

    public static final Creator<Ad> CREATOR = new Creator<Ad>() {
        @Override
        public Ad createFromParcel(Parcel in) {
            return new Ad(in);
        }

        @Override
        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(appId);
        parcel.writeString(productName);
        parcel.writeString(productThumbnailURL);
        parcel.writeString(productRating);
        parcel.writeString(productLink);
        for (String s : args) {
            parcel.writeString(s);
        }
    }

    public Uri getimageURI() {
        return imageURI;
    }
}
