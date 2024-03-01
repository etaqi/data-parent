package com.tdp.data.web.service;

import lombok.Data;

@Data
public class QueueModel {

    /**
     * 消费者个数
     */
    private int consumers;

    /**
     * 准备发送给客户端的消息个数
     */
    private int messages_ready;

    /**
     * 发送给客户端尚未回应的消息个数
     */
    private int messages_unacknowledged;

    /**
     * 准备发送给客户端和末应答消息的总和
     */
    private int messages;
}
