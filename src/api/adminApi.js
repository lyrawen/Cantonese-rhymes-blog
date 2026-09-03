const API_BASE = '/api/admin'

function authHeaders() {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function request(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...authHeaders(),
      ...(options.headers || {})
    }
  })
  const data = await res.json().catch(() => ({}))
  if (!res.ok) {
    throw new Error(data.error || '请求失败')
  }
  return data
}

export const adminApi = {
  getStats: () => request('/stats'),
  getPendingArticles: () => request('/articles/pending'),
  getAllArticles: () => request('/articles'),
  approveArticle: (id) => request(`/articles/${id}/approve`, { method: 'PUT' }),
  rejectArticle: (id) => request(`/articles/${id}/reject`, { method: 'PUT' }),
  deleteArticle: (id) => request(`/articles/${id}`, { method: 'DELETE' }),
  getComments: () => request('/comments'),
  deleteComment: (id) => request(`/comments/${id}`, { method: 'DELETE' }),
  updateCommentStatus: (id, status) => request(`/comments/${id}/status`, { method: 'PUT', body: JSON.stringify({ status }) }),
  getCategories: () => request('/categories'),
  createCategory: (categoryName, sortOrder) => request('/categories', { method: 'POST', body: JSON.stringify({ categoryName, sortOrder }) }),
  deleteCategory: (id) => request(`/categories/${id}`, { method: 'DELETE' }),
  getTags: () => request('/tags'),
  createTag: (tagName) => request('/tags', { method: 'POST', body: JSON.stringify({ tagName }) }),
  deleteTag: (id) => request(`/tags/${id}`, { method: 'DELETE' }),
  getUsers: () => request('/users'),
  updateUserStatus: (id, status) => request(`/users/${id}/status`, { method: 'PUT', body: JSON.stringify({ status }) }),
  getAnnouncements: () => request('/announcements'),
  createAnnouncement: (title, content) => request('/announcements', { method: 'POST', body: JSON.stringify({ title, content }) }),
  updateAnnouncementStatus: (id, status) => request(`/announcements/${id}/status`, { method: 'PUT', body: JSON.stringify({ status }) }),
  deleteAnnouncement: (id) => request(`/announcements/${id}`, { method: 'DELETE' }),
  getAiChat: () => request('/ai-chat'),
  deleteAiChat: (id) => request(`/ai-chat/${id}`, { method: 'DELETE' }),
  getRanking: (metric) => request(`/ranking/${metric}`)
}
