package com.actsj13production.imagepicker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FindingMetaDataActivity extends AppCompatActivity implements View.OnClickListener {
    Bitmap b;
    boolean modifiedOrNot;
    TextView myTextView;
    Button ModifyData, SaveIt;
    String filename;
    ExifInterface exif;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_meta_data);

        filename = getIntent().getStringExtra("filePath");
        b = BitmapFactory.decodeFile(filename);

        try {
            exif = new ExifInterface(filename);
            //b= BitmapFactory.decodeStream(this.openFileInput("myImage"));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
        }

        ModifyData = (Button) findViewById(R.id.Modify);
        SaveIt = (Button) findViewById(R.id.SaveToSDcard);
        ModifyData.setOnClickListener(this);
        SaveIt.setOnClickListener(this);
        modifiedOrNot = false;
        Viewdata();
    }

    public void Viewdata() {
        myTextView = (TextView) findViewById(R.id.textview);
        ShowExif(exif);
    }

    private void ShowExif(ExifInterface exif) {
        String myAttribute = "";
        myAttribute += getTagString(ExifInterface.TAG_DATETIME, exif);
        myAttribute += getTagString(ExifInterface.TAG_FLASH, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif);
        myAttribute += getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif);
        myAttribute += "Resolution : " + exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) + " * " + exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) + "\n";
        myAttribute += getTagString(ExifInterface.TAG_MAKE, exif);
        myAttribute += getTagString(ExifInterface.TAG_MODEL, exif);
        myAttribute += getTagString(ExifInterface.TAG_ORIENTATION, exif);
        myAttribute += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);
        myTextView.setText(myAttribute);
        myTextView.setTextColor(Color.parseColor("#000000"));
        try {
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }


    public void modifyData() {

        int length = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
        int width = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
        if (length != width * 2)
        {
            Toast.makeText(this,"Image is not right proportionate", Toast.LENGTH_LONG).show();
            ShowExif(exif);
        }
        else {
            if (modifiedOrNot == false) {
                exif.setAttribute(ExifInterface.TAG_MAKE, "RICOH");
                exif.setAttribute(ExifInterface.TAG_MODEL, "RICOH THETA S");
                ShowExif(exif);
                modifiedOrNot = true;
                Toast.makeText(this, "Modified", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Already Modified", Toast.LENGTH_LONG).show();
            }
        }

        try {
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveIt() {
        boolean success = false;
        try {
            String path = Environment.getExternalStorageDirectory().toString();
            new File(path + "/DCIM/Camera").mkdirs();
            String newPath = path + "/DCIM/Camera/image.jpg";
            File image = new File(newPath);

            FileOutputStream out = new FileOutputStream(image);

            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();


            MediaStore.Images.Media.insertImage(getContentResolver(), image.getAbsolutePath(), image.getName(), image.getName());
            success = true;
        } catch (Exception o) {
            o.printStackTrace();
        }

        if (success) {
            Toast.makeText(this, "Image saved with success", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error during image saving", Toast.LENGTH_LONG).show();
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Modify:
                modifyData();
                break;
            case R.id.SaveToSDcard:
                //Toast.makeText(this, "Image saved with success", Toast.LENGTH_LONG).show();
                SaveIt();
                break;
        }
    }
}