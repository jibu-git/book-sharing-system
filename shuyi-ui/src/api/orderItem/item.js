import request from '@/utils/request'

// 查询借阅订单明细列表
export function listItem(query) {
  return request({
    url: '/orderItem/item/list',
    method: 'get',
    params: query
  })
}

// 查询借阅订单明细详细
export function getItem(orderItemId) {
  return request({
    url: '/orderItem/item/' + orderItemId,
    method: 'get'
  })
}

// 新增借阅订单明细
export function addItem(data) {
  return request({
    url: '/orderItem/item',
    method: 'post',
    data: data
  })
}

// 修改借阅订单明细
export function updateItem(data) {
  return request({
    url: '/orderItem/item',
    method: 'put',
    data: data
  })
}

// 删除借阅订单明细
export function delItem(orderItemId) {
  return request({
    url: '/orderItem/item/' + orderItemId,
    method: 'delete'
  })
}
