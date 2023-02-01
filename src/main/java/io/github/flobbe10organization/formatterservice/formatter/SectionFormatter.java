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

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import io.github.flobbe10organization.formatterservice.template.models.Template;
import io.github.flobbe10organization.formatterservice.template.models.section.Section;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.NestedKey;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.NestedValue;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.SectionElement;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.separator.Separator;
import io.github.flobbe10organization.formatterservice.template.models.section.elements.separator.SeparatorCategory;
import io.github.flobbe10organization.formatterservice.template.models.style.Style;
import lombok.RequiredArgsConstructor;


/**
 * Class to add the content from a {@link Template} to the {@link XWPFDocument}.
 * <p>
 * Uses {@link StyleFormatter}s methods for the styling.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
@RequiredArgsConstructor
public class SectionFormatter {

    private final Template template;

    private final XWPFDocument document;


    /**
     * Iterates sections from the template and adds content and style to the document.
     * Also adds the template title.
     * <p>
     * Adds a line break after every section.
     */
    void addContent() {
        
        // add template title
        addTitle(template.getTitle(), template.getStyle());

        // add all sections of template
        for (Section section : template.getSections()) {
            
            // add section
            addSection(section);

            // add line break
            document.createParagraph(); 
        }
    }


    /** 
     * Adds a title with style and separator to the document if title's not blank.
     * 
     * @param title to add.
     * @param style style to use on the title.
     */
    private void addTitle(String title, Style style) {

        // case: empty title
        if (title.isBlank())
            return;
        
        // create line
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        // add text
        run.setText(title);

        // set style
        StyleFormatter.addStyle(paragraph, run, style);

        // add separator
        StyleFormatter.addSeparatorBehind(paragraph, run, style.getSeparator());
    }
    

    /**
     * Iterates the section elements of a section and adds them to the document.
     * <p>
     * Also adds the section title.
     * 
     * @param section to add.
     */
    private void addSection(Section section) {
        
        // get section elements
        List<SectionElement> sectionElements = section.getElements();
        
        // add section title
        if (section.isShowTitle())
            addTitle(section.getTitle(), section.getStyleTitle()); 
        
        // add section elements
        for (SectionElement sectionElement : sectionElements) {
            // create paragraph
            addSectionElement(sectionElement, section.getStyleKey(), section.getStyleValue());
        }
    }
    
    
    /**
     * Adds key and value of a section element to the document. One paragraph will be used for one section element.
     * <p>
     * If the section element is not nested (thus has no paragraph yet), one will be created.
     * 
     * @param sectionElement to add.
     * @param styleKeySection of the section.
     * @param styleValueSection of the section.
     * @param sectionElementParagraph array with the paragraph of the section element (if nested).
     */
    private void addSectionElement(SectionElement sectionElement, 
                                   Style styleKeySection, 
                                   Style styleValueSection, 
                                   XWPFParagraph... sectionElementParagraph) {
        
        // create paragraph
        XWPFParagraph paragraph;
        try {
            paragraph = sectionElementParagraph[0];

        } catch (ArrayIndexOutOfBoundsException e) {
            paragraph = document.createParagraph();
        }
        
        // add key
        addKeyOrValue(paragraph, sectionElement, styleKeySection, styleValueSection, true);
        
        // add value
        addKeyOrValue(paragraph, sectionElement, styleKeySection, styleValueSection, false);
    }
    
    
    /**
     * Adds either a key or a value and the related style and separator (if key) of a section element to the document.
     * <p>
     * Covers any key or value type a template can have: String, List, {@link NestedKey} or {@link NestedValue}.
     * <p>
     * If the section element has no style objects defined (those are optional) the style of the section will be used 
     * as fallback (those are not optional). 
     * 
     * @param paragraph to add a new run to.
     * @param sectionElement to take style and content from.
     * @param styleKeySection style of a the section key.
     * @param styleValueSection style of the section value.
     * @param isKey true if a key should be added.
     * @see StyleFormatter
     */
    private void addKeyOrValue(XWPFParagraph paragraph, 
                               SectionElement sectionElement, 
                               Style styleKeySection, 
                               Style styleValueSection, 
                               boolean isKey) {
 
        // create run
        XWPFRun run = paragraph.createRun();

        // get key / value
        Object keyOrValue = isKey ? sectionElement.getKey() : sectionElement.getValue();

        // get keyString / valueString
        String keyOrValueString = isKey ? sectionElement.getKeyString() : sectionElement.getValueString();
        
        // get style
        Style style = StyleFormatter.getKeyOrValueStyle(sectionElement, styleKeySection, styleValueSection, isKey);
 
        // case: empty
        if (keyOrValueString.isBlank())
            return;

        // case: nested key / nested value
        if (keyOrValue instanceof NestedKey || keyOrValue instanceof NestedValue) {
            addSectionElement((SectionElement)keyOrValue, 
                              sectionElement.getStyleKey(), 
                              sectionElement.getStyleValue(), 
                              paragraph);
            
            return;
        }
        
        // case: value and list
        if (!isKey && sectionElement.getValue() instanceof List) {
            addListValues(paragraph, run, sectionElement, styleKeySection, styleValueSection);

        // case: any
        } else
            run.setText(keyOrValueString);

        // add style
        StyleFormatter.addStyle(paragraph, run, style);

        // add separator
        if(isKey)
            StyleFormatter.addSeparatorBehind(paragraph, run, style.getSeparator());
    }
    
    /**
     * Adds any value of a section element that is a {@link java.util.List}.
     * <p>
     * Covers both types of list elements: String and {@link NestedValue}.
     * 
     * @param paragraph to add a nested value to.
     * @param run to add the list values to.
     * @param sectionElement containing the style.
     * @param styleKeySection containing the style fall back for a key (if section element has no style).
     * @param styleValueSection containing the style fall back for a value (if section element has no style).
     */
    private void addListValues(XWPFParagraph paragraph, 
                               XWPFRun run, 
                               SectionElement sectionElement, 
                               Style styleKeySection, 
                               Style styleValueSection) {

        // get values
        List<?> values = (List<?>)sectionElement.getValue();
        
        // get list size
        int numValues = values.size();

        // case: list empty
        if (values.isEmpty())
            return;
        
        // add values
        for (int i = 0; i < numValues; i++) {
            // get is last value
            boolean isLastValue = (i == numValues - 1);

            // get value
            Object listValue = values.get(i);
            
            // case: nested value
            if (listValue instanceof NestedValue) {
                // add nested value
                addSectionElement((SectionElement)listValue, 
                                  sectionElement.getStyleKey(), 
                                  sectionElement.getStyleValue(),
                                  paragraph);

                // add break 
                run.addBreak();
                
            // case: any value
            } else {
                Style styleValue = StyleFormatter.getKeyOrValueStyle(sectionElement, styleKeySection, styleValueSection, false);
                addListValue(run, listValue, styleValue.getSeparator(), i, isLastValue); 
            }
        }
    }


    /**
     * Adds any list value that is not a {@link NestedValue} and its separator.
     * 
     * @param run the value is added to.
     * @param value to add.
     * @param separator to add after or in front of the value.
     * @param listIndex of the value in the regarding value list.
     * @param isLastValue true if value is the last in the regarding value list.
     */
    private void addListValue(XWPFRun run, 
                              Object value, 
                              Separator separator, 
                              int listIndex, 
                              boolean isLastValue) {

        // case: separator in front
        if (separator.getCategory().equals(SeparatorCategory.IN_FRONT)) {
            // add separator
            StyleFormatter.addSeparatorInFront(run, separator, listIndex);

            // add value
            run.setText(value.toString());

            // add breaks
            run.addBreak();
            if (isLastValue) 
                run.addBreak();

        // case: separator behind
        } else {
            // add value
            run.setText(value.toString());

            // add separator (except after last value)
            if (!isLastValue)
                StyleFormatter.addSeparatorBehind(null, run, separator);
        }
    }
}