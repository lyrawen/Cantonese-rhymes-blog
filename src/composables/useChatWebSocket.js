import { ref, onUnmounted } from 'vue'

export function useChatWebSocket(getToken, { onMessage, onError } = {}) {
  const connected = ref(false)
  let socket = null
  let reconnectTimer = null

  const resolveToken = () => {
    if (typeof getToken === 'function') return getToken()
    return getToken?.value ?? getToken
  }

  const getWsUrl = (token) => {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    const host = window.location.host
    return `${protocol}//${host}/ws/chat?token=${encodeURIComponent(token)}`
  }

  const connect = () => {
    const token = resolveToken()
    if (!token) return
    disconnect(false)

    socket = new WebSocket(getWsUrl(token))

    socket.onopen = () => {
      connected.value = true
    }

    socket.onmessage = (event) => {
      try {
        const payload = JSON.parse(event.data)
        onMessage?.(payload)
      } catch (e) {
        console.error('WebSocket 消息解析失败', e)
      }
    }

    socket.onerror = () => {
      onError?.('WebSocket 连接异常')
    }

    socket.onclose = () => {
      connected.value = false
      if (resolveToken()) {
        reconnectTimer = setTimeout(connect, 3000)
      }
    }
  }

  const sendText = (peerId, content) => {
    if (!socket || socket.readyState !== WebSocket.OPEN) {
      throw new Error('连接未就绪，请稍后重试')
    }
    socket.send(JSON.stringify({ type: 'send', peerId, content }))
  }

  const disconnect = (clearReconnect = true) => {
    if (clearReconnect && reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }
    if (socket) {
      socket.onclose = null
      socket.close()
      socket = null
    }
    connected.value = false
  }

  onUnmounted(() => disconnect())

  return { connected, connect, disconnect, sendText }
}
