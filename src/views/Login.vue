<template>
  <div class="page active">
    <div class="auth-page">
      <div class="auth-left">
        <div class="auth-left-bg"></div>
        <div class="auth-left-grid"></div>
        <div class="auth-left-bigchar">粤</div>
        <div class="auth-left-pos">
          <div class="auth-left-logo">
            <div class="auth-left-logo-seal">
              <span>粤</span>
            </div>
            <div class="auth-left-logo-name">
              粤韵<em>志</em>
            </div>
          </div>
          <div class="auth-left-title">
            传承岭南<em>文化</em><br>
            记录粤韵<em>风华</em>
          </div>
          <div class="auth-left-desc">
            在这里，我们探索粤剧、粤菜、粤语、民俗等广东文化的精髓与魅力，传承千年的岭南文化瑰宝。
          </div>
          <div class="auth-left-tags">
            <span class="auth-left-tag">粤剧艺术</span>
            <span class="auth-left-tag">粤菜美食</span>
            <span class="auth-left-tag">粤语文化</span>
            <span class="auth-left-tag">民俗传统</span>
          </div>
        </div>
      </div>
      <div class="auth-right">
        <div class="auth-box">
          <div class="auth-tabs">
            <button 
              class="auth-tab" 
              :class="{ on: activeTab === 'login' }"
              @click="activeTab = 'login'"
            >登录</button>
            <button 
              class="auth-tab" 
              :class="{ on: activeTab === 'register' }"
              @click="activeTab = 'register'"
            >注册</button>
          </div>
          
          <!-- 登录表单 -->
          <div v-if="activeTab === 'login'">
            <div class="auth-form-title">欢迎归来</div>
            <div class="auth-form-sub">登录您的粤韵志账号</div>
            
            <div class="form-group">
              <label class="form-label">用户名/邮箱</label>
              <input 
                type="text" 
                class="form-input" 
                placeholder="请输入您的用户名或邮箱"
                v-model="loginForm.usernameOrEmail"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">密码</label>
              <input 
                type="password" 
                class="form-input" 
                placeholder="请输入您的密码"
                v-model="loginForm.password"
              />
            </div>
            
            <div class="form-error" v-if="errorMsg">{{ errorMsg }}</div>
            
            <div class="form-checkbox">
              <input type="checkbox" id="remember" v-model="loginForm.remember" />
              <span>记住我 · <a>忘记密码？</a></span>
            </div>
            
            <button 
              class="btn btn-v btn-lg" 
              style="width: 100%;"
              @click="handleLogin"
              :disabled="isLoading"
            >
              {{ isLoading ? '登录中...' : '登录' }}
            </button>
          </div>
          
          <!-- 注册表单 -->
          <div v-if="activeTab === 'register'">
            <div class="auth-form-title">加入粤韵志</div>
            <div class="auth-form-sub">创建账号，开启岭南文化之旅</div>
            
            <div class="form-group">
              <label class="form-label">邮箱地址</label>
              <input 
                type="email" 
                class="form-input" 
                placeholder="请输入您的邮箱"
                v-model="registerForm.email"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">用户名</label>
              <input 
                type="text" 
                class="form-input" 
                placeholder="请输入您的用户名"
                v-model="registerForm.username"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">密码</label>
              <input 
                type="password" 
                class="form-input" 
                placeholder="请输入您的密码"
                v-model="registerForm.password"
              />
            </div>
            
            <div class="form-group">
              <label class="form-label">确认密码</label>
              <input 
                type="password" 
                class="form-input" 
                placeholder="请再次输入您的密码"
                v-model="registerForm.confirmPassword"
              />
            </div>
            
            <div class="form-error" v-if="errorMsg">{{ errorMsg }}</div>
            
            <div class="form-checkbox">
              <input type="checkbox" id="agree" v-model="registerForm.agree" />
              <span>我已阅读并同意 <a>用户协议</a> 和 <a>隐私政策</a></span>
            </div>
            
            <button 
              class="btn btn-v btn-lg" 
              style="width: 100%;"
              @click="handleRegister"
              :disabled="isLoading"
            >
              {{ isLoading ? '注册中...' : '注册' }}
            </button>
          </div>
          
          <div class="form-divider">
            <div class="form-divider-line"></div>
            <div class="form-divider-txt">或使用以下方式{{ activeTab === 'login' ? '登录' : '注册' }}</div>
            <div class="form-divider-line"></div>
          </div>
          
          <div class="form-socials">
            <button class="btn btn-ghost">微信</button>
            <button class="btn btn-ghost">QQ</button>
            <button class="btn btn-ghost">微博</button>
          </div>
          
          <!-- 测试账号提示 -->
          <div class="test-accounts" v-if="activeTab === 'login'">
            <div class="test-title">测试账号（密码均为 123456）：</div>
            <div class="test-list">
              <div class="test-item" @click="fillTestAccount('admin')">admin (管理员)</div>
              <div class="test-item" @click="fillTestAccount('zhang_ming')">zhang_ming (普通用户)</div>
              <div class="test-item" @click="fillTestAccount('chen_juan')">chen_juan (普通用户)</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('login')
