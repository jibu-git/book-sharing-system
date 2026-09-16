package com.ruoyi.tAddress.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.tAddress.mapper.TAddressMapper;
import com.ruoyi.tAddress.domain.TAddress;
import com.ruoyi.tAddress.service.ITAddressService;

/**
 * 收货地址Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-04
 */
@Service
public class TAddressServiceImpl implements ITAddressService 
{
    @Autowired
    private TAddressMapper tAddressMapper;

    /**
     * 查询收货地址
     * 
     * @param aid 收货地址主键
     * @return 收货地址
     */
    @Override
    public TAddress selectTAddressByAid(Long aid)
    {
        return tAddressMapper.selectTAddressByAid(aid);
    }

    /**
     * 查询收货地址列表
     * 
     * @param tAddress 收货地址
     * @return 收货地址
     */
    @Override
    public List<TAddress> selectTAddressList(TAddress tAddress)
    {
        if (tAddress.getUserId() == 1){
            tAddress.setUserId(null);
            return tAddressMapper.selectTAddressList(tAddress);
        }
        return tAddressMapper.selectTAddressList(tAddress);
    }

    /**
     * 新增收货地址
     * 
     * @param tAddress 收货地址
     * @return 结果
     */
    @Override
    public int insertTAddress(TAddress tAddress)
    {
        tAddress.setCreatedTime(new Date());

        // 查询用户是否已有收货地址
        TAddress query = new TAddress();
        query.setUserId(tAddress.getUserId());
        List<TAddress> existingAddresses = tAddressMapper.selectTAddressList(query);

        if (existingAddresses.isEmpty()) {
            // 如果没有地址，设置为默认地址
            tAddress.setIsDefault(1L);
        } else {
            // 如果用户希望将新地址设为默认
            if (tAddress.getIsDefault() != null && tAddress.getIsDefault() == 1L) {
                // 查找当前默认地址
                TAddress defaultQuery = new TAddress();
                defaultQuery.setUserId(tAddress.getUserId());
                defaultQuery.setIsDefault(1L);
                List<TAddress> defaultAddresses = tAddressMapper.selectTAddressList(defaultQuery);

                if (!defaultAddresses.isEmpty()) {
                    // 将原有默认地址设为非默认
                    TAddress oldDefault = defaultAddresses.get(0);
                    oldDefault.setIsDefault(0L);
                    oldDefault.setModifiedTime(new Date());
                    tAddressMapper.updateTAddress(oldDefault);
                }
                // 新地址设为默认
                tAddress.setIsDefault(1L);
            } else {
                // 如果用户没有指定默认，设为非默认
                tAddress.setIsDefault(0L);
            }
        }

        return tAddressMapper.insertTAddress(tAddress);
    }

    /**
     * 修改收货地址
     * 
     * @param tAddress 收货地址
     * @return 结果
     */
    @Override
    public int updateTAddress(TAddress tAddress)
    {
        tAddress.setModifiedTime(new Date());

        // 如果用户希望将此地址设为默认
        if (tAddress.getIsDefault() != null && tAddress.getIsDefault() == 1L) {
            // 查找当前默认地址
            TAddress defaultQuery = new TAddress();
            defaultQuery.setUserId(tAddress.getUserId());
            defaultQuery.setIsDefault(1L);
            List<TAddress> defaultAddresses = tAddressMapper.selectTAddressList(defaultQuery);

            if (!defaultAddresses.isEmpty()) {
                TAddress oldDefault = defaultAddresses.get(0);
                // 如果原有默认地址不是当前要修改的地址
                if (!oldDefault.getAid().equals(tAddress.getAid())) {
                    // 将原有默认地址设为非默认
                    oldDefault.setIsDefault(0L);
                    oldDefault.setModifiedTime(new Date());
                    tAddressMapper.updateTAddress(oldDefault);
                }
            }
            // 确保当前地址设为默认
            tAddress.setIsDefault(1L);
        }

        return tAddressMapper.updateTAddress(tAddress);
    }

    /**
     * 批量删除收货地址
     * 
     * @param aids 需要删除的收货地址主键
     * @return 结果
     */
    @Override
    public int deleteTAddressByAids(Long[] aids)
    {
        return tAddressMapper.deleteTAddressByAids(aids);
    }

    /**
     * 删除收货地址信息
     * 
     * @param aid 收货地址主键
     * @return 结果
     */
    @Override
    public int deleteTAddressByAid(Long aid)
    {
        return tAddressMapper.deleteTAddressByAid(aid);
    }

    /**
     * 获取用户默认收货地址
     *
     * @param userId 用户ID
     * @return 默认收货地址
     */
    @Override
    public TAddress getDefaultAddressByUserId(Long userId)
    {
        TAddress query = new TAddress();
        query.setUserId(userId);
        query.setIsDefault(1L); // 1表示默认地址

        List<TAddress> addresses = tAddressMapper.selectTAddressList(query);
        if (addresses.isEmpty()) {
            return null;
        }
        return addresses.get(0);
    }
}
