# Telegram-Voting-Bot

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5bdf2233088b4f0db5548d337bf7c78c)](https://app.codacy.com/manual/Munoon/Telegram-Voting-Bot?utm_source=github.com&utm_medium=referral&utm_content=Munoon/Telegram-Voting-Bot&utm_campaign=Badge_Grade_Dashboard)

Bot for voting using telegram

## How to launch
1. Setup files `src/main/resources/application.properties`, `src/main/resources/players.json` and `src/main/resources/keys.txt`.
2. Open console in project's folder.
3. Package project using `$ mvn clean package`.
4. Open created folder `target` in cosole using command `$ cd target`.
5. Run project using command `$ java -jar bank.jar`.

### How to setup `application.properties`
1. Open file `src/main/resources/application.properties`.
2. Open chat in telegram with [@BotFather](https://t.me/BotFather).
3. Press start.
4. Send command `/newbot`, then type bot's name.
5. Copy HTTP Api token from bot's answer and past it to third line in opened file.
6. If you want, type admin chat id on seventh line. You may get it from console after launch application.

### How to setup `players.json`
1. Open file `src/main/resources/players.json`.
2. This file use json format. Type massives to root massive. Players name should by typed in `"` bracket.
3. Each massive - new line in telegram button.

### How to setup `keys.txt`
1. Open file `src/main/resources/keys.txt`.
2. Type each key from new line.

## Requirement
1. Java 11
2. Maven