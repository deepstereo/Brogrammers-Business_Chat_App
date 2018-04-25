package com.centennialcollege.brogrammers.businesschatapp.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.widget.TextView;

import com.centennialcollege.brogrammers.businesschatapp.R;

/**
 * Utility class for getting and setting user related attributes.
 */
public class UserAttributesUtils {

    private static final String TAG = HashHelper.class.getSimpleName();

    /**
     * Utility method to calculate and return a color for a user based on it's username.
     * @return an Integer color value.
     */
    public static int getAccountColor(String name, Context context) {
        //Getting the array of pre-defined colors.
        TypedArray colors = context.getResources().obtainTypedArray(R.array.account_colors);
        // Calculate the hashcode of user's username's string, and if it's a negative integer, convert it to a positive value.
        int unsignedHash = name.hashCode() > 0 ? name.hashCode() : name.hashCode() * -1;
        int colorToAssign = colors.getColor(unsignedHash % colors.length(), 0);
        colors.recycle();
        return colorToAssign;
    }

    /**
     * Utility method to set a color for a user's profile photo in case user hasn't explicitly set one yet.
     * @param accountCircle The view object on which to apply color as background.
     * @param name Username of user.
     */
    public static void setAccountColor(TextView accountCircle, String name, Context context) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        GradientDrawable gradientDrawable = (GradientDrawable) accountCircle.getBackground().mutate();
        gradientDrawable.setColor(getAccountColor(name, context));
        accountCircle.setText(String.valueOf(name.toUpperCase().charAt(0)));
    }

}
