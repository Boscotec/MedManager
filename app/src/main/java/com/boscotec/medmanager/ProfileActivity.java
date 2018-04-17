package com.boscotec.medmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInAccount account = null;

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;

    private Toolbar mToolbar;
    private ImageView mProfilePic;
    private EditText mNameText, mAddressText, mPhoneText, mEmailText;
    private RadioGroup genderGroup;

    Uri mFileUri;
    private String userChoosenTask;
    CharSequence[] items = {"Camera", "Gallery", "Cancel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        account = GoogleSignIn.getLastSignedInAccount(this);
        String email = account.getEmail();
     //   String displayName = account.getDisplayName();
      //  String url = account.getPhotoUrl().toString();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mProfilePic = findViewById(R.id.user_photo);
        mProfilePic.setOnClickListener(this);
        mNameText = findViewById(R.id.name);
        mAddressText = findViewById(R.id.address);
        mPhoneText = findViewById(R.id.phoneNo);
        mEmailText = findViewById(R.id.email);

        genderGroup = findViewById(R.id.rg_gender);
        Button btnSave = findViewById(R.id.save_button);
        btnSave.setOnClickListener(this);

        mEmailText.setText(email);
    }

    public void setName(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Name");

        // Create EditText box to input name
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String in = input.getText().toString();
                if (in.length() == 0) {
                    mNameText.setText(in);
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
     }

    public void setAddress(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Address");

        // Create EditText box to input address
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText().toString().length() == 0) {
                    mAddressText.setText(input.getText().toString().trim());
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }

    public void setPhoneNumber(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Phone Number");

        // Create EditText box to input number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText().toString().length() == 0) {
                    mPhoneText.setText(input.getText().toString().trim());
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.user_photo:
                getImageFrom();
                break;
            case R.id.save_button:
                saveToDatabase();
                break;
        }
    }

    private void saveToDatabase(){
        if(!validated()) return;

    }

    private boolean validated(){

        String mName = mNameText.getText().toString();
        if (TextUtils.isEmpty(mName)){
            mNameText.setError(getString(R.string.no_name));
            mNameText.requestFocus();
            return false;
        }

        return true;
    }

    private void getImageFrom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Get photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int chosenitem) {
                boolean result = TimeUtils.checkStoragePermission(getApplicationContext());
                if (items[chosenitem].equals(items[0])) {
                    userChoosenTask = items[0].toString();
                    if(result) photoCameraIntent();
                } else if (items[chosenitem].equals(items[1])) {
                    userChoosenTask =items[1].toString();
                    if(result) photoGalleryIntent();
                } else if (items[chosenitem].equals(items[2])) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void photoCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAMERA_REQUEST);
    }

    private void photoGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case TimeUtils.REQUEST_EXTERNAL_STORAGE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals(items[0].toString()))
                        photoCameraIntent();
                    else if(userChoosenTask.equals(items[1].toString()))
                        photoGalleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                mFileUri = data.getData();
                if (mFileUri != null) onSelectFromGalleryResult(data);
            }
            else if (requestCode == IMAGE_CAMERA_REQUEST) {
                mFileUri = data.getData();
                if (mFileUri != null) onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        ImageSaver saver =
                new ImageSaver(this)
                        .setFileName(System.currentTimeMillis() + ".jpg");
        saver.save(thumbnail);

        /*
        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        mProfilePic.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mProfilePic.setImageBitmap(bm);
    }

    // On clicking menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadImage(String url) {
        if(url.length() == 0) return;

        Glide.with(this).load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(new ImageView(this));
    }

}