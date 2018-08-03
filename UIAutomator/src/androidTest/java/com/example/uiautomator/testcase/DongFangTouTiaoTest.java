package com.example.uiautomator.testcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;
import android.widget.ListView;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 东方头条测试 提成较高 1000金币=1元 分享点击80金币一次 徒弟分享贡献16金币
 */
public class DongFangTouTiaoTest extends BaseTest {

  private int readCount = 0; // 阅读次数
  private int commentCount = 0; // 评论次数
  private int shareCount = 0; // 分享次数
  private int restartCount = 0;//重启次数

  public DongFangTouTiaoTest() {
    super();
  }

  @Override
  public void start() {
    // 打开app
    startAPP();

    // 执行阅读,播放操作 com.songheng.eastnews:id/xq
    while (readCount < 200) {
      try {
        Log.e(TAG, ":\n********************************************\n第 "
          + readCount
          + " 次\n********************************************\n");

        // 判断是否为首页
        UiObject2 tab = findById("l6");
        if (tab == null) {// 如果找不到底部导航栏有可能是有对话框在上面
          closeDialog();
          tab = findById("l6");
          if (tab == null) {// 关闭对话框之后再次查找是否已经回到首页
            if (restartCount++ < 3) {
              Log.e(TAG, "应用可能已经关闭,重新启动");
              startAPP();
            } else {
              Log.e(TAG, "退出应用");
              break;
            }
          }
        }
        /*if (random.nextInt(6) % 3 == 0) {
          doPlay(); // 播放
        } else {
          doRead();// 阅读
        }*/
        doRead();// 阅读
      } catch (Throwable e) {
        if (e instanceof IllegalStateException) {
          Log.e(TAG, "阅读失败,结束运行:阅读次数" + readCount, e);
          break;
        }
        Log.e(TAG, "阅读失败:阅读次数" + readCount, e);
      }
    }

    // 关闭App
    closeAPP();
  }

  /**
   * 阅读文章
   *
   * @return 成功
   */
  private boolean doRead() {
    // 如果当前不是文章列表 ,切换到文章列表 判断是否有发布按钮 com.songheng.eastnews:id/ne
    if (findById("ne") == null) {
      UiObject2 toolBar = findById("l6");
      // 切换到文章列表
      if (toolBar == null) {
        Log.e(TAG, "阅读失败:没有底部栏");
        return false;
      }
      toolBar.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "切换到文章列表");
    }

    // 向上滚动列表
    int startY = height / 2;
    int endY = height / 4;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    Log.e(TAG, "列表向上滑动");

