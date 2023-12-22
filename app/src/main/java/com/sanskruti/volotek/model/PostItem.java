package com.sanskruti.volotek.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostItem implements Serializable {

    @NonNull
    @SerializedName("postId")
    public String postId;

    @SerializedName("id")
    public String fest_id;

    @SerializedName("type")
    public String type;

    @SerializedName("thumbnail")
    public String thumbnail;
    @SerializedName("image")
    public String image_url;

    @SerializedName("language")
    public String language;

    @SerializedName("is_paid")
    public boolean is_premium;

    public boolean is_trending;

    @SerializedName("video")
    public boolean is_video;

    public PostItem(@NonNull String postId, String fest_id, String type, String image_url, String language, boolean is_premium, boolean is_trending, boolean is_video) {
        this.postId = postId;
        this.fest_id = fest_id;
        this.type = type;
        this.image_url = image_url;
        this.language = language;
        this.is_premium = is_premium;
        this.is_trending = is_trending;
        this.is_video = is_video;
    }

}
