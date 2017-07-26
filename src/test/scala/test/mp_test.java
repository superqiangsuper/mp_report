package test;

import com.jfbank.data.dataToHive.UserGroupSqoop;
import org.junit.Test;

/**
 * Created by zhangzhiqiang on 2017/7/4.
 */
public class mp_test {

    @Test
    public void test1(){
        UserGroupSqoop userGroupSqoop = new UserGroupSqoop();
        userGroupSqoop.setSqoopCode("ls /Users/admin/IdeaProjects/mp-report");
        if(userGroupSqoop.execSqoopCode()){
            System.out.println("success");
        }else{
            System.out.println("failed");

        }

    }

}
