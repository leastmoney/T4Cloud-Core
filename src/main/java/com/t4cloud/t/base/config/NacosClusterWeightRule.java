//package com.t4cloud.t.base.config;
//
//import com.netflix.client.config.IClientConfig;
//import com.netflix.loadbalancer.AbstractLoadBalancerRule;
//import com.netflix.loadbalancer.Server;
//
///**
// * NacosClusterChoose
// * 自定义负载均衡方式，这里主要是为了实现同组调用
// * <p>
// * ---------------------
// *
// * @author TeaR
// * @date 2020/3/2 16:04
// */
//public class NacosClusterWeightRule extends AbstractLoadBalancerRule {
//    @Override
//    public void initWithNiwsConfig(IClientConfig iClientConfig) {
//
//    }
//
//    @Override
//    public Server choose(Object o) {
//        //选择所有实例，然后过滤出同组实例，最后采用负载均衡
//        /** TODO 额，留个坑，以后填，这边需要加载nacos服务的元数据，通过元数据中的属性判断。元数据也是需要在启动的时候配置。
//          *
//          * -by TeaR  -2020/3/2-17:00
//          */

//
//        return null;
//    }
//}
