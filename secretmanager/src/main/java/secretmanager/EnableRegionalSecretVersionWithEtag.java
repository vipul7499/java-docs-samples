/*
 * Copyright 2021 Google LLC
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

package secretmanager;

// [START secretmanager_enable_regional_secret_version_with_etag]
import com.google.cloud.secretmanager.v1.EnableSecretVersionRequest;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.secretmanager.v1.SecretVersion;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import java.io.IOException;

public class EnableRegionalSecretVersionWithEtag {

  public static void enableRegionalSecretVersion() throws IOException {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "your-project-id";
    String locationId = "your-location-id";
    String secretId = "your-secret-id";
    String versionId = "your-version-id";
    // Including the quotes is important.
    String etag = "\"1234\"";
    enableRegionalSecretVersion(projectId, locationId, secretId, versionId, etag);
  }

  // Enable an existing secret version.
  public static void enableRegionalSecretVersion(
      String projectId, String locationId, String secretId, String versionId, String etag)
      throws IOException {
    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    String apiEndpoint = String.format("secretmanager.%s.rep.googleapis.com:443", locationId);
    SecretManagerServiceSettings secretManagerServiceSettings =
        SecretManagerServiceSettings.newBuilder().setEndpoint(apiEndpoint).build();
    try (SecretManagerServiceClient client = 
        SecretManagerServiceClient.create(secretManagerServiceSettings)) {
      // Build the name from the version.
      SecretVersionName secretVersionName = 
          SecretVersionName.ofProjectLocationSecretSecretVersionName(
          projectId, locationId, secretId, versionId);

      // Build the request.
      EnableSecretVersionRequest request =
          EnableSecretVersionRequest.newBuilder()
              .setName(secretVersionName.toString())
              .setEtag(etag)
              .build();

      // Enable the secret version.
      SecretVersion version = client.enableSecretVersion(request);
      System.out.printf("Enabled regional secret version %s\n", version.getName());
    }
  }
}
// [END secretmanager_enable_regional_secret_version_with_etag]
