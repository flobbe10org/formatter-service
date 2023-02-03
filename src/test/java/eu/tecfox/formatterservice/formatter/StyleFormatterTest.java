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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;

import eu.tecfox.formatterservice.template.models.section.elements.SectionElement;
import eu.tecfox.formatterservice.template.models.section.elements.StringElement;
import eu.tecfox.formatterservice.template.models.section.elements.separator.Separator;
import eu.tecfox.formatterservice.template.models.section.elements.separator.SeparatorCategory;
import eu.tecfox.formatterservice.template.models.section.elements.separator.SeparatorValue;
import eu.tecfox.formatterservice.template.models.style.Style;


/** 
 * Test class for {@link StyleFormatter}.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
public class StyleFormatterTest {

    private static final String TEST_TEXT = "This is a test.";

    private XWPFDocument document;

    private Style style;


    @BeforeEach
    void setup() throws IOException {

        // create resource folders
        ResourceHandler.createDirs(INPUT_RESOURCE_PATH, OUTPUT_RESOURCE_PATH);

        // create empty document
        this.document = new XWPFDocument();

        // set style
        this.style = new Style();
        this.style.setFontFamily("Tahoma");
        this.style.setFontSize(11);
        this.style.setColor("7F7F7F");
        this.style.setBold(true);
        this.style.setItalic(true);
        this.style.setTextAlign(ParagraphAlignment.CENTER);
        this.style.setStartOnNewPage(true);
    }

    
    @Test
    void addSeparatorInFront_shouldAddSeparatorInFrontText() {

        // create Separator
        Separator separator = new Separator(SeparatorValue.NUMBER_AND_DOT, SeparatorCategory.IN_FRONT);
    
        // create run
        XWPFRun run = document.createParagraph().createRun();

        // set list index
        int listIndex = 0;

        // add separator, then add text
        StyleFormatter.addSeparatorInFront(run, separator, listIndex);
        run.setText(TEST_TEXT);

        // check for correct string
        assertEquals((listIndex + 1) + ". " + TEST_TEXT, run.text());
    }
    

    @Test
    void addSeparatorBehind_shouldAddSeparatorBehindText() {
        
        // create Separator
        Separator separator = new Separator(SeparatorValue.DASH, SeparatorCategory.BEHIND);
    
        // create run
        XWPFRun run = document.createParagraph().createRun();
        
        // add text, then add separator
        run.setText(TEST_TEXT);

        StyleFormatter.addSeparatorBehind(null, run, separator);

        // check for correct string
        assertEquals(TEST_TEXT + separator.getChars(), run.text());
    }


    @Test
    void addIndentAfterKey_shouldShiftLeftBorder() {

        // set amount
        BigInteger amount = BigInteger.valueOf(3000);

        // add text
        XWPFParagraph paragraph = document.createParagraph();

        // add indent
        StyleFormatter.addIndentAfterKey(paragraph, amount, false);

        // check amount
        assertEquals(paragraph.getIndentFromLeft(), amount.intValue());
    }


    @Test
    void testAddStyle() {
        
        // create paragraph
        XWPFParagraph paragraph = document.createParagraph();

        // create run
        XWPFRun run = paragraph.createRun();

        // add style
        StyleFormatter.addStyle(paragraph, run, this.style);

        // check every style attribute
        assertEquals(style.getFontFamily(), run.getFontFamily());
        assertEquals(style.getFontSize(), run.getFontSizeAsDouble().intValue());
        assertEquals(style.getColor(), run.getColor());
        assertEquals(style.isBold(), run.isBold());
        assertEquals(style.isItalic(), run.isItalic());
        assertEquals(style.isStartOnNewPage(), paragraph.isPageBreak());
        assertEquals(style.getTextAlign(), paragraph.getAlignment());
    }


    @Test
    void getKeyOrValueStyle_shouldUseStyleOfSection() {

        // create section element without styles
        SectionElement sectionElement = new StringElement();

        // should use styles from parameter as fall back
        Style actualStyle = StyleFormatter.getKeyOrValueStyle(sectionElement, style, null, true);
        assertEquals(this.style, actualStyle);
    }


    @Test
    void getKeyOrValueStyle_shouldUseStyleOfSectionElement() {

        // create section element
        SectionElement sectionElement = new StringElement();

        // create key style for section element
        Style styleKeySectionElement = new Style();

        // should use keyStyle from section element
        Style actualStyle = StyleFormatter.getKeyOrValueStyle(sectionElement, styleKeySectionElement, null, true);
        assertEquals(styleKeySectionElement, actualStyle);
    }


    @Test
    void testSetTabStop() {

        // create paragraph
        XWPFParagraph paragraph = document.createParagraph();

        // get cursor position
        BigInteger cursorPosition = BigInteger.valueOf(3000);

        // set tab stop
        StyleFormatter.setTabStop(paragraph, STTabJc.CENTER, cursorPosition);

        // check tab position
        Object tabPosition = paragraph.getCTP().getPPr().getTabs().getTabArray()[0].getPos();
        assertEquals(cursorPosition, tabPosition);
    }
}