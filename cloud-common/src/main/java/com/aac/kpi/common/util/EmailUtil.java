package com.aac.kpi.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class EmailUtil {

    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String PLACEHOLDER_SUFFIX = "}";

    private static MailAccount getMailAccount(String host, String sender){
        MailAccount account = new MailAccount();
        account.setHost(host);
        account.setPort(25);
        account.setFrom(sender);
        account.setAuth(false);
        return account;
    }


    /**
     * 发送邮件-简单文本内容
     * @param host SMTP服务器域名
     * @param sender
     * @param receiver
     * @param ccs
     * @param subject
     * @param content
     */
    public static void sendSimpleMail(String host, String sender, String receiver, Collection<String> ccs, String subject, String content) {
        if (StrUtil.isEmpty(host) || StrUtil.isEmpty(sender) || StrUtil.isEmpty(receiver)) {
            return;
        }
        MailUtil.send(getMailAccount(host, sender), CollUtil.newArrayList(receiver), ccs, null, subject, content, false);
    }

    public static void sendSimpleMail(String host, String sender, String receiver, String subject, String content) {
        sendSimpleMail(host, sender, receiver, null, subject, content);
    }


    /**
     * 发送邮件-HTML文本内容
     * @param host SMTP服务器域名
     * @param sender
     * @param receiver
     * @param ccs
     * @param title
     * @param htmlContent
     */
    public static void sendHtmlMail(String host, String sender, String receiver, Collection<String> ccs, String title, String htmlContent){
        if (StrUtil.isEmpty(host) || StrUtil.isEmpty(sender) || StrUtil.isEmpty(receiver)) {
            return;
        }
        MailUtil.send(getMailAccount(host, sender), CollUtil.newArrayList(receiver), ccs, null, title, htmlContent, true);
    }

    public static void sendHtmlMail(String host, String sender, String receiver, String subject, String htmlContent) {
        sendSimpleMail(host, sender, receiver, null, subject, htmlContent);
    }

    /**
     * 批量发送邮件-HTML文本内容
     * @param host SMTP服务器域名
     * @param sender
     * @param receivers
     * @param ccs
     * @param subject
     * @param htmlContent
     */
    public static void sendHtmlMailBatch(String host, String sender, Collection<String> receivers, Collection<String> ccs, String subject, String htmlContent){
        if (CollUtil.isEmpty(receivers)) {
            return;
        }
        List<String> validReceivers = new ArrayList<>();
        for (String receiver : receivers) {
            if (StrUtil.isNotEmpty(receiver)) {
                validReceivers.add(receiver);
            }
        }
        MailUtil.send(getMailAccount(host, sender), validReceivers, ccs, null, subject, htmlContent, true);
    }

    public static void sendHtmlMailBatch(String host, String sender, Collection<String> receivers, String subject, String htmlContent) {
        sendHtmlMailBatch(host, sender, receivers, null, subject, htmlContent);
    }

    public static String resolvePlaceholders(String text, Map<String, String> parameter) {
        if (parameter == null || parameter.isEmpty()) {
            return text;
        }
        StringBuilder buf = new StringBuilder(text);
        int startIndex = buf.indexOf(PLACEHOLDER_PREFIX);
        while (startIndex != -1) {
            int endIndex = buf.indexOf(PLACEHOLDER_SUFFIX, startIndex + PLACEHOLDER_PREFIX.length());
            if (endIndex != -1) {
                String placeholder = buf.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
                int nextIndex = endIndex + PLACEHOLDER_SUFFIX.length();
                try {
                    String propVal = parameter.get(placeholder);
                    if (propVal != null) {
                        buf.replace(startIndex, endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
                        nextIndex = startIndex + propVal.length();
                    } else {
                        log.warn("Could not resolve placeholder '" + placeholder + "' in [" + text + "] ");
                    }
                } catch (Exception ex) {
                    log.warn("Could not resolve placeholder '" + placeholder + "' in [" + text + "]: " + ex);
                }
                startIndex = buf.indexOf(PLACEHOLDER_PREFIX, nextIndex);
            } else {
                startIndex = -1;
            }
        }
        return buf.toString();
    }

}
