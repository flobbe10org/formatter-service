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
package eu.tecfox.formatterservice.formatter;

import static eu.tecfox.formatterservice.formatter.ResourceHandler.INPUT_RESOURCE_PATH;
import static eu.tecfox.formatterservice.formatter.ResourceHandler.OUTPUT_RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 * Test class for {@link DocxToPdf}.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
public class DocxToPdfTest {

    private static String docxPath = INPUT_RESOURCE_PATH + ResourceHandlerTest.TEST_DOCX;
    private static String pdfPath = OUTPUT_RESOURCE_PATH + ResourceHandlerTest.TEST_PDF;


    @BeforeAll
    void setup() {

        // create resource folders
        ResourceHandler.createDirs(INPUT_RESOURCE_PATH, OUTPUT_RESOURCE_PATH);
    }


    // @Test
    // TODO: uncomment this when DocxToPdf is fixed
    void convert_shouldParseDocxToPdf_fileShouldExist() throws FileNotFoundException, IOException {

        // convert
        DocxToPdf.convert(docxPath, pdfPath);

        // should exist
        assertTrue(new File(pdfPath).delete());
    }


    @AfterAll
    void cleanUp() {

        // delete all test files
        ResourceHandler.clearDirectory(OUTPUT_RESOURCE_PATH);
    }
}