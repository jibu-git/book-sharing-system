import request from '@/utils/request'

// 查询收货地址列表
export function listTAddress(query) {
  return request({
    url: '/TAddress/tAddress/list',
    method: 'get',
    params: query
  })
}

// 查询收货地址详细
export function getTAddress(aid) {
  return request({
    url: '/TAddress/tAddress/' + aid,
    method: 'get'
  })
}

// 新增收货地址
export function addTAddress(data) {
  return request({
    url: '/TAddress/tAddress',
    method: 'post',
    data: data
  })
}

// 修改收货地址
export function updateTAddress(data) {
  return request({
    url: '/TAddress/tAddress',
    method: 'put',
    data: data
  })
}

// 删除收货地址
export function delTAddress(aid) {
  return request({
    url: '/TAddress/tAddress/' + aid,
    method: 'delete'
  })
}
