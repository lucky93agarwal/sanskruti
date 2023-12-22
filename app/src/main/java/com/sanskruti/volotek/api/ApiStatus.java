package com.sanskruti.volotek.api;

import com.sanskruti.volotek.model.FrameModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiStatus {

    @SerializedName("status")
    public final String status;

    @SerializedName("message")
    public final String message;

    @SerializedName("frames")
    List<FrameModel> desiredFramesModels;
    public List<FrameModel> getDesiredFramesModels() {
        return desiredFramesModels;
    }

    public ApiStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }
}