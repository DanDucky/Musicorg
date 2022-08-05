import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
//import org.xml.sax.SAXException;

//import org.xml.sax.ContentHandler;

public class FileRead {
        private String originalPath;
        private char[] folderScheme;
        private char[] fileScheme;
        boolean[][] errorLogFolder;
        boolean thrownError = false;
        int currentErrorPos = 0;
        String[] metadataFile = {"null"};
        Mp3Parser parseMp3 = new Mp3Parser();
        ParseContext parseC = new ParseContext();
        Metadata metadataTika = new Metadata();
        BodyContentHandler handler = new BodyContentHandler();
        public FileRead(String path, char[] folder, char[] file) {
                this.originalPath = path;
                this.folderScheme = folder;
                this.fileScheme = file;
        }
        public void recurse(String path) {
                String[] dirIndex;
                try {
                        File folder = new File(path);
                        dirIndex = folder.list();
                        if(dirIndex!=null) {
                                for (int i = 0; i < dirIndex.length; i++) {
                                        File f = new File(dirIndex[i]);
                                        if (!f.isFile()) {
                                                String passPath = path + "/" + dirIndex[i];
                                                recurse(passPath);
                                        }
                                }
                                readRename(dirIndex, path);
                        }
                } catch(Exception e) {
                        e.printStackTrace();
                }
        }
        public void readRename(String[] dirIndex, String path) {
                thrownError = false;
//                String[] metadata;
//                String[][] metadataFile;
                boolean isMusicFolder = false;
//                boolean discrepancy;
                for (int i = 0; isMusicFolder == false && i < dirIndex.length; i++) {
                        if (dirIndex[i].contains(".ogg") || dirIndex[i].contains(".mp3") || dirIndex[i].contains(".flac") || dirIndex[i].contains(".wav")) {
                                isMusicFolder = true;
                        }
                }
                if (isMusicFolder == false) {
                        return;
                }

                //loop through files and find all required metadata and write to metadata[], 
                //put any weird metadata on new lines of array and change bool[discrepancy] to true and 
                //pass that to a function that logs the folder name and position to tell the user at the end of the program. 
                //At the end if no discrepancies are found rename files and folder
                /*
                 * ----|----|----
                 * year art  albm 
                 *   1  sam  yes
                 *   2
                 *   2
                 *   
                 *   above is what metadata[] should look like assuming multiple years and one artist and one album
                 */
                String fileType = "null";
                for (int i = 0; i < dirIndex.length; i++) {
                        File currentFile = new File(path + "/" +dirIndex[i]);
                        fileType = "null";
                        Arrays.fill(metadataFile, null);
						FileInputStream fileInput = null;
						try {
							fileInput = new FileInputStream(currentFile);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                        if(!currentFile.isDirectory() && dirIndex[i].contains(".")) {
                                fileType = dirIndex[i].substring(dirIndex[i].lastIndexOf("."), dirIndex[i].length());
                        }
                        System.out.println(fileType);
                        if(fileType.equals(".mp3")) {
                                try {
									parseMp3.parse(fileInput, handler, metadataTika, parseC);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                                for (int ii = 0; i < fileScheme.length; ii++) {
                                        switch (fileScheme[ii]) {
                                                case 0:
                                                        mp3Get(ii, "xmpDM:albumArtist");
                                                        break;
                                                case 1:
                                                        mp3Get(ii, "xmpDM:album");
                                                        break;
                                                case 2:
                                                        mp3Get(ii, "xmpDM:releaseDate");
                                                        break;
                                                case 3:
                                                        mp3Get(ii, "xmpDM:genre");
                                                        break;
                                                case 4:
                                                        mp3Get(ii, "dc:title");
                                                        break;
                                                case 5:
                                                        mp3Get(ii, "xmpDM:trackNumber");
                                                        break;
                                                case 6:
                                                        mp3Get(ii, "xmpDM:artist");
                                                        break;
                                        }
                                        if (thrownError == true) {
                                                File pathObj = new File(path);
                                                renameFolder(pathObj, "Error" + String.valueOf(currentErrorPos - 1));
                                                return;
                                        }
                                }
                        } else if (fileType.equals(".flac")) {

                        }
                }

                if (!path.substring(0, path.lastIndexOf("/")).equals(originalPath)) { // move statement to end (after rename)
                        //copy to main dir

                } else if (path == originalPath) {
                        return;
                }
        }
        private void mp3Get(int position, String request) {
                String handleMeta = "null";
                handleMeta = metadataTika.get(request);
                if (handleMeta != null) {
                        metadataFile[position] = handleMeta;
                } else {
                        metadataFile[position] = null;
                        errorLogFolder[currentErrorPos][1] = true; // Mark missing metadata
                        errorLogFolder[currentErrorPos][0] = true; // Mark error in this column
                        thrownError = true;
                        currentErrorPos++;
                }
        }
        private void renameFolder(File toRename, String name) {
                File rename = new File(name);
        }
}