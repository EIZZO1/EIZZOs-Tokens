# 🪙 EIZZOs-Tokens

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![API Version](https://img.shields.io/badge/Paper-1.21.1-blue.svg)](https://papermc.io/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A robust, high-performance multi-token economy system for Minecraft servers. **EIZZOs-Tokens** allows you to create and manage custom virtual currencies with ease, featuring deep integration with the most popular server utilities.

## ✨ Features

- 💎 **Multi-Currency Support**: Create as many token types as you need.
- 🗄️ **Flexible Storage**: Supports both **SQLite** for simplicity and **MariaDB** for high-performance networks.
- 🔗 **Vault Integration**: Seamlessly hooks into Vault to act as a primary or secondary economy provider.
- 📊 **PlaceholderAPI**: Display player balances anywhere with custom placeholders.
- 🖥️ **Dynamic GUIs**: Built-in interactive menus for players to view balances and admins to manage accounts.
- ⚡ **Asynchronous Operations**: Database queries are handled off-thread to ensure zero impact on server TPS.

## 🛠️ Installation

1. Download the latest `EIZZOs-Tokens.jar`.
2. Place the file into your server's `plugins/` folder.
3. Start the server to generate the default configuration.
4. Edit `plugins/EIZZOs-Tokens/config.yml` to set up your database and token types.
5. Restart or reload the plugin.

## 📜 Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/tokens` | Opens your token wallet GUI | *None* |
| `/tokens balance [id]` | Check your balance for a specific token | *None* |
| `/tokens help` | Shows command help menu | *None* |
| `/tokens admin` | Opens the Admin Management GUI | `eizzotokens.admin` |
| `/tokens give <player> <id> <qty>` | Grant tokens to a player | `eizzotokens.admin` |
| `/tokens take <player> <id> <qty>` | Remove tokens from a player | `eizzotokens.admin` |

## 🧩 Placeholders

Requires [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).

- `%eizzotokens_balance_<token_id>%` - Displays the player's balance.
- `%eizzotokens_name_<token_id>%` - Displays the display name of the token.

## ⚙️ Configuration

```yaml
database:
  type: "sqlite" # Options: mariadb, sqlite
  host: "localhost"
  port: 3306
  database: "minecraft"
  username: "root"
  password: ""
```

---
*Developed by Gemini for EIZZO.*
