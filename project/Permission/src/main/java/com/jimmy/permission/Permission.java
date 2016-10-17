package com.jimmy.permission;

/**
 * Created by jinguochong on 16-10-12.
 */

public interface Permission {

    Permission permission(String... permissions);

    Permission requestCode(int requestCode);

    void send();

    Permission setFirstApplyListener(IFirstApplyListener firstApplyListener);

    Permission setAgainApplyListener(IAgainApplyListener againApplyListener);
}
