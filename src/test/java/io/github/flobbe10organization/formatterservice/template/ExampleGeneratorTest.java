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

package io.github.flobbe10organization.formatterservice.template;

import io.github.flobbe10organization.formatterservice.template.ExampleGenerator;
import io.github.flobbe10organization.formatterservice.template.models.ExampleValues;
import io.github.flobbe10organization.formatterservice.template.models.Template;
import io.github.flobbe10organization.formatterservice.template.models.section.Section;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.DateElement;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.SectionElement;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.StringElement;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.StringListElement;
import io.github.flobbe10organization.formatterservice.testdata.TestDataGenerator;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExampleGeneratorTest {

    Template template;

    ExampleGeneratorTest() throws IOException {
        template = TestDataGenerator.generateValidTemplate();
    }

    @Test
    void Should_GenerateExampleProfile() {
        ExampleGenerator.build(template);
        for (Section section: template.getSections()) {
            for (SectionElement sectionElement: section.getElements()) {
                assertSectionElement(sectionElement);
            }
        }
    }

    void assertSectionElement(SectionElement sectionElement) {
        if (sectionElement instanceof DateElement) {
            assertDateElement((DateElement) sectionElement);
        }
        if (sectionElement instanceof StringElement) {
            assertStringElement((StringElement) sectionElement);
        }
        if (sectionElement instanceof StringListElement) {
            assertStringListElement((StringListElement) sectionElement);
        }
       /* if (sectionElement instanceof NestedElement) {
            assertSectionElement((SectionElement) sectionElement.getKey());
            ((NestedElement) sectionElement).getValue().forEach(this::assertSectionElement);
        }*/
    }

    void assertDateElement(DateElement dateElement) {
        assertNotNull(dateElement.getValue());
        assertEquals(LocalDate.now(), dateElement.getValue());
    }

    void assertStringElement(StringElement stringElement) {
        assertNotNull(stringElement.getValue());
        assertEquals(ExampleValues.EXAMPLE_STRING.getValue(), stringElement.getValue());
    }

    void assertStringListElement(StringListElement stringListElement) {
        assertNotNull(stringListElement.getValue());
        assertTrue(stringListElement.getValue().size() > 0);
        assertEquals(ExampleValues.EXAMPLE_LIST.getValue(), stringListElement.getValue());
    }
}