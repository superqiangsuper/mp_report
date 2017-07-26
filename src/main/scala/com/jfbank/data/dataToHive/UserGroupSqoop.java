package com.jfbank.data.dataToHive;

import com.jfbank.data.dataToHive.sqoop.SqoopMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangzhiqiang on 2017/7/3.
 */
public class UserGroupSqoop extends SqoopMethod{


    private final Logger logger = LoggerFactory.getLogger(UserGroupSqoop.class);

    public String setSqoopCode(String sqoopCode) {

        super.sqoopCode=sqoopCode;
        logger.info(sqoopCode);
        return sqoopCode;

    }




}
