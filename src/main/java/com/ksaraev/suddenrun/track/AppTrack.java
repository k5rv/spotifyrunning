package com.ksaraev.suddenrun.track;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public interface AppTrack {

    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

}
