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

package io.smartspaces.sandbox.interaction.entity.model;

import io.smartspaces.logging.ExtendedLog;
import io.smartspaces.sandbox.event.observable.EventObservable;
import io.smartspaces.sandbox.interaction.entity.PhysicalSpaceSensedEntityDescription;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import rx.Observer;
import rx.observers.TestSubscriber;

import java.util.List;
import java.util.Set;

/**
 * Tests for the {@link SimplePhysicalSpaceSensedEntityModel}
 * 
 * @author Keith M. Hughes
 */
public class SimplePhysicalSpaceSensedEntityModelTest {

  private SimplePhysicalSpaceSensedEntityModel model;

  private EventObservable<PhysicalLocationOccupancyEvent> occupancyObservable;

  @Mock
  private PhysicalSpaceSensedEntityDescription entityDescription;

  @Mock
  private SensedEntityModelCollection sensedEntityModelCollection;

  @Mock
  private ExtendedLog log;

  private TestSubscriber<PhysicalLocationOccupancyEvent> occupancySubscriber;

  @Mock
  private Observer<PhysicalLocationOccupancyEvent> occupancyObserver;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    occupancyObservable = new EventObservable<PhysicalLocationOccupancyEvent>(log);

    occupancySubscriber = TestSubscriber.create(occupancyObserver);
    occupancyObservable.subscribe(occupancySubscriber);

    model = new SimplePhysicalSpaceSensedEntityModel(entityDescription, sensedEntityModelCollection,
        occupancyObservable);
  }

  /**
   * Test that an event comes out of a single person entering a room even though
   * the model update happens twice.
   */
  @Test
  public void testSinglePersonEnters() {
    PersonSensedEntityModel personModel = Mockito.mock(PersonSensedEntityModel.class);

    model.occupantEntered(personModel);
    model.occupantEntered(personModel);

    occupancySubscriber.assertNoErrors();
    ArgumentCaptor<PhysicalLocationOccupancyEvent> argumentCaptor =
        ArgumentCaptor.forClass(PhysicalLocationOccupancyEvent.class);

    Mockito.verify(occupancyObserver, Mockito.times(1)).onNext(argumentCaptor.capture());

    Set<PersonSensedEntityModel> peopleEntered = Sets.newHashSet(personModel);
    Assert.assertEquals(peopleEntered, argumentCaptor.getValue().getEntered());
    Assert.assertEquals(peopleEntered, model.getOccupants());
  }

  /**
   * Test that an event comes out of a single person entering a room and then
   * leaving twice.
   */
  @Test
  public void testSinglePersonEntersAndExits() {
    PersonSensedEntityModel personModel = Mockito.mock(PersonSensedEntityModel.class);

    model.occupantEntered(personModel);
    model.occupantExited(personModel);
    model.occupantExited(personModel);

    occupancySubscriber.assertNoErrors();
    ArgumentCaptor<PhysicalLocationOccupancyEvent> argumentCaptor =
        ArgumentCaptor.forClass(PhysicalLocationOccupancyEvent.class);

    // First call will be person entering.
    Mockito.verify(occupancyObserver, Mockito.times(2)).onNext(argumentCaptor.capture());

    Set<PersonSensedEntityModel> peopleEntered = Sets.newHashSet(personModel);
    
    // The last event will be captured.
    List<PhysicalLocationOccupancyEvent> allValues = argumentCaptor.getAllValues();
    Assert.assertEquals(peopleEntered, allValues.get(1).getExited());
    Assert.assertTrue(model.getOccupants().isEmpty());
  }
}
