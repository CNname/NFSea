package fi.jamk.nfsea;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by h3409 on 8.11.2016.
 */

public class NFSeaLightTextView extends TextView {

    public NFSeaLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));
    }
}
