/**
 * Copyright 2015 Netflix, Inc.
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
package com.netflix.spectator.api;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class UtilsTest {

  private List<Measurement> newList(int size) {
    Registry r = new DefaultRegistry();
    List<Measurement> data = new ArrayList<>();
    for (int i = 0; i < size; ++i) {
      data.add(new Measurement(r.createId("foo", "i", "" + i), 0L, i));
    }
    return data;
  }

  @Test
  public void getTagValueIdNoTags() {
    Registry r = new DefaultRegistry();
    Id id = r.createId("foo");
    Assert.assertEquals(null, Utils.getTagValue(id, "abc"));
  }

  @Test
  public void getTagValueId() {
    Registry r = new DefaultRegistry();
    Id id = r.createId("foo", "bar", "baz", "abc", "def");
    Assert.assertEquals("def", Utils.getTagValue(id, "abc"));
    Assert.assertEquals("baz", Utils.getTagValue(id, "bar"));
  }

  @Test
  public void firstTag() {
    List<Measurement> ms = newList(10);
    Tag t = new BasicTag("i", "7");
    Measurement m = Utils.first(ms, t);
    Assert.assertEquals(m.id().tags(), ArrayTagSet.create(t));
  }

  @Test
  public void firstKV() {
    List<Measurement> ms = newList(10);
    Tag t = new BasicTag("i", "7");
    Measurement m = Utils.first(ms, "i", "7");
    Assert.assertEquals(m.id().tags(), ArrayTagSet.create(t));
  }

  @Test
  public void firstPredicate() {
    List<Measurement> ms = newList(10);
    Tag t = new BasicTag("i", "7");
    Measurement m = Utils.first(ms, v -> v.value() == 7.0);
    Assert.assertEquals(m.id().tags(), ArrayTagSet.create(t));
  }

  @Test
  public void firstPredicateEmpty() {
    List<Measurement> ms = newList(10);
    Measurement m = Utils.first(ms, v -> false);
    Assert.assertEquals(null, m);
  }

  @Test
  public void filterTag() {
    List<Measurement> ms = newList(10);
    Tag t = new BasicTag("i", "7");
    List<Measurement> out = Utils.toList(Utils.filter(ms, t));
    Assert.assertEquals(1, out.size());
    Assert.assertEquals(out.get(0).id().tags(), ArrayTagSet.create(t));
  }

  @Test
  public void filterKV() {
    List<Measurement> ms = newList(10);
    Tag t = new BasicTag("i", "7");
    List<Measurement> out = Utils.toList(Utils.filter(ms, "i", "7"));
    Assert.assertEquals(1, out.size());
    Assert.assertEquals(out.get(0).id().tags(), ArrayTagSet.create(t));
  }

  @Test
  public void filterPredicate() {
    List<Measurement> ms = newList(10);
    Tag t = new BasicTag("i", "7");
    List<Measurement> out = Utils.toList(Utils.filter(ms, v -> v.value() == 7.0));
    Assert.assertEquals(1, out.size());
    Assert.assertEquals(out.get(0).id().tags(), ArrayTagSet.create(t));
  }

  @Test
  public void filterPredicateEmpty() {
    List<Measurement> ms = newList(10);
    List<Measurement> out = Utils.toList(Utils.filter(ms, v -> false));
    Assert.assertEquals(0, out.size());
  }
}
