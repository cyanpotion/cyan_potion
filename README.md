# cyan_potion
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.xenoamess.cyan_potion/cyan_potion/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.xenoamess.cyan_potion/cyan_potion)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://travis-ci.org/cyanpotion/cyan_potion.svg?branch=master)](https://travis-ci.org/cyanpotion/cyan_potion)
<!--
[![Total Lines](https://tokei.rs/b1/github/cyanpotion/cyan_potion/)](https://github.com/XAMPPRocky/tokei).
no, it is totally unusable.
resources/www/data/Map001.json has 60K lines itself.
and have no way to exclude it.
-->

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=cyan_potion_base&metric=alert_status)](https://sonarcloud.io/dashboard?id=cyan_potion_base)
cyan_potion_base

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=cyan_potion_coordinate&metric=alert_status)](https://sonarcloud.io/dashboard?id=cyan_potion_coordinate)
cyan_potion_coordinate

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=cyan_potion_rpg_module&metric=alert_status)](https://sonarcloud.io/dashboard?id=cyan_potion_rpg_module)
cyan_potion_rpg_module

A lightweight 2d game engine in java.

Introduction
----------
After a long period of hesitation, I managed to convince myself to open source most parts of it, yes, even MIT instead of GNU.

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

Demo
----------
Here is the [demo](https://github.com/cyanpotion/cyan_potion/tree/develop/src/demo).

(The demo is actually for cyan_potion_rpg_module, but I guess this counts.)

![image](src/demo/demo.gif)

You can also take a look at [this](https://store.steampowered.com/app/999030/).

Contribute
----------

Also, if there be people who want to contribute codes/ideas/suggestions, feel free to do so.

Please also notice that if I receive your pull request, you will be put in the authors list.

If you want to be put in the licenses folder also, please notice that we accept only MIT license for your pull request.

Add the MIT license to your code piece and put it into into license folder, and write description about it.

Special Thanks
----------
[![Java Profiler](https://www.ej-technologies.com/images/product_banners/jprofiler_small.png)Java profiler](https://www.ej-technologies.com/products/jprofiler/overview.html)
Thanks ej-technologies for providing such a wonderful performance analysing tool.

Thanks for reading this
----------

-----XenoAmess
