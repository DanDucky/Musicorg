import java.io.File;

import org.apache.tika.parser.mp3.ID3Tags;

public class FileRead {
	private String originalPath;
	private char[] folderScheme;
	private char[] fileScheme;
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
		String[] metadata;
		String[] metadataFile;
		boolean isMusicFolder = false;
		boolean discrepancy;
		for (int i = 0; isMusicFolder == false && i < dirIndex.length; i++) {
//			System.out.println(dirIndex[i]);
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
		for (int i = 0; i < dirIndex.length; i++) {
			File currentFile = new File(dirIndex[i]);
			String fileType = "null";
			if(!currentFile.isDirectory() && dirIndex[i].contains(".")) {
				fileType = dirIndex[i].substring(dirIndex[i].lastIndexOf("."), dirIndex[i].length());
			}
			System.out.println(fileType);
			if(fileType.equals(".mp3")) {
				String bep = currentFile.getAlbumArtist();
			}
		}
		
		if (!path.substring(0, path.lastIndexOf("/")).equals(originalPath)) { // move statement to end (after rename)
			//copy to main dir
			
		} else if (path == originalPath) {
			return;
		}
	}
}
