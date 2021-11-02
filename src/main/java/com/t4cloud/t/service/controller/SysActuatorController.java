package com.t4cloud.t.service.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.t4cloud.t.base.annotation.AutoLog;
import com.t4cloud.t.base.entity.RedisInfo;
import com.t4cloud.t.base.entity.SystemLogRequest;
import com.t4cloud.t.base.entity.dto.R;
import com.t4cloud.t.base.utils.RedisUtil;
import com.t4cloud.t.base.utils.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;

import static com.t4cloud.t.base.constant.CacheConstant.*;

/**
 * 菜单权限表 控制器
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-13
 */
@Slf4j
@RestController
@Api(value = "服务资源监控", tags = "运行资源监控", position = 100)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/actuator")
@ConditionalOnProperty(value = "t4cloud.actuator.open", havingValue = "true")
public class SysActuatorController {

    private final DiscoveryClient discoveryClient;
    private final RedisConnectionFactory redisConnectionFactory;
    @Value("${spring.application.name}")
    private String application;

    @Autowired
    private com.t4cloud.t.service.service.IT4CommonService commonService;

    // ----------------------------------------------- CLOUD -----------------------------------------------
    @AutoLog(value = "获取现有可用的微服务", operateType = 4)
    @GetMapping("/serviceList")
    @ApiOperation(position = 1, value = "获取现有可用的微服务", notes = "获取现有可用的微服务，然后拼接上例如druid/login.html，就能访问到对应的监控页面")
    public R<?> serviceList() {
        List<JSONObject> result = new ArrayList<>();
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            if (instances.size() != 0) {
                JSONObject object = new JSONObject();
                object.put("name", service);
                object.put("number", instances.size());
                result.add(object);
            }
        }
        return R.ok("获取现有可用的微服务列表成功", result);
    }


    // ----------------------------------------------- REDIS -----------------------------------------------

    @AutoLog(value = "获取redis的运行信息", operateType = 4)
    @GetMapping("/redis/info")
    @ApiOperation(position = 2, value = "获取redis的运行信息", notes = "获取redis的的实时运行信息")
    public R<?> redisInfo() {

        Properties info = redisConnectionFactory.getConnection().info();
        List<RedisInfo> infoList = new ArrayList<>();
        RedisInfo redisInfo = null;
        for (Map.Entry<Object, Object> entry : info.entrySet()) {
            redisInfo = new RedisInfo();
            redisInfo.setKey(StringUtils.trim(entry.getKey() + ""));
            redisInfo.setValue(StringUtils.trim(entry.getValue() + ""));

            if (!StringUtils.isEmpty(redisInfo.getDescription())) {
                //没有注解的就不要了
                infoList.add(redisInfo);
            }
        }
        infoList.sort(Comparator.comparingInt(RedisInfo::getSort));
        return R.ok("获取redis的运行信息成功", infoList);
    }


    @AutoLog(value = "获取redis实时存储信息", operateType = 4)
    @GetMapping("/redis/liveInfo")
    @ApiOperation(position = 3, value = "获取redis实时存储信息", notes = "获取redis实时存储信息")
    public R<?> redisLiveInfo() {
        //缓存中读取（30分钟内的数据。一分钟一个）
        Object result = RedisUtil.get(SYS_REDIS_MONITOR + application);
        return R.ok("获取redis实时存储信息成功", result);
    }

    // ----------------------------------------------- SYSTEM -----------------------------------------------

    @AutoLog(value = "获取当前系统的磁盘空间", operateType = 4)
    @ApiOperation(position = 4, value = "获取当前系统的磁盘空间", notes = "获取当前系统的磁盘空间")
    @GetMapping("/system/diskInfo")
    public R<?> systemDiskInfo() {
        JSONObject result = new JSONObject();
        //查询磁盘信息
        try {
            // 当前文件系统类
            FileSystemView fsv = FileSystemView.getFileSystemView();
            // 列出所有windows 磁盘
            File[] fs = File.listRoots();
            log.info("查询磁盘信息:" + fs.length + "个");
            List<Map<String, Object>> diskList = new ArrayList<>();
            for (int i = 0; i < fs.length; i++) {
                if (fs[i].getTotalSpace() == 0) {
                    continue;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("name", fsv.getSystemDisplayName(fs[i]));
                map.put("max", fs[i].getTotalSpace() / 1024 / 1024 / 1024);
                map.put("rest", fs[i].getFreeSpace() / 1024 / 1024 / 1024);
                map.put("restPPT", (fs[i].getTotalSpace() - fs[i].getFreeSpace()) * 100 / fs[i].getTotalSpace());
                diskList.add(map);
                log.info(map.toString());
            }
            result.put("diskInfo", diskList);
        } catch (Exception e) {
            R.error("查询磁盘信息失败" + e.getMessage());
        }

        return R.ok("查询磁盘信息成功！", result);
    }

// ----------------------------------------------- JVM -----------------------------------------------

    @AutoLog(value = "获取JVM实时存储信息", operateType = 4)
    @GetMapping("/jvm/liveInfo")
    @ApiOperation(position = 3, value = "获取Jvm实时存储信息", notes = "获取Jvm实时存储信息")
    public R<?> jvmLiveInfo() {
        //缓存中读取（60分钟内的数据。一分钟一个）
        Object result = RedisUtil.get(SYS_JVM_MONITOR + application);
        return R.ok("获取Jvm实时存储信息成功", result);
    }


    // ----------------------------------------------- SYSTEMLOG -----------------------------------------------

    @AutoLog(value = "获取请求数据统计信息", operateType = 4)
    @GetMapping("/systemLog/request")
    @ApiOperation(position = 3, value = "获取请求数据统计信息", notes = "获取请求数据统计信息")
    public R<?> systemLogRequest() {
        //先从Redis中获取，保留时间为1分钟，如果没有，再从数据库中查询在放入redsi中
        List<SystemLogRequest> result = (List<SystemLogRequest>)RedisUtil.get(SYS_LOG_MONITOR + application);
        if(null == result){
            //说明没有
            //获取用户数据权限列表
            List<String> dataRuleList = UserUtil.getDataRuleList();
            //如果没有就报错
            if(null == dataRuleList){
                //没有权限的人
                R.error("请先登录");
            }

            //创建租户idStringBuffer
            StringBuffer sb = new StringBuffer();
            for (String s : dataRuleList) {
                sb.append("'" + s + "',");
            }
            String s = sb.toString();
            String tenantIds = s.substring(0, s.length() - 1);

            //设置时间,开始时间和结束时间
            Date now = DateUtil.date();
            //查询数据
            //每一天都去用sql查询，防止某一天没有数据，则list中会少数据
            List<SystemLogRequest> systemLogRequestList = new LinkedList<>();
            for (int i = 6; i >= 0 ; i--) {
                //用做最终放入list的对象
                DateTime startTime = DateUtil.offsetDay(DateUtil.beginOfDay(now), -i);
                String startDate = DateFormatUtils.format(startTime, "yyyy-MM-dd 00:00:00");
                String endDate = DateFormatUtils.format(startTime, "yyyy-MM-dd 23:59:59");
                //查询一天的总和
                SystemLogRequest slr = commonService.countSystemLog(startDate, endDate, tenantIds);
                if(0 == slr.getRequestCount()){
                    //总量为0，说明当天没有请求量，手动塞值。
                    slr.setAbnormalRequestCount(0L)
                        .setNormalRequestCount(0L)
                        .setTimestamp(startTime.toJdkDate());
                        //加入list中
                        systemLogRequestList.add(slr);
                }else{
                    //如果查出来有，就放入systemLog中，之后还要拼接正常俩和异常量
                    //查询异常量
                    SystemLogRequest systemLogRequest = commonService.countSystemLogWithType(startDate, endDate, tenantIds, "0");
                    if(null == systemLogRequest){
                        slr.setAbnormalRequestCount(0L);
                    }else{
                        slr.setAbnormalRequestCount(systemLogRequest.getRequestCount());
                    }
                    //查询正常量
                    SystemLogRequest systemLogRequest1 = commonService.countSystemLogWithType(startDate, endDate, tenantIds, "1");
                    if(null == systemLogRequest1){
                        slr.setNormalRequestCount(0L);
                    }else{
                        slr.setNormalRequestCount(systemLogRequest1.getRequestCount());
                    }
                    //吧对象放进入list中
                    systemLogRequestList.add(slr);
                }
            }
            //赋值
            result = systemLogRequestList;

            //放入缓存
            RedisUtil.set(SYS_LOG_MONITOR + application, result, 60);
        }
        return R.ok("获取请求数据统计信息成功", result);
    }
}
