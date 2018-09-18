package test.jitendra.android.androidchat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import test.jitendra.android.androidchat.lib.GlideImageLoader;
import test.jitendra.android.androidchat.lib.ImageLoader;


public class AndroidChatApplication extends Application {
    private ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        setupFirebase();
        setupImageLoader();
    }

    private void setupImageLoader() {
        imageLoader = new GlideImageLoader(this);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    private void setupFirebase(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
