import request from '@/utils/request'

// 查询借阅车列表
export function listCart(query) {
  return request({
    url: '/TCart/cart/list',
    method: 'get',
    params: query
  })
}

// 查询借阅车详细
export function getCart(cartId) {
  return request({
    url: '/TCart/cart/' + cartId,
    method: 'get'
  })
}

// 新增借阅车
export function addCart(data) {
  return request({
    url: '/TCart/cart',
    method: 'post',
    data: data
  })
}

// 修改借阅车
export function updateCart(data) {
  return request({
    url: '/TCart/cart',
    method: 'put',
    data: data
  })
}

// 删除借阅车
export function delCart(cartId) {
  return request({
    url: '/TCart/cart/' + cartId,
    method: 'delete'
  })
}

//借阅车下单功能
export function orderCart(cartIds){
  return request({
    url: '/TCart/cart/checkout',
    method: 'post',
    data: cartIds
  })
}

