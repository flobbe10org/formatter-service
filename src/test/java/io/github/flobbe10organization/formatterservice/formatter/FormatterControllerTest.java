/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.flobbe10organization.formatterservice.formatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static io.github.flobbe10organization.formatterservice.formatter.ResourceHandler.INPUT_RESOURCE_PATH;
import static io.github.flobbe10organization.formatterservice.formatter.ResourceHandler.OUTPUT_RESOURCE_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.github.flobbe10organization.formatterservice.formatter.FormatterController;
import io.github.flobbe10organization.formatterservice.formatter.ResourceHandler;
import io.github.flobbe10organization.formatterservice.template.models.Template;
import io.github.flobbe10organization.formatterservice.testdata.TestDataGenerator;


/**
 * Test class for {@link FormatterController}.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
@WebMvcTest(FormatterController.class)
@TestInstance(Lifecycle.PER_CLASS)
// TODO: rewrite every test
public class FormatterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Template template;

    
    @BeforeAll
    void setup() throws IOException { 
        
        // create resource folders
        ResourceHandler.createDirs(INPUT_RESOURCE_PATH, OUTPUT_RESOURCE_PATH);

        // set template
        this.template = TestDataGenerator.generateValidTemplate();
    }


    // @Test
    void formatAndDownload_asDocx_shouldBeOk() throws Exception {

        // send request
        this.mockMvc.perform(get("/api/formatter/pdf=false"))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }


    // @Test
    void formatAndDownload_asPdf_shouldBeOk() throws Exception {

        // send request
        this.mockMvc.perform(get("/api/formatter/pdf=true"))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }


    // @Test
    void formatAndDownload_shouldBeNotFoundIfUserHasNoProfile() throws Exception {

        // send request
        this.mockMvc.perform(get("/api/formatter/pdf=false"))
                            .andExpect(status().isNotFound());
    }


    // @Test
    void deleteFiles_shouldBeOk() throws Exception {

        // send request
        this.mockMvc.perform(delete("/api/formatter/deleteFiles"))
                            .andExpect(status().isOk());
    }


    @AfterAll
    void cleanUp() {

        // delete all test files
        ResourceHandler.clearDirectory(OUTPUT_RESOURCE_PATH);
    }
}