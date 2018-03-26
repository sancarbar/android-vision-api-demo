package com.google.sample.cloudvision.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.sample.cloudvision.ui.activity.MainActivity;
import com.google.sample.cloudvision.R;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * @author Santiago Carrillo
 * 3/26/18.
 */


public abstract class DetectionTask
    extends AsyncTask<Object, Void, String>
{
    private final String TAG = "DetectionTask";

    private final WeakReference<MainActivity> mActivityWeakReference;

    private Vision.Images.Annotate mRequest;

    DetectionTask( MainActivity activity, Vision.Images.Annotate annotate )
    {
        mActivityWeakReference = new WeakReference<>( activity );
        mRequest = annotate;
    }

    @Override
    protected String doInBackground( Object... params )
    {
        try
        {
            Log.d( TAG, "created Cloud Vision request object, sending request" );
            BatchAnnotateImagesResponse response = mRequest.execute();
            return convertResponseToString( response );

        }
        catch ( GoogleJsonResponseException e )
        {
            Log.d( TAG, "failed to make API request because " + e.getContent() );
        }
        catch ( IOException e )
        {
            Log.d( TAG, "failed to make API request because of other IOException " + e.getMessage() );
        }
        return "Cloud Vision API request failed. Check logs for details.";
    }

    protected abstract String convertResponseToString( BatchAnnotateImagesResponse response );

    protected void onPostExecute( String result )
    {
        MainActivity activity = mActivityWeakReference.get();
        if ( activity != null && !activity.isFinishing() )
        {
            TextView imageDetail = activity.findViewById( R.id.image_details );
            imageDetail.setText( result );
        }
    }

}
