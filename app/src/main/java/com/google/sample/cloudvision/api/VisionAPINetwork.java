package com.google.sample.cloudvision.api;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.sample.cloudvision.BuildConfig;
import com.google.sample.cloudvision.utils.PackageManagerUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Santiago Carrillo
 *         3/26/18.
 */


public class VisionAPINetwork
{
    private static final String CLOUD_VISION_API_KEY = BuildConfig.API_KEY;

    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";

    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final int MAX_LABEL_RESULTS = 10;

    private static final String TAG = "VisionAPINetwork";

    public static Vision.Images.Annotate prepareAnnotationRequest( Bitmap bitmap, PackageManager packageManager,
                                                                   String packageName, @VisionAPIType String type )
        throws IOException
    {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer = new VisionRequestInitializer( CLOUD_VISION_API_KEY )
        {
            /**
             * We override this so we can inject important identifying fields into the HTTP
             * headers. This enables use of a restricted cloud platform API key.
             */
            @Override
            protected void initializeVisionRequest( VisionRequest<?> visionRequest )
                throws IOException
            {
                super.initializeVisionRequest( visionRequest );

                visionRequest.getRequestHeaders().set( ANDROID_PACKAGE_HEADER, packageName );

                String sig = PackageManagerUtils.getSignature( packageManager, packageName );

                visionRequest.getRequestHeaders().set( ANDROID_CERT_HEADER, sig );
            }
        };

        Vision.Builder builder = new Vision.Builder( httpTransport, jsonFactory, null );
        builder.setVisionRequestInitializer( requestInitializer );

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests( new ArrayList<AnnotateImageRequest>()
        {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress( Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream );
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent( imageBytes );
            annotateImageRequest.setImage( base64EncodedImage );

            // add the features we want
            annotateImageRequest.setFeatures( new ArrayList<Feature>()
            {{
                Feature feature = new Feature();
                feature.setType( type );
                feature.setMaxResults( MAX_LABEL_RESULTS );
                add( feature );
            }} );

            // Add the list of one thing to the request
            add( annotateImageRequest );
        }} );

        Vision.Images.Annotate annotateRequest = vision.images().annotate( batchAnnotateImagesRequest );
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent( true );
        Log.d( TAG, "created Cloud Vision request object, sending request" );

        return annotateRequest;
    }
}
