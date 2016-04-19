package vesnell.pl.quiz;

import android.app.Application;
import android.content.Context;

/**
 * Created by alek6 on 19.04.2016.
 */
public class App extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getContext() {
        return App.context;
    }

}
