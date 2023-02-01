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

package io.github.flobbe10organization.formatterservice.testdata;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.flobbe10organization.formatterservice.formatter.ResourceHandlerTest;
import io.github.flobbe10organization.formatterservice.template.models.Template;
import io.github.flobbe10organization.formatterservice.template.models.section.Section;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.NestedElement;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.NestedValue;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.SectionElement;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.StringElement;


public class TestDataGenerator {

    public static Template generateValidTemplate() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.readTree(
            Paths.get(ResourceHandlerTest.TEST_TEMPLATE).toFile()).toString();
        return mapper.readValue(json, Template.class);
    }

    public static Template generateValidNewTemplate() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.readTree(
            Paths.get(ResourceHandlerTest.NEW_TEST_TEMPLATE).toFile()).toString();
        return mapper.readValue(json, Template.class);
    }

    public static Template generateValidContract() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.readTree(
            Paths.get(ResourceHandlerTest.CONTRACT_TEMPLATE).toFile()).toString();
        return mapper.readValue(json, Template.class);
    }

    public static List<Section> generateEmptyTemplateSection(String identifier) {
        Section section = new Section();
        section.setTitle("Section 1");
        section.setIdentifier(identifier);
        section.setShowTitle(true);
        section.setElements(new ArrayList<>());
        return List.of(section);
    }

    public static SectionElement generateSectionStringElement(boolean unique, boolean required, String key) {
        StringElement sectionElement = new StringElement();
        sectionElement.setIdentifier(UUID.randomUUID().toString());
        sectionElement.setUnique(unique);
        sectionElement.setRequired(required);
        sectionElement.setKey(key);
        sectionElement.setValue(key);
        return sectionElement;
    }

    public static Section generateMergeTestSection(boolean withValues, boolean withUnique) {
        Section section = new Section();
        List<SectionElement> elements = new ArrayList<>();
        StringElement firstElement = new StringElement();
        firstElement.setIdentifier("uuid1");
        firstElement.setUnique(withUnique);
        firstElement.setRequired(true);
        firstElement.setKey("First Element");
        firstElement.setValue(withValues ? "First Value" : null);
        elements.add(firstElement);

        NestedElement secondElement = new NestedElement();
        secondElement.setIdentifier("uuid2");
        secondElement.setUnique(withUnique);
        StringElement nestedKey = new StringElement();
        nestedKey.setIdentifier("uuid3");
        nestedKey.setValue("Nested Name");
        secondElement.setKey(nestedKey);
        List<NestedValue> values = new ArrayList<>();

        StringElement nestedValueElement = new StringElement();
        nestedValueElement.setIdentifier("uuid4");
        nestedValueElement.setKey("Nested Value 1");
        nestedValueElement.setValue(withValues ? " Nested Value" : "");
        values.add(nestedValueElement);
        secondElement.setValue(values);
        elements.add(secondElement);

        section.setElements(elements);
        return section;
    }
}
