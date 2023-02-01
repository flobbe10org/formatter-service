package eu.tecfox.formatterservice.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import eu.tecfox.formatterservice.formatter.ResourceHandler;


/**
 * Class providing test resources and methods for test classes.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
@TestInstance(Lifecycle.PER_CLASS)
public class ResourceHandlerTest {

    public static final String TEST_TEMPLATE = "./src/test/java/eu/tecfox/formatterservice/testdata/testTemplate.json";

    public static final String NEW_TEST_TEMPLATE = "./src/test/java/eu/tecfox/formatterservice/testdata/newTestTemplate.json";

    public static final String CONTRACT_TEMPLATE = "./src/test/java/eu/tecfox/formatterservice/testdata/contractTemplate.json";

    public static final String TEST_DOCX = "TestDocx.docx";

    public static final String TEST_PDF = "TestDocx.pdf";

    private final String testDir1 = "./dir1/";
    private final String testDir2 = "./dir2/";


    @BeforeAll
    void setup() {


        // create test dirs
        ResourceHandler.createDirs(testDir1, testDir2);
    }


    @Test
    @Order(0)
    void createDirs_shouldCreateEmptyDirs() {

        // should exist
        assertTrue(new File(testDir1).exists());
        assertTrue(new File(testDir2).exists());

        // should be dir and not file
        assertTrue(new File(testDir1).isDirectory());
        assertTrue(new File(testDir2).isDirectory());
    }


    @Test
    @Order(1)
    void deleteFiles_shouldThrowNullPointerIfDirEmpty() throws IOException {

        // fill test dir with mock file
        try (FileOutputStream os = new FileOutputStream(testDir1 + "test.txt")) {
            os.write("TestFile".length());
        }

        // should have created 1 file
        assertEquals(1, new File(testDir1).listFiles().length);

        // delete files
        ResourceHandler.clearDirectory(testDir1);

        // testDir1 should now be empty
        assertEquals(0, new File(testDir1).listFiles().length);
    }


    @AfterAll
    void cleanUp() {

        // delete test files
        assertTrue(new File(testDir1).delete());
        assertTrue(new File(testDir2).delete());
    }
}