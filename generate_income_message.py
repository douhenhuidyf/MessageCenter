import json
import os
import random

new_contacts = [
    "AAA饼干打孔小熊",
    "爱吸猫的小李",
    "aiyowei",
    "AlPtZn",
    "Antonia.蒙",
    "嗷。",
    "奥特曼踩单车",
    "Aspiration",
    "摆渡百度Baidu",
    "抱一下",
    "BOWEN🌟",
    "不爱吃鱼",
    "Captain",
    "Cecilia·李",
    "Christina",
    "Clara",
    "大海波涛",
    "Downforce",
    "Edward",
    "evaaa💜",
    "风声吹乱你构想",
    "过尽千帆_",
    "火花",
    "今天只做一件事",
    "坑神本坑",
    "快乐勇士",
    "老薛",
    "伦敦大爵爷",
    "南城以北花似海",
    "南烛",
    "NULL",
    "热心市民王先生",
    "-",
    "･ᴗ･꧞",
    "🤔"
]

if __name__ == "__main__":
    project_dir = os.getcwd()
    mock_data_dir = os.path.join(project_dir, "app", "src", "main", "assets",
                                 "mock_data")
    received_data_dir = os.path.join(project_dir, "app", "src", "main", "assets",
                                     "received_data")
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

    contact_list = [contact["contactName"] for contact in contacts] + new_contacts
    messages = []
    for income_message in income_messages:
        sender = random.choice(contact_list)
        message = {
            "senderName": sender,
            "receiverName": "USER",
            "messageText": income_message,
        }
        messages.append(message)

    with open(os.path.join(received_data_dir, "income_messages.json"),
              "w",
              encoding="utf-8") as f:
        json.dump(messages, f, ensure_ascii=False, indent=4)
