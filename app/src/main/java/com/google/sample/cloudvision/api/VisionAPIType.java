package com.google.sample.cloudvision.api;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;

import static com.google.sample.cloudvision.api.VisionAPIType.FACE_DETECTION;
import static com.google.sample.cloudvision.api.VisionAPIType.LABEL_DETECTION;
import static com.google.sample.cloudvision.api.VisionAPIType.TEXT_DETECTION;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author Santiago Carrillo
 *         3/26/18.
 */


@Retention( SOURCE )
@StringDef( { FACE_DETECTION, LABEL_DETECTION, TEXT_DETECTION } )
public @interface VisionAPIType
{
    String FACE_DETECTION = "FACE_DETECTION";

    String LABEL_DETECTION = "LABEL_DETECTION";

    String TEXT_DETECTION = "TEXT_DETECTION";
}





