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

package secretmanager.regionalsamples;

// [START secretmanager_destroy_regional_secret_version_with_etag]
import com.google.cloud.secretmanager.v1.DestroySecretVersionRequest;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.secretmanager.v1.SecretVersion;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import java.io.IOException;

public class DestroyRegionalSecretVersionWithEtag {

  public static void main(String[] args) throws IOException {
    // TODO(developer): Replace these variables before running the sample.
    
    // id of the GCP project
    String projectId = "your-project-id";
    // id of location where secret is located
    String locationId = "your-location-id";
    // id of the secret
    String secretId = "your-secret-id";
    // id of the secret version
    String versionId = "your-version-id";
    // etag associated with the secret. Including the quotes is important.
    String etag = "\"1234\"";
    destroyRegionalSecretVersionWithEtag(projectId, locationId, secretId, versionId, etag);
  }

  // Destroy an existing secret version.
  public static SecretVersion destroyRegionalSecretVersionWithEtag(
      String projectId, String locationId, String secretId, String versionId, String etag)
      throws IOException {

    // Endpoint to call the regional secret manager sever
    String apiEndpoint = String.format("secretmanager.%s.rep.googleapis.com:443", locationId);
    SecretManagerServiceSettings secretManagerServiceSettings =
        SecretManagerServiceSettings.newBuilder().setEndpoint(apiEndpoint).build();

    // Initialize the client that will be used to send requests. This client only needs to be
    // created once, and can be reused for multiple requests.
    try (SecretManagerServiceClient client = 
        SecretManagerServiceClient.create(secretManagerServiceSettings)) {
      // Build the name from the version.
      SecretVersionName secretVersionName = 
          SecretVersionName.ofProjectLocationSecretSecretVersionName(
          projectId, locationId, secretId, versionId);

      // Build the request.
      DestroySecretVersionRequest request =
          DestroySecretVersionRequest.newBuilder()
              .setName(secretVersionName.toString())
              .setEtag(etag)
              .build();

      // Destroy the secret version.
      SecretVersion version = client.destroySecretVersion(request);
      System.out.printf("Destroyed regional secret version %s\n", version.getName());

      return version;
    }
  }
}
// [END secretmanager_destroy_regional_secret_version_with_etag]
