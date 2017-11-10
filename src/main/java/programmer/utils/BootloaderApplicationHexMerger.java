package programmer.utils;


import java.io.*;
import java.util.ArrayList;

/**
 * Created by Burak on 10/29/17.
 */
public class BootloaderApplicationHexMerger {


    private String applicationHexFilePath;
    private String bootloaderHexFilePath;
    private BufferedReader applicationBufferedReader;
    private BufferedReader bootloaderBufferedReader;
    private ArrayList<String> applicationList;
    private ArrayList<String> bootloaderList;
    private PrintWriter printWriter;



    public BootloaderApplicationHexMerger(String applicationHexFilePath, String bootloaderHexFilePath) throws FileNotFoundException {
        printWriter = new PrintWriter("/resources/merged_flash.hex");
        this.applicationHexFilePath = applicationHexFilePath;
        this.bootloaderHexFilePath = bootloaderHexFilePath;
    }

    private void openFiles() throws FileNotFoundException
    {
         applicationBufferedReader = new BufferedReader(new FileReader(new File(BootloaderApplicationHexMerger.class.getResource(applicationHexFilePath).getPath())));
         bootloaderBufferedReader = new BufferedReader(new FileReader(new File((BootloaderApplicationHexMerger.class.getResource(bootloaderHexFilePath).getPath()))));
    }

    private ArrayList<String> loadHexFiles(BufferedReader bufferedReader) throws IOException {
        ArrayList<String> storage = new ArrayList<>();

            String text  = null;
            while ((text = bufferedReader.readLine()) != null)
            {
                storage.add(text);

            }


        return  storage;
    }



    public void mergeThem()
    {
        try {
            openFiles();
            applicationList = loadHexFiles(applicationBufferedReader);
            bootloaderList =  loadHexFiles(bootloaderBufferedReader);

            applicationList.remove(applicationList.size()-1);
            applicationList.addAll(bootloaderList);


            for(String line: applicationList)
            {
                line.replace("\n","");
                line.replace("\r","");
                line.replace(" ","");

                if(line.compareTo("") != 0)
                {
                    printWriter.write(line);
                    printWriter.write("\n");

                }
            }
            printWriter.flush();
            printWriter.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }


//    public static void main(String args[])
//    {
//        try {
//            BootloaderApplicationHexMerger bootloaderApplicationHexMerger = new BootloaderApplicationHexMerger("/paper_test_ino.hex", "/stk500boot_v2_mega2560.hex");
//            bootloaderApplicationHexMerger.mergeThem();
//            System.out.println("BRK");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//    }
}

