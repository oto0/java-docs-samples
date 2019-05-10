/*
 * Copyright 2017 Google Inc.
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


package com.example.vision;

// [START vision_quickstart]
// Imports the Google Cloud client library

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import org.threeten.bp.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class QuickstartSample {
  public static void main(String... args) throws Exception {
    // Instantiates a client
    try {

      // The path to the image file to annotate
      String fileName = "./resources/wakeupcat.jpg";

      // Reads the image file into memory
      Path path = Paths.get(fileName);
      byte[] data = Files.readAllBytes(path);
      ByteString imgBytes = ByteString.copyFrom(data);

      ImageAnnotatorSettings.Builder settingsBuilder = ImageAnnotatorSettings.newBuilder();


      settingsBuilder.batchAnnotateImagesSettings()
          .setSimpleTimeoutNoRetries(Duration.ofMillis(3));

      ImageAnnotatorSettings settings = settingsBuilder.build();

      ImageAnnotatorClient vision = ImageAnnotatorClient.create(settings);

      // Builds the image annotation request
      List<AnnotateImageRequest> requests = new ArrayList<>();
      Image img = Image.newBuilder().setContent(imgBytes).build();
      Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
      AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
          .addFeatures(feat)
          .setImage(img)
          .build();
      requests.add(request);

      // Performs label detection on the image file
      BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();

      for (AnnotateImageResponse res : responses) {
        if (res.hasError()) {
          System.out.printf("Error: %s\n", res.getError().getMessage());
          return;
        }

        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
          annotation.getAllFields().forEach((k, v) ->
              System.out.printf("%s : %s\n", k, v.toString()));
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
// [END vision_quickstart]
