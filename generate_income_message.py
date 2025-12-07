import json
import os
import random

IMAGE_URLS = [
    "https://img.cdn1.vip/i/6935470c16799_1765099276.webp",
    "https://img.cdn1.vip/i/6935470a3af33_1765099274.webp",
    "https://img.cdn1.vip/i/6935470a08f2f_1765099274.webp",
    "https://img.cdn1.vip/i/69354707c858b_1765099271.webp",
    "https://img.cdn1.vip/i/6935470769233_1765099271.webp",
    "https://img.cdn1.vip/i/693547036c501_1765099267.webp",
    "https://img.cdn1.vip/i/693546fedc57b_1765099262.webp",
    "https://img.cdn1.vip/i/693546fc5bff9_1765099260.webp",
    "https://img.cdn1.vip/i/693546fb1d2ab_1765099259.webp"
]

system_contacts = ["每日签到", "热门推荐", "在线商城"]

system_messages = ["【新客专享】您好！您有一份「新用户专属礼包」待查收：注册即享 [50 元无门槛券] + 首单额外 9 折，点击领取→[立即注册]，有效期仅 3 天！",
"【好友邀请福利】邀请 1 位好友注册并完成首单，您和好友各得 [20 元现金红包] （满 20 元可提现），好友越多奖励越多，点击生成邀请链接→[邀请好友]",
"【首次体验特惠】新用户专享！首次购买「[年度会员]」仅需 [99 元 / 年] （原价 199 元），开通即享 [免费数据导出 + 优先客服] 权益，点击锁定优惠→[立即开通]",
"【您的专属优惠即将到期】您于 [3 天前] 加入购物车的「[无线蓝牙耳机]」，专属折扣价 [149 元] （原价 199 元）将于 24 小时后失效，点击抢购→[去结算]",
"【限时秒杀提醒】您关注的「[2024 新款连衣裙]」今日 10:00 开启秒杀，限量 50 件，秒杀价 [89 元] （直降 60 元），点击设置开抢提醒→[提醒我]",
"【未完成订单提醒】您有一笔未完成订单（订单号 #[82910]）：「[办公笔记本电脑]」，当前库存仅剩 3 台，支付立减 [100 元] ，点击继续支付→[去付款]",
"【个性化推荐福利】根据您的浏览偏好，为您推荐「[智能手表]」，今日下单赠 [免费表带 + 1 年保修] ，点击查看详情→[去了解]"]

if __name__ == "__main__":
    project_dir = os.getcwd()
    mock_data_dir = os.path.join(project_dir, "app", "src", "main", "assets",
                                 "mock_data")
    received_data_dir = os.path.join(project_dir, "app", "src", "main",
                                     "assets", "received_data")
    os.makedirs(mock_data_dir, exist_ok=True)
    os.makedirs(received_data_dir, exist_ok=True)

    with open(os.path.join(mock_data_dir, "mock_contacts.json"),
              "r",
              encoding="utf-8") as f:
        contacts = json.load(f)
    with open(os.path.join(project_dir, "income_messages.txt"),
              "r",
              encoding="utf-8") as f:
        income_messages = [line.strip() for line in f.readlines()]
    random.shuffle(income_messages)

    contact_list = [(contact["contactName"], contact["contactId"])
                    for contact in contacts[:90]]
    system_contacts = [(name, idx + len(contact_list))
                       for idx, name in enumerate(system_contacts)]
    messages = []
    for income_message in income_messages:
        senderName, senderId = random.choice(contact_list)

        rand_val = random.random()
        msg_type = 0
        extra_data = None
        message_text = income_message

        if rand_val < 0.25:
            msg_type = 1
            message_text = "[图片]"
            extra_data = random.choice(IMAGE_URLS)
        elif rand_val < 0.5:
            senderName, senderId = random.choice(system_contacts)
            msg_type = 2
            message_text = random.choice(system_messages)
            extra_data = "点击查看详情"

        message = {
            "contactName": senderName,
            "senderId": senderId + 1,
            "receiverId": 0,
            "messageText": message_text,
            "msgType": msg_type,
            "extraData": extra_data
        }
        messages.append(message)

    with open(os.path.join(received_data_dir, "income_messages.json"),
              "w",
              encoding="utf-8") as f:
        json.dump(messages, f, ensure_ascii=False, indent=4)
