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

import static io.github.flobbe10organization.formatterservice.formatter.StyleFormatter.CURSOR_CENTER;
import static io.github.flobbe10organization.formatterservice.formatter.StyleFormatter.CURSOR_RIGHT;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;
import org.springframework.http.HttpStatus;

import io.github.flobbe10organization.formatterservice.exception.ApiRequestException;
import io.github.flobbe10organization.formatterservice.template.models.Template;
import io.github.flobbe10organization.formatterservice.template.models.elements.Footer;
import io.github.flobbe10organization.formatterservice.template.models.style.Style;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class HeaderFooterFormatter {

    /** Width of the TecFox logo. */
    private static final Double TECFOX_LOGO_WIDTH = 3.53;

    /** Height of the TecFox logo. */
    private static final Double TECFOX_LOGO_HEIGHT = 0.9;

    /** 
     * Used for calculating picture dimensions.
     * @see org.apache.poi.util.Units
     */
    private static final Integer EMU_PER_CENTIMETER = 360000;

    private final Template template;
    
    private final XWPFDocument document;
    

    /**
     * Adds header and footer to every page in the document.
     * <p>
     * Header and footer are specified by the template field.
     */
    void addHeaderAndFooter() {

        // create header and footer in document
        XWPFHeaderFooterPolicy policy = document.createHeaderFooterPolicy();
        XWPFFooter footer = policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFHeader header = policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

        addHeader(header);
        
        addFooter(footer);
    }


    /**
     * Adds the specified logo using the path in the header object from the template field.
     * <p>
     * Places it on the top left of every page in the document as header.
     * Formats the logo with the size specified on top of this class.
     * 
     * @param header {@link XWPFHeader} to add the actual header to.
     * @throws ApiRequestException if the logo file is not found or is badly formatted.
     */
    private void addHeader(XWPFHeader header) {

        // create paragraph
        XWPFParagraph paragraph = header.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.RIGHT);

        // get file name of logo
        String logoFileName = template.getHeader().getLogo();
        
        // add logo 
        try {
            paragraph.createRun()
                     .addPicture(new FileInputStream(logoFileName), 
                                 getPictureFormat(logoFileName),
                                 "HeaderLogo", 
                                 cmToEMUs(TECFOX_LOGO_WIDTH), 
                                 cmToEMUs(TECFOX_LOGO_HEIGHT));
        
        // case: file not found
        } catch (IOException e) {
            paragraph.createRun().setText("Failed to load logo.");

        // case: wrong format
        } catch (InvalidFormatException e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    /**
     * Adds the footer text from the footer object from the template field.
     * <p>
     * Divides the footer in three parts (left, middle and right) and applies the given style.
     * Text alignment in each part will be the same as the part itself (so text-align right in the right section etc.).
     * <p>
     * Adds this text to every page of the document as footer.
     * 
     * @param documentFooter {@link XWPFFooter} object to add the actual footer to.
     */
    private void addFooter(XWPFFooter documentFooter) {

        // get footer data
        Footer footer = template.getFooter();
        List<String> leftSection = footer.getLeftSection();
        List<String> middleSection = footer.getMiddleSection();
        List<String> rightSection = footer.getRightSection();
        
        // create buffer line above footer
        documentFooter.createParagraph();

        // get size of longest list
        int maxListSize = getMaxValue(leftSection.size(), middleSection.size(), rightSection.size());

        // add line by line until no section elements are left
        for (int i = 0; i < maxListSize; i++) {
            // create paragraph
            XWPFParagraph paragraph = documentFooter.createParagraph();
            // create run
            XWPFRun run = paragraph.createRun();
            // get style
            Style style = footer.getStyle();

            // make tabs stop in the middle and on the right
            StyleFormatter.setTabStop(paragraph, STTabJc.Enum.forString("center"), CURSOR_CENTER);
            StyleFormatter.setTabStop(paragraph, STTabJc.Enum.forString("center"), CURSOR_RIGHT);
            
            // decrease line hight
            paragraph.setSpacingAfter(0);
            run.setFontSize(style.getFontSize());

            // add text
            addFooterText(run, leftSection, i, true);            
            addFooterText(run, middleSection, i, true);            
            addFooterText(run, rightSection, i, false);            
                
            // set style
            StyleFormatter.addStyle(paragraph, run, style);
        }
    }


    /**
     * Helper method for {@link #addFooter(XWPFFooter)} to add text and possibly a tab.
     * 
     * @param run to add the text to.
     * @param footerSection to take the text value from.
     * @param index of the text value in the footerSection.
     * @param addTab true if a tab should be added.
     */
    private void addFooterText(XWPFRun run, List<String> footerSection, int index, boolean addTab) {

        try {
            // add text
            run.setText(footerSection.get(index));
            
        } catch(IndexOutOfBoundsException e) {}

        if (addTab) 
            run.addTab();
    }


    /**
     * Iterates array and returns the biggest value from it.
     * 
     * @param values array of integers.
     * @return the maximum value of the array.
     */
    private int getMaxValue(int... values) {

        int maxValue = Integer.MIN_VALUE;

        // iterate values
        for (int value : values) 
            if (maxValue < value) maxValue = value;

        return maxValue;
    }


    /**
     * Converts centimeters to EMUs. Rounds up from .5 on (e.g. 0.5 = 1 but 0.4 = 0).
     * 
     * @see org.apache.poi.util.Units
     * @param centimeters to convert.
     * @return EMUs as integer.
     */
    private int cmToEMUs(@DecimalMin("0.0") double centimeters) {

        return (int) Math.round(EMU_PER_CENTIMETER * centimeters);
    }


    /**
     * Gets the format a picture is in using the last characters of the given file name.
     * <p>
     * Currently only "jpeg" and "png" are available.
     * 
     * @see org.apache.poi.xwpf.usermodel.Document
     * @param fileName name of the picture file.
     * @return the picture format as int, representing an enum.
     * @throws ApiRequestException if the give format is not supported.
     */
    private int getPictureFormat(String fileName) {

        // get file name length
        int fileNameLength = fileName.length();

        // get picture format as string
        String lastFourChars = fileName.substring(fileNameLength - 4);
        String lastThreeChars = fileName.substring(fileNameLength - 3);

        if (lastFourChars.equalsIgnoreCase("jpeg"))
            return Document.PICTURE_TYPE_JPEG;

        if (lastThreeChars.equalsIgnoreCase("png"))
            return Document.PICTURE_TYPE_PNG;

        throw new ApiRequestException("Unknown picture format of file " + fileName + ". Use either png or jpeg.");
    }
}
