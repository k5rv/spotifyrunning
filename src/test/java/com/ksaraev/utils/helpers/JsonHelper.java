package com.ksaraev.utils.helpers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ksaraev.suddenrun.playlist.AppPlaylist;
import com.ksaraev.suddenrun.playlist.SuddenrunPlaylist;
import com.ksaraev.suddenrun.track.SuddenrunTrack;
import java.io.IOException;
import java.util.List;
import org.springframework.test.util.AssertionErrors;

public class JsonHelper {
  public static <T> T jsonToObject(String json, Class<T> aClass) {
    try {
      return new ObjectMapper().readValue(json, aClass);
    } catch (JsonProcessingException e) {
      AssertionErrors.fail(
          "Fail to convert Json [" + json + "] to instance of [" + aClass + "]: " + e.getMessage());
      return null;
    }
  }

  public static String objectToJson(Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      AssertionErrors.fail("Fail to convert object [" + object + "] to Json: " + e.getMessage());
      return null;
    }
  }

  public static AppPlaylist jsonToAppPlaylist(String json) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(AppPlaylist.class, new AppPlaylistDeserializer());
    mapper.registerModule(module);
    return mapper.readValue(json, AppPlaylist.class);
  }

  public static class AppPlaylistDeserializer extends StdDeserializer<AppPlaylist> {

    public AppPlaylistDeserializer() {
      this(null);
    }

    public AppPlaylistDeserializer(Class<?> vc) {
      super(vc);
    }

    @Override
    public AppPlaylist deserialize(
        JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode node = jsonParser.getCodec().readTree(jsonParser);
      String id = node.get("id").asText();
      String snapshotId = node.get("snapshotId").asText();
      String tracks = node.get("tracks").toString();
      String inclusions = node.get("inclusions").toString();
      String exclusions = node.get("exclusions").toString();
      List<SuddenrunTrack> suddenrunTracks =
          new ObjectMapper()
              .readValue(
                  tracks,
                  objectMapper
                      .getTypeFactory()
                      .constructCollectionType(List.class, SuddenrunTrack.class));
      List<SuddenrunTrack> suddenrunInclusions =
          new ObjectMapper()
              .readValue(
                  inclusions,
                  objectMapper
                      .getTypeFactory()
                      .constructCollectionType(List.class, SuddenrunTrack.class));
      List<SuddenrunTrack> suddenrunExclusions =
          new ObjectMapper()
              .readValue(
                  exclusions,
                  objectMapper
                      .getTypeFactory()
                      .constructCollectionType(List.class, SuddenrunTrack.class));
      return SuddenrunPlaylist.builder()
          .id(id)
          .snapshotId(snapshotId)
          .tracks(suddenrunTracks)
          .inclusions(suddenrunInclusions)
          .exclusions(suddenrunExclusions)
          .build();
    }
  }
}
