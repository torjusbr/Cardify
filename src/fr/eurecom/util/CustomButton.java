package fr.eurecom.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import fr.eurecom.cardify.R;

public class CustomButton extends Button {
    private final static int HOBBY_OF_NIGHT= 0;
    private final static int LAVOIR = 1;
    
    
    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs); 
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
    }
    
	private void parseAttributes(Context ctx, AttributeSet attrs){
    	TypedArray values = ctx.obtainStyledAttributes(attrs, R.styleable.CustomButton);

        //The value 0 is a default, but shouldn't ever be used since the attr is an enum
        int typeface = values.getInt(R.styleable.CustomButton_typeface, 0);
        
        values.recycle();

        if(!isInEditMode()){
        	switch(typeface) {
            case HOBBY_OF_NIGHT: default:
            	Typeface hobby_of_night = Typeface.createFromAsset(ctx.getAssets(), "fonts/Hobby-of-night.ttf");
                setTypeface(hobby_of_night); 
                break;
            case LAVOIR:
            	Typeface lavoir = Typeface.createFromAsset(ctx.getAssets(), "fonts/Lavoir.ttf");
                setTypeface(lavoir);
            	
        	}
        }
        
    }
}