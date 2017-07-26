package com.jfbank.data.dataToHive.sqoop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created by zhangzhiqiang on 2017/7/3.
 */
public abstract class SqoopMethod {


    private final Logger logger = LoggerFactory.getLogger(SqoopMethod.class);
    public String sqoopCode;
    BufferedReader bufferedReader;

    //设置sqoop代码
    public abstract String setSqoopCode(String sqoopCode);

   //执行脚本

    public Boolean execSqoopCode() {


        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(sqoopCode);

            String ls_1;
            bufferedReader = new BufferedReader( new InputStreamReader(proc.getInputStream()));
            while ( (ls_1=bufferedReader.readLine()) != null)
            {
                logger.info(ls_1);
            }

            proc.waitFor();
            if(proc.exitValue()==0){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
                proc.destroy();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;

    }




}
