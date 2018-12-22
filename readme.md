# MCpal - A lightweight Minecraft Server manager :)

## What does it do?
- Automatic backups once a day (currently at 4am)
- Extendes features (while fully supporting the native commands) like manual backups
- It's possible to automatically run other programs (like Overviewer to generate a map out of your world) after each backup

## What are the benefits?
- Small application (<20 KB)
- Designed to provide function without a loss of performance
- Automate the things you would need to do by yourself otherwise
- Simplicity

## You are a potential user if...
- You only host one world
- You like to concentrate your system performance fully on minecraft instead of supporting tools
- Your server doesn't have that much power
- Other tools you know are to complicated

## Future ideas:
- [x] Implement automatic server backups.
- [x] Implement additional parameters to run other programs (like Overviewer).
- [x] Implement dynamic parameters to use, such as the world-name or the current backup path.
- [x] Implement the option to run external programs with parameters after a backup.
- [ ] Give an optional time-parameter when the backup starts.
- [ ] Add the possibility to do multiple automatic backups a day.
- [ ] Space management: Set a size of the backup folder and automatically delete the oldest backups.
- [ ] Make a "once per month" and "once per year" backup.


## Install & Run:
**!! IMPORTANT !!**
- (CAPS LOCK words need to be replaced with your own data)
- If a path contains whitespaces (that's what the space-key creates), you need to wrap "" around it
- You must use "/" in paths, even on windows, where "\" is the standard.

1. Download "MCpal.jar"
2. Move it into your minecraft server directory
3. Open a console window and jump to the specific server directory (example: cd /home/USERNAME/minecraft_server/)
4. Now run "java -jar MCpal.jar b:PATH_TO_BACKUP_FOLDER r:RAM_TO_ALLOCATE j:MINECRAFT_SERVER.JAR
Example: java -jar MCpal.jar b:C:/Users/Potato/mc_backups r:1024 j:minecraft_server.jar
5. A config file will be generated, which means that from now on you can call "java -jar MCpal.jar" after the first run.

## Installation help:

**Example 1 (Linux):** java -jar /home/trudler/minecraft_server/MCpal.jar b:/media/trudler/HDD/mc_backups r:1024 j:minecraft_server.jar

**Example 2 (Windows):** java -jar C:/Users/trudler/Desktop/minecraft_server.jar b:C:/users/trudler/Desktop/mc_backups r:1024 j:minecraft_server.jar

**Example 3 (only for experienced users with custom needs, lol):** java -jar /home/trudler/minecraft_server/MCpal.jar b:/media/trudler/HDD/mc_backups r:1024 j:minecraft_server.jar "a:overviewer.py --config=/home/trudler/minecraft/Overviewer/overworld.conf"
(I installed Minecraft Overviewer and the server.jar updates our map once a day. [See here](https://overviewer.org/example/#/-310/64/90/-4/0/0) )


## Running MCpal including external programs (optional):
This is a feature to primarily support generating a "google map" out of you minecraft world after each backup. You can find an
example here: https://overviewer.org/example/#/-310/64/90/-4/0/0
After MCpal finished a backup, it restarts the server and begins to run the additional programs that have been appended
to it.

You can add additional programs by using the parameter "a:" and append it. My setup looks like this:
"a:overviewer.py --renderingmodes=smooth-lighting {2}/Galamor /home/Potato/Desktop/MC-Map"

Let me explain a few things here:
- Additinal parameters can exist multiple times. If you want to run more than one program, just create another "a:XXXX" command.
- The additional path needs to be within "" because it will contain whitespaces. Therefore, all paths that are used within must not contain whitespaces.
- {2} is a dynamic parameter. It does contain the path to the newest backup folder. At all times when it is called, it will contain the very latest backup path. The available dynamic parameters are listed in the next chapter.

## Dynamic parameters:
You can use dynamic parameters within your "a:XXXX" arguments.</br>
{1} = The name of the world</br>
{2} = The path to the most current backup</br>
