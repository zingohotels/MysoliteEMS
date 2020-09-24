package app.zingo.mysolite.Custom;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

public class MyArialText extends AppCompatTextView {

    public MyArialText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyArialText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyArialText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/arial_bold.ttf");
            setTypeface(tf);
        }
    }

}