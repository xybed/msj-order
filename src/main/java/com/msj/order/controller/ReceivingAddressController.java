package com.msj.order.controller;

import com.msj.common.constant.Result;
import com.msj.common.constant.ResultEnum;
import com.msj.common.constant.ResultGenerator;
import com.msj.common.entity.ReceivingAddress;
import com.msj.order.annotation.NeedLogin;
import com.msj.order.service.ReceivingAddressService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class ReceivingAddressController {

    @Resource
    private ReceivingAddressService receivingAddressService;

    @NeedLogin
    @RequestMapping(value = "/addresses", method = RequestMethod.GET)
    public Result getReceivingAddress(@RequestParam("user_id") Integer userId){
        if(StringUtils.isEmpty(userId)){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        return ResultGenerator.genSuccessResult(receivingAddressService.getReceivingAddress(userId));
    }

    @NeedLogin
    @RequestMapping(value = "/address", method = RequestMethod.POST)
    public Result addReceivingAddress(@RequestBody ReceivingAddress receivingAddress){
        if(StringUtils.isEmpty(receivingAddress.getUserId()) ||
                StringUtils.isEmpty(receivingAddress.getName()) ||
                StringUtils.isEmpty(receivingAddress.getPhone()) ||
                StringUtils.isEmpty(receivingAddress.getProvince()) ||
                StringUtils.isEmpty(receivingAddress.getCity()) ||
                StringUtils.isEmpty(receivingAddress.getAddress())){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        receivingAddressService.addReceivingAddress(receivingAddress);
        return ResultGenerator.genSuccessResult("添加成功");
    }

    @NeedLogin
    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public Result updateReceivingAddress(@RequestBody ReceivingAddress receivingAddress){
        if(StringUtils.isEmpty(receivingAddress.getId()) ||
                StringUtils.isEmpty(receivingAddress.getUserId()) ||
                StringUtils.isEmpty(receivingAddress.getName()) ||
                StringUtils.isEmpty(receivingAddress.getPhone()) ||
                StringUtils.isEmpty(receivingAddress.getProvince()) ||
                StringUtils.isEmpty(receivingAddress.getCity()) ||
                StringUtils.isEmpty(receivingAddress.getAddress())){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        receivingAddressService.updateReceivingAddress(receivingAddress);
        return ResultGenerator.genSuccessResult("更新成功");
    }

    @NeedLogin
    @RequestMapping(value = "/addresses/{id}", method = RequestMethod.DELETE)
    public Result deleteReceivingAddress(@PathVariable Integer id){
        if(StringUtils.isEmpty(id)){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        receivingAddressService.deleteReceivingAddress(id);
        return ResultGenerator.genSuccessResult("删除成功");
    }

    @RequestMapping(value = "/address", method = RequestMethod.GET)
    public Result getDefaultReceivingAddress(@RequestParam("user_id") Integer userId){
        if(StringUtils.isEmpty(userId)){
            return ResultGenerator.genFailResult(ResultEnum.PARAM_ERROR);
        }
        return ResultGenerator.genSuccessResult(receivingAddressService.getDefaultReceivingAddress(userId));
    }
}
