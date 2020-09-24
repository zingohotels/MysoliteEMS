package app.zingo.mysolite.Custom;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * Created by one on 3/12/15.
 */
public class MyEditText extends AppCompatEditText {

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Lato-Light.ttf");
            setTypeface(tf);
        }
    }

}