import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.gagravarr.tika.FlacParser;
import org.gagravarr.tika.VorbisParser;

public class FileRead {
	private String originalPath;
	private char[] folderScheme;
	private char[] fileScheme;
	boolean[][] errorLogFolder;
	boolean thrownError = false;
	int currentErrorPos = 0;
	String[] metadataFile = { null, null, null, null, null, null, null };
	String[] metadataFolder = { null, null, null, null };
	Mp3Parser parseMp3 = new Mp3Parser();
	FlacParser parseFlac = new FlacParser();
	VorbisParser parseVorbis = new VorbisParser();
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
			if (dirIndex != null) {
				for (int i = 0; i < dirIndex.length; i++) {
					File f = new File(dirIndex[i]);
					if (!f.isFile()) {
						String passPath = path + "/" + dirIndex[i];
						recurse(passPath);
					}
				}
				readRename(dirIndex, path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readRename(String[] dirIndex, String path) {
		thrownError = false;
		boolean isMusicFolder = false;
		for (int i = 0; isMusicFolder == false && i < dirIndex.length; i++) {
			if (dirIndex[i].contains(".ogg") || dirIndex[i].contains(".mp3") || dirIndex[i].contains(".flac")
					|| dirIndex[i].contains(".wav")) {
				isMusicFolder = true;
			}
		}
		if (isMusicFolder == false) {
			return;
		}

		// loop through files and find all required metadata and write to metadata[],
		// put any weird metadata on new lines of array and change bool[discrepancy] to
		// true and
		// pass that to a function that logs the folder name and position to tell the
		// user at the end of the program.
		// At the end if no discrepancies are found rename files and folder
		/*
		 * ----|----|---- year art albm 1 sam yes 2 2
		 * 
		 * above is what metadata[] should look like assuming multiple years and one
		 * artist and one album
		 */
		String fileType = "null";
		for (int i = 0; i < dirIndex.length; i++) {
			File currentFile = new File(path + "/" + dirIndex[i]);
			fileType = "null";
			FileInputStream fileInput = null;
			try {
				fileInput = new FileInputStream(currentFile);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if (!currentFile.isDirectory() && dirIndex[i].contains(".")) {
				fileType = dirIndex[i].substring(dirIndex[i].lastIndexOf("."), dirIndex[i].length());
			}
			System.out.println(fileType + " " + i);
			if (fileType.equals(".mp3")) {
				try {
					parseMp3.parse(fileInput, handler, metadataTika, parseC);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			} else if (fileType.equals(".flac")) {
				InputStream bufferedIn = new BufferedInputStream(fileInput);
				try {
					parseFlac.parse(bufferedIn, handler, metadataTika, parseC); // ERROR
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
//                      else if (fileType.equals(".ogg")) {
//                              try {
//                                      parseVorbis.parse(fileInput, handler, metadataTika, parseC);
//                              } catch (Exception e) {
//                                      e.printStackTrace();
//                                      return;
//                              }
//                      }
			for (int ii = 0; ii < fileScheme.length; ii++) {
				switch (fileScheme[ii]) {
				case '0':
					getFile(ii, "xmpDM:albumArtist");
					break;
				case '1':
					getFile(ii, "xmpDM:album");
					break;
				case '2':
					getFile(ii, "xmpDM:releaseDate");
					break;
				case '3':
					getFile(ii, "xmpDM:genre");
					break;
				case '4':
					getFile(ii, "dc:title");
					break;
				case '5':
					getFile(ii, "xmpDM:trackNumber");
					break;
				case '6':
					getFile(ii, "xmpDM:artist");
					break;
				default:
					System.out.println("error in the big mp3 switch");
					break;

				}
				if (thrownError == true) {
					System.out.println("thrownError == true");
					File pathObj = new File(path);
					rename(pathObj, "Error" + String.valueOf(currentErrorPos - 1));
					return;
				}
			}
			String rename = "";
			for (int renameArrIndex = 0; renameArrIndex < fileScheme.length; renameArrIndex++) {
				rename = rename + metadataFile[renameArrIndex];
				if (renameArrIndex != fileScheme.length - 1) {
					rename = rename + " - ";
				}
			}
			rename(currentFile, path + "/" + rename + fileType);
		}
		File currentFolder = new File(path);
//		System.out.println(originalPath + "/" + path.substring(path.lastIndexOf('/') + 1, path.length()));

		for (int ii = 0; ii < folderScheme.length; ii++) {
			switch (folderScheme[ii]) {
			case '0':
				getFolder(ii, "xmpDM:albumArtist");
				break;
			case '1':
				getFolder(ii, "xmpDM:album");
				break;
			case '2':
				getFolder(ii, "xmpDM:releaseDate");
				break;
			case '3':
				getFolder(ii, "xmpDM:genre");
				break;
			case '4':
				getFolder(ii, "xmpDM:albumArtist");
				getFolder(ii++, "xmpDM:album");
				getFolder(ii += 2, "xmpDM:releaseDate");
				break;
			}
		}

		String renameFolder = "";
		for (int renameArrIndex = 0; renameArrIndex < folderScheme.length; renameArrIndex++) {
			renameFolder = renameFolder + metadataFolder[renameArrIndex];
			if (renameArrIndex != folderScheme.length - 1) {
				renameFolder = renameFolder + " - ";
			}
		}
		System.out.println(renameFolder);
		rename(currentFolder, originalPath + "/" + renameFolder);
//		if (!path.substring(0, path.lastIndexOf("/")).equals(originalPath)) {
//			// copy to main dir
//
//		} else if (path == originalPath) {
//			return;
//		}
	}

	private void getFile(int position, String request) {
		String handleMeta = "null";
		metadataFile[position] = handleMeta.toString();
		handleMeta = metadataTika.get(request);
		if (handleMeta != null) {
			metadataFile[position] = handleMeta.toString();
			return;
		} else {
			System.out.println("ERROR in metadata parsing");
			metadataFile[position] = null;
			errorLogFolder[currentErrorPos][1] = true; // Mark missing metadata
			errorLogFolder[currentErrorPos][0] = true; // Mark error in this column
			thrownError = true;
			currentErrorPos++;
			return;
		}
	}

	private void rename(File toRename, String name) {
		File rename = new File(name);
		toRename.renameTo(rename);
	}

	private void getFolder(int position, String request) {
		String handleMeta = "null";
		// TODO: make it check for metadata issues
		handleMeta = metadataTika.get(request);
		metadataFolder[position] = handleMeta.toString();
	}
}