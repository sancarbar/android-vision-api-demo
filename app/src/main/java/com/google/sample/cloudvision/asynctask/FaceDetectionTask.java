package com.google.sample.cloudvision.asynctask;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.sample.cloudvision.ui.activity.MainActivity;

import java.util.List;
import java.util.Locale;

/**
 * @author Santiago Carrillo
 * 3/26/18.
 */

public class FaceDetectionTask
    extends DetectionTask
{
    public FaceDetectionTask( MainActivity activity, Vision.Images.Annotate annotate )
    {
        super( activity, annotate );
    }

    @Override
    protected String convertResponseToString( BatchAnnotateImagesResponse response )
    {
        StringBuilder message = new StringBuilder( "I found these things:\n\n" );

        List<FaceAnnotation> faceAnnotations = response.getResponses().get( 0 ).getFaceAnnotations();
        if ( faceAnnotations != null )
        {
            for ( FaceAnnotation annotation : faceAnnotations )
            {
                message.append( String.format( Locale.US, "%.3f: %s", annotation.getDetectionConfidence(),
                                               "Detection Confidence" ) );
                message.append( "\n" );

                String emotion = "Not detected";

                if ( annotation.getJoyLikelihood().equals( "VERY_LIKELY" ) )
                {
                    emotion = "Joy";
                }
                else if ( annotation.getSorrowLikelihood().equals( "VERY_LIKELY" ) )
                {
                    emotion = "Sorrow";
                }
                else if ( annotation.getAngerLikelihood().equals( "VERY_LIKELY" ) )
                {
                    emotion = "Anger";
                }
                else if ( annotation.getSurpriseLikelihood().equals( "VERY_LIKELY" ) )
                {
                    emotion = "Surprise";
                }

                message.append( String.format( Locale.US, "%s: %s", "Emotion: ", emotion ) );
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
