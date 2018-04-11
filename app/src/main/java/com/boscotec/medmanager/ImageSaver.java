package com.boscotec.medmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Johnbosco on 11-Apr-18.
 */

public class ImageSaver {
    private String directoryName = "images";
    private String fileName = "image.png";
    private Context context;
    private boolean external = false;

    public ImageSaver(Context context){
        this.context = context;
    }

    public ImageSaver setDirectoryName(String directoryName){
      this.directoryName = directoryName;
      return this;
    }

    public ImageSaver setFileName(String fileName){
        this.fileName = fileName;
        return this;
    }

    public ImageSaver setExternal(boolean external){
        this.external = external;
        return this;
    }


    public boolean deleteFile(){
        File file = createFile();
        return file.delete();
    }

    @NonNull
    private File createFile(){
        File directory;
        if(external){ directory = getAlbumStorageDir(directoryName);}
        else { directory = context.getDir(directoryName, Context.MODE_PRIVATE); }
        return new File(directory, fileName);
    }

    private File getAlbumStorageDir(String albumName){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if(!file.mkdirs()){
            Log.e("ImageSaver", "Directory not created");
        }
        return file;
    }

    private File createDirectoryIfNotExist(String albumName){
        File file = new File(Environment.getExternalStorageDirectory(), File.separator + context.getString(R.string.app_name) + File.separator + albumName);
        if (!file.exists() && !file.mkdirs()) {
            Log.e("ImageSaver", "Directory not created");
        }
        return file;
    }

    public static boolean isExternalStorageWritable(){
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public File loadFile(){
        return createFile();
    }

    public Bitmap loadBitmap(){
        FileInputStream inputStream = null;
        try{
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("ImageSaver", e.getLocalizedMessage());
        }finally {
            try{
                if(inputStream!=null){
                    inputStream.close();
                }
            }
            catch (IOException io){
                io.printStackTrace();
            }
        }
        return null;
    }

    public void save(Bitmap bitmap){
        FileOutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream(createFile());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("ImageSaver", e.getLocalizedMessage());
        }finally {
            try{ if(outputStream!=null) outputStream.close();}
            catch (IOException io){ io.printStackTrace(); }
        }
    }

}
