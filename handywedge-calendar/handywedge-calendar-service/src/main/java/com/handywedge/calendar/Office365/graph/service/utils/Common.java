package com.handywedge.calendar.Office365.graph.service.utils;

import java.util.ArrayList;
import java.util.List;

public class Common {

    /**
     * １次元配列を３次元配列へ変換
     *  [メールアドレス］→　[回数][リクエスト数][ユーザ数]
     * @param requestUsers　一次元配列（メールアドレス）
     * @param usrCount ユーザ数
     * @param rqstCount 要求数
     * @return
     */
    public static List<List<List<String>>> covert3DimensionList(List requestUsers, int usrCount, int rqstCount) {

        final int usersCount = usrCount;
        final int requestCount = rqstCount;
        final int allNumber = requestUsers.size();
        final int xLoopNum = allNumber % (usersCount * requestCount) > 0 ? allNumber / (usersCount * requestCount) + 1 : allNumber / (usersCount * requestCount);

        List newBatchList = new ArrayList();
        for (int i = 0; i < xLoopNum; i++) {
            List xList = new ArrayList();
            if ((i + 1) * (usersCount * requestCount) > allNumber) {
                xList = requestUsers.subList( i * (usersCount * requestCount), allNumber);
            } else {
                xList = requestUsers.subList( i * (usersCount * requestCount), (i + 1) * (usersCount * requestCount));
            }

            final int nextLayerNumber = xList.size();
            final int yLoopNum = nextLayerNumber % usersCount > 0 ? nextLayerNumber / usersCount + 1 : nextLayerNumber / usersCount;

            List newRequestList = new ArrayList();
            for (int j = 0; j < yLoopNum; j++) {
                List yList = new ArrayList();
                if ((j + 1) * usersCount > nextLayerNumber) {
                    yList = xList.subList( j * usersCount, nextLayerNumber );
                } else {
                    yList = xList.subList( j * usersCount, (j + 1) * usersCount);
                }
                newRequestList.add( yList );
            }
            newBatchList.add( newRequestList );
        }

        return newBatchList;
    }
}
