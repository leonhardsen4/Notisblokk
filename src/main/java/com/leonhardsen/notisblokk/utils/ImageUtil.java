package com.leonhardsen.notisblokk.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class ImageUtil {

    public static void insertImage(String string, ImageView imageView){
        File logoFile = new File("src/main/resources/com/leonhardsen/notisblokk/image/" + string);
        Image logoImage = new Image(logoFile.toURI().toString());
        imageView.setImage(logoImage);
    }

}
