package io.github.xiewinson.movingparticlesview.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by winson on 2017/5/24.
 */

public class ScreenUtil {

    public static float dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
