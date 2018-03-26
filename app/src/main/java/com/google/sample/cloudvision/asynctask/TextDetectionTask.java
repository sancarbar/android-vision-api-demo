package com.google.sample.cloudvision.asynctask;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.sample.cloudvision.ui.activity.MainActivity;

/**
 * @author Santiago Carrillo
 *         3/26/18.
 */

public class TextDetectionTask
    extends DetectionTask
{
    public TextDetectionTask( MainActivity activity, Vision.Images.Annotate annotate )
    {
        super( activity, annotate );
    }

    @Override
    protected String convertResponseToString( BatchAnnotateImagesResponse response )
    {
        StringBuilder message = new StringBuilder( "I found these things:\n\n" );

        AnnotateImageResponse annotateImageResponse = response.getResponses().get( 0 );
        if ( annotateImageResponse != null )
        {
            message.append( "text: " ).append( annotateImageResponse.getFullTextAnnotation().getText() );
            message.append( "\n" );
        }
        else
        {
            message.append( "nothing" );
        }

        return message.toString();
    }
}
