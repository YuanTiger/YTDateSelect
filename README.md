## 描述 ##
参考支付宝所制作的日期选择页，效果如下:

![效果展示](https://github.com/YuanTiger/YTDateSelect/blob/master/YTDateSelect_show_1.gif)

## 知识点与难点 ##
### 1、获取指定月份有多少天 ###
```
public static int getDayCountOfMonth(int year, int month) {
    int[] arr = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    int days = 0;
    //如果是闰年，二月= 29天
    if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
        arr[1] = 29;
    }
    try {
        days = arr[month - 1];
    } catch (Exception e) {
        e.printStackTrace();
    }
    return days;
}
```

### 2、计算指定日期是周几 ###
```
/**
 * 计算指定日期是周几
 * 1为周日，2为周一，3为周二，4为周三，5为周四，6为周五，7为周六
 */
public static int getDayOfWeekInMonth(int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DATE, day);
    return calendar.get(Calendar.DAY_OF_WEEK);
}
```

### 3、开发思路 ###
整个项目的结构是**RecyclerView嵌套RecyclerView**。

外层RecyclerView包含了固定头部条目与月份RecyclerView。

每个月份RecyclerView对应了一个月份的日期展示。

日期数据是单例的，在进入日期选择结果展示页的时候，第一次点击日期选择时初始化数据，接着在离开日期选择页的时候销毁数据。

这样做的好处不仅是节省了内存开支，减少了数据来回传递接收的繁琐操作，还可以让我们很简单地实现数据回显。

关于RecyclerView的刷新，我监听了外层RecyclerView，当外层RecyclerView的某个条目或多个条目需要改变时，就会去刷新某个条目或者多个条目。

不会去刷新无关的条目。

因为内层RecyclerView的高度是不固定的，所以在调用` adapter.notifyItemChanged(int position)`时，当position为0或1时，有时会导致屏幕滚动至顶部，目前还没有找到合适的解决方案，各位看官可以自己试一试。

### 4、难点 ###
跨月、跨年选择时的UI改变。



## 感谢 ##
其中RecyclerView的头部固定引用了[sticky-headers-recyclerview](https://github.com/timehop/sticky-headers-recyclerview)库。

在[Android日期显示和选择库](http://www.jianshu.com/p/c0097ff3d44c)学到了很多东西。
