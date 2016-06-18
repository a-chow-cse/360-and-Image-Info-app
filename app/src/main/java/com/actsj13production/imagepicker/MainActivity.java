package com.actsj13production.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.media.*;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PICTURE_SELECTED=1;
    boolean selectedOrNot=false;
    ImageView iv;
    Button selectPhoto;
    Button proceed;
    Bitmap yourSelectedImage;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv=(ImageView)findViewById(R.id.imageView);
        selectPhoto=(Button)findViewById(R.id.button);
        proceed=(Button)findViewById(R.id.ProceedButton);
        selectPhoto.setTextColor(Color.WHITE);
        proceed.setTextColor(Color.WHITE);
        selectPhoto.setOnClickListener(this);
        proceed.setOnClickListener(this);
    }

    public void btnClick(View v)
    {
        Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,PICTURE_SELECTED);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case PICTURE_SELECTED:
                if(resultCode==RESULT_OK)
                {
                    Uri uri=data.getData();
                    String[] projection={MediaStore.Images.Media.DATA};

                    Cursor cursor= getContentResolver().query(uri,projection,null,null,null);
                    cursor.moveToFirst();

                    int columnIndex=cursor.getColumnIndex(projection[0]);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();

                    yourSelectedImage= BitmapFactory.decodeFile(filePath);
                    Drawable drawable= new BitmapDrawable(yourSelectedImage);
                    try {
                        ExifInterface exif = new ExifInterface(filePath);
                        exif.getAttribute(ExifInterface.TAG_DATETIME);

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    int height=drawable.getMinimumHeight();
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int windowHeight=size.y;
                    if(height>=(windowHeight*2)/3)
                    {
                        height=(windowHeight*2)/3;
                    }
                    iv.getLayoutParams().height=height;
                    iv.setBackground(drawable);

                }
                break;
        }
    }

    public void nextActivityMetaData(View v)
    {
        Intent nextActivity= new Intent(this,FindingMetaDataActivity.class);
        nextActivity.putExtra("filePath",filePath);
        createImageFromBitmap(yourSelectedImage);
        startActivity(nextActivity);

    }
    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button:
                btnClick(v);
                selectedOrNot=true;
                break;
            case R.id.ProceedButton:
                if(selectedOrNot==false)
                {
                    Toast.makeText(this, "Please select an Image",Toast.LENGTH_LONG).show();
                }
                else
                {
                    nextActivityMetaData(v);
                }

                break;

        }
    }
}