const isLoading = ref(false)
const errorMsg = ref('')

const loginForm = reactive({
  usernameOrEmail: '',
  password: '',
  remember: false
})

const registerForm = reactive({
  email: '',
  username: '',
  password: '',
  confirmPassword: '',
  agree: false
})

const fillTestAccount = (usernameOrEmail) => {
  loginForm.usernameOrEmail = usernameOrEmail
  loginForm.password = '123456'
}

const handleLogin = async () => {
  if (!loginForm.usernameOrEmail || !loginForm.password) {
    errorMsg.value = '请填写用户名/邮箱和密码'
    return
  }

  isLoading.value = true
  errorMsg.value = ''

  try {
    await authStore.login(loginForm.usernameOrEmail, loginForm.password)
    
    if (authStore.user?.role === 'admin') {
      router.push('/admin')
    } else {
      router.push('/')
    }
  } catch (error) {
    errorMsg.value = error.message
  } finally {
    isLoading.value = false
  }
}

const handleRegister = async () => {
  if (!registerForm.email || !registerForm.username || !registerForm.password) {
    errorMsg.value = '请填写所有必填项'
    return
  }
  
  if (registerForm.password !== registerForm.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }
  
  if (!registerForm.agree) {
    errorMsg.value = '请同意用户协议和隐私政策'
    return
  }
  
  isLoading.value = true
  errorMsg.value = ''
  
  try {
    await authStore.register(registerForm.email, registerForm.password, registerForm.username)
    router.push('/')
  } catch (error) {
    errorMsg.value = error.message
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
  overflow: hidden;
}

.auth-left {
  background: #1A1008;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 60px;
  height: 100%;
}

.auth-left-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1A0804 0%, #3A100A 40%, #6B1A14 70%, #1A0804 100%);
}

.auth-left-grid {
  position: absolute;
  inset: 0;
  background-image:
    repeating-linear-gradient(0deg, transparent, transparent 40px, rgba(212, 160, 23, 0.04) 40px, rgba(212, 160, 23, 0.04) 41px),
    repeating-linear-gradient(90deg, transparent, transparent 40px, rgba(212, 160, 23, 0.04) 40px, rgba(212, 160, 23, 0.04) 41px);
}

.auth-left-bigchar {
  position: absolute;
  right: -40px;
  top: -40px;
  font-family: 'Ma Shan Zheng', serif;
  font-size: 380px;
  color: rgba(192, 57, 43, 0.06);
  line-height: 1;
  user-select: none;
  transform: rotate(-8deg);
}

.auth-left-pos {
  position: relative;
  z-index: 1;
}

.auth-left-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 60px;
}

