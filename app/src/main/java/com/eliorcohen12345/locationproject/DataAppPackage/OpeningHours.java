package com.eliorcohen12345.locationproject.DataAppPackage;

import java.io.Serializable;

class OpeningHours implements Serializable {

    private boolean open_now;

    public boolean isOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

}
