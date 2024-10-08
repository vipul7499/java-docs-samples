/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.stitcher;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import com.google.cloud.testing.junit4.MultipleAttemptsRule;
import com.google.cloud.video.stitcher.v1.VideoStitcherServiceClient.ListVodAdTagDetailsPagedResponse;
import com.google.cloud.video.stitcher.v1.VideoStitcherServiceClient.ListVodStitchDetailsPagedResponse;
import com.google.cloud.video.stitcher.v1.VodAdTagDetail;
import com.google.cloud.video.stitcher.v1.VodConfig;
import com.google.cloud.video.stitcher.v1.VodSession;
import com.google.cloud.video.stitcher.v1.VodStitchDetail;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class VodSessionTest {

  @Rule
  public final MultipleAttemptsRule multipleAttemptsRule = new MultipleAttemptsRule(5);
  private static final String VOD_CONFIG_ID = TestUtils.getVodConfigId();
  private static String VOD_CONFIG_NAME;
  private static String VOD_SESSION_NAME_PREFIX;
  private static String VOD_SESSION_NAME;
  private static String SESSION_ID;
  private static String AD_TAG_DETAIL_NAME;
  private static String AD_TAG_DETAIL_ID;
  private static String STITCH_DETAIL_NAME;
  private static String STITCH_DETAIL_ID;
  private static String PROJECT_ID;
  private static PrintStream originalOut;
  private static ByteArrayOutputStream bout;

  private static String requireEnvVar(String varName) {
    String varValue = System.getenv(varName);
    assertNotNull(
        String.format("Environment variable '%s' is required to perform these tests.", varName));
    return varValue;
  }

  @BeforeClass
  public static void beforeTest()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    requireEnvVar("GOOGLE_APPLICATION_CREDENTIALS");
    PROJECT_ID = requireEnvVar("GOOGLE_CLOUD_PROJECT");

    originalOut = System.out;
    bout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(bout));

    TestUtils.cleanStaleVodConfigs(PROJECT_ID, TestUtils.LOCATION);

    VOD_CONFIG_NAME =
        String.format("locations/%s/vodConfigs/%s", TestUtils.LOCATION, VOD_CONFIG_ID);
    VodConfig configResponse =
        CreateVodConfig.createVodConfig(
            PROJECT_ID,
            TestUtils.LOCATION,
            VOD_CONFIG_ID,
            TestUtils.VOD_URI,
            TestUtils.VOD_AD_TAG_URI);
    assertThat(configResponse.getName(), containsString(VOD_CONFIG_NAME));

    // Session IDs are autogenerated.
    VOD_SESSION_NAME_PREFIX = String.format("locations/%s/vodSessions/", TestUtils.LOCATION);
    VodSession sessionResponse =
        CreateVodSession.createVodSession(PROJECT_ID, TestUtils.LOCATION, VOD_CONFIG_ID);
    assertThat(sessionResponse.getName(), containsString(VOD_SESSION_NAME_PREFIX));

    VOD_SESSION_NAME = sessionResponse.getName();
    String[] id = VOD_SESSION_NAME.split("/");
    SESSION_ID = id[id.length - 1];

    ListVodAdTagDetailsPagedResponse adtagResponse =
        ListVodAdTagDetails.listVodAdTagDetails(PROJECT_ID, TestUtils.LOCATION, SESSION_ID);
    for (VodAdTagDetail vodAdTagDetail : adtagResponse.iterateAll()) {
      AD_TAG_DETAIL_NAME = vodAdTagDetail.getName();
    }
    id = AD_TAG_DETAIL_NAME.split("/");
    AD_TAG_DETAIL_ID = id[id.length - 1];

    ListVodStitchDetailsPagedResponse stitchResponse =
        ListVodStitchDetails.listVodStitchDetails(PROJECT_ID, TestUtils.LOCATION, SESSION_ID);
    for (VodStitchDetail vodStitchDetail : stitchResponse.iterateAll()) {
      STITCH_DETAIL_NAME = vodStitchDetail.getName();
    }
    id = STITCH_DETAIL_NAME.split("/");
    STITCH_DETAIL_ID = id[id.length - 1];
  }

  @Test
  public void testGetVodSession() throws IOException {
    VodSession response = GetVodSession.getVodSession(PROJECT_ID, TestUtils.LOCATION, SESSION_ID);
    assertThat(response.getName(), containsString(VOD_SESSION_NAME));
  }

  @Test
  public void testListVodAdTagDetailsTest() throws IOException {
    ListVodAdTagDetailsPagedResponse response =
        ListVodAdTagDetails.listVodAdTagDetails(PROJECT_ID, TestUtils.LOCATION, SESSION_ID);

    Boolean pass = false;
    for (VodAdTagDetail vodAdTagDetail : response.iterateAll()) {
      if (vodAdTagDetail.getName().contains(VOD_SESSION_NAME.concat("/vodAdTagDetails/"))) {
        pass = true;
        break;
      }
    }
    assert (pass);
  }

  @Test
  public void testGetVodAdTagDetailTest() throws IOException {
    VodAdTagDetail response =
        GetVodAdTagDetail.getVodAdTagDetail(
            PROJECT_ID, TestUtils.LOCATION, SESSION_ID, AD_TAG_DETAIL_ID);
    assertThat(response.getName(), containsString(AD_TAG_DETAIL_NAME));
  }

  @Test
  public void testListVodStitchDetailsTest() throws IOException {
    ListVodStitchDetailsPagedResponse response =
        ListVodStitchDetails.listVodStitchDetails(PROJECT_ID, TestUtils.LOCATION, SESSION_ID);
    Boolean pass = false;
    for (VodStitchDetail vodStitchDetail : response.iterateAll()) {
      if (vodStitchDetail.getName().contains(VOD_SESSION_NAME.concat("/vodStitchDetails/"))) {
        pass = true;
        break;
      }
    }
    assert (pass);
  }

  @Test
  public void testGetVodStitchDetailTest() throws IOException {
    VodStitchDetail response =
        GetVodStitchDetail.getVodStitchDetail(
            PROJECT_ID, TestUtils.LOCATION, SESSION_ID, STITCH_DETAIL_ID);
    assertThat(response.getName(), containsString(STITCH_DETAIL_NAME));
  }

  @After
  public void tearDown() {
    bout.reset();
  }

  @AfterClass
  public static void afterTest()
      throws IOException, ExecutionException, InterruptedException, TimeoutException {
    // No delete method for VOD sessions
    DeleteVodConfig.deleteVodConfig(PROJECT_ID, TestUtils.LOCATION, VOD_CONFIG_ID);
    String deleteResponse = bout.toString();
    assertThat(deleteResponse, containsString("Deleted VOD config"));
    System.out.flush();
    System.setOut(originalOut);
  }
}
