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

// [START secretmanager_update_regional_secret_with_etag]
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.secretmanager.v1.SecretName;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.FieldMaskUtil;
import java.io.IOException;

public class UpdateRegionalSecretWithEtag {

  public static void main(String[] args) throws IOException {
    // TODO(developer): Replace these variables before running the sample.
    
    // This is the id of the GCP project
    String projectId = "your-project-id";
    // This is the id of location where secret is located
    String locationId = "your-location-id";
    // This is the id of the secret
    String secretId = "your-secret-id";
    // This is the etag associated with the secret. Including the quotes is important.
    String etag = "\"1234\"";
    updateRegionalSecretWithEtag(projectId, locationId, secretId, etag);
  }

  // Update an existing secret with etag.
  public static Secret updateRegionalSecretWithEtag(
      String projectId, String locationId, String secretId, String etag)
      throws IOException {
    
    // Endpoint to call the regional secret manager sever
    String apiEndpoint = String.format("secretmanager.%s.rep.googleapis.com:443", locationId);
    SecretManagerServiceSettings secretManagerServiceSettings =
        SecretManagerServiceSettings.newBuilder().setEndpoint(apiEndpoint).build();

    // Initialize the client that will be used to send requests. This client only needs to be
    // created once, and can be reused for multiple requests.
    try (SecretManagerServiceClient client = 
        SecretManagerServiceClient.create(secretManagerServiceSettings)) {
      // Build the name.
      SecretName secretName = 
          SecretName.ofProjectLocationSecretName(projectId, locationId, secretId);

      // Build the updated secret.
      Secret secret =
          Secret.newBuilder()
              .setName(secretName.toString())
              .setEtag(etag)
              .putLabels("secretmanager", "rocks")
              .build();

      // Build the field mask.
      FieldMask fieldMask = FieldMaskUtil.fromString("labels");

      // Update the secret.
      Secret updatedSecret = client.updateSecret(secret, fieldMask);
      System.out.printf("Updated regional secret %s\n", updatedSecret.getName());

      return updatedSecret;
    }
  }
}
// [END secretmanager_update_regional_secret_with_etag]
