/*
 * Copyright (C) 2016 Keith M. Hughes
 * Copyright (C) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.smartspaces.example.activity.control.opensoundcontrol.server;

import io.smartspaces.activity.impl.BaseActivity;
import io.smartspaces.sandbox.service.control.opensoundcontrol.OpenSoundControlMethod;
import io.smartspaces.sandbox.service.control.opensoundcontrol.OpenSoundControlServerCommunicationEndpoint;
import io.smartspaces.sandbox.service.control.opensoundcontrol.OpenSoundControlServerCommunicationEndpointService;
import io.smartspaces.sandbox.service.control.opensoundcontrol.OpenSoundControlServerPacket;
import io.smartspaces.service.audio.player.AudioTrackPlayer;
import io.smartspaces.service.audio.player.AudioTrackPlayerService;
import io.smartspaces.service.audio.player.PlayableAudioTrack;

import com.google.common.collect.Maps;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * A Smart Spaces Java-based activity that plays audio tracks based on Open Sound Control messages coming into an
 * OSC server.
 *
 * <p>
 * This activity only plays sounds if it is activated.
 *
 * @author Keith M. Hughes
 */
public class OpenSoundControlServerExampleActivity extends BaseActivity {

  /**
   * The separator between portions of the track map.
   */
  public static final String TRACKMAP_SEPARATOR = ":";

  /**
   * The name of the config property for the Open Sound Control server port.
   */
  public static final String CONFIGURATION_PROPERTY_OPEN_SOUND_CONTROL_SERVER_PORT =
      "space.opensoundcontrol.server.port";

  /**
   * The name of the config property for the Open Sound Control address that will invoke the play method.
   */
  public static final String CONFIGURATION_PROPERTY_OPEN_SOUND_CONTROL_METHOD_ADDRESS = "space.opensoundcontrol.methodaddress";

  /**
   * The name of the config property for the Open Sound Control track map, which maps sound IDs to tracks to be played.
   */
  public static final String CONFIGURATION_PROPERTY_OPEN_SOUND_CONTROL_TRACK_MAP = "space.opensoundcontrol.trackmap";

  /**
   * The map of recording names to the track to be played.
   */
  private Map<String, PlayableAudioTrack> soundIdToTrack = Maps.newHashMap();

  /**
   * The audio player for playing the requested sounds.
   */
  private AudioTrackPlayer audioPlayer;

  @Override
  public void onActivitySetup() {
    OpenSoundControlServerCommunicationEndpointService serverService =
        getSpaceEnvironment().getServiceRegistry().getRequiredService(
            OpenSoundControlServerCommunicationEndpointService.SERVICE_NAME);

    int serverPort =
        getConfiguration().getRequiredPropertyInteger(CONFIGURATION_PROPERTY_OPEN_SOUND_CONTROL_SERVER_PORT);

    OpenSoundControlServerCommunicationEndpoint oscServer = serverService.newUdpEndpoint(serverPort, getLog());
    addManagedResource(oscServer);

    String methodAddress = getConfiguration().getRequiredPropertyString(CONFIGURATION_PROPERTY_OPEN_SOUND_CONTROL_METHOD_ADDRESS);
    oscServer.registerMethod(methodAddress, new OpenSoundControlMethod() {
      @Override
      public void invoke(OpenSoundControlServerPacket packet) {
        handleOscPacket(packet);
      }
    });

    populateTrackMap();

    AudioTrackPlayerService audioService =
        getSpaceEnvironment().getServiceRegistry().getRequiredService(AudioTrackPlayerService.SERVICE_NAME);
    audioPlayer = audioService.newTrackPlayer(getLog());
    addManagedResource(audioPlayer);
  }

  /**
   * Populate the track map from the configuration.
   */
  private void populateTrackMap() {
    List<String> trackMap =
        getConfiguration().getPropertyStringList(CONFIGURATION_PROPERTY_OPEN_SOUND_CONTROL_TRACK_MAP,
            TRACKMAP_SEPARATOR);
    while (!trackMap.isEmpty()) {
      String soundId = trackMap.remove(0);
      String trackFile = trackMap.remove(0);
      soundIdToTrack.put(soundId, new PlayableAudioTrack(null, new File(trackFile)));
    }
  }

  /**
   * Handle an incoming OSC packet.
   *
   * @param packet
   *          the packet to handle
   */
  private void handleOscPacket(OpenSoundControlServerPacket packet) {
    if (isActivated()) {
      String trackToPlay = packet.getStringArgument(0);

      final PlayableAudioTrack track = soundIdToTrack.get(trackToPlay);
      if (track != null) {
        // Run in another thread so that we don't block the OSC server.
        getManagedCommands().submit(new Runnable() {
          @Override
          public void run() {
            audioPlayer.start(track);
          }
        });
      } else {
        getLog().warn(String.format("Unknown sound %s", trackToPlay));
      }
    }
  }
}
