# Revolution
Revolution is a project intended to replace the aging codebase of Clockwork (which itself was a fork of a fairly old
version of the TotalFreedomMod). It is still in a work in progress and is not ready for production use at this moment in
time.

## Project Structure
Unlike Clockwork, Revolution is split up into multiple subproject with each one geared to replicate the functions of a
particular component of Clockwork.
* **Administration** contains admin commands and features like Command Spy, Admin Chat, bans, mutes, and inventory
viewing.
* **Basics** contains more functional commands (such as vanilla replacement commands) and aims to replicate
functionality from plugins like Essentials.
* **Capitalism** contains functionality like balances and economies. Supports the Vault API.
* **Dimension** contains a custom world system along with basic protection functionality. Supports most world generator
plugins.
* **Foundation** is exactly what it says, the foundation of Revolution and its subprojects. It doesn't contain any
features on its own aside from a message system, a player data system, and a command system.
* **Integration** integrates some of Revolution's functions into other plugins optionally. **It currently only
integrates into DiscordSRV in a limited capacity for the admin chat, and will likely going be removed in the future.**
* **Regulation** regulates vanilla gameplay mechanics (such as custom spawn eggs, explosives, etc) and includes
protection against certain hacks like spammers and nukers.

## Building
TBD
