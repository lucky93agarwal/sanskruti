package com.sanskruti.volotek.custom.poster.interfaces;


import com.sanskruti.volotek.custom.poster.model.BackgroundImage;

import java.util.ArrayList;


public interface GetSnapListenerData {
    void onSnapFilter(int i, int i2, String str);

    void onSnapFilter(ArrayList<BackgroundImage> arrayList, int i);
}
