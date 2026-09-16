<template>
    <div class="dream-origin-wrapper">
      <div class="div_caihead">
        <div class="div_caihead1">
          <div class="div_caihead2">
            <a href="#"><img src="@/assets/images/dream/shuyi2.png" width="350" height="70" /></a>
          </div>
        </div>
        <div class="div_caihead1">
          <form action="#" method="get">
            <input class="search-input" name="s" type="text" placeholder="站内搜索……" autocomplete="off" />
          </form>
        </div>
      </div>
  
      <div class="div_mid">
        <div class="div_mid1" style="margin-left:17%;"><a href="#"><font color="white">首页</font></a></div>
        <div class="div_mid1"><a href="#"><font color="white">热门推荐</font></a></div>
        <div class="div_mid1"><a href="#"><font color="white">新书上架</font></a></div>
        <div class="div_mid1"><a href="#"><font color="white">书忆栈</font></a></div>
        <div class="div_mid1"><a href="#"><font color="white">作品投稿</font></a></div>
        <div class="div_mid1"><a href="#"><font color="white">小黑屋</font></a></div>
        <div class="div_mid1"><a href="#"><font color="white">加入我们</font></a></div>
        <div class="div_mid1"><a href="#"><font color="white">下载APP</font></a></div>
        <div class="div_mid1"><a href="#"><font color="white">登录/注册</font></a></div>
        
        <div id="weather">
          <h3>新品排名</h3>
          <table border="0">
            <tr>
              <td> 1.<a href="#"><font color="#000000">刀剑神域</font></a></td>
              <td>&nbsp;&nbsp;&nbsp;&nbsp;<a href="#"><img src="@/assets/images/dream/daojianshenyu.jpg" class="rank-img"></a></td>
            </tr>
            <tr>
              <td>2.<a href="#"><font color='#000000'>汽水里的千岁同学</font></a></td>
              <td>&nbsp;&nbsp;&nbsp;&nbsp;<a href="#"><img src="@/assets/images/dream/qiansuitongxue.jpg" class="rank-img"></a></td>
            </tr>
            <tr>
              <td>3.<a href="#"><font color="#000000">凉宫春日的直觉</font></a></td>
              <td>&nbsp;&nbsp;&nbsp;&nbsp;<a href="#"><img src="@/assets/images/dream/lianggongchunr.jpg" class="rank-img"></a></td>
            </tr>
          </table>
        </div>
        <div id="today">
          <h3>全站排名top</h3>
          <table border="0">
            <tr>
              <td> 1.<a href="#"><font color="#000000">来自新世界</font></a></td>
              <td>&nbsp;&nbsp;&nbsp;&nbsp;<a href="#"><img src="@/assets/images/dream/laizixinshijie.jpg" class="rank-img"></a></td>
            </tr>
            <tr>
              <td>2.<a href="#"><font color="#000000">银河英雄传说</font></a></td>
              <td>&nbsp;&nbsp;&nbsp;&nbsp;<a href="#"><img src="@/assets/images/dream/yinheyingxiongchuanshuo.jpg" class="rank-img"></a></td>
            </tr>
            <tr>
              <td>3.<a href="#"><font color="#000000">十二国记</font></a></td>
              <td>&nbsp;&nbsp;&nbsp;&nbsp;<a href="#"><img src="@/assets/images/dream/shierguoji.jpg" class="rank-img"></a></td>
            </tr>
          </table>
        </div>
      </div>
  
      <div class="div_left">
        <div v-for="link in leftLinks" :key="link" class="div_left1">
          <a href="#"><font color="#000000">{{ link }}</font></a>
        </div>
      </div>
  
      <div class="div_right">
        <div class="div_right1">
          <h3>※什么是书忆APP?</h3>
          <font>&nbsp;&nbsp;&nbsp;始于2021，致力于为广大轻小说爱好者建立一个：完全免费，便捷，干净，全面的阅读平台...</font>
        </div>
        <div class="div_right2">
          <h3>☆什么是书忆栈？</h3>
          <font>&nbsp;&nbsp;&nbsp;该栈是供广大阅读爱好者交流讨论的平台... 采用者将获得一千元以上到一万元以下稿费。</font>
        </div>
        <div class="div_right3">
          <h3>○入站须知</h3>
          <font>&nbsp;&nbsp;&nbsp;禁止制作、上传、复制、传送、传播包含任何反对宪法所确定的基本原则...</font>
          <hr/>
          <h1 class="join-text"><font color="brown">期待你的&nbsp;<a href="#">加入</a>，☜(ﾟヮﾟ☜)</font></h1>
        </div>
      </div>
  
      <div class="div_right4">
        <div v-for="news in newsList" :key="news" id="new">
          <a href="#"><font color="#000000">{{ news }}</font></a>
        </div>
      </div>
  
      <div id="footer">
        <hr/>
        <small>©21人工智能梁嘉豪<br/>2021期末作业</small>
      </div>
    </div>
  </template>
  
  <script setup>
  const leftLinks = [
    "书库", "努努书坊", "Google Books", "Project Gutenberg", "好讀", 
    "DAO on | 道卬", "一博书库", "Account · GitBook", "马克笔记", 
    "九九藏书", "天闻角川", "电击文库", "初音的青葱"
  ];
  
  const newsList = [
    "○日本公务员病假期间写轻小说发售 遭惩戒后辞职-----10月21日 星期四",
    "○德语版《为美好的世界献上祝福》等作品未按日语翻译引争议-----10月19日 星期二",
    "○游戏《天穗之咲稻姬》推出官方后传小说-----8月11日 星期三",
    "○和遥树奈计划VTuber轻小说作家出道！-----6月18日 星期五",
    "○轻小说《剃须。然后捡到女高中生。》完结-----5月3日 星期一",
    "○新作短篇小说《新·欢迎加入NHK！》发售-----5月2日 星期天",
    "○麻枝准首部小说《狩猫族族长》发售！-----4月12日 星期一",
    "○角川“轻小说EXPO2020”人气投票结果公布-----3月11日 星期四"
  ];
  </script>
  
  <style scoped>
  /* 这里完全平移并适配了 shuyi.css 的内容 */
  .dream-origin-wrapper {
    background-image: url('@/assets/images/dream/bj5.jpg'); /* 对应原 body 背景 */
    background-position: center;
    background-repeat: no-repeat;
    background-attachment: fixed;
    min-height: 1200px;
    width: 100%;
    overflow-x: hidden;
  }
  
  a { text-decoration: none; }
  a:link { color: #000000; }
  a:visited { color: red; }
  a:hover { color: #FF00ff; }
  a:active { color: #0000FF; }
  
  font { font-family: "宋体"; }
  
  .div_caihead { width: 100%; height: 100px; float: left; }
  .div_caihead1 { width: 47%; height: 60px; float: left; margin-left: 3%; margin-top: 1%; }
  .div_caihead2 { width: 253px; height: 30px; float: left; margin-left: 10%; }
  .search-input { width: 450px; height: 40px; }
  
  .div_mid { width: 100%; height: 40px; float: left; background-color: #ffc89b; }
  .div_mid1 { width: 6%; height: 40px; float: left; background-color: #ffc89b; text-align: center; line-height: 40px; }
  
  .div_left { width: 190px; height: 690px; float: left; background-color: bisque; opacity: 0.95; margin-left: 260px; }
  .div_left1 { width: 190px; height: 50px; float: left; background-color: antiquewhite; opacity: 0.9; margin-top: 2px; text-align: center; line-height: 50px; }
  
  .div_right { width: 40%; height: 800px; float: left; background-color: blanchedalmond; opacity: 0.9; margin-left: 1%; }
  .div_right1, .div_right2, .div_right3 { width: 96%; margin-left: 2%; margin-top: 1%; }
  .div_right2, .div_right3 { margin-top: 20px; height: auto; } /* 修正了原 CSS 的过大 margin */
  
  .join-text { margin-top: 30px; }
  
  #weather, #today { width: 255px; height: 380px; clear: both; margin-top: 50px; opacity: 0.95; padding: 10px; }
  #weather { background-color: #f1ffd2; }
  #today { background-color: #d6ffc7; }
  .rank-img { width: 85px; height: 100px; }
  
  .div_right4 { width: 350px; height: 600px; background-color: #f7ffde; opacity: 0.8; float: left; margin-left: 50px; margin-top: 30px; padding: 10px; }
  #new { width: 330px; height: 50px; margin-left: 10px; margin-top: 10px; }
  
  #footer { width: 100%; height: 40px; float: left; margin-top: 50px; text-align: center; color: #FFFFFF; }
  </style>