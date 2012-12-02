package eu.andlabs.studiolounge.lobby.parser;

import android.graphics.drawable.Drawable;

public class PlayResult {

    public Drawable mPromo;
    public String mPackageName;
    
    public PlayResult(Drawable pPromo, String pPackageName) {
        super();
        mPromo = pPromo;
        mPackageName = pPackageName;
    }

    public Drawable getPromo() {
        return mPromo;
    }

    public void setPromo(Drawable pPromo) {
        mPromo = pPromo;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String pPackageName) {
        mPackageName = pPackageName;
    }
}
