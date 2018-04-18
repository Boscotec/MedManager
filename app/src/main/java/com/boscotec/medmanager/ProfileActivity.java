package com.boscotec.medmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.boscotec.medmanager.database.DbHelper;
import com.boscotec.medmanager.model.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInAccount account = null;
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private ImageView mProfilePic;
    private TextView mNameText, mAddressText, mPhoneText, mEmailText;
    private RadioGroup genderGroup;
    private Uri mFileUri;
    private String userChoosenTask;
    private CharSequence[] items = {"Camera", "Gallery", "Cancel"};
    private String mPhone, mAddress, mName;
    private boolean isSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar mToolbar = findViewById(R.id.toolbar);
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

        findViewById(R.id.save_button).setOnClickListener(this);
        findViewById(R.id.llAddress).setOnClickListener(this);
        findViewById(R.id.llPhoneNo).setOnClickListener(this);
        checkDb();
    }

    private void checkDb(){
        account = GoogleSignIn.getLastSignedInAccount(this);
        String email = account.getEmail();
        String displayName = account.getDisplayName();

        DbHelper db =  new DbHelper(this);
        User user = db.readUser(email);
        if(user != null){
            isSaved = true;
            mAddressText.setText(user.getAddress());
            mPhoneText.setText(String.valueOf(user.getPhone()));
            Glide.with(this).load(user.getThumbnail()).into(mProfilePic);
        }else{
            isSaved = false;
        }

        mEmailText.setText(email);
        mNameText.setText(displayName);
    }

    public void setTextView(String title,final int id, int textType){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);

        // Create EditText box to input address
        final EditText input = new EditText(this);
        input.setInputType(textType);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String in = input.getText().toString();
                if (in.length() != 0) {  ((TextView) findViewById(id)).setText(in); }}
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}});
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.user_photo: getImageFrom(); break;
            case R.id.save_button: saveToDatabase(); break;
            case R.id.llAddress: setTextView("Enter Address:", R.id.address, InputType.TYPE_CLASS_TEXT); break;
            case R.id.llPhoneNo: setTextView("Enter Phone Number:", R.id.phoneNo, InputType.TYPE_CLASS_PHONE); break;
        }
    }

    private void saveToDatabase(){
        if(!validated()) return;

        DbHelper db =  new DbHelper(this);
        User user = new User();
        user.setEmail(mEmailText.getText().toString());
        user.setPhone(Integer.valueOf(mPhoneText.getText().toString()));
        user.setAddress(mAddressText.getText().toString());
        user.setName(mNameText.getText().toString());
        user.setThumbnail(mFileUri.toString());

        if(isSaved){
            if(db.updateUser(user) > 0) Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }else{
            if(db.insertUser(user) > 0) Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
        db.close();


    }

    private boolean validated(){
        mAddress = mAddressText.getText().toString();
        if (TextUtils.isEmpty(mName)){
            mAddressText.setError(getString(R.string.no_name));
            mAddressText.requestFocus();
            return false;
        }

        mPhone = mPhoneText.getText().toString();
        if (TextUtils.isEmpty(mName)){
            mPhoneText.setError(getString(R.string.no_name));
            mPhoneText.requestFocus();
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

        if(requestCode != TimeUtils.REQUEST_EXTERNAL_STORAGE_PERMISSIONS) return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(userChoosenTask.equals(items[0].toString()))  photoCameraIntent();
            else if(userChoosenTask.equals(items[1].toString())) photoGalleryIntent();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == IMAGE_GALLERY_REQUEST) onSelectFromGalleryResult(data);
            else onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        mFileUri = data.getData();
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
        mFileUri = data.getData();
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
}