package com.neteast.lib.utils;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-1
 * Time: 下午5:07
 * To change this template use File | Settings | File Templates.
 */
public class PageUtils {
    /**
     * 计算分页的页数
     * @param totalCount   总数
     * @param singlePageCount 每页的元素个数
     * @return 能分几页
     */
    public static int computePages(int totalCount, int singlePageCount){

        if (totalCount<0){
            throw new IllegalArgumentException("总数不能小于0");
        }

        if (singlePageCount<=0){
            throw new IllegalArgumentException("每份的数量必须大于0");
        }

        return totalCount%singlePageCount==0 ? totalCount/singlePageCount : totalCount/singlePageCount+1;
    }

    /**
     * 得到当前页面的数据
     * @param currentPage 当前第几页，从1开始
     * @param singlePageItem 每页有几个
     * @param totalPageData 总数据
     * @param <T>
     * @return
     */
    public static <T> List<T> getPageData(int currentPage,int singlePageItem,List<T> totalPageData){
        int startIndex=(currentPage-1)*singlePageItem;
        int endIndex=currentPage*singlePageItem;
        if ( endIndex > totalPageData.size()){
            endIndex=totalPageData.size();
        }
        return totalPageData.subList(startIndex,endIndex);
    }

    /**
     * 获得所有的页标
     * @param singlePageCount  每页的数量
     * @param totalCount 总的数量
     * @return
     */
    public static List<String> getPageTitles(int singlePageCount, int totalCount){

        if (singlePageCount <= 0){
            throw new IllegalArgumentException("每份的数量必须大于0");
        }
        int maxPage=computePages(totalCount,singlePageCount);

        List<String> pageTitles= Lists.newArrayList();
        for(int i=1;i<=maxPage;i++){
            int[] indexs = computeStartAndLastIndex(i, singlePageCount, totalCount);
            pageTitles.add(getPageTitle(indexs[0],indexs[1]));
        }

        return pageTitles;
    }

    /**
     * 根据起始和结束位置得到页标
     * @param startIndex
     * @param lastIndex
     * @return
     */
    public static String getPageTitle(int startIndex,int lastIndex){
        if (startIndex==lastIndex){
            return String.valueOf(startIndex);
        }else {
            return startIndex+"-"+lastIndex;
        }
    }

    /**
     * 计算分页起始和结束的值
     * @param pageIndex 当前位于第几页，页数从1开始
     * @param singlePageCount 每页包含多少元素
     * @param totalCount 一共有多少元素
     * @return int[2] [0]=startIndex,[1]=lastIndex
     */
    public static int[] computeStartAndLastIndex(int pageIndex,int singlePageCount,int totalCount){
        int startIndex= (pageIndex-1)*singlePageCount+1;
        int lastIndex= pageIndex*singlePageCount>totalCount ? totalCount : pageIndex*singlePageCount;

        return new int[]{startIndex,lastIndex};
    }
    /**
     * 计算当前item属于的PageTitle
     * @param itemIndex
     * @param singlePageCount
     * @param totalCount
     * @return
     */
    public static String getItemPageTitle(int itemIndex,int singlePageCount,int totalCount){
        int maxPage= computePages(totalCount, singlePageCount);
        int[] indexs=null;
        for(int i=1;i<=maxPage;i++){
            indexs = computeStartAndLastIndex(i, singlePageCount, totalCount);
            if (belongPage(itemIndex,indexs[0],indexs[1])){
                break;
            }
        }
        return getPageTitle(indexs[0],indexs[1]);
    }


    private static boolean belongPage(int itemIndex,int startIndex,int lastIndex){
        return itemIndex >= startIndex && itemIndex <= lastIndex;
    }

}
