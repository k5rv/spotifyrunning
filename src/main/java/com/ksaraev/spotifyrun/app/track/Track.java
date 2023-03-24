package com.ksaraev.spotifyrun.app.track;

import lombok.Data;

@Data
public class Track implements AppTrack {

    private String id;
    private String name;
}
