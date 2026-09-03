<template>
  <footer class="site-footer">
    <div class="footer-inner">
      <div class="footer-col">
        <div class="footer-brand-seal">
          <span>粤</span>
        </div>
        <div class="footer-brand-title">
          粤韵<em>志</em>
        </div>
        <div class="footer-brand-desc">
          传承岭南文化，记录粤韵风华。在这里，我们探索粤剧、粤菜、粤语、民俗等广东文化的精髓与魅力。
        </div>
      </div>
      <div class="footer-col">
        <div class="footer-col-title">探索</div>
        <a class="footer-col-link">粤剧艺术</a>
        <a class="footer-col-link">粤菜美食</a>
        <a class="footer-col-link">粤语文化</a>
        <a class="footer-col-link">民俗传统</a>
      </div>
      <div class="footer-col">
        <div class="footer-col-title">关于</div>
        <a class="footer-col-link">关于我们</a>
        <a class="footer-col-link">联系方式</a>
        <a class="footer-col-link">加入我们</a>
        <a class="footer-col-link">隐私政策</a>
      </div>
      <div class="footer-col">
        <div class="footer-col-title">关注</div>
        <a class="footer-col-link">微信公众号</a>
        <a class="footer-col-link">微博</a>
        <a class="footer-col-link">知乎</a>
        <a class="footer-col-link">B站</a>
      </div>
    </div>
    <div class="footer-bottom">
      <div class="footer-bottom-logo">粤韵志 · Cantonese Culture Blog</div>
      <div>© 2024 粤韵志 All Rights Reserved</div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.site-footer {
  background: #1A1008;
  color: rgba(255, 255, 255, 0.45);
  padding: 56px 36px 32px;
  margin-top: auto;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 48px;
}

.footer-brand-seal {
  width: 52px;
  height: 52px;
  background: rgba(212, 160, 23, 0.1);
  border: 1px solid rgba(212, 160, 23, 0.25);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
}

.footer-brand-seal span {
  font-family: 'Ma Shan Zheng', serif;
  font-size: 26px;
  color: #D4A017;
}

.footer-brand-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 22px;
  letter-spacing: 4px;
  color: #fff;
  margin-bottom: 8px;
}

.footer-brand-title em {
  color: #C0392B;
  font-style: normal;
}

.footer-brand-desc {
  font-size: 13px;
  line-height: 1.9;
  max-width: 280px;
}

.footer-col-title {
  font-size: 11px;
  letter-spacing: 4px;
  color: #D4A017;
  text-transform: uppercase;
  margin-bottom: 18px;
}

.footer-col-link {
  display: block;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.45);
  padding: 5px 0;
  cursor: pointer;
  transition: color 0.2s;
}

.footer-col-link:hover {
  color: rgba(255, 255, 255, 0.85);
}

.footer-bottom {
  max-width: 1200px;
  margin: 40px auto 0;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  letter-spacing: 2px;
}

.footer-bottom-logo {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 14px;
  letter-spacing: 4px;
  color: rgba(255, 255, 255, 0.3);
}
</style>