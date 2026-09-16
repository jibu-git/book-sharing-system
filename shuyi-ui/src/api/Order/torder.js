import request from '@/utils/request'

// 查询借阅订单列表
export function listOrder(query) {
  return request({
    url: '/Order/order/list',
    method: 'get',
    params: query
  })
}

// 查询借阅订单详细
export function getOrder(orderId) {
  return request({
    url: '/Order/order/' + orderId,
    method: 'get'
  })
}

// 新增借阅订单
export function addOrder(data) {
  return request({
    url: '/Order/order',
    method: 'post',
    data: data
  })
}

// 修改借阅订单
export function updateOrder(data) {
  return request({
    url: '/Order/order',
    method: 'put',
    data: data
  })
}

// 删除借阅订单
export function delOrder(orderId) {
  return request({
    url: '/Order/order/' + orderId,
    method: 'delete'
  })
}

// 书主同意借阅申请（排他性处理）
export function agreeLendOrder(orderId) {
  return request({
    url: '/Order/order/agreeLend/' + orderId,
    method: 'put'
  })
}

// 借书人：发起归还图书（状态 2 -> 3）
export function returnOrder(orderId) {
  return request({
    url: '/Order/order/returnOrder/' + orderId,
    method: 'put'
  })
}

// 书主：确认收到归还（状态 3 -> 4，同时图书状态恢复 0）
export function confirmReceipt(orderId) {
  return request({
    url: '/Order/order/confirmReceipt/' + orderId,
    method: 'put'
  })
}


// 立即申请下单
export function quickCheckout(data) {
  return request({
    url: '/Order/order/quickCheckout', // 必须与后端 Controller 的 @PostMapping 路径一致
    method: 'post',
    data: data // data 是一个对象，包含 booksId 和 num
  })
}