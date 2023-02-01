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
package eu.tecfox.formatterservice.template.models.section.elements.separator;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * Enum defining the actual character sequence used to separate Strings in a document.
 * 
 * @see Separator
 * @author Florin Schikarski
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum SeparatorValue {

    /** element1,_element2*/
    COMMA(", "),

    /** element1:_element2 */
    COLON(": "),

    /** element1;_element2 */
    SEMICOLON("; "),

    /** element1._element2 */
    DOT(". "),

    /** element1_-_element2 */
    DASH(" - "),

    /** element1
     * <p>
     *  element2
     */
    LINE_BREAK("\n"),

    /** element1_element2 */
    SPACE(" "),

    /** element1_____element2 */
    TAB("    "),

    /** Must be specified by method. */
    INDENT(""),

    /** 
     * -_element1
     * <p>
     * -_element2
     */
    BULLETPOINT("•   "),

    /**
     * 1._element1
     * <p>
     * 2._element2
     */
    NUMBER_AND_DOT("%d. "),

    /**
     * (1)_element1
     * <p>
     * (2)_element2
     */
    NUMBER_AND_PARANTHESES("(%d) ");


    private String value;
}