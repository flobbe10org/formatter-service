package eu.tecfox.formatterservice.formatter;

import java.io.File;
import java.util.UUID;

import eu.tecfox.formatterservice.exception.ApiRequestException;
import lombok.extern.log4j.Log4j2;


/**
 * Class handling resource files, providing their paths and directories.
 * 
 * @since 1.0
 * @author Florin Schikarski
 */
@Log4j2
public class ResourceHandler {
    
    public static final String INPUT_RESOURCE_PATH = "./inputResources/";

    public static final String OUTPUT_RESOURCE_PATH = "./outputResources/";

    public static final String TECFOX_LOGO_PATH = "./inputResources/TecFox_Logo.png";

    public static final String PROFILE_TEMPLATE = "src/main/java/eu/tecfox/profileconfig/template/profileTemplate.json";

    public static final String DOCX_FORMATTED_BY_API = "Profile-" + UUID.randomUUID() + ".docx";

    public static final String PDF_FORMATTED_BY_API = "Profile.pdf";


    /**
     * Creates directories at the specified path if they don't already exist.
     * 
     * @param dirs array with paths of the directories to create relative to the execution folder (where some command like
     *            'gradle bootRun' is run).
     * @return true if dir was created successfully or already existed.
     * @throws IllegalStateException if target directory could not be created.
     */
    public static boolean createDirs(String... dirs) {

        for (String dir : dirs) {

            File targetDir = new File(dir);
            
            if (!targetDir.exists()) {
                if (!targetDir.mkdirs()) 
                    throw new ApiRequestException("Failed to create output directory at path " + targetDir + ".");
                
                log.info("Target directorie created");
            }
        }

        return true;
    }


    /**
     * Removes all files from the given directory (but not the directory itself).
     * <p>
     * Logs message if some could not be deleted.
     * 
     * @param path to the directory.
     * @return true if all files have been deleted.
     */
    public static boolean clearDirectory(String path) {

        // get dir 
        File dir = new File(path);

        // case: is not a dir
        if (!dir.isDirectory()) {
            log.info("Failed to clear directory " + path + ".");
            return false;
        }

        // iterate files
        for (File file : dir.listFiles()) {
            if (!file.delete()) {
                log.info("Failed to delete file " + file.getPath() + ".");
                return false;
            }
        }

        return true;
    }
}