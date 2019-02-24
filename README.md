# one一个的第三方客户端One For All

one一个是一个文艺应用软件，其中的文章都很小清新，应该会很符合很多朋友的兴趣。

## 0x01 实现功能
![main](https://github.com/yjosx/OneForAll/blob/master/screenshoots/Screenshot_1550974397.png)

- 主页是每日更新的阅读

![nivagation](https://github.com/yjosx/OneForAll/blob/master/screenshoots/Screenshot_1550974500.png)

- 侧滑栏

![reading](https://github.com/yjosx/OneForAll/blob/master/screenshoots/Screenshot_1550974514.png)

- 阅读

## 0x02 动图演示

![image](https://github.com/yjosx/OneForAll/blob/master/screenshoots/TIM%E5%9B%BE%E7%89%8720190224110437.gif)

![image](https://github.com/yjosx/OneForAll/blob/master/screenshoots/TIM%E5%9B%BE%E7%89%8720190224110447.gif)

![image](https://github.com/yjosx/OneForAll/blob/master/screenshoots/TIM%E5%9B%BE%E7%89%8720190224110453.gif)

##0x03  实现细节

主页由recyclerview嵌套cardview实现，并在外层在嵌套了一层横向的recyclerview实现了翻页。

同时在menu中加入了回到主页的功能。

而在nivagationview中包含了“阅读”和“关于”的按键，

- “关于”只是一个简单的弹出窗口，以后应该会改成一个页面。
- 而“阅读”复用了主页的界面，但其中的recyclerview可以无限下滑，向下加载。

## 0x04 小感受

第一次写的一个较完备的应用程序在各个方面都有较大的不足，但在查阅各类资料的时候也体会到了巨大的满足

o(*￣︶￣*)o，，，

开心。
## 0x05 TODO

下一步要做的有
- 将主页的双recyclerview换为viewpager
- 加入音乐和影视的详情
- 目前不知道怎么获取其余主页，所以主页将一直保持在10页，如果有知道的朋友感谢提交issue
