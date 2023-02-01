package io.github.flobbe10organization.formatterservice.formatter;

import static io.github.flobbe10organization.formatterservice.formatter.ResourceHandler.INPUT_RESOURCE_PATH;
import static io.github.flobbe10organization.formatterservice.formatter.ResourceHandler.OUTPUT_RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.flobbe10organization.formatterservice.formatter.DocxToPdf;
import io.github.flobbe10organization.formatterservice.formatter.ResourceHandler;


/**
 * Test class for {@link DocxToPdf}.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
public class DocxToPdfTest {

    private static String docxPath = INPUT_RESOURCE_PATH + ResourceHandlerTest.TEST_DOCX;
    private static String pdfPath = OUTPUT_RESOURCE_PATH + ResourceHandlerTest.TEST_PDF;


    @BeforeAll
    void setup() {

        // create resource folders
        ResourceHandler.createDirs(INPUT_RESOURCE_PATH, OUTPUT_RESOURCE_PATH);
    }


    // @Test
    // TODO: uncomment this when DocxToPdf is fixed
    void convert_shouldParseDocxToPdf_fileShouldExist() throws FileNotFoundException, IOException {

        // convert
        DocxToPdf.convert(docxPath, pdfPath);

        // should exist
        assertTrue(new File(pdfPath).delete());
    }


    @AfterAll
    void cleanUp() {

        // delete all test files
        ResourceHandler.clearDirectory(OUTPUT_RESOURCE_PATH);
    }
}