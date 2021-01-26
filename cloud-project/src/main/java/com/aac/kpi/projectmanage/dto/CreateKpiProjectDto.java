package com.aac.kpi.projectmanage.dto;

import com.aac.kpi.projectmanage.entity.KpiProject;
import lombok.Data;

import java.util.List;

@Data
public class CreateKpiProjectDto extends KpiProject {

    // 以下都是项目成员信息
    List<String> archs;
    List<String> sms;
    List<String> pos;
    List<String> bts;
    List<String> uis;
    List<String> des;
    List<String> devs;

    List<RoleUser> roleUsers;

    @Data
    public static class RoleUser {
        private List<String> users;
        private String role;
        private Integer id;
    }


}
