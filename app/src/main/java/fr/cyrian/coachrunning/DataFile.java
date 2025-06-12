package fr.cyrian.coachrunning;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class DataFile {

    final Context context = MyApplication.getContext();
    final File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

    public File file;

    // constructor
    public DataFile(String name){
        this.file = new File(path, name);
    }

    // return True if file didn't exist
    public Boolean initialize(){
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return true;
        }
    }

    // write a line at the end of the file
    public void writeLine(String line) {
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter p = new PrintWriter(bw);
            p.println(line);
            bw.close();
            p.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // remove content of a file without deleting it
    public void removeContent(){
        file.delete();
        initialize();
    }

    // get file's content as an array for each line
    public String[] getFileContent(){
        String tab[] = {};
        ArrayList<String> arrlist = new ArrayList<String>(Arrays.asList(tab));
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                arrlist.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrlist.toArray(tab);
    }
}











