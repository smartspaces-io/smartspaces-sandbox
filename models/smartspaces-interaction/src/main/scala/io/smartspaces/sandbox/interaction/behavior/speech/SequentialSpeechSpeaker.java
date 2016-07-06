/*
 * Copyright (C) 2016 Keith M. Hughes
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

package io.smartspaces.sandbox.interaction.behavior.speech;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;

import io.smartspaces.service.speech.synthesis.SpeechSynthesisPlayer;
import io.smartspaces.service.speech.synthesis.SpeechSynthesisService;
import io.smartspaces.system.SmartSpacesEnvironment;

/**
 * The standard speech source that uses routes.
 * 
 * @author Keith M. Hughes
 */
public class SequentialSpeechSpeaker implements SpeechSpeaker {

  /**
   * The queue of text to speak.
   */
  private LinkedBlockingQueue<String> speechToSpeak = new LinkedBlockingQueue<>();

  /**
   * The player for speech.
   */
  private SpeechSynthesisPlayer speechPlayer;

  /**
   * The space environment to use.
   */
  private SmartSpacesEnvironment spaceEnvironment;

  /**
   * The logger to use.
   */
  private Log log;

  /**
   * The future for the thread that calls the speech player.
   */
  private Future<?> speechFuture;

  /**
   * Construct a new speaker.
   * 
   * @param spaceEnvironment
   *          the space environment to use
   * @param log
   *          logger to use the
   */
  public SequentialSpeechSpeaker(SmartSpacesEnvironment spaceEnvironment, Log log) {
    this.spaceEnvironment = spaceEnvironment;
    this.log = log;
  }

  @Override
  public void startup() {
    SpeechSynthesisService speechSynthesisService = spaceEnvironment.getServiceRegistry()
        .getRequiredService(SpeechSynthesisService.SERVICE_NAME);
    speechPlayer = speechSynthesisService.newPlayer(log);
    speechPlayer.startup();

    speechFuture = spaceEnvironment.getExecutorService().submit(new Runnable() {
      @Override
      public void run() {
        speakNextContent();
      }

    });
  }

  @Override
  public void shutdown() {
    speechFuture.cancel(true);
    speechPlayer.shutdown();
  }

  @Override
  public void addSpeech(String content) {
    speechToSpeak.offer(content);
  }

  /**
   * Keep speaking until the speaking thread is interrupted.
   * 
   * <p>
   * Blocks while waiting for something in the speech queue.
   */
  private void speakNextContent() {
    while (!Thread.interrupted()) {
      try {
        String content = speechToSpeak.take();
        speechPlayer.speak(content, true);
      } catch (InterruptedException e) {
        log.warn("Sequential speech speaker interrupted in speech loop");
      }
    }
  }
}
