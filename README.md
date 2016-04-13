[![Build Status](https://travis-ci.org/Sciss/DockingFrames.svg?branch=sbtfied)](https://travis-ci.org/Sciss/DockingFrames)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/docking-frames-common/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/docking-frames-common)

# DockingFrames

This is a fork of the [DockingFrames project](https://github.com/Benoker/DockingFrames), a Java Swing 
window docking framework. Please also see the [original README](readme.txt). It is published
under the GNU Lesser General Public License (LGPL) v2.1+.

This fork is mostly identical with upstream, but includes a few fixes and is published to Maven Central.
To accomplish this, and to avoid confusion with the original project, we use a different group-identifier and version.
The group-id is now `"de.sciss"` instead of `"org.dockingframes"`, however the library and package structure 
is identical.

This branch is not built with Maven but using [sbt](http://www.scala-sbt.org/), a modern build tool known from the 
Scala world.

## Published artifacts

WebLaF can be used in your maven project using the following information:

    <dependency>
      <groupId>de.sciss</groupId>
      <artifactId>docking-frames-common</artifactId>
      <version>{v}</version>
    </dependency>

Or in an sbt based project:

    "de.sciss" % "docking-frames-common" % v

The current version `v` is `"0.1.0"` (no relation to original DockingFrames project version).

## Building

For simplicity, the `sbt` shell script by [Paul Phillips](https://github.com/paulp/sbt-extras) is included, 
made available under a BSD license. For example, you can publish a locally available artifact
using `./sbt publish-local`.
