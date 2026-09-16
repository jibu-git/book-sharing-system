import request from '@/utils/request'

// 查询消息提醒列表
export function listMessage(query) {
  return request({
    url: '/message/message/list',
    method: 'get',
    params: query
  })
}

// 查询消息提醒详细
export function getMessage(msgId) {
  return request({
    url: '/message/message/' + msgId,
    method: 'get'
  })
}

// 新增消息提醒
export function addMessage(data) {
  return request({
    url: '/message/message',
    method: 'post',
    data: data
  })
}

// 修改消息提醒
export function updateMessage(data) {
  return request({
    url: '/message/message',
    method: 'put',
    data: data
  })
}

// 删除消息提醒
export function delMessage(msgId) {
  return request({
    url: '/message/message/' + msgId,
    method: 'delete'
  })
}

// 获取未读数
export function getUnreadCount() {
  return request({
    url: '/message/message/unreadCount',
    method: 'get'
  })
}

