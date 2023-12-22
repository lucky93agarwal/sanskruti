package com.sanskruti.volotek.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sanskruti.volotek.custom.poster.model.ThumbBG;
import com.sanskruti.volotek.custom.poster.model.ThumbnailInfo;
import com.sanskruti.volotek.custom.poster.model.ThumbnailThumbFull;
import com.sanskruti.volotek.custom.poster.model.ThumbnailWithList;
import com.sanskruti.volotek.model.video.DashboardTemplateResponseMain;
import com.sanskruti.volotek.model.video.TemplateResponse;
import com.sanskruti.volotek.respository.TemplateBasedRepository;

public class TemplateBasedViewModel extends ViewModel {

    TemplateBasedRepository respository;

    public TemplateBasedViewModel() {
        this.respository = new TemplateBasedRepository();
    }


   public LiveData<DashboardTemplateResponseMain> getDashboardTemplate(Integer page){
        return respository.getDashboardTemplate(page);
    }

  public LiveData<TemplateResponse> getSearchedTemplates(Integer page, String cat, Integer limit){
        return respository.getSearchedTemplates(page,cat,limit);
    }
public LiveData<ThumbnailWithList> getPosterHome(Integer page){
        return respository.getPosterHome(page);
    }


    public LiveData<ThumbBG> getPosterStickers() {

        return respository.getPosterStickers();
    }

    public LiveData<ThumbnailThumbFull> getPosterSearch(Integer page, String cat) {

        return respository.getPosterSearch(page,cat);
    }
public LiveData<ThumbnailInfo> getPosterData(Integer page) {

        return respository.getPosterData(page);
    }


    public LiveData<ThumbBG> getPosterBackground() {

        return respository.getPosterBackground();
    }


}
