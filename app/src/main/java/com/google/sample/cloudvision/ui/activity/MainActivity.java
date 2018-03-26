/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sample.cloudvision.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.sample.cloudvision.R;
import com.google.sample.cloudvision.api.VisionAPINetwork;
import com.google.sample.cloudvision.api.VisionAPIType;
import com.google.sample.cloudvision.asynctask.FaceDetectionTask;
import com.google.sample.cloudvision.asynctask.LabelDetectionTask;
import com.google.sample.cloudvision.asynctask.TextDetectionTask;
import com.google.sample.cloudvision.utils.ImageUtils;
import com.google.sample.cloudvision.utils.PermissionUtils;

import java.io.File;
import java.io.IOException;


public class MainActivity
    extends AppCompatActivity
{


    public static final String FILE_NAME = "temp.jpg";

    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int GALLERY_PERMISSIONS_REQUEST = 0;

    private static final int GALLERY_IMAGE_REQUEST = 1;

    public static final int CAMERA_PERMISSIONS_REQUEST = 2;

    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;

    private ImageView mMainImage;

    private AppCompatSpinner spinner;

    private Bitmap imageBitmap;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( view ->
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
                                    builder.setMessage( R.string.dialog_select_prompt ).setPositiveButton(
                                        R.string.dialog_select_gallery,
                                        ( dialog, which ) -> startGalleryChooser() ).setNegativeButton(
                                        R.string.dialog_select_camera, ( dialog, which ) -> startCamera() );
                                    builder.create().show();
                                } );

        mImageDetails = findViewById( R.id.image_details );
        mMainImage = findViewById( R.id.main_image );
        spinner = findViewById( R.id.detection_options );

        ArrayAdapter<CharSequence> adapter =
            ArrayAdapter.createFromResource( this, R.array.image_detection_options_array,
                                             android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner.setAdapter( adapter );

    }

    public void startGalleryChooser()
    {
        if ( PermissionUtils.requestPermission( this, GALLERY_PERMISSIONS_REQUEST,
                                                Manifest.permission.READ_EXTERNAL_STORAGE ) )
        {
            Intent intent = new Intent();
            intent.setType( "image/*" );
            intent.setAction( Intent.ACTION_GET_CONTENT );
            startActivityForResult( Intent.createChooser( intent, "Select a photo" ), GALLERY_IMAGE_REQUEST );
        }
    }

    public void startCamera()
    {
        if ( PermissionUtils.requestPermission( this, CAMERA_PERMISSIONS_REQUEST,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA ) )
        {
            Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
            Uri photoUri = FileProvider.getUriForFile( this, getApplicationContext().getPackageName() + ".provider",
                                                       getCameraFile() );
            intent.putExtra( MediaStore.EXTRA_OUTPUT, photoUri );
            intent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
            startActivityForResult( intent, CAMERA_IMAGE_REQUEST );
        }
    }

    public File getCameraFile()
    {
        File dir = getExternalFilesDir( Environment.DIRECTORY_PICTURES );
        return new File( dir, FILE_NAME );
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult( requestCode, resultCode, data );

        Uri photoUri = null;

        if ( requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null )
        {
            photoUri = data.getData();
        }
        else if ( requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK )
        {
            photoUri = FileProvider.getUriForFile( this, getApplicationContext().getPackageName() + ".provider",
                                                   getCameraFile() );
        }

        try
        {
            if ( photoUri != null )
            {
                imageBitmap =
                    ImageUtils.scaleBitmapDown( MediaStore.Images.Media.getBitmap( getContentResolver(), photoUri ),
                                                MAX_DIMENSION );
                mMainImage.setImageBitmap( imageBitmap );
            }
        }
        catch ( IOException e )
        {
            Log.d( TAG, "Image picking failed because " + e.getMessage() );
            Toast.makeText( this, R.string.image_picker_error, Toast.LENGTH_LONG ).show();
        }

    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults )
    {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        switch ( requestCode )
        {
            case CAMERA_PERMISSIONS_REQUEST:
                if ( PermissionUtils.permissionGranted( requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults ) )
                {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if ( PermissionUtils.permissionGranted( requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults ) )
                {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage( String type )
    {
        if ( imageBitmap != null )
        {
            callCloudVision( imageBitmap, type );
        }
        else
        {
            Log.d( TAG, "Image picker gave us a null image." );
            Toast.makeText( this, R.string.image_picker_error, Toast.LENGTH_LONG ).show();
        }
    }


    public void onSendButtonClicked( View view )
    {
        String option = (String) spinner.getSelectedItem();
        String type = VisionAPIType.LABEL_DETECTION;
        switch ( option )
        {
            case "Face Detection":
                type = VisionAPIType.FACE_DETECTION;
                break;
            case "Label Detection":
                type = VisionAPIType.LABEL_DETECTION;
                break;
            case "Text Detection":
                type = VisionAPIType.TEXT_DETECTION;
                break;
        }
        uploadImage( type );
    }


    private void callCloudVision( final Bitmap bitmap, @VisionAPIType String type )
    {
        // Switch text to loading
        mImageDetails.setText( R.string.loading_message );

        // Do the real work in an async task, because we need to use the network anyway
        try
        {

            AsyncTask<Object, Void, String> detectionTask = null;
            switch ( type )
            {
                case VisionAPIType.FACE_DETECTION:
                    detectionTask = new FaceDetectionTask( this, VisionAPINetwork.prepareAnnotationRequest( bitmap,
                                                                                                            getPackageManager(),
                                                                                                            getPackageName(),
                                                                                                            type ) );
                    break;
                case VisionAPIType.LABEL_DETECTION:
                    detectionTask = new LabelDetectionTask( this, VisionAPINetwork.prepareAnnotationRequest( bitmap,
                                                                                                             getPackageManager(),
                                                                                                             getPackageName(),
                                                                                                             type ) );
                    break;

                case VisionAPIType.TEXT_DETECTION:
                    detectionTask = new TextDetectionTask( this, VisionAPINetwork.prepareAnnotationRequest( bitmap,
                                                                                                            getPackageManager(),
                                                                                                            getPackageName(),
                                                                                                            type ) );
                    break;
            }

            if ( detectionTask != null )
            {
                detectionTask.execute();
            }
        }
        catch ( IOException e )
        {
            Log.d( TAG, "failed to make API request because of other IOException " + e.getMessage() );
        }
    }


}
