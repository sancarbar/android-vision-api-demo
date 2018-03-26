package com.google.sample.cloudvision.utils;

import android.graphics.Bitmap;

/**
 * Created by sancarbar on 3/26/18.
 */

public class ImageUtils
{

    public static Bitmap scaleBitmapDown( Bitmap bitmap, int maxDimension )
    {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if ( originalHeight > originalWidth )
        {
            resizedHeight = maxDimension;
            resizedWidth = (int) ( resizedHeight * (float) originalWidth / (float) originalHeight );
        }
        else if ( originalWidth > originalHeight )
        {
            resizedWidth = maxDimension;
            resizedHeight = (int) ( resizedWidth * (float) originalHeight / (float) originalWidth );
        }
        else if ( originalHeight == originalWidth )
        {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap( bitmap, resizedWidth, resizedHeight, false );
    }
}
