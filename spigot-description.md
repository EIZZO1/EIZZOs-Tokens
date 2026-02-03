[CENTER]
[SIZE=7][COLOR=#FFD700][B]🪙 EIZZOs-Tokens[/B][/COLOR][/SIZE]
[SIZE=5][I]The Ultimate High-Performance Multi-Currency System[/I][/SIZE]

[IMG]https://img.shields.io/badge/Version-1.0-orange[/IMG] [IMG]https://img.shields.io/badge/Java-21-blue[/IMG] [IMG]https://img.shields.io/badge/Paper-1.21.1-green[/IMG]

[HR][/HR]
[/CENTER]

[SIZE=6][COLOR=#FFA500]✨ Overview[/COLOR][/SIZE]
[B]EIZZOs-Tokens[/B] is a robust and scalable economy solution designed for modern Minecraft networks. Whether you need a simple "Vote Points" system or a complex network-wide "Crystals" economy, this plugin provides the tools to manage it all with zero impact on server performance.

[SIZE=6][COLOR=#FFA500]🚀 Key Features[/COLOR][/SIZE]
[LIST]
[*]💎 [B]Multi-Token Support[/B] - Create unlimited types of virtual currencies.
[*]⚡ [B]Asynchronous Core[/B] - All database operations (SQLite or MariaDB) happen off-thread.
[*]🖥️ [B]Dynamic GUIs[/B] - Beautiful built-in menus for players to view balances and admins to manage tokens.
[*]🔗 [B]Deep Integration[/B] - Seamlessly hooks into [B]Vault[/B] and [B]PlaceholderAPI[/B].
[*]🗄️ [B]Dual Storage[/B] - Choose between local SQLite for easy setup or MariaDB for high-performance networks.
[*]🛡️ [B]Admin Tools[/B] - Full command-line and GUI support for managing player balances.
[/LIST]

[SIZE=6][COLOR=#FFA500]📜 Commands & Permissions[/COLOR][/SIZE]
[TABLE]
[TR]
[TD][B]Command[/B][/TD]
[TD][B]Description[/B][/TD]
[TD][B]Permission[/B][/TD]
[/TR]
[TR]
[TD]/tokens[/TD]
[TD]Open your token wallet[/TD]
[TD][I]None[/I][/TD]
[/TR]
[TR]
[TD]/tokens balance [id][/TD]
[TD]Check specific balance[/TD]
[TD][I]None[/I][/TD]
[/TR]
[TR]
[TD]/tokens admin[/TD]
[TD]Admin Management Menu[/TD]
[TD]eizzotokens.admin[/TD]
[/TR]
[TR]
[TD]/tokens give <p> <id> <q>[/TD]
[TD]Add tokens to a player[/TD]
[TD]eizzotokens.admin[/TD]
[/TR]
[TR]
[TD]/tokens take <p> <id> <q>[/TD]
[TD]Remove tokens from a player[/TD]
[TD]eizzotokens.admin[/TD]
[/TR]
[/TABLE]

[SIZE=6][COLOR=#FFA500]📊 Placeholders[/COLOR][/SIZE]
[I]Requires PlaceholderAPI[/I]
[LIST]
[*][CODE]%eizzotokens_balance_<token_id>%[/CODE] - Player balance.
[*][CODE]%eizzotokens_name_<token_id>%[/CODE] - Display name of the token.
[/LIST]

[SIZE=6][COLOR=#FFA500]⚙️ Configuration Preview[/COLOR][/SIZE]
[SPOILER="Click to view config.yml"]
[CODE=yaml]
database:
  type: "sqlite" # Options: mariadb, sqlite
  host: "localhost"
  port: 3306
  database: "eizzostokensdb"
  username: "root"
  password: ""
  pool-size: 10
[/CODE]
[/SPOILER]

[CENTER]
[HR][/HR]
[SIZE=4][I]Documentation and Source Code available on [URL='https://github.com/EIZZO1/EIZZOs-Tokens']GitHub[/URL].[/I][/SIZE]
[/CENTER]
