package barhop.app;

import android.app.Application;

import io.realm.Realm;

public class BarHop extends Application {

    public void onCreate()
    {
        super.onCreate();

        Realm.init(this);

    }
}
