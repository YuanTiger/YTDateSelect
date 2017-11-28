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

### 3、基础架构 ###
整个项目的结构是**RecyclerView嵌套RecyclerView**:
![项目结构](http://7xvzby.com1.z0.glb.clouddn.com/YTDateSelect/YTDateSelect_01.png)

红色为外层RecyclerView，蓝色为内部RecyclerView。

外层RecyclerView包含了固定头部条目与月份RecyclerView。

每个月份RecyclerView对应了一个月份的日期展示。

### 4、数据存储 ###
关于日期数据，我们需要考虑到很多情况。

因为日期数据可能很大，几年的数据甚至更多，如果传递来传递去会在内存中产生多份数据(根据引用地址不同可以看出)。

为了节省内存，我选择使用[单例模式](http://www.jianshu.com/p/7268470ef013)来实现，并且是懒汉式单例模式，因为此功能用户不一定每次打开App都会使用。

这样做的好处不仅是节省了内存、减少了来回传递数据的繁琐操作，还有就是很方便地让我们实现了回显功能，从上面的Gif图中也可以看出回显功能。

那么还有一个问题就是，什么时候销毁数据？

我的想法是，在用户第一次打开日期选择页时，进行数据的初始化，接着用户反复在结果展示页和日期选择页来回切换时，用的都是这一批数据。

当用户离开结果展示页(提交、或者放弃)时，就要销毁数据，清理内存，具体创建、销毁逻辑可以查看代码。

### 5、刷新 ###
关于每个日期的状态初始化以及选择后的刷新，是比较麻烦的。

尤其是跨月、跨年选择日期！因为我们的每个月是独立的嵌套RecyclerView，当你跨月选择时，你就需要刷新多条内部RecyclerView。

为了解决这种情况，我在代码中使用了外部Adapter来进行条目的刷新，并且不会调用全局刷新：`adapter.notifyDataSetChanged();`

选择使用局部刷新:
```
adapter.notifyItemRangeChanged(int position, int count);
adapter.notifyItemChanged(int position);

```
不会去刷新无关的条目。

还有一点要说的是，嵌套RecyclerView的高度是不固定的：可能有5行、6行，也可能有7行。


关于嵌套RecyclerView的高度问题，确实已经无需计算高度了，可以直接将内部RecyclerView的高度设置为`wrap_content`。

之前需要动态计算高度，是因为RecyclerView兼容包的Bug，不过已经在23.2.0之后修复了，[具体参考这里](http://blog.csdn.net/u011240877/article/details/51204829)。

不过我在嵌套RecyclerView还是计算了高度，我尝试了不计算高度，感觉性能远不如计算高度，具体就需要看看源码了。

至此，难点基本已经介绍完了。

不过项目当中因为上述高度不固定的原因，产生了一个小问题：

在调用` adapter.notifyItemChanged(int position)`时，当position为0或1时，有时会导致屏幕自动滚动至顶部，目前还没有找到合适的解决方案，各位看官可以自己试一试，找找解决方案。

## 感谢 ##
其中RecyclerView的头部固定引用了[sticky-headers-recyclerview](https://github.com/timehop/sticky-headers-recyclerview)库。

在[Android日期显示和选择库](http://www.jianshu.com/p/c0097ff3d44c)学到了很多东西。
