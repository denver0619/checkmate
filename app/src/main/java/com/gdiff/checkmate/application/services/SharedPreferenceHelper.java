package com.gdiff.checkmate.application.services;

public class SharedPreferenceHelper {
    private static SharedPreferenceHelper _instance;

    private SharedPreferenceHelper() {}

    public static SharedPreferenceHelper getInstance() {
        if (_instance == null) {
            synchronized (SharedPreferenceHelper.class) {
                if(_instance==null){
                    _instance = new SharedPreferenceHelper();
                }

            }
        }
        return _instance;
    }


}
