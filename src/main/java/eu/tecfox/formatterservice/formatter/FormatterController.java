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

import static eu.tecfox.formatterservice.formatter.ResourceHandler.OUTPUT_RESOURCE_PATH;
import static eu.tecfox.formatterservice.formatter.ResourceHandler.PDF_FORMATTED_BY_API;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.tecfox.formatterservice.template.models.Template;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Class handling all endpoints related to {@link Formatter}.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
@RestController
@RequestMapping("/api/formatter")
@Api(tags = {"Formatter Controller"})
@Tag(name = "Formatter Controller", description = "All endpoints related to the Formatter object.")
public class FormatterController {


    @GetMapping(value = "/{userId}")
    @ApiOperation(value = "Format and download a user's profile as docx or pdf.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "The formatted profile as docx or pdf."),
        @ApiResponse(code = 404, message = "The user with id <userId> has no profile yet."),
    })
    public ResponseEntity<Resource> formatAndDownload(@PathVariable String userId, @RequestParam boolean pdf) throws IOException {

        // get profile from db
        // Profile profile = profileService.findByUserId(userId).orElseThrow(() ->
        //     new ApiRequestException("The user with id " + userId + " has no profile yet.", HttpStatus.NOT_FOUND));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.readTree(
            Paths.get("./src/test/java/eu/tecfox/profileconfig/testdata/newTestTemplate.json").toFile()).toString();
        Template template = mapper.readValue(json, Template.class);

        // format profile
        Formatter formatter = new Formatter(template);
        String docxPath = formatter.formatDocument();

        // pdf path
        String pdfPath = OUTPUT_RESOURCE_PATH + PDF_FORMATTED_BY_API;

        // path to the download file
        String filePath;
        
        // download as pdf
        if (pdf) {
            // convert
            // TODO: uncomment this line when DocxConverter is fixed
            // DocxToPdf.convert(docxPath, pdfPath);

            // set download path
            // filePath = pdfPath;
            // TODO: delete the line below and use the line above when DocxConverter is fixed
            filePath = docxPath;
            
        // download as docx
        } else {          
            // set download path
            filePath = docxPath;
        }

        // get resource
        File file = new File(filePath);
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        
        // download file
        return ResponseEntity.ok()
                             .headers(getHttpHeaders(file.getName()))
                             .contentLength(file.length())
                             .contentType(MediaType.parseMediaType("application/octet-stream"))
                             .body(isr);
    }


    @DeleteMapping("/deleteFiles")
    @ApiOperation(value = "Delete all files created during the formatting process.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "All files in ./outpuresources have been deleted"),
        @ApiResponse(code = 500, message = "Failed to delete some files.")
    })
    public ResponseEntity<Boolean> deleteFiles() {

        // delete all files in ./outputResources
        Boolean allDeleted = ResourceHandler.clearDirectory(OUTPUT_RESOURCE_PATH);

        // choose HttpStatus
        HttpStatus httpStatus = allDeleted ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(httpStatus).body(allDeleted);
    }
    

    /**
     * Create http headers for the download request.
     * 
     * @param fileName to use for the downloaded file.
     * @return {@link HttpHeaders} object.
     */
    private HttpHeaders getHttpHeaders(String fileName) {

        HttpHeaders header = new HttpHeaders();

        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return header;
    }
}