    // 打开文章 com.songheng.eastnews:id/a6b  标签 (打开不是百度的)
    int repeat = 0;
    UiObject2 read = findById("a6b");
    List<String> key = Arrays.asList("百度");
    while (repeat++ < 4 && (read == null || key.contains(read.getText()))) {
      Log.e(TAG, "阅读失败,没有找到文章");
      mDevice.swipe(centerX, startY, centerX, endY, 30);
      sleep(1);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "列表向上滑动,向上滚动查找文章");
      read = findById("a6b");
    }
    read.click();
    sleep(4);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "打开文章,开始阅读");

    int count = 0;
    while (count++ < 10) {
      if (count == 4) {
        UiObject2 seeAll = findByText("点击阅读全文", 3);
        if (seeAll != null) {
          seeAll.click();
          mDevice.waitForIdle(timeOut);
          Log.e(TAG, "点击阅读全文");
        }
      }
      mDevice.swipe(centerX, startY, centerX, endY, 20);
      mDevice.waitForIdle(timeOut);
      sleep(3);
    }
    readCount++;

     /* // 发表评论
      if (commentCount < 2 && commentArticle()) {
        commentCount++;
      }
      // 分享
      if (shareCount < 10 && shareArticle()) {
        shareCount++;
      }*/

    mDevice.pressBack();
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "阅读完成,返回首页");
    // } else { // 页面可能未打开
    //   Log.e(TAG, "返回首页:可能没有打开页面");
    //   mDevice.pressBack();
    //   mDevice.waitForIdle(timeOut);
    // }
    return true;
  }

  /**
   * 播放视频
   *
   * @return 成功
   */
  private boolean doPlay() {
    UiObject2 tab2 = findById("tv_tab2");
    // 切换到视频列表
    if (tab2 == null) {
      Log.e(TAG, "播放失败,没有找到视频Tab");
      return false;
    }
    // 如果当前不是视频列表 ,切换到视频列表
    if (!tab2.getText().equals("刷新")) {
      tab2.click();
      sleep(3);
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "切换到视频列表");
    }

    // 需要向上滚动列表
    int startY = height / 2;
    int endY = height / 10;
    mDevice.swipe(centerX, startY, centerX, endY, 30);
    Log.e(TAG, "视频列表向上滑动");

    // 点击播放
    UiObject2 play = findById("item_video_play_num");
    if (play == null) {
      Log.e(TAG, "播放失败:没有播放按钮");
      return false;
    }
    play.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击开始播放视频");

    // com.xiangzi.jukandian:id/video_detail_bottom_comment_write_text
    if (findById("video_detail_bottom_comment_write_text", 3) != null) {// 视频页面
      sleep(35);
      readCount++;
      // 发表评论
      if (commentCount < 2 && commentVideo()) {
        commentCount++;
      }
      /*// 分享
      if (shareCount < 10 && shareVideo()) {
        shareCount++;
      }*/

      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "播放完成,返回首页");
    } else {
      mDevice.pressBack();
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "返回首页:打开的不是视频页面");
    }
    return true;
  }

  /**
   * 关闭对话框
   */
  private boolean closeDialog() {
    mDevice.pressBack();
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "关闭对话框");
    /*UiObject2 close = findByText("我知道了", 3);
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "关闭对话框");
      return true;
    }

    close = findByText("忽略", 3);
    if (close != null) {
      close.click();
      mDevice.waitForIdle(timeOut);
      Log.e(TAG, "关闭对话框");
      return true;
    }*/
    return false;
  }

  /**
   * 文章评论
   *
   * @return 成功
   */
  private boolean commentArticle() {
    // 1.弹出输入
    UiObject2 commentBtn = findById("tv_web_comment_hint");
    if (commentBtn == null) {
      Log.e(TAG, "没有评论文本框");
      return false;
    }
    Log.e(TAG, "点击评论文本框,弹出键盘");
    commentBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);

    // 2.输入评论 com.xiangzi.jukandian:id/dialog_comment_content
    UiObject2 contentText = findById("dialog_comment_content");
    if (contentText == null) {
      Log.e(TAG, "没有评论文本框");
      return false;
    }
    contentText.setText(getComment(random.nextInt(10) + 5)); // 这里使用中文会出现无法填写的情况
    sleep(2); // 等待评论填写完成
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "填写评论内容");

    // 3.点击发表评论 com.xiangzi.jukandian:id/dialog_comment_send
    UiObject2 sendBtn = findById("dialog_comment_send");
    if (sendBtn == null) {
      Log.e(TAG, "没有发表评论按钮");
      return false;
    }
    sendBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "******发表评论成功!******\n");

    return true;
  }

  /**
   * 文章评论
   *
   * @return 成功
   */
  private boolean commentVideo() {
    // 1.弹出输入
    UiObject2 commentBtn = findById("video_detail_bottom_comment_write_text");
    if (commentBtn == null) {
      Log.e(TAG, "没有评论文本框");
      return false;
    }
    commentBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击评论文本框,弹出键盘");

    // 2.输入评论 com.xiangzi.jukandian:id/dialog_comment_content
    UiObject2 contentText = findById("dialog_comment_content");
    if (contentText == null) {
      Log.e(TAG, "没有评论文本框");
      return false;
    }
    contentText.setText(getComment(new Random().nextInt(10) + 5)); // 这里使用中文会出现无法填写的情况
    sleep(2); // 等待内容填写完成
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "填写评论内容");

    // 3.点击发表评论 com.xiangzi.jukandian:id/dialog_comment_send
    UiObject2 sendBtn = findById("dialog_comment_send");
    if (sendBtn == null) {
      Log.e(TAG, "没有发表评论按钮");
      return false;
    }
    sendBtn.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "******发表评论成功!******\n");
    return true;
  }

  /**
   * 分享
   */
  private boolean shareVideo() {
    // 检测页面是否是阅读页面,阅读页面下面的操作按钮
    // com.jifen.qukan:id/lw 评论
    // com.jifen.qukan:id/ji 进入评论列表
    // com.jifen.qukan:id/jj 进入评论列表
    // com.jifen.qukan:id/jh 收藏
    // com.jifen.qukan:id/ls 分享
    // com.jifen.qukan:id/jf 调整字体

    // 1.弹出分享对话框
    UiObject2 shareBtn = findById("ls");
    if (shareBtn == null) {
      Log.e(TAG, "没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击分享按钮,弹出分享对话框");

    // 2.调取分享到QQ ,此处只能用文本搜索
    UiObject2 share = findByText("QQ好友");
    if (share == null) {
      Log.e(TAG, "没有打开QQ分享");
      return false;
    }
    share.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "打开QQ分享");

    // 3.点击发表评论
    UiObject2 publish = mDevice.wait(Until.findObject(By.textContains("我的电脑")), 1000 * 10);
    if (publish == null) {
      Log.e(TAG, "分享到我的电脑失败");
      return false;
    }
    publish.getParent().click();
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "分享到我的电脑");

    // 分享到我的电脑确认
    UiObject2 confirm = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogRightBtn")), 1000 * 10);
    if (confirm == null) {
      Log.e(TAG, "分享到我的电脑确认失败");
      return false;
    }
    confirm.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "分享到我的电脑确认");

    // 返回
    UiObject2 back = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogLeftBtn")), 1000 * 10);
    if (back == null) {
      Log.e(TAG, "没有返回按钮");
      return false;
    }
    back.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "******分享成功返回******");

    return true;
  }

  /**
   * 分享
   */
  private boolean shareArticle() {
    // 检测页面是否是阅读页面,阅读页面下面的操作按钮
    // com.jifen.qukan:id/jk 评论
    // com.jifen.qukan:id/ji 进入评论列表
    // com.jifen.qukan:id/jj 进入评论列表
    // com.jifen.qukan:id/jh 收藏
    // com.jifen.qukan:id/jg 分享
    // com.jifen.qukan:id/jf 调整字体

    UiObject2 shareBtn = findById("jg");
    if (shareBtn == null) {
      Log.e(TAG, "没有分享按钮");
      return false;
    }
    shareBtn.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "点击分享按钮,弹出分享对话框");

    // 2.调取分享到QQ ,此处只能用文本搜索
    UiObject2 share = findByText("QQ好友");
    if (share == null) {
      Log.e(TAG, "没有打开QQ分享");
      return false;
    }
    share.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "打开QQ分享");

    // 点击发表评论
    UiObject2 publish = mDevice.wait(Until.findObject(By.textContains("我的电脑")), 1000 * 10);
    if (publish == null) {
      Log.e(TAG, "分享到我的电脑失败");
      return false;
    }
    publish.getParent().click();
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "分享到我的电脑");

    // 分享到我的电脑确认
    UiObject2 confirm = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogRightBtn")), 1000 * 10);
    if (confirm == null) {
      Log.e(TAG, "分享到我的电脑确认失败");
      return false;
    }
    confirm.click();
    sleep(3);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "分享到我的电脑确认");

    // 返回
    UiObject2 back = mDevice.wait(Until.findObject(By.res("com.tencent.mobileqq", "dialogLeftBtn")), 1000 * 10);
    if (back == null) {
      Log.e(TAG, "没有返回按钮");
      return false;
    }
    back.click();
    sleep(1);
    mDevice.waitForIdle(timeOut);
    Log.e(TAG, "******分享成功返回******");

    return true;
  }

  @Override
  String getAPPName() {
    return "东方头条";
  }

  @Override
  String getPackageName() {
    return "com.songheng.eastnews";
  }
}