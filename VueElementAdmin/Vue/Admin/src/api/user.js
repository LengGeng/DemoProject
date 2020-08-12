import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/admin?method=login',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/admin?method=info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/admin?method=logout',
    method: 'post'
  })
}
