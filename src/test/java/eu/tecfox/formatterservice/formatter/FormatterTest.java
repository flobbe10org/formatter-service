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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import eu.tecfox.formatterservice.formatter.ResourceHandler;

import eu.tecfox.formatterservice.template.models.Template;
import eu.tecfox.formatterservice.testdata.TestDataGenerator;


/**
 * Test class for {@link Formatter}.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
@TestInstance(Lifecycle.PER_CLASS)
public class FormatterTest {
    
    private Template template;

    private Formatter formatter;
    
    private String docxFilePath;

    private File docxFile;


    @BeforeAll
    void setup() throws IOException { 

        // create resource folders
        ResourceHandler.createDirs(INPUT_RESOURCE_PATH, OUTPUT_RESOURCE_PATH);

        // create template
        // this.template = TestDataGenerator.generateValidContract();
        this.template = TestDataGenerator.generateValidTemplate();

        // create formatter
        this.formatter = new Formatter(template);
    
        // format
        this.docxFilePath = formatter.formatDocument();
    
        // create file
        this.docxFile = new File(docxFilePath);
    }
    
    
    @Test
    void formatDocument_shouldWriteDocxFile() throws IOException {

        // exists
        assertTrue(docxFile.exists());

        // is .docx
        int docxFilePathLength = docxFilePath.length();
        String praefix = docxFilePath.substring(docxFilePathLength - 5, docxFilePathLength);
        assertEquals(".docx", praefix);
    }


    @Test
    void formatDocument_shouldNotBeEmpty() throws IOException {

        // create document
        XWPFDocument document = readFileToDocx(docxFilePath);

        // not empty
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        assertFalse(paragraphs.isEmpty());
        assertFalse(paragraphs.get(0).getRuns().isEmpty());
        assertFalse(paragraphs.get(0).getText().isEmpty());
    }


    @AfterAll
    void cleanUp() {

        // delete all test files
        ResourceHandler.clearDirectory(OUTPUT_RESOURCE_PATH);
    }


    private XWPFDocument readFileToDocx(String path) throws IOException {

        try (FileInputStream fis = new FileInputStream(path);) {
            return new XWPFDocument(fis);
        }
    }
}           