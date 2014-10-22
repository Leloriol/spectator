/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.spectator.servo;

import com.netflix.servo.monitor.Monitor;
import com.netflix.spectator.api.Clock;
import com.netflix.spectator.api.Counter;
import com.netflix.spectator.api.Id;
import com.netflix.spectator.api.Measurement;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/** Counter implementation for the servo registry. */
class ServoCounter implements Counter, ServoMeter {

  private final Clock clock;
  private final com.netflix.servo.monitor.StepCounter impl;
  private final AtomicLong count;

  /** Create a new instance. */
  ServoCounter(Clock clock, com.netflix.servo.monitor.StepCounter impl) {
    this.clock = clock;
    this.impl = impl;
    this.count = new AtomicLong(0L);
  }

  @Override public void addMonitors(List<Monitor<?>> monitors) {
    monitors.add(impl);
  }

  @Override public Id id() {
    return new ServoId(impl.getConfig());
  }

  @Override public boolean hasExpired() {
    return false;
  }

  @Override public Iterable<Measurement> measure() {
    long now = clock.wallTime();
    double v = impl.getValue(0).doubleValue();
    return Collections.singleton(new Measurement(id(), now, v));
  }

  @Override public void increment() {
    impl.increment();
    count.incrementAndGet();
  }

  @Override public void increment(long amount) {
    impl.increment(amount);
    count.addAndGet(amount);
  }

  @Override public long count() {
    return count.get();
  }
}
