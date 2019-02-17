package com.uni.unitor.unitorm2.File.sharedpreference;

public interface PreferenceKey {
    String KEY_REPOSITORY_INFO = "Info";
    String KEY_REPOSITORY_KILL = "Kill";

    String KEY_INFO_TITLE = "Title";
    String KEY_INFO_PRODUCER = "Producer";
    String KEY_INFO_CHAIN = "Chain";
    String KEY_INFO_PATH = "Path";

    String KEY_KILL_DIED = "Killed";
    String KEY_SOUND_INIT = "SIN";
    boolean KEY_KILL_SELF = true;
    boolean KEY_KILL_FORCE = false;
}
