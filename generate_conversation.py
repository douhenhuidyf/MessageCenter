import json
import os
import random

one_minute = 60 * 1000  # milliseconds
one_hour = 60 * one_minute
one_day = 24 * one_hour
yesterday = 2 * one_day
seven_days = 7 * one_day

time_offset = [
    0, one_minute, one_hour, one_day, yesterday, seven_days, seven_days * 10
]

if __name__ == "__main__":
    project_dir = os.getcwd()
    mock_data_dir = os.path.join(project_dir, "app", "src", "main", "assets",
                                 "mock_data")

    with open(os.path.join(mock_data_dir, "mock_contacts.json"),
              "r",
              encoding="utf-8") as file:
        contacts = json.load(file)

    num_contacts = len(contacts)
    conversations = []
    for contact in contacts:
        for l, r in zip(time_offset, time_offset[1:]):
            for _ in range(2):
                timestamp_offset = int(random.randint(l, r - 1))
                days = timestamp_offset / 1000 // (24 * 3600)
                remaining = timestamp_offset / 1000 % (24 * 3600)
                hours = remaining // 3600
                remaining %= 3600
                minutes = remaining // 60
                seconds = remaining % 60

                time_offset_string = ""
                if days > 0:
                    time_offset_string += f"{days:.0f}d "
                if hours > 0:
                    time_offset_string += f"{hours:.0f}h "
                if minutes > 0:
                    time_offset_string += f"{minutes:.0f}m "
                if seconds > 0:
                    time_offset_string += f"{seconds:.0f}s"

                conversation = {
                    "conversationId": contact["contactId"],
                    "senderName": contact["contactName"],
                    "receiverName": "USER",
                    "messageText":
                    f"Message from {contact['contactName']} to you before {time_offset_string}! Message from {contact['contactName']} to you before {time_offset_string}!",
                    "timestampOffset": timestamp_offset
                }
                conversations.append(conversation)

    with open(os.path.join(mock_data_dir, "mock_messages.json"),
              "w",
              encoding="utf-8") as file:
        json.dump(conversations, file, indent=4, ensure_ascii=False)
