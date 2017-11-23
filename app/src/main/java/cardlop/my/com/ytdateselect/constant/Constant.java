package cardlop.my.com.ytdateselect.constant;

/**
 * Author：mengyuan
 * Date  : 2017/11/22上午11:28
 * E-Mail:mengyuanzz@126.com
 * Desc  :
 */

public interface Constant {

    interface Week {
        //周日
        public int SUN = 1;
        //周一
        public int MON = 2;
        //周二
        public int TUE = 3;
        //周三
        public int WED = 4;
        //周四
        public int THU = 5;
        //周五
        public int FRI = 6;
        //周六
        public int SAT = 7;


    }

    interface DayState{
        //不可点击状态
        int UNCLICK = -1;
        //默认
        int NORMAL = 0;
        //开始日期
        int START = 1;
        //选中的日期，不是开始，也不是结束
        int SELECT = 2;
        //结束日期
        int END = 3;


    }

}
