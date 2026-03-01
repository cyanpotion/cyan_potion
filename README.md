# cyan_potion
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xenoamess.cyan_potion/cyan_potion/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xenoamess.cyan_potion/cyan_potion)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/cyanpotion/cyan_potion)
<!--
[![Total Lines](https://tokei.rs/b1/github/cyanpotion/cyan_potion/)](https://github.com/XAMPPRocky/tokei).
no, it is totally unusable.
resources/www/data/Map001.json has 60K lines itself.
and have no way to exclude it.
-->

A lightweight 2d game engine in java.

Introduction
----------
After a long period of hesitation, I managed to convince myself to open source most parts of it.

**License Notice:** This project is licensed under GPLv3. However, the project owner (XenoAmess) reserves the right to relicense contributions under different terms (including proprietary licenses). See [CLA.md](CLA.md) for details.

This engine is built on the several principles:

    1. completing 2d games in java.
    2. prefer more programmers friendly, not artists friendly.
    3. high freedom + high expandability.
    4. be crazy.

Current state
----------
It is still in the process of development, and many places are incomplete.

Many codes related to animation/display have not been completed yet.

The event system is also very rudimentary.

And no much materials for the inner logic for now.

Notice about sonar cloud
----------
Right now we (or I, exactly) decided just use sonar cloud as code smell detector or something.

We do not care much about test coverage because it is really hard to write some auto-test for a GUI program,

and this engine not works very well when there be no window(though it really can work but...).

We might add some tests when repairing some bug and make sure it not happened again,
but no plan for adding more tests just for gaining coverage and make the data looks more beautiful now. 

But we will not reject all reasonable adding test pull requests.

Demo
----------
Here is the [demo](https://github.com/cyanpotion/cyan_potion/tree/develop/src/demo).

(The demo is actually for cyan_potion_rpg_module, but I guess this counts.)

![image](src/demo/demo.gif)

You can also take a look at [this](https://store.steampowered.com/app/999030/).

Contribute
----------

Also, if there be people who want to contribute codes/ideas/suggestions, feel free to do so.

**Important:** By submitting a pull request, you agree to the [Contributor License Agreement (CLA)](CLA.md). 
This grants the project owner the right to relicense your contributions under different terms (including proprietary licenses).

Please also notice that if I receive your pull request, you will be put in the authors list.

If you want to be put in the licenses folder also, please notice that we accept only contributions that comply with the CLA.

Special Thanks
----------
[![Java Profiler](https://www.ej-technologies.com/images/product_banners/jprofiler_small.png)Java profiler](https://www.ej-technologies.com/products/jprofiler/overview.html)
Thanks ej-technologies for providing such a wonderful performance analysing tool.

Thanks for reading this
----------

-----XenoAmess
