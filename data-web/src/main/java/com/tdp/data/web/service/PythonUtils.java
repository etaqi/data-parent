package com.tdp.data.web.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class PythonUtils {

    /**
     * 调用python消费者进行消费
     * @param filePath
     */
    @Async
    public void startPythonProduct(String filePath) {
        try {

            String[] commandArray =
          new String[] {
            "D:/Programs/Python/Python38/python.exe",
            "d:/Project/PY/filter-data-master/Product1.py",
            filePath
          };
            Process process = Runtime.getRuntime().exec(commandArray);
            BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                System.out.println(line);
            }
            bufferedReader.close();

            process.waitFor();
            System.out.println(printError(process.getErrorStream()));
        } catch (Exception e) {
            e.printStackTrace();

        }
        System.out.println("执行完毕!");
    }

  public String printError(InputStream inputStream){
      try {
          BufferedReader bufferedReader = new BufferedReader(new
                  InputStreamReader(inputStream,"gb2312"));
          String line;
          StringBuffer sb = new StringBuffer();
          while ((line = bufferedReader.readLine()) != null){
             sb.append(line).append(System.getProperty("line.separator"));
          }
          bufferedReader.close();
          return sb.toString();
      } catch (IOException e) {
          e.printStackTrace();
      }
      return null;
  }


  public static void main(String[] args) {
    new PythonUtils().startPythonProduct("C:/Users/admin/Desktop/VUE/a.txt");
  }
}
