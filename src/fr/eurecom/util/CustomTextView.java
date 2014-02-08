package fr.eurecom.util;

import fr.eurecom.cardify.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class CustomTextView extends TextView{
	
	private final static int HOBBY_OF_NIGHT= 0;
    private final static int LAVOIR = 1;

	public CustomTextView(Context context){
		super(context);
	}
	
	public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);  
    }
	
	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
    }
	
	private void parseAttributes(Context context, AttributeSet attrs){
        
		TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);

        //The value 0 is a default, but shouldn't ever be used since the attr is an enum
        int typeface = values.getInt(R.styleable.CustomTextView_typeface, 0);
        
        values.recycle();

        switch(typeface) {
            case HOBBY_OF_NIGHT: default:
            	Typeface hobby_of_night = Typeface.createFromAsset(context.getAssets(), "fonts/Hobby-of-night.ttf");
                super.setTypeface(hobby_of_night); 
                break;
            case LAVOIR:
            	Typeface lavoir = Typeface.createFromAsset(context.getAssets(), "fonts/lavoir.ttf");
                setTypeface(lavoir);
        }

	}	
}