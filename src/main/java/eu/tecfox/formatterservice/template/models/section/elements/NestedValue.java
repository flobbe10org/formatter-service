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

package eu.tecfox.formatterservice.template.models.section.elements;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import eu.tecfox.formatterservice.template.models.style.Style;


/**
 * Interface that identifies all elements that can be used as
 * a value in {@link NestedElement}.
 *
 * @author Valentin Laucht
 * @author Florin Schikarski
 * @version 1.0
 */
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DateElement.class, name = "date"),
    @JsonSubTypes.Type(value = StringElement.class, name = "string"),
    @JsonSubTypes.Type(value = StringListElement.class, name = "stringList"),
    @JsonSubTypes.Type(value = DateRangeElement.class, name = "dateRange")
})
public interface NestedValue {
    void generateExample();
    void clearValue();

    String getIdentifier();

    Style getStyleKey();
    Style getStyleValue();
}
