import request from '@/utils/request'

// 查询图书评论列表
export function listComment(query) {
  return request({
    url: '/Comment/comment/list',
    method: 'get',
    params: query
  })
}

// 查询图书评论详细
export function getComment(commentId) {
  return request({
    url: '/Comment/comment/' + commentId,
    method: 'get'
  })
}

// 新增图书评论
export function addComment(data) {
  return request({
    url: '/Comment/comment',
    method: 'post',
    data: data
  })
}

// 修改图书评论
export function updateComment(data) {
  return request({
    url: '/Comment/comment',
    method: 'put',
    data: data
  })
}

// 删除图书评论
export function delComment(commentId) {
  return request({
    url: '/Comment/comment/' + commentId,
    method: 'delete'
  })
}


// 获取书籍评论树
export function getCommentTree(bookId) {
  return request({
    url: '/Comment/comment/treeList/' + bookId,
    method: 'get'
  })
}

