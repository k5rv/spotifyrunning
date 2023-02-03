package com.ksaraev.spotifyrun.client.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.List;

public record AddItemsRequest(@JsonProperty("uris") List<URI> itemUris) {}
