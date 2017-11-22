package cardlop.my.com.ytdateselect.bean;

/**
 * Author：mengyuan
 * Date  : 2017/11/22上午9:49
 * E-Mail:mengyuanzz@126.com
 * Desc  :一天的Bean对象
 */

public class DayBean {
    //这一天的年份
    public int year;
    //这一天的月份
    public int month;
    //这一天的具体日期
    public int day;
    //星期几
    public int week;
    //这一天是否属于本月
    public boolean isBelongMonth = false;
    //这一天是否被选中
    public boolean isSelect = false;


    //如果这一天有节日，则是具体的日期名称，否则为空
    //未实现，全部为空
    public String holiday;


}
