const API_BASE = '/api/chat'

function authHeaders(token) {
  return {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
}

async function request(path, token, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      ...authHeaders(token),
      ...(options.headers || {})
    }
  })
  const data = await response.json().catch(() => ({}))
  if (!response.ok) {
    throw new Error(data.error || '请求失败')
  }
  return data
}

export function fetchConversations(token) {
  return request('/conversations', token)
}

export function fetchUnreadTotal(token) {
  return request('/unread-total', token)
}

export function fetchChatStatus(token, peerId) {
  return request(`/status/${peerId}`, token)
}

export function fetchMessages(token, peerId, page = 0, size = 50) {
  return request(`/messages/${peerId}?page=${page}&size=${size}`, token)
}

export function markConversationRead(token, peerId) {
  return request(`/messages/${peerId}/read`, token, { method: 'POST' })
}

export function deleteConversation(token, peerId) {
  return request(`/conversations/${peerId}`, token, { method: 'DELETE' })
}
