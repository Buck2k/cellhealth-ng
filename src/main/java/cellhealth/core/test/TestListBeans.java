package cellhealth.core.test;

import cellhealth.core.connection.MBeansManager;
import cellhealth.core.connection.WASConnection;

import javax.management.ObjectName;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Alberto Pascual on 24/07/15.
 */
public class TestListBeans {

    private MBeansManager mbeansManager;
    private Scanner scanner;
    private final String prefix;
    private final String sufic;
    private File file;

    public TestListBeans(WASConnection wasConnection) {
        this.mbeansManager = new MBeansManager(wasConnection);
        this.scanner = new Scanner(System.in);
        this.prefix = "logs/";
        this.sufic = "TestBeans.txt";
    }


    public void list(){
        String pathMetricsResult = prefix + sufic;
        this.file = new File(pathMetricsResult);
        if(this.file.exists()){
            this.file.delete();
        }
        try {
            this.file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String query;
        System.out.print("Enter the query (*:* default)");
        query = scanner.nextLine();
        if(query == null || query.length() == 0) {
            query = "*:*";
        }
        for(ObjectName objectName: mbeansManager.getMBeans(query)){
            print(objectName + "\n");
        }
    }

    public void print(String line) {
        System.out.print(line);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(this.file, true);
            fileWriter.write(line);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
