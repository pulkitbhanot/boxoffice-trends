package com.boxoffice;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by pulkit on 3/10/15.
 */
public class ListMergerRunnable implements Runnable {

    private static final Logger logger = Logger.getLogger(ListMergerRunnable.class);

    private List<MovieViews> list1;
    private List<MovieViews> list2;
    private List<MovieViews> returnList;
    private CountDownLatch latch;

    public ListMergerRunnable(List<MovieViews> list1, List<MovieViews> list2, CountDownLatch latch) {
        this.list1 = list1;
        this.list2 = list2;
        this.latch = latch;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        if (list2 == null) {
            logger.info("Merger has only 1 list setting this as returnlist");
            returnList = list1;
        } else {

            logger.info("Starting merger with lists of size [" + list1.size() + "," + list2.size() + "]");
            returnList = new ArrayList<MovieViews>();
            int i = 0, j = 0;
            for (; i < list1.size() && j < list2.size(); ) {
                if (list1.get(i).getViews() > list2.get(j).getViews()) {
                    returnList.add(list1.get(i));
                    i++;
                } else {
                    returnList.add(list2.get(j));
                    j++;
                }
            }
            for (; i < list1.size(); i++) {
                returnList.add(list1.get(i));
            }
            for (; j < list2.size(); j++) {
                returnList.add(list2.get(j));
            }
        }
        long timeTaken = System.currentTimeMillis() - startTime;
        logger.info("Counting down, tike taken" + timeTaken);
        latch.countDown();
    }

    public List<MovieViews> getResult() {
        return returnList;
    }
}
