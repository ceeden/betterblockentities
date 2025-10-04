## Better Block Entities

BBE is a client side mod that adds upon Sodium's Block Entity Rendering Pipeline inorder to improve performance.
It works by rendering Block Entities statically in Chunk Meshes (just like regular blocks), and when they are animating 
the mod switches rendering to be done regularly via the Block Entity Pipeline therefor preserving animations and maximizing
performance<br/><br/>
This mod is designed to be very light weight and not too integrated with **[Sodium](https://modrinth.com/mod/sodium)** and **[Fabric](https://fabricmc.net/use/)** to make it easy to update between
minecraft versions<br/><br/>
## Mod Dependancies
--**[Fabric API](https://fabricmc.net/use/)**<br/>
--**[Sodium](https://modrinth.com/mod/sodium)**<br/>
--**[ModMenu](https://modrinth.com/mod/modmenu)** (Optional)

## Current Mod Status
The mod is still in a core developing stage so there may be bugs and such that has not yet been discovered. Because Im currently 
alone on this project and low on free time I can't promise super fast updates and bug fixes. I will be working on the mod when I can
and review pull requests to further improve upon the mod.<br/><br/>
Some things that currently has to be fixed: <br/>
-Adding support for vanilla Fabric<br/>
-Fix Bell Floor meshed model
-Fix lighting bug for all block entities
-Add support for more block entities
