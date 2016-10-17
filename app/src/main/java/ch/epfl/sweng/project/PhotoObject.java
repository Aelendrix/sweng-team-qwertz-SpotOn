package ch.epfl.sweng.project;


import java.sql.Timestamp;

public class PhotoObject {

    private String name;
    private Timestamp createdDate;
    private Timestamp timeStamp;
    private double latitude;
    private double longitude;
    private int pictureId;
    private int radius;
    private int user;
    private String fullImgLink;
    private String thumbImgLink;

    Image thumbnailImg;
    boolean hasThumbImage;
    Image fullSizeImg;
    boolean hasFullSizedImage;

    public sendToDatabase() {
        //send fullSizeImage to fileServer
        fullSizeImg = null;
        hasFullSIzeImage = false;
    }

    public Image getFullSizeImage() {
        if (hasFullSizedImage) {
            return fullSizeImg;
        }else{
        //fetch full size image from file server
        hasFullSizeImage = true;
        return fullSizeImg;
        }
    }


    public PhotoObject(){
    //constructor needed for firebase object upload
    }

    public PhotoObject(String name){

    }


    //GETTER


}
