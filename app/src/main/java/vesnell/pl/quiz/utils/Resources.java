package vesnell.pl.quiz.utils;

import vesnell.pl.quiz.App;

/**
 * Created by alek6 on 19.04.2016.
 */
public class Resources {

    public static String getString(int resId, Object ... params) {
        return App.getContext().getString(resId, params);
    }

}
