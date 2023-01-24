package com.tectoy.play.utils;

public interface IMPlayer {

    void setSource(String url, int position) throws MPlayerException;


    void setDisplay(IMDisplay display);

    void play() throws MPlayerException;

    void pause();


    void onPause();

    void onResume();

    void onDestroy();

}
