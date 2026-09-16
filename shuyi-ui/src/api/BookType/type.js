import request from '@/utils/request'

// 查询图书标签列表
export function listType(query) {
  return request({
    url: '/BookType/type/list',
    method: 'get',
    params: query
  })
}

// 查询图书标签详细
export function getType(bookTypeId) {
  return request({
    url: '/BookType/type/' + bookTypeId,
    method: 'get'
  })
}

// 新增图书标签
export function addType(data) {
  return request({
    url: '/BookType/type',
    method: 'post',
    data: data
  })
}

// 修改图书标签
export function updateType(data) {
  return request({
    url: '/BookType/type',
    method: 'put',
    data: data
  })
}

// 删除图书标签
export function delType(bookTypeId) {
  return request({
    url: '/BookType/type/' + bookTypeId,
    method: 'delete'
  })
}
