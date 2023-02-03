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

import static eu.tecfox.formatterservice.formatter.ResourceHandler.DOCX_FORMATTED_BY_API;
import static eu.tecfox.formatterservice.formatter.ResourceHandler.OUTPUT_RESOURCE_PATH;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.http.HttpStatus;

import eu.tecfox.formatterservice.exception.ApiRequestException;
import eu.tecfox.formatterservice.template.models.Template;


/**
 * Class formatting a given {@link Template} to a MS Word document in .docx format. 
 * <p>
 * Writes the finished document to a file with a unique name in the 'inputResources' 
 * folder at the project root.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
public class Formatter {

    /** Target path to write the formatted docx file to. */
    private static final String DOCX_PATH = OUTPUT_RESOURCE_PATH + DOCX_FORMATTED_BY_API;

    private final XWPFDocument document = new XWPFDocument();
    
    private final SectionFormatter sectionFormatter;

    private final HeaderFooterFormatter headerFooterFormatter;


    public Formatter(Template template) {
        
        this.sectionFormatter = new SectionFormatter(template, document);

        this.headerFooterFormatter = new HeaderFooterFormatter(template, document);
    } 
    
    
    /**
     * Will execute the whole process of formatting and writing the document to a file.
     * 
     * @return the relative path to the formatted .docx file.
     */
    public String formatDocument() {

        // add text content
        sectionFormatter.addContent();

        // add heder and footer
        headerFooterFormatter.addHeaderAndFooter();

        // write to .docx file
        writeDocxToFile(DOCX_PATH);

        return DOCX_PATH;
    }


    /**
     * Writes a {@link XWPFDocument} to a .docx file to the given path.
     * 
     * @param path of the .docx file.
     * @throws ApiRequestException if any path is not found.
     */
    private void writeDocxToFile(String path) {

        try (OutputStream os = new FileOutputStream(path)) {
            document.write(os);
            document.close();

        } catch (IOException e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}