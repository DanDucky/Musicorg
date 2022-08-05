import java.util.Scanner;

public class Main {
        public static void main(String[] args) {
                Scanner sc = new Scanner(System.in);
                boolean options = false;
                char[] folder;
                char[] file;
                System.out.println("---------< WElCOME TO MUSICORG >---------\n");
                do {
                        System.out.print(  "Would you like to run Musicorg in memory-conservation mode? [y/n/help] ");
                        String memoSafe = sc.nextLine();
                        if (memoSafe.equals("y") || memoSafe.equals("Y")) {
                                options = true;
                        } else if (memoSafe.equals("n") || memoSafe.equals("N")) {
                                options = true;

                        } else if (memoSafe.equals("help")) {
                                options = false;
                                System.out.println("\n\tMemory-conservation mode writes an index file instead of saving a directory index to memory. After the program has run index.txt will be removed.\n");
                        } else {
                                options = false;
                                System.out.println("\n\tinputs are 'y', 'n', or 'help'. Please try again.\n");
                        }
                } while (options == false);
                options = false;
//              do {
                        System.out.print("\nWhat naming convention would you like to use for the folders?\n\n\t[0] Album Artist\n\t[1] Album Name\n\t[2] Album Year\n\t[3] Album Genre\n\nPre-made naming schemes:\n\n\t[4] Album Artist - Album Name - Album Year\n\nPlease choose a naming scheme by listing a series of the above numbers to choose the information order. Example: 0132\n\n\t");
                        folder = sc.next().toCharArray();
//                      for (int i = 0; i < folder.length && options == true; i++) {
//                              if (folder)
//                      }
//              } while (options == false);
                System.out.print("\nWhat naming convention would you like to use for the folders?\n\n\t[0] Album Artist\n\t[1] Album Name\n\t[2] Album Year\n\t[3] Album Genre\n\t[4] Track Title\n\t[5] Track Number\n\t[6] Track Artist\n\nPlease choose a naming scheme by listing a series of the above numbers to choose the information order. Example: 0132\n\n\t");
                file = sc.next().toCharArray();
                FileRead read = new FileRead(args[0], folder, file);
                read.recurse(args[0]);
                sc.close();
        }
}
