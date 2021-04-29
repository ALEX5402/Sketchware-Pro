package mod.hey.studios.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Helper {

    public static Type TYPE_MAP = new TypeToken<HashMap<String, Object>>() {
    }.getType();
    public static Type TYPE_MAP_LIST = new TypeToken<ArrayList<HashMap<String, Object>>>() {
    }.getType();
    public static Type TYPE_STRING = new TypeToken<ArrayList<String>>() {
    }.getType();
    public static Type TYPE_STRING_MAP = new TypeToken<HashMap<String, String>>() {
    }.getType();

    public static void fixFileprovider() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Class.forName("android.os.StrictMode").getMethod("disableDeathOnFileUriExposure").invoke(null);
            } catch (ClassNotFoundException e) {
                throw new NoClassDefFoundError(e.getMessage());
            } catch (Exception e) {
                Log.e("Helper", "An error occurred while trying to fix death on file URI exposure: " + e.getMessage(), e);
            }
        }
    }

    public static View.OnClickListener getBackPressedClickListener(final Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        };
    }

    public static View.OnClickListener getDialogDismissListener(final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
    }

    public static void applyRipple(Context context, View view) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843868, typedValue, true);
        view.setBackgroundResource(typedValue.resourceId);
        view.setClickable(true);
    }

    public static void applyRippleToToolbarView(View view) {
        GradientDrawable content = new GradientDrawable();
        content.setColor(Color.parseColor("#008dcd"));
        content.setCornerRadius(90);
        view.setBackground(new RippleDrawable(new ColorStateList(new int[][]{new int[]{0}}, new int[]{Color.parseColor("#64b5f6")}),
                content, null));
    }

    public static void applyRippleEffect(final View target, final int rippleColor, int standardColor) {
        RippleDrawable drawable = new RippleDrawable(new ColorStateList(new int[][]{
                new int[]{}
        }, new int[]{
                rippleColor
        }), new ColorDrawable(standardColor), null);
        if (!target.isClickable()) {
            target.setClickable(true);
        }
        target.setBackground(drawable);
    }
}