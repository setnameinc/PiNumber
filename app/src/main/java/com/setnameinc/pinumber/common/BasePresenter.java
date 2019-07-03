package com.setnameinc.pinumber.common;

public abstract class BasePresenter<View> {

    private View view;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public abstract void onStart();
    public abstract void onStop();

}
