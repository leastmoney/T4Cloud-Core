package com.t4cloud.t.base.utils;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import javax.mail.*;
import java.io.IOException;
import java.util.Properties;

/**
 * EmailImapReceiveUtil
 * <p>
 * 邮件接收工具类 - IMAP方式
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2021/7/13 15:33
 */
public class EmailImapReceiveUtil {

    /**
     * 获取指定邮箱所有邮件
     *
     * @param user    账号
     * @param pwd     密码
     * @param host    邮件服务器
     * @param handler 邮件处理方法
     *                <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/7/13 15:41
     */
    public static void handleMessages(String user, String pwd, String host, MessageHandler handler) throws MessagingException, IOException {
        //连接邮箱
        IMAPFolder inbox = getFolder(user, pwd, host, "INBOX");
        inbox.open(Folder.READ_WRITE);

        //获取邮件
        Message[] messages = inbox.getMessages();

        //处理未读消息
        handler.handler(messages);

        //释放资源
        closeFolder(inbox);
    }

    /**
     * 获取指定邮箱未读邮件
     *
     * @param user    账号
     * @param pwd     密码
     * @param host    邮件服务器
     * @param handler 邮件处理方法
     *                <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/7/13 15:42
     */
    public static void handleUnreadMessages(String user, String pwd, String host, MessageHandler handler) throws MessagingException, IOException {

        //连接邮箱
        IMAPFolder inbox = getFolder(user, pwd, host, "INBOX");
        inbox.open(Folder.READ_WRITE);

        //获取邮件
        Message[] messages = inbox.getMessages(inbox.getMessageCount() - inbox.getUnreadMessageCount() + 1, inbox.getMessageCount());

        //处理未读消息
        handler.handler(messages);

        //释放资源
        closeFolder(inbox);
    }

    /**
     * 将邮件标记为已读
     *
     * @param msg 邮件对象
     *            <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/7/13 15:44
     */
    public static void markRead(Message msg) throws MessagingException {
        msg.setFlag(Flags.Flag.SEEN, true);
    }

    /**
     * 获得邮件文本内容
     *
     * @param part    邮件体
     * @param content 存储邮件文本内容的字符串
     *                <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/7/13 15:55
     */
    public static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, content);
            }
        }
    }


    // ----------------------------------------------- private func -----------------------------------------------

    /**
     * 获取邮箱文件夹
     *
     * @param user       账号
     * @param pwd        密码
     * @param host       邮件服务器
     * @param folderName 文件夹名
     *                   <p>
     * @return com.sun.mail.imap.IMAPFolder
     * --------------------
     * @author TeaR
     * @date 2021/7/13 15:36
     */
    private static IMAPFolder getFolder(String user, String pwd, String host, String folderName) throws MessagingException {
        Properties prop = System.getProperties();
        prop.put("mail.store.protocol", "imap");
        prop.put("mail.imap.host", host);
        Session session = Session.getInstance(prop);
        // 使用imap会话机制，连接服务器
        IMAPStore store = (IMAPStore) session.getStore("imap");
        store.connect(user, pwd);
        //获取指定文件夹，并返回
        return (IMAPFolder) store.getFolder(folderName);
    }
    
    /**
     * 释放资源
     *
     * @param folder 打开的邮箱目录
     *               <p>
     * @return com.sun.mail.imap.IMAPFolder
     * --------------------
     * @author TeaR
     * @date 2021/7/13 16:07
     */
    private static void closeFolder(IMAPFolder folder) throws MessagingException {
        // 释放资源
        Store store = folder.getStore();
        if (folder != null) {
            folder.close(true);
        }
        if (store != null) {
            store.close();
        }
    }

    /**
     * 内部实现接口，处理邮件内容
     * <p>
     * --------------------
     *
     * @author TeaR
     * @date 2021/7/13 16:11
     */
    public interface MessageHandler {
        void handler(Message[] messages) throws MessagingException, IOException;
    }

}
