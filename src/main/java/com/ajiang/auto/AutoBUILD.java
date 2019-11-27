package com.ajiang.auto;

import com.ajiang.auto.consts.Consts;
import com.surenpi.jenkins.client.Jenkins;
import com.surenpi.jenkins.client.job.Jobs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AutoBUILD
{
    public static void main(String[] args) throws URISyntaxException{
        URI serverURI = new URI(Consts.JENKINS_URI);
        Jenkins jenkins = new Jenkins(serverURI, Consts.JENKINS_User, Consts.JENKINS_Password);
        final Jobs jobMgr = jenkins.getJobs();
//        String fileName = (new AutoBUILD()).getClass().getClassLoader().getResource("JOB.csv").getPath();
        String fileName = "C:\\Users\\Administrator\\Desktop\\JOB.csv";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            while((line=reader.readLine())!=null) {
                if (line.startsWith("#")) {
                } else {
                    final String item[] = line.split(",");
                    int itemNum = item.length;

                    if (itemNum > 1) {
                        List<String> itemList = new ArrayList<String>();
                        ExecutorService thPool = Executors.newFixedThreadPool(itemNum);
                        System.out.println("开始并发线程数" + itemNum);
                        while (itemNum != 0) {
                            itemList.add(item[itemNum - 1]);
                            itemNum--;
                        }
                        Set<Future> hashSet = new HashSet<Future>();
                        for (final String eachitem : itemList) {
                            Future fu = thPool.submit(new Callable<Object>() {
                                public Object call() throws Exception {
                                    int eachNextNum = 0;
                                    try {
                                        System.out.println(eachitem);
                                        eachNextNum = jobMgr.getDetails(eachitem).getNextBuildNumber();
                                        jobMgr.build(eachitem);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        Thread.currentThread().sleep(10000);
                                        while (jobMgr.getBuildDetails(eachitem, eachNextNum).isBuilding()) {
                                            System.out.println(eachitem + "还在打包中。。。等待5秒");
                                            Thread.currentThread().sleep(5000);
                                        }
                                        System.out.println("打包完成，完成的状态是：" + jobMgr.getBuildDetails(eachitem, eachNextNum).getResult().toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return "打包完成";
                                }
                            });
//                            Thread.currentThread().sleep(10000);

                            hashSet.add(fu);
                        }
                        for (Future eachfu : hashSet) {
                            eachfu.get();
                        }
                    } else {
                        String last = item[item.length - 1];
                        int nextNum = 0;
                        try {
                            nextNum = jobMgr.getDetails(last).getNextBuildNumber();
                        } catch (Exception e) {
                            System.out.println("未找到相应的Job" + last);
                        }
                        System.out.println(last + "开始Build,第" + nextNum + "版本");
                        jobMgr.build(last);
                        Thread.currentThread().sleep(10000);
                        while (jobMgr.getBuildDetails(last, nextNum).isBuilding()) {
                            System.out.println(last + "还在打包中。。。等待5秒");
                            Thread.currentThread().sleep(5000);
                        }
                        System.out.println("打包完成，完成的状态是：" + jobMgr.getBuildDetails(last, nextNum).getResult().toString());
                    }
                }
            }
            TeleMessage.sendMessageRequest(Consts.BOT_CHAT_ID,"全部打包完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}