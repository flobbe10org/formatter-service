package io.github.flobbe10organization.formatterservice.formatter;

import static io.github.flobbe10organization.formatterservice.formatter.ResourceHandler.INPUT_RESOURCE_PATH;
import static io.github.flobbe10organization.formatterservice.formatter.ResourceHandler.OUTPUT_RESOURCE_PATH;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.github.flobbe10organization.formatterservice.formatter.HeaderFooterFormatter;
import io.github.flobbe10organization.formatterservice.formatter.ResourceHandler;
import io.github.flobbe10organization.formatterservice.template.models.Template;
import io.github.flobbe10organization.formatterservice.testdata.TestDataGenerator;


/**
 * Test class for {@link HeaderFooterFormatter}.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
@TestInstance(Lifecycle.PER_CLASS)
public class HeaderFooterFormatterTest {

    private Template template;
    
    private XWPFDocument document = new XWPFDocument();
    
    private HeaderFooterFormatter headerFooterFormatter;



    @BeforeAll
    void setup() throws IOException {

        // create resource folders
        ResourceHandler.createDirs(INPUT_RESOURCE_PATH, OUTPUT_RESOURCE_PATH);

        // set template
        this.template = TestDataGenerator.generateValidNewTemplate();

        // set headerFooterFormatter
        this.headerFooterFormatter = new HeaderFooterFormatter(template, document);

        // add header and footer
        headerFooterFormatter.addHeaderAndFooter();
    }

    
    @Test
    void addHeader_shouldBeCorrectContentOnAllPages() {

        // get header on page 1
        XWPFHeader headerPageOne = document.getHeaderFooterPolicy().getHeader(0);

        // header should have picture
        assertFalse(headerPageOne.getAllPictures().isEmpty());

        // get header on page 2
        XWPFHeader headerPageTwo = document.getHeaderFooterPolicy().getHeader(1);

        // header should have picture
        assertFalse(headerPageTwo.getAllPictures().isEmpty());
    }


    @Test
    void addFooter_shouldBeCorrectContentOnAllPages() {

        // get footer on page 1
        XWPFFooter footerPageOne = document.getHeaderFooterPolicy().getFooter(0);
        List<XWPFParagraph> paragraphsPageOne = footerPageOne.getParagraphs();

        // footer should have some text
        assertFalse(paragraphsPageOne.isEmpty());
        assertFalse(paragraphsPageOne.get(1).getText().isEmpty());

        // get footer on page 2
        XWPFFooter footerPageTwo = document.getHeaderFooterPolicy().getFooter(1);
        List<XWPFParagraph> paragraphsPageTwo = footerPageTwo.getParagraphs();
        
        // footer should have some text
        assertFalse(paragraphsPageTwo.isEmpty());
        assertFalse(paragraphsPageTwo.get(1).getText().isEmpty());
    }
}