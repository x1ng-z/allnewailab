package hs.industry.ailab.utils.help;

import hs.industry.ailab.utils.bridge.ExecutePythonBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;
import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/18 13:01
 */
public class FileHelp {
    private static Logger logger = LoggerFactory.getLogger(ExecutePythonBridge.class);
    public static String pyfilenamepath = System.getProperty("user.dir") + "\\" + "DLLS" + "\\";

    public static void clearInfoForFile(String fileName) {
        File file = new File(pyfilenamepath + fileName);
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    public static void updateFile(String fileName, String codecontext) throws IOException {


        File pyfile = new File(pyfilenamepath + fileName);
        PrintWriter newpyfilestream = null;
        try {
            if (pyfile.exists()) {
                clearInfoForFile(fileName);
                newpyfilestream = new PrintWriter(pyfile);
                String s;
                newpyfilestream.write(codecontext);
                newpyfilestream.flush();
            } else {
                throw new RuntimeException(pyfilenamepath + fileName + " file is alread exit");
            }
        } finally {
            newpyfilestream.close();
        }

    }

    public static String readInfoFromFile(String fileName) {
        File file = new File(pyfilenamepath + fileName);
        if (!file.exists()) {
            return null;
        }
        StringBuilder resultStr = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String str = null;
            while (null != (str = bufferedReader.readLine())) {
                resultStr.append(str + "\n");
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return resultStr.toString();
    }


    public static void creatpyfile(String pyfilename, String pytemplete) throws IOException {

//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        Resource resource = resolver.getResource(pytemplete);
        BufferedReader comstomizpytempletreader = null;
        String pythontemplet=System.getProperty("user.dir") + "\\config"+"\\"+pytemplete;
        try {
            comstomizpytempletreader = new BufferedReader(new FileReader(new File(pythontemplet)));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("can't find customizepythontemplates"+ "the dir is "+pythontemplet);
        }


        File pyfile = new File(pyfilenamepath + pyfilename);
        logger.info("pyfilename :" + pyfilenamepath + pyfilename);
        PrintWriter newpyfilestream = null;
        try {
            if (!pyfile.exists()) {
                if (pyfile.createNewFile()) {
                    newpyfilestream = new PrintWriter(pyfile);
                    String s;
                    while ((s = comstomizpytempletreader.readLine()) != null) {
                        newpyfilestream.println(s);
                    }
                    newpyfilestream.flush();
                }
            } else {
                throw new RuntimeException(pyfilenamepath + pyfilename + " file is alread exit");
            }
        } finally {
            comstomizpytempletreader.close();
            newpyfilestream.close();
        }
    }


}
