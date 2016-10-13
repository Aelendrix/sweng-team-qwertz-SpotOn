package ch.epfl.sweng.project.backgroudapplication;


import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bruno on 11/10/2016.
 *
 * Represents a list of PhotoFiles which we can add and delete photos, and obtains the number
 * of photos stored in it.
 */

public class PhotoList {
    private List<PhotoFile> mPhotos;

    /**
     * Creates a empty list of PhotoFiles
     */
    public  PhotoList(){
        mPhotos = new ArrayList<PhotoFile>();
    }

    /**
     *
     * @return the list of PhotoFiles contained
     */
    public List<PhotoFile> getPhotos(){
        return Collections.unmodifiableList(mPhotos);
    }

    /**
     * computes the size of the list
     * @return the size of the list of PhotoFiles
     */
    public int size(){
        return mPhotos.size();
    }

    /**
     * deletes a photo
     * @param index the position of the element that will be deleted
     */
    public void deletePhoto(int index){
        if(index < this.size()){
            mPhotos.remove(index);
        }
        else{
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * add a PhotoFile to the list
     * @param photo the PhotoFile that will be added in the list
     */
    public void addPhoto(PhotoFile photo){
       mPhotos.add(photo);
    }

    /**
     * determines if the list is empty
     * @return true if the list is empty, else false
     */
    public Boolean isEmpty(){return this.size() == 0; }
}
