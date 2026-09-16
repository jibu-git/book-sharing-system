<template>
    <div class="gal-game-container">
      <div class="gal-bg"></div>
  
      <div class="particles">
        <span v-for="i in 15" :key="i" :style="getRandomParticleStyle()"></span>
      </div>
  
      <main class="gal-main">
        <Transition name="sprite-fade" mode="out-in">
          <img 
            :key="currentSprite" 
            :src="currentSprite" 
            class="character-sprite" 
            alt="渚"
          />
        </Transition>
  
        <div class="msg-box-wrapper" @click="nextStep">
          <div class="name-tag">古河 渚</div>
          <div class="msg-box">
            <p class="msg-content">
              {{ displayContent }}
              <span v-if="typeFinished && !showChoices" class="next-animated">▼</span>
            </p>
          </div>
        </div>
  
        <Transition name="fade">
          <div v-if="showChoices" class="choices-layer">
            <div class="choice-item" @click="selectChoice('share')">
              我也想...分享我的宝藏书籍
            </div>
            <div class="choice-item" @click="selectChoice('explore')">
              先带我...去四处看看吧
            </div>
          </div>
        </Transition>
      </main>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, computed, reactive } from 'vue'
  import { useRouter } from 'vue-router'
  
  const router = useRouter()
  
  // ================= 静态资源路径配置 =================
  // 使用 reactive 包装，方便管理
  const sprites = reactive({
    happy: new URL('@/assets/images/galgame/03.png', import.meta.url).href,
    normal: new URL('@/assets/images/galgame/04.png', import.meta.url).href,
    serious: new URL('@/assets/images/galgame/05.png', import.meta.url).href,
    confused: new URL('@/assets/images/galgame/06.png', import.meta.url).href,
    encourage: new URL('@/assets/images/galgame/07.png', import.meta.url).href,
    think: new URL('@/assets/images/galgame/08.png', import.meta.url).href,
    sad: new URL('@/assets/images/galgame/09.png', import.meta.url).href,
  })
  
  // ================= 古河渚风格脚本 (Script) =================
  // 格式：[立绘Key, 文本内容]
  const scenario = [
    ['normal', "那个...您好。我是古河渚。您似乎迷路了...。"],
    ['think', "这里以前...似乎是一个叫做『书忆』的在线轻小说图书馆。听起来...是个很温暖的地方呢。"],
    ['sad', "但是，因为各种各样的原因，它就像演出的帷幕落下一样，安静地离开了..."],
    ['confused', "那个，请不要露出悲伤的表情...因为，梦想是不会轻易终结的。"],
    ['serious', "现在，这里有了新的开始。它是吉卜的毕业设计——一个全新的『网上图书分享网』。"],
    ['normal', "不再仅仅是被动地阅读，而是希望大家能把自己的宝藏书籍分享出来。"],
    ['happy', "就像...就像把团子大家族的一个个成员，介绍给新的朋友认识一样。一定，会是非常幸福的事情。"],
    ['encourage', "虽然现在还很幼小，但如果有您的加入，这个分享网一定会变得非常热闹的。"],
    ['normal', "所以，如果可以的话...您愿意和我们一起，重新开始这个梦想吗？"]
  ]
  
  const currentIndex = ref(0)
  const displayContent = ref("")
  const showChoices = ref(false)
  const typeFinished = ref(false) // 标记当前段落打字是否完成
  let timer = null
  
  // 计算当前应该显示的立绘路径
  const currentSprite = computed(() => {
    const spriteKey = scenario[currentIndex.value][0]
    return sprites[spriteKey] || sprites.normal // 默认为常态
  })
  
  // 打字机特效
  const typeWriter = (text) => {
    clearInterval(timer)
    let i = 0
    displayContent.value = ""
    typeFinished.value = false
    
    // 渚说话比较慢，语速设为 70ms
    timer = setInterval(() => {
      if (i < text.length) {
        displayContent.value += text.charAt(i)
        i++
      } else {
        clearInterval(timer)
        typeFinished.value = true
        // 播到最后一句，显示选择肢
        if (currentIndex.value === scenario.length - 1) {
          showChoices.value = true
        }
      }
    }, 70)
  }
  
  // 点击对话框进行下一步
  const nextStep = () => {
    // 如果正在显示选择肢，或者打字未完成，点击无效
    if (showChoices.value || !typeFinished.value) return
    
    if (currentIndex.value < scenario.length - 1) {
      currentIndex.value++
      typeWriter(scenario[currentIndex.value][1])
    }
  }
  
  // 选择肢回调
  const selectChoice = (type) => {
    showChoices.value = false
    if (type === 'share') {
      typeWriter("真的吗？太好了！那...请您务必去试试『分享藏书』的功能。我也...非常期待看到您的分享。")
      // 3秒后跳转到发布页面
      setTimeout(() => router.push('/user/publishBook'), 3500)
    } else {
      typeWriter("好的，明白了。请在这座城镇...啊不，这座网站里自由地漫步吧。愿您能找到喜欢的书。")
      // 3秒后跳转到找书页面
      setTimeout(() => router.push('/findBooks'), 3500)
    }
  }
  
  // 粒子随机样式
  const getRandomParticleStyle = () => {
    return {
      left: Math.random() * 100 + '%',
      top: Math.random() * 100 + '%',
      animationDelay: Math.random() * 5 + 's',
      animationDuration: (Math.random() * 5 + 5) + 's'
    }
  }
  
  onMounted(() => {
    typeWriter(scenario[0][1])
  })
  </script>
  
  <style scoped>
  /* ================= 核心 Galgame 样式 ================= */
  .gal-game-container {
    height: 100vh;
    width: 100%;
    position: relative;
    overflow: hidden;
    background: #000; /* 兜底黑 */
    color: #fff;
    /* 渚的风格，使用偏柔和的字体 */
    font-family: 'Times New Roman', STKaiti, '楷体', serif;
  }
  
  /* 背景图：bg01.jpg */
  .gal-bg {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    /* 替换为你的路径 */
    background: url('@/assets/images/galgame/bg01.jpg') center/cover no-repeat;
    filter: brightness(0.8); /* 稍微调暗，突出立绘和文字 */
    z-index: 1;
  }
  
  /* 光玉粒子动画 */
  .particles span {
    position: absolute;
    width: 8px; height: 8px;
    background: rgba(255, 255, 200, 0.4);
    border-radius: 50%;
    box-shadow: 0 0 10px rgba(255, 255, 200, 0.6);
    opacity: 0;
    animation: floatUp infinite linear;
    z-index: 2;
  }
  @keyframes floatUp {
    0% { transform: translateY(20px) scale(0); opacity: 0; }
    20% { opacity: 1; }
    80% { opacity: 0.8; }
    100% { transform: translateY(-100px) scale(1.2); opacity: 0; }
  }
  
  .gal-main {
    position: relative;
    z-index: 10;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: flex-end; /* 内容靠下 */
    align-items: center;
    padding-bottom: 40px;
  }
  
  /* ================= 立绘 Sprite 样式 ================= */
  .character-sprite {
    position: absolute;
    bottom: 150px; /* 位于对话框上方 */
    height: 85vh; /* 占据屏幕大半高度 */
    object-fit: contain;
    z-index: 5;
    /* 增加柔和的投影，使其与背景分离 */
    filter: drop-shadow(0 0 15px rgba(255, 255, 255, 0.2));
    pointer-events: none; /* 防止遮挡对话框点击 */
  }
  
  /* 立绘切换时的淡入淡出动画 */
  .sprite-fade-enter-active, .sprite-fade-leave-active {
    transition: opacity 0.3s ease, transform 0.3s ease;
  }
  .sprite-fade-enter-from {
    opacity: 0;
    transform: translateY(10px); /* 轻微浮动 */
  }
  .sprite-fade-leave-to {
    opacity: 0;
  }
  
  /* ================= 对话框样式 ================= */
  .msg-box-wrapper {
    width: 95%;
    max-width: 1100px;
    cursor: pointer;
    position: relative;
    z-index: 11;
  }
  
  /* 渚的名字标签 */
  .name-tag {
    background: rgba(230, 160, 170, 0.85); /* 淡淡的粉红色 */
    color: #fff;
    display: inline-block;
    padding: 10px 40px;
    font-weight: bold;
    font-size: 1.2rem;
    border-radius: 15px 15px 0 0; /* 圆角顶部 */
    margin-left: 30px;
    border: 2px solid rgba(255, 255, 255, 0.4);
    border-bottom: none;
    text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
  }
  
  .msg-box {
    background: rgba(20, 20, 30, 0.8); /* 深色半透明 */
    border: 3px solid rgba(230, 160, 170, 0.5); /* 粉色边框 */
    padding: 35px 50px;
    min-height: 140px;
    border-radius: 0 25px 25px 25px; /* 渚的柔和感觉 */
    backdrop-filter: blur(10px);
    box-shadow: 0 10px 30px rgba(0,0,0,0.5);
    transition: border-color 0.3s;
  }
  .msg-box-wrapper:hover .msg-box {
    border-color: rgba(230, 160, 170, 1); /* 悬停时边框变亮 */
  }
  
  .msg-content {
    font-size: 1.3rem;
    line-height: 1.8;
    letter-spacing: 3px;
    color: #fff;
    text-shadow: 1px 1px 3px #000;
  }
  
  /* 继续提示符 ▼ 动画 */
  .next-animated {
    display: inline-block;
    margin-left: 15px;
    color: #e6a0aa;
    animation: blinkY 1s infinite;
  }
  @keyframes blinkY {
    0%, 100% { transform: translateY(0); opacity: 1; }
    50% { transform: translateY(5px); opacity: 0; }
  }
  
  /* ================= 选择肢样式 ================= */
  .choices-layer {
    position: absolute;
    top: 35%; /* 位于屏幕偏上方 */
    display: flex;
    flex-direction: column;
    gap: 25px;
    width: 100%;
    align-items: center;
    z-index: 20;
  }
  
  .choice-item {
    background: rgba(230, 160, 170, 0.2); /* 淡淡的粉色背景 */
    border: 2px solid rgba(230, 160, 170, 0.6);
    padding: 18px 80px;
    border-radius: 50px;
    cursor: pointer;
    transition: all 0.3s ease;
    backdrop-filter: blur(8px);
    color: #fff;
    font-size: 1.2rem;
    font-weight: bold;
    text-shadow: 1px 1px 2px #000;
  }
  
  .choice-item:hover {
    background: rgba(230, 160, 170, 0.8);
    transform: scale(1.05);
    box-shadow: 0 0 25px rgba(230, 160, 170, 0.5);
  }
  
  /* 选项淡入淡出动画 */
  .fade-enter-active, .fade-leave-active { transition: opacity 0.5s ease; }
  .fade-enter-from, .fade-leave-to { opacity: 0; }
  
  @media (max-width: 768px) {
    .name-tag { font-size: 1rem; padding: 8px 25px; }
    .msg-content { font-size: 1.1rem; letter-spacing: 1px; }
    .msg-box { padding: 25px; }
    .character-sprite { height: 70vh; bottom: 180px; }
    .choice-item { padding: 15px 40px; font-size: 1rem; }
  }
  </style>