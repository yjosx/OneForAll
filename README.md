# one一个的第三方客户端One For All

one一个是一个文艺应用软件，其中的文章都很小清新，应该会很符合很多朋友的兴趣。

## 0x01 实现功能
<img src="https://github.com/yjosx/OneForAll/blob/master/screenshoots/Screenshot_1550974397.png" alt="main"  width="350">

- 主页是每日更新的阅读

<img src="https://github.com/yjosx/OneForAll/blob/master/screenshoots/Screenshot_1550974500.png" alt="navigation"  width="350">

- 侧滑栏

<img src="https://github.com/yjosx/OneForAll/blob/master/screenshoots/Screenshot_1550974514.png" alt="reading"  width="350">

- 阅读

## 0x02 动图演示

![image](https://github.com/yjosx/OneForAll/blob/master/screenshoots/TIM%E5%9B%BE%E7%89%8720190224110437.gif)

![image](https://github.com/yjosx/OneForAll/blob/master/screenshoots/TIM%E5%9B%BE%E7%89%8720190224110447.gif)

![image](https://github.com/yjosx/OneForAll/blob/master/screenshoots/TIM%E5%9B%BE%E7%89%8720190224110453.gif)

## 0x03 实现细节

### 主页

主页由recyclerView嵌套cardView实现，并在外层再嵌套了一层viewPager实现了翻页。

同时在menu中加入了回到今日主页的功能。

使用了navigationView实现侧滑栏。

### 侧滑栏

侧滑栏中包含了“阅读”和“关于”的按键，

- “关于”只是一个简单的弹出窗口，以后应该会改成一个页面。

- 而“阅读”复用了主页的界面，但其中的recyclerView可以无限下滑，向下加载。

  （！！阅读的api目前似乎已不再更新，但功能是支持的。）

  （尝试以抓包的方式获取新的api，目前没有成功）

### 其他功能

- 建立了一个接口用于recyclerView的“下滑刷新”

- 没有网络或者加载不出来时，会使用缓存的主页。

## 0x04 致谢开源库

{

- 由litepal提供数据库支持
- 由glide提供图片显示
- 由okhttp提供网络请求
- 由gson提供json解析

}

## 0x05 小感受

第一次写的一个较完备的应用程序在各个方面都有较大的不足，但在查阅各类资料的时候也体会到了巨大的满足

o(*￣︶￣*)o，，，

开心。

## 0x06 更新

####0.2.6

1. 由androidx强力支持
2. 修复主页左右滑动不灵敏的问题

####0.2.5

1. 连载现在可以查看上下篇了
2. 现在可以下拉刷新了
3. 可以查看点赞数了

（电影无法获取点赞数，自动隐藏）

（下个版本将支持点赞（本地），同时数据库也将改为realm）

（异步加载主页太难了╥﹏╥... 下版本再做）

p.s. 不再支持4.4以下版本的手机了

####0.2.1

优化本地加载逻辑

####0.2

1. 增加启动页
2. 使用viewPager替代横向recyclerView
3. 修复bug

####0.1.1

更新部分名称表述

####0.1-alpha

第一个版本

## 0x07 TODO

下一步要做的有

- 懒加载
- 数据库改为realm
- 加入音乐和影视的详情
- 封装主页和阅读的数据请求
- 点赞（本地）
- 天气

不会做的有

- 由于post数据是加密上传，点赞数据上传的功能无限期后延
- 目前不知道怎么获取其余主页，所以主页将一直保持在10页，如果有知道的朋友感谢提交issue
