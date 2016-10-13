package ch.epfl.sweng.project.backgroudapplication;


import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bruno on 11/10/2016.
 */

public class PhotoList {
    private List<PhotoFile> mPhotos;

    public  PhotoList(){
        mPhotos = new ArrayList<PhotoFile>();
    }

    public List<PhotoFile> getPhotos(){
        return Collections.unmodifiableList(mPhotos);
    }

    public int size(){
        return mPhotos.size();
    }

    public void deletePhoto(int index){
        mPhotos.get(index).removeFile();
        mPhotos.remove(index);
    }

    public void addPhoto(PhotoFile photo){
       mPhotos.add(photo);
    }
}
