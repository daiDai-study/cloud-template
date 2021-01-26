# Spring Cloud 微服务基础模板项目

## 技术
采用 Spring Cloud + Spring Cloud Alibaba 的最新技术搭建
如：Nacos、Sentinel

## 模板项目组成
+ cloud-common:作为基本公用模块，供其他所有模块使用
+ cloud-gateway:作为网关微服务，集成Shiro的登录认证功能，提供接口认证和API统一入口的功能
+ cloud-system:作为整个系统的系统管理的微服务，主要实现了用户登录、用户管理、角色管理、菜单管理和字典管理四个基础功能
+ cloud-performance:第一个业务微服务（绩效微服务），提供绩效录入、绩效确认、绩效审核和绩效展示的功能
+ cloud-project:第二个业务微服务（项目微服务），提供项目管理、项目成员管理、项目成本统计展示的功能