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

import java.io.IOException;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.tecfox.formatterservice.template.models.Template;
import eu.tecfox.formatterservice.testdata.TestDataGenerator;


/**
 * Test class for {@link SectionFormatter}.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
public class SectionFormatterTest {

    private Template template;

    private XWPFDocument document;

    private SectionFormatter sectionFormatter;


    @BeforeEach
    void setup() throws IOException {

        // create resource folders
        ResourceHandler.createDirs(INPUT_RESOURCE_PATH, OUTPUT_RESOURCE_PATH);

        // create empty document
        this.document = new XWPFDocument();
        
        // set mock data
        this.template = TestDataGenerator.generateNewValidTemplate();

        // create sectionFormatter
        this.sectionFormatter = new SectionFormatter(template, document);

        // add test content
        sectionFormatter.addContent();
    }


    @Test
    void addContent_documentShouldNotBeEmpty() {

        // should have paragraphs
        List<XWPFParagraph> paragraphs = document.getParagraphs(); 
        assertFalse(paragraphs.isEmpty());
        
        // should have runs
        List<XWPFRun> runs = paragraphs.get(0).getRuns();
        assertFalse(runs.isEmpty());

        // first run should have some text
        assertFalse(runs.get(0).text().isEmpty());
    }
}