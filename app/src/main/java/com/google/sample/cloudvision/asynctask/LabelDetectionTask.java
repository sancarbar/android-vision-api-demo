package com.google.sample.cloudvision.asynctask;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.sample.cloudvision.ui.activity.MainActivity;

import java.util.List;
import java.util.Locale;

/**
 * @author Santiago Carrillo
 * 3/26/18.
 */


public class LabelDetectionTask
    extends DetectionTask
{
    public LabelDetectionTask( MainActivity activity, Vision.Images.Annotate annotate )
    {
        super( activity, annotate );
    }

    @Override
    protected String convertResponseToString( BatchAnnotateImagesResponse response )
    {
        StringBuilder message = new StringBuilder( "I found these things:\n\n" );

        List<EntityAnnotation> labels = response.getResponses().get( 0 ).getLabelAnnotations();
        if ( labels != null )
        {
            for ( EntityAnnotation label : labels )
            {
                message.append( String.format( Locale.US, "%.3f: %s", label.getScore(), label.getDescription() ) );

                message.append( "\n" );
            }
        }
        else
        {
            message.append( "nothing" );
        }

        return message.toString();
    }
}
