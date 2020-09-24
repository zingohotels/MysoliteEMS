package app.zingo.mysolite.Custom;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by one on 3/12/15.
 */
public class MyRegulerText extends AppCompatTextView {

    public MyRegulerText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyRegulerText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyRegulerText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Lato-Regular.ttf");
            setTypeface(tf);
        }
    }

}