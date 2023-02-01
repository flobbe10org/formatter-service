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
package io.github.flobbe10organization.formatterservice.template.models.section.elements.separator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * Class defining a character(-sequence) separating two Strings in a document.
 * 
 * @see SeparatorValue
 * @see SeparatorCategory
 * @author Florin Schikarski
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Separator {
    
    /** The actual separator. */
    private SeparatorValue value;

    /** Specifies wether the separator should be placed in front or in behind. */
    private SeparatorCategory category;


    public Separator() {}


    /**
     * Gets the actual character sequence of a separator (e.g. ", " or ": ").
     * 
     * @return a String with the actual separator.
     */
    @JsonIgnore
    public String getChars() {

        return this.value.getValue();
    }


    @Override
    public String toString() {

        return this.value.name();
    }
}