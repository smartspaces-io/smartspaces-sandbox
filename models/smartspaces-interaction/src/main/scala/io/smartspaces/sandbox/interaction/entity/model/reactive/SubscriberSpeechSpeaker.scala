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

package io.smartspaces.sandbox.interaction.entity.model.reactive;

import io.smartspaces.sandbox.interaction.behavior.speech.SpeechSpeaker;
import io.smartspaces.sandbox.interaction.entity.model.PersonSensedEntityModel;
import io.smartspaces.sandbox.interaction.entity.model.PhysicalLocationOccupancyEvent;

import rx.Subscriber;

import java.util.Set;

/**
 * A reactive subscriber for speech events.
 *
 * @author Keith M. Hughes
 */
class SubscriberSpeechSpeaker(private val speechSpeaker: SpeechSpeaker) extends Subscriber[PhysicalLocationOccupancyEvent] {

  override def onCompleted(): Unit = {
    // Nothing to do
  }

  override def onError(error: Throwable): Unit = {
    // Nothing to do
  }

  override def onNext(event: PhysicalLocationOccupancyEvent): Unit = {
    val entered = event.getEntered()
    if (entered != null) {
      entered.foreach((person: PersonSensedEntityModel) => {
        speechSpeaker.addSpeech(
          String.format("%s has entered %s", person.getSensedEntityDescription.getDisplayName,
            event.getPhysicalSpace.getSensedEntityDescription.getDisplayName))
      })
    }

    val exited = event.getExited()
    if (exited != null) {
      exited.foreach((person: PersonSensedEntityModel) => {
        speechSpeaker.addSpeech(
          String.format("%s has exited %s", person.getSensedEntityDescription.getDisplayName,
            event.getPhysicalSpace.getSensedEntityDescription.getDisplayName))
      })
    }
  }
}
