import request from '@/utils/request'

// 查询图书商品列表
export function listBooks(query) {
  return request({
    url: '/Books/books/list',
    method: 'get',
    params: query
  })
}


// 给买家查询查询前台图书商品列表
export function portalListBooks(query) {
  return request({
    url: '/Books/books/list/portal',
    method: 'get',
    params: query
  })
}



// 查询图书商品详细
export function getBooks(booksId) {
  return request({
    url: '/Books/books/' + booksId,
    method: 'get'
  })
}

// 新增图书商品
export function addBooks(data) {
  return request({
    url: '/Books/books',
    method: 'post',
    data: data
  })
}

// 修改图书商品
export function updateBooks(data) {
  return request({
    url: '/Books/books',
    method: 'put',
    data: data
  })
}

// 删除图书商品
export function delBooks(booksId) {
  return request({
    url: '/Books/books/' + booksId,
    method: 'delete'
  })
}

// 获取当前库存数量
export function getCurrentStockQuantity(booksId) {
  return request({
    url: '/BookStock/stock/' + booksId,
    method: 'get'
  })
}

