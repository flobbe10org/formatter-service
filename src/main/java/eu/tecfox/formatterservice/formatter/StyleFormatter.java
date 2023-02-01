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

import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabs;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;

import eu.tecfox.formatterservice.template.models.section.elements.SectionElement;
import eu.tecfox.formatterservice.template.models.section.elements.separator.Separator;
import eu.tecfox.formatterservice.template.models.section.elements.separator.SeparatorCategory;
import eu.tecfox.formatterservice.template.models.section.elements.separator.SeparatorValue;
import eu.tecfox.formatterservice.template.models.style.Style;


/**
 * Class to add styling to a {@link org.apache.poi.xwpf.usermodel.XWPFDocument}.
 * 
 * @author Florin Schikarski
 * @since 1.0
 */
public class StyleFormatter {
    
    /** Cursor position starting at second third of page width. */
    static final BigInteger CURSOR_FIRST_THIRD = BigInteger.valueOf(3000);

    /** Cursor position in the center of a line. */
    static final BigInteger CURSOR_CENTER = BigInteger.valueOf(4500);

    /** Cursor position on the right of a line. */
    static final BigInteger CURSOR_RIGHT = BigInteger.valueOf(9000);

    
    /**
     * Appends a {@link Separator} to the given run. Should be used if {@link SeparatorCategory} is 'IN_FRONT'.
     * <p>
     * Covers edge cases like a numberd list.
     * 
     * @param run to add the separator to.
     * @param separator to append.
     * @param listIndex of the regarding value, in case it should be numbered.
     * @see SeparatorValue
     */
    static void addSeparatorInFront(XWPFRun run, Separator separator, Integer listIndex) {

        // get sparator value and chars
        SeparatorValue separatorValue = separator.getValue();
        String separatorChars = separator.getChars();

        // case: number
        if (separatorValue.equals(SeparatorValue.NUMBER_AND_DOT) || 
            separatorValue.equals(SeparatorValue.NUMBER_AND_PARANTHESES)) {
            run.setText((listIndex != null) ? separatorChars.formatted(listIndex + 1) : separatorChars);

        // case: any other
        } else {
            run.setText(separatorChars);
        }
    }


    /**
     * Appends a {@link Separator} to the given run. Should be used if {@link SeparatorCategory} is 'BEHIND'.
     * <p>
     * Covers edge cases like an indent.
     * 
     * @param paragraph to make the indent in. May be null if no indent is used.
     * @param run to append the separator to.
     * @param separator to append.
     * @see SeparatorValue
     */
    static void addSeparatorBehind(XWPFParagraph paragraph, XWPFRun run, Separator separator) {

        // case: indent
        if (separator.getValue().equals(SeparatorValue.INDENT) && paragraph != null) {
            // add indent
            addIndentAfterKey(paragraph, CURSOR_FIRST_THIRD, true);

            // add tab for first line
            run.addTab();

        // case: line break
        } else if (separator.getValue().equals(SeparatorValue.LINE_BREAK)) {
            run.addBreak();
        
        // case: any other
        } else 
            run.setText(separator.getChars());
    }


    /**
     * Adds an indent to the paragraph meaning that the left border of the document will shift by the given amount. 
     * 
     * @param paragraph to add the indent to. 
     * @param amount by which to shift the left border: 
     *               <p>
     *               3000 would make the border start at the second third of the documents width.
     *               4500 would make the border start at the center of the doucment.
     * @param firstLineHanging set this to true if very first line of the paragraph
     *                         should not be affected by the indent.
     */
    static void addIndentAfterKey(XWPFParagraph paragraph, BigInteger amount, boolean firstLineHanging) {

        // add indent 
        paragraph.setIndentFromLeft(amount.intValue());

        // case: first line hanging
        if (firstLineHanging) {
            // remove indent for first line only
            paragraph.setIndentationHanging(amount.intValue());

            // set tab stop
            setTabStop(paragraph, STTabJc.LEFT, CURSOR_FIRST_THIRD);
        }
    }


    /**
     * Sets a runs' style.
     * 
     * @param paragraph to set the text-align in.
     * @param run to style.
     * @param styleTitle with the style information to use.
     */
    static void addStyle(XWPFParagraph paragraph, XWPFRun run, Style style) {

        // set fon-family
        run.setFontFamily(style.getFontFamily());

        // font-size
        run.setFontSize(style.getFontSize());

        // color
        run.setColor(style.getColor());

        // bold
        run.setBold(style.isBold());

        // italic
        run.setItalic(style.isItalic());

        // text-align
        paragraph.setAlignment(style.getTextAlign());

        // add page break
        paragraph.setPageBreak(style.isStartOnNewPage());
    }


    /**
     * Decides which style to use. The section element's style will be returned (if not null), otherwise 
     * the style of the section.
     * 
     * @param sectionElement to check and return the style object of.
     * @param styleKeySection the fall back style key if the section element's is null.
     * @param styleValueSection the fall back style value if the section element's is null.
     * @param isStyleKey true if the styleKey object should be checked inside the section element
     *                   and false if the value should be checked.
     * @return a {@link Style} object.
     */
    static Style getKeyOrValueStyle(SectionElement sectionElement, Style styleKeySection, Style styleValueSection, boolean isStyleKey) {

        // case: style value
        if (isStyleKey) {
            Style syleKeySectionElement = sectionElement.getStyleKey();
            return (syleKeySectionElement != null) ? syleKeySectionElement : styleKeySection;
        }

        // case: style value
        Style syleValueSectionElement = sectionElement.getStyleValue();
        return (syleValueSectionElement != null) ? syleValueSectionElement : styleValueSection;
    }


    
    /**
     * Specifies where the cursor should stop when adding a tab. Will be applied only for the 
     * given {@link XWPFParagraph}.
     * <p>
     * Also adds the text alignment that should be applied starting after the tab.
     * 
     * @param paragraph to adjust the tab stop in.
     * @param textAlign of any text coming after the tab.
     * @param cursorPos specifies the new cursor position: 
     *                  <p>
     *                  9000 will set the cursor to the very right border.
     *                  4500 will set the cursor to center of the page.
     */
    static void setTabStop(XWPFParagraph paragraph, STTabJc.Enum textAlign, BigInteger cursorPos) {

        CTP ctp = paragraph.getCTP();

        CTPPr ctppr = ctp.getPPr();
        if (ctppr == null) ctppr = ctp.addNewPPr();

        CTTabs tabs = ctppr.getTabs();
        if (tabs == null) tabs = ctppr.addNewTabs();
        
        // set text-align
        CTTabStop tabStop = tabs.addNewTab();
        tabStop.setVal(textAlign);
        
        // set cursor position
        tabStop.setPos(cursorPos);
    }
}
