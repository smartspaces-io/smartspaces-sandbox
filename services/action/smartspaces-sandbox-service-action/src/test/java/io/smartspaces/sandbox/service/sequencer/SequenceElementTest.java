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

package io.smartspaces.sandbox.service.sequencer;

import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A series of tests for various {@link SequenceElement} subclasses.
 * 
 * @author Keith M. Hughes
 */
public class SequenceElementTest {
  
  @Mock Sequencer sequencer;

  @Test
  public void testGroupSequenceElement() {
    final List<String> actual = new ArrayList<>();
    
    SequenceElement element1 = new SequenceElement() {
      @Override
      public void run(Sequencer scheduler) {
        actual.add("a");
      }
    };
    SequenceElement element2 = new SequenceElement() {
      @Override
      public void run(Sequencer scheduler) {
        actual.add("b");
      }
    };
    
    GroupSequenceElement sequence = new GroupSequenceElement(element1, element2);
    sequence.run(sequencer);
    
    Assert.assertEquals(Lists.newArrayList("a",  "b"), actual);
  }

  @Test
  public void testRepeatingSequenceElement() {
    final AtomicInteger actual = new AtomicInteger();
    
    SequenceElement element1 = new SequenceElement() {
      @Override
      public void run(Sequencer scheduler) {
        actual.incrementAndGet();
      }
    };
    
    RepeatingSequenceElement sequence = new RepeatingSequenceElement(10, element1);
    sequence.run(sequencer);
    
    Assert.assertEquals(10, actual.get());
  }

  @Test
  public void testRunnableSequenceElement() {
    Runnable runnable = Mockito.mock(Runnable.class);
    
    RunnableSequenceElement sequence = new RunnableSequenceElement(runnable);
    sequence.run(sequencer);
    
    Mockito.verify(runnable).run();
  }
}
