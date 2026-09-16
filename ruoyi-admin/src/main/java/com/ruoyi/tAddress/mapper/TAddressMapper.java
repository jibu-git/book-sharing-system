package com.ruoyi.tAddress.mapper;

import java.util.List;
import com.ruoyi.tAddress.domain.TAddress;

/**
 * 收货地址Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-04
 */
public interface TAddressMapper 
{
    /**
     * 查询收货地址
     * 
     * @param aid 收货地址主键
     * @return 收货地址
     */
    public TAddress selectTAddressByAid(Long aid);

    /**
     * 查询收货地址列表
     * 
     * @param tAddress 收货地址
     * @return 收货地址集合
     */
    public List<TAddress> selectTAddressList(TAddress tAddress);

    /**
     * 新增收货地址
     * 
     * @param tAddress 收货地址
     * @return 结果
     */
    public int insertTAddress(TAddress tAddress);

    /**
     * 修改收货地址
     * 
     * @param tAddress 收货地址
     * @return 结果
     */
    public int updateTAddress(TAddress tAddress);

    /**
     * 删除收货地址
     * 
     * @param aid 收货地址主键
     * @return 结果
     */
    public int deleteTAddressByAid(Long aid);

    /**
     * 批量删除收货地址
     * 
     * @param aids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTAddressByAids(Long[] aids);
}
