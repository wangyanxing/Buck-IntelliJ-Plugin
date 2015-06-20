# Buck plugin for IntelliJ

### Facebook 2015 Summer Intern Hackathon Project

Buck build system (http://buckbuild.com/) is widely used as a fast build tool especially for Android development. Unfortunately IntelliJ still has no plugin for it, therefore we plan to hack a Buck plugin for all JetBrains IDEs and raise our working efficiency.

<a href="https://www.flickr.com/photos/128908106@N06/18978890005" title="Screen Shot 2015-06-20 at 1.01.48 AM by Yanxing Wang, on Flickr"><img src="https://c1.staticflickr.com/1/401/18978890005_b92d0b3d75_o.png" width="1047" height="639" alt="Screen Shot 2015-06-20 at 1.01.48 AM"></a>

## Features

* Syntax highlighting for BUCK file
* Keywords auto completion
* Go to buck file with one click
* Go to to paths of dependencies with one click
* Shortcuts for buck commands, e.g. buck install/build/test (still under development)

![Plugin in action](http://i.giphy.com/3o85xwC8dOyakxqhag.gif)

## Installation

- Using IDEA built-in plugin platform:
  - <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "Buck"</kbd> > <kbd>Install Plugin</kbd>
  
- Or manually:
  - Download the Buck.jar of this repo and install it via <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>
  
Restart your IDE.

## Contributors

* Yanxing Wang
* Sha Ni
* Long Ma

## Bugs

Bug reporting and feedbacks are super welcome!
Just post something here: https://github.com/wangyanxing/buck_idea_plugin/issues