.auth-left-logo-seal {
  width: 44px;
  height: 44px;
  background: rgba(212, 160, 23, 0.08);
  border: 1px solid rgba(212, 160, 23, 0.2);
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-left-logo-seal span {
  font-family: 'Ma Shan Zheng', serif;
  font-size: 22px;
  color: #D4A017;
}

.auth-left-logo-name {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 22px;
  letter-spacing: 3px;
  color: #fff;
}

.auth-left-logo-name em {
  color: #C0392B;
  font-style: normal;
}

.auth-left-title {
  font-family: 'Ma Shan Zheng', serif;
  font-size: 48px;
  color: #fff;
  letter-spacing: 8px;
  line-height: 1.3;
  margin-bottom: 16px;
}

.auth-left-title em {
  color: #D4A017;
  font-style: normal;
}

.auth-left-desc {
  font-size: 13.5px;
  color: rgba(255, 255, 255, 0.45);
  line-height: 1.9;
  margin-bottom: 40px;
  max-width: 320px;
}

.auth-left-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.auth-left-tag {
  background: rgba(212, 160, 23, 0.06);
  border: 1px solid rgba(212, 160, 23, 0.15);
  color: rgba(255, 255, 255, 0.4);
  font-size: 11px;
  letter-spacing: 2px;
  padding: 6px 16px;
  border-radius: 2px;
}

.auth-right {
  background: #FBF6EC;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 60px 80px;
  overflow-y: auto;
  height: 100%;
}

.auth-box {
  width: 100%;
  max-width: 420px;
  margin: auto 0;
  padding: 40px 0;
}

.auth-tabs {
  display: flex;
  gap: 0;
  margin-bottom: 40px;
  border-bottom: 2px solid #EAD8BE;
}

.auth-tab {
  padding: 12px 28px;
  font-size: 14px;
  letter-spacing: 2px;
  font-family: 'ZCOOL XiaoWei', serif;
  color: #6B5744;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  background: none;
  border-left: none;
  border-right: none;
  border-top: none;
  transition: all 0.2s;
}

.auth-tab.on {
  color: #C0392B;
  border-bottom-color: #C0392B;
}

.auth-form-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 26px;
  letter-spacing: 4px;
  margin-bottom: 8px;
}

.auth-form-sub {
  font-size: 13px;
  color: rgba(107, 87, 68, 0.5);
  margin-bottom: 32px;
  letter-spacing: 1px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  font-size: 12px;
  letter-spacing: 2px;
  color: #6B5744;
  margin-bottom: 8px;
  display: block;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1.5px solid #D4B896;
  border-radius: 3px;
  background: #fff;
  font-size: 14px;
  color: #1A1008;
  outline: none;
  transition: border-color 0.2s;
}

.form-input:focus {
  border-color: #C0392B;
}

.form-input::placeholder {
  color: rgba(107, 87, 68, 0.5);
}

.form-error {
  color: #C0392B;
  font-size: 13px;
  margin-bottom: 16px;
  padding: 8px 12px;
  background: rgba(192, 57, 43, 0.1);
  border-radius: 3px;
}

.form-checkbox {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 24px;
  cursor: pointer;
}

.form-checkbox input {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: #C0392B;
}

.form-checkbox span {
  font-size: 13px;
  color: #6B5744;
}

.form-checkbox a {
  color: #C0392B;
  text-decoration: underline;
  cursor: pointer;
}

.form-divider {
  display: flex;
  align-items: center;
  gap: 14px;
  margin: 24px 0;
}

.form-divider-line {
  flex: 1;
  height: 1px;
  background: #EAD8BE;
}

.form-divider-txt {
  font-size: 12px;
  color: rgba(107, 87, 68, 0.5);
  letter-spacing: 2px;
}

.form-socials {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border: none;
  font-family: 'Noto Serif SC', serif;
  font-size: 13px;
  letter-spacing: 2px;
  border-radius: 3px;
  transition: all 0.2s ease;
  white-space: nowrap;
  cursor: pointer;
}

.btn-v {
  background: linear-gradient(135deg, #C0392B, #E04B3A);
  color: #fff;
  box-shadow: 0 4px 18px rgba(192, 57, 43, 0.25);
}

.btn-v:hover {
  background: linear-gradient(135deg, #E04B3A, #F15C4A);
  box-shadow: 0 6px 24px rgba(192, 57, 43, 0.35);
  transform: translateY(-2px);
}

.btn-v:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-lg {
  padding: 14px 36px;
  font-size: 15px;
}

.btn-ghost {
  background: #fff;
  color: #6B5744;
  border: 1px solid #D4B896;
}

.btn-ghost:hover {
  border-color: #C0392B;
  color: #C0392B;
}

.test-accounts {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #EAD8BE;
}

.test-title {
  font-size: 12px;
  color: #6B5744;
  margin-bottom: 8px;
}

.test-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.test-item {
  font-size: 11px;
  color: #C0392B;
  padding: 4px 10px;
  background: rgba(192, 57, 43, 0.1);
  border-radius: 3px;
  cursor: pointer;
  transition: all 0.2s;
}

.test-item:hover {
  background: #C0392B;
  color: #fff;
}

@media (max-width: 900px) {
  .auth-page {
    grid-template-columns: 1fr;
  }
  
  .auth-left {
    display: none;
  }
  
  .auth-right {
    padding: 40px 24px;
  }
}
</style